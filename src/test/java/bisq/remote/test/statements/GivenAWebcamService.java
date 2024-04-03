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

import bisq.remote.image.capture.*;
import bisq.remote.test.doubles.DisconnectedWebcamDeviceFake;
import bisq.remote.test.doubles.NonOpeningWebcamDeviceFake;
import bisq.remote.test.doubles.WebcamDeviceFake;
import bisq.remote.test.doubles.WebcamFake;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;

import java.awt.image.BufferedImage;

public class GivenAWebcamService {
    private final ImageCaptureService imageCaptureService;

    public GivenAWebcamService(ImageCaptureService imageCaptureService) {
        this.imageCaptureService = imageCaptureService;
    }

    @SuppressWarnings("EmptyMethod")
    public void withNoWebcam() {
        // Nothing to do!
    }

    public void withNonOpeningWebcamDevice(RuntimeException thrownException) {
        WebcamDevice webcamDevice = new NonOpeningWebcamDeviceFake(thrownException);
        Webcam webcam = new WebcamFake(webcamDevice);
        imageCaptureService.setWebcam(webcam);
    }

    public void withDisconnectedWebcamDevice() {
        WebcamDevice webcamDevice = new DisconnectedWebcamDeviceFake();
        Webcam webcam = new WebcamFake(webcamDevice);
        imageCaptureService.setWebcam(webcam);
    }

    public void withFunctioningWebcamDevice(BufferedImage bufferedImage) {
        WebcamDevice webcamDevice = new WebcamDeviceFake(bufferedImage);
        Webcam webcam = new WebcamFake(webcamDevice);
        imageCaptureService.setWebcam(webcam);
    }
}
