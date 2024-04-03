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

package bisq.remote.test.statements;

import bisq.remote.image.capture.ImageCaptureService;
import bisq.remote.test.utils.JavaFxServiceUtil;
import bisq.remote.test.utils.JavaFxThreadUtil;
import javafx.concurrent.Worker;
import org.assertj.core.api.SoftAssertions;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenTheWebcamService {
    private final ImageCaptureService imageCaptureService;
    private final JavaFxServiceUtil<BufferedImage> webcamServiceUtil;

    public ThenTheWebcamService(ImageCaptureService imageCaptureService) {
        this.imageCaptureService = imageCaptureService;
        this.webcamServiceUtil = new JavaFxServiceUtil<>(imageCaptureService);
    }

    public ThenTheWebcamService failsWithException(Exception expectedException) {
        JavaFxServiceUtil.ServiceStatus<BufferedImage> webcamServiceStatus =
                webcamServiceUtil.waitForServiceDesiredState(Worker.State.FAILED);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(webcamServiceStatus.exception().getClass())
                    .isEqualTo(expectedException.getClass());
            softly.assertThat(webcamServiceStatus.exception().getMessage())
                    .isEqualTo(expectedException.getMessage());
        });
        return this;
    }

    public void hasValue(BufferedImage value)
            throws ExecutionException, InterruptedException, TimeoutException {
        JavaFxThreadUtil.runInFxThread(() -> assertThat(imageCaptureService.getValue()).isEqualTo(value));
    }
}
