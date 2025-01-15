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

package bisq.remote.test.doubles;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDevice;

import java.awt.image.BufferedImage;

public class WebcamFake extends Webcam {
    protected static final double FPS = 30.0;
    protected long isImageNewTimestamp = 0;

    public WebcamFake(WebcamDevice device) {
        super(device);
    }

    @Override
    public boolean open() {
        getDevice().open();
        return true;
    }

    @Override
    public boolean close() {
        getDevice().close();
        return true;
    }

    @Override
    public boolean isOpen() {
        return getDevice().isOpen();
    }

    @Override
    public BufferedImage getImage() {
        return getDevice().getImage();
    }

    @Override
    public boolean isImageNew() {
        // Introduce a delay when a new image is available in order to simulate the FPS of the webcam
        if (isImageNewTimestamp == 0) {
            isImageNewTimestamp = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - isImageNewTimestamp < (1 / getFPS()) * 1000) {
            return false;
        }

        isImageNewTimestamp = 0;

        return true;
    }

    @Override
    public double getFPS() {
        return FPS;
    }
}
