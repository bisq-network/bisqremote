/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.remote.image.capture;

import bisq.remote.test.doubles.WebcamDeviceFake;
import bisq.remote.test.doubles.WebcamFake;
import bisq.remote.test.extensions.JavaFxToolkitExtension;
import bisq.remote.test.statements.GivenAWebcamService;
import bisq.remote.test.statements.ThenTheWebcamService;
import bisq.remote.test.statements.WhenTheWebcamService;
import bisq.remote.test.utils.JavaFxServiceUtil;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static bisq.remote.test.utils.JavaFxThreadUtil.runInFxThread;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(JavaFxToolkitExtension.class)
class ImageCaptureServiceTest {
    private final BufferedImage bufferedImage = new BufferedImage(640, 480, TYPE_INT_RGB);
    private final ImageCaptureService imageCaptureService = new ImageCaptureService();
    private final GivenAWebcamService givenAWebcamService = new GivenAWebcamService(imageCaptureService);
    private final WhenTheWebcamService whenTheWebcamService =
            new WhenTheWebcamService(imageCaptureService);
    private final ThenTheWebcamService thenTheWebcamService =
            new ThenTheWebcamService(imageCaptureService);

    @AfterEach
    void cleanup() {
        Platform.runLater(imageCaptureService::cancel);
    }

    @Test
    void startingServiceWithoutWebcamSpecified_serviceFailsWithException() {
        givenAWebcamService.withNoWebcam();
        whenTheWebcamService.isStarted();
        thenTheWebcamService.failsWithException(
                new ImageCaptureException("Webcam must be set before starting the service"));
    }

    @Test
    void startingServiceWhenWebcamDeviceFailsToOpen_serviceFailsWithException() {
        RuntimeException thrownException = new WebcamException("Webcam exception when opening");
        givenAWebcamService.withNonOpeningWebcamDevice(thrownException);
        whenTheWebcamService.isStarted();
        thenTheWebcamService.failsWithException(thrownException);
    }

    @Test
    void startingServiceWhenWebcamDeviceIsDisconnected_serviceFailsWithException()
            throws ExecutionException, InterruptedException, TimeoutException {
        RuntimeException thrownException = new WebcamException("Failed to get image");
        givenAWebcamService.withDisconnectedWebcamDevice();
        whenTheWebcamService.isStarted();
        thenTheWebcamService.failsWithException(thrownException).hasValue(null);
    }

    @Test
    void startingServiceWithFunctioningWebcam_serviceUpdatesValue() {
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        JavaFxServiceUtil.ServiceStatus<BufferedImage> webcamServiceStatus =
                whenTheWebcamService.isStarted().andTheValueUpdates();
        assertThat(webcamServiceStatus.value().getWidth())
                .describedAs("Image width")
                .isEqualTo(bufferedImage.getWidth());
        assertThat(webcamServiceStatus.value().getHeight())
                .describedAs("Image height")
                .isEqualTo(bufferedImage.getHeight());
    }

    @Test
    void changingWebcamWhileServiceIsRunning_throwsException()
            throws ExecutionException, InterruptedException, TimeoutException {
        Webcam newWebcam = mock(Webcam.class);
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        whenTheWebcamService.isStarted().andTheStateChangesToRunning();
        runInFxThread(
                () ->
                        assertThrows(
                                IllegalStateException.class,
                                () -> imageCaptureService.setWebcam(newWebcam)));
    }

    @Test
    void changingWebcamWhileServiceIsStopped_changesWebcam()
            throws ExecutionException, InterruptedException, TimeoutException {
        WebcamDevice newWebcamDevice = new WebcamDeviceFake(bufferedImage);
        Webcam newWebcam = new WebcamFake(newWebcamDevice);
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        runInFxThread(
                () -> {
                    Webcam currentWebcam = imageCaptureService.getWebcam();
                    currentWebcam.open();
                    imageCaptureService.setWebcam(newWebcam);
                    assertThat(imageCaptureService.getWebcam()).isEqualTo(newWebcam);
                    assertThat(currentWebcam.isOpen()).isFalse();
                    assertThat(newWebcam.isOpen()).isFalse();
                });
    }

    @Test
    void changingResolutionWhileServiceIsRunning_throwsException()
            throws ExecutionException, InterruptedException, TimeoutException {
        Dimension newResolution = WebcamResolution.HD.getSize();
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        whenTheWebcamService.isStarted().andTheStateChangesToRunning();
        runInFxThread(
                () ->
                        assertThrows(
                                IllegalStateException.class,
                                () -> imageCaptureService.setResolution(newResolution)));
    }

    @Test
    void changingResolutionWhileServiceIsStopped_changesResolution()
            throws ExecutionException, InterruptedException, TimeoutException {
        Dimension newResolution = WebcamResolution.HD.getSize();
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        runInFxThread(
                () -> {
                    imageCaptureService.setResolution(newResolution);
                    assertThat(imageCaptureService.getResolution()).isEqualTo(newResolution);
                });
    }

    @Test
    void cancellingServiceAfterValueUpdated_valueIsNullAndWebcamIsClosed()
            throws ExecutionException, InterruptedException, TimeoutException {
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        whenTheWebcamService.isStarted().andTheValueUpdates();
        whenTheWebcamService.isStopped().andTheValueIsNull();
        runInFxThread(
                () -> {
                    assertThat(imageCaptureService.getWebcam().isOpen()).isFalse();
                    assertThat(imageCaptureService.getWebcam().getDevice().isOpen()).isFalse();
                });
    }

    @Test
    void checkingValueBeforeValueUpdated_valueIsNull()
            throws ExecutionException, InterruptedException, TimeoutException {
        givenAWebcamService.withFunctioningWebcamDevice(bufferedImage);
        whenTheWebcamService.isStarted();
        thenTheWebcamService.hasValue(null);
    }
}
