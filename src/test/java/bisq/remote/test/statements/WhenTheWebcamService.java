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

import bisq.remote.test.utils.JavaFxServiceUtil;
import bisq.remote.test.utils.JavaFxThreadUtil;
import bisq.remote.image.capture.ImageCaptureService;
import javafx.concurrent.Worker;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenTheWebcamService {
    private final ImageCaptureService imageCaptureService;
    private final JavaFxServiceUtil<BufferedImage> webcamServiceUtil;

    public WhenTheWebcamService(ImageCaptureService imageCaptureService) {
        this.imageCaptureService = imageCaptureService;
        this.webcamServiceUtil = new JavaFxServiceUtil<>(imageCaptureService);
    }

    public WhenTheWebcamService isStarted() {
        assertEquals(Worker.State.READY, imageCaptureService.getState());
        imageCaptureService.start();
        return this;
    }

    public WhenTheWebcamService isStopped()
            throws ExecutionException, InterruptedException, TimeoutException {
        JavaFxThreadUtil.runInFxThread(imageCaptureService::cancel);
        return this;
    }

    public void andTheStateChangesToRunning() {
        webcamServiceUtil.waitForServiceDesiredState(Worker.State.RUNNING);
    }

    public JavaFxServiceUtil.ServiceStatus<BufferedImage> andTheValueUpdates() {
        return webcamServiceUtil.waitForServiceUpdateValue(null);
    }

    public void andTheValueIsNull() {
        webcamServiceUtil.waitForServiceNullValue();
    }
}
