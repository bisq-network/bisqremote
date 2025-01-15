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

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.concurrent.Task;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Uses the {@link com.github.sarxos.webcam} library to provide a {@link javafx.concurrent.Service} that,
 * when started, will continuously update its value with the latest captured image from a specified {@link Webcam}.
 * <p>
 * A listener can be attached to the value property to act upon the captured images.
 * <p>
 * For example:
 * <pre>
 *     {@code ObjectProperty<Image>} webcamImage = new SimpleObjectProperty<>();
 *     ImageCaptureService imageCaptureService = new ImageCaptureService();
 *     imageCaptureService.setWebcam(Webcam.getDefault());
 *     imageCaptureService
 *         .valueProperty()
 *         .addListener(
 *             (observable, oldValue, newValue) -> {
 *                 if (newValue == null) {
 *                     webcamImage.setValue(null);
 *                     return;
 *                 }
 *                 webcamImage.setValue(SwingFXUtils.toFXImage(newValue, null));
 *             });
 *     imageCaptureService.start();
 * </pre>
 */
@Getter
@Service
public class ImageCaptureService extends javafx.concurrent.Service<BufferedImage> {
    private static final WebcamResolution DEFAULT_RESOLUTION = WebcamResolution.VGA;

    private Webcam webcam;
    private Dimension resolution;

    @Override
    protected Task<BufferedImage> createTask() {
        return new Task<>() {
            @Override
            protected BufferedImage call() {
                if (webcam == null) {
                    throw new ImageCaptureException("Webcam must be set before starting the service");
                }

                updateValue(null);
                try {
                    webcam.setViewSize(resolution);
                    webcam.open();
                    while (!isCancelled()) {
                        if (webcam.isImageNew()) {
                            BufferedImage image;
                            if ((image = webcam.getImage()) == null) {
                                throw new WebcamException("Failed to get image");
                            }
                            updateValue(image);
                        }
                    }
                } finally {
                    webcam.close();
                    updateValue(null);
                }
                return getValue();
            }
        };
    }

    /**
     * Set the {@link Webcam} from which to capture images from.
     */
    public void setWebcam(@NotNull final Webcam webcam) {
        if (isRunning()) {
            throw new IllegalStateException("Service must be stopped in order to set webcam");
        }
        if (this.webcam != null && this.webcam.isOpen()) {
            this.webcam.close();
        }
        this.webcam = webcam;
        this.resolution =
                Arrays.stream(webcam.getDevice().getResolutions())
                        .max(new DimensionComparator())
                        .orElse(DEFAULT_RESOLUTION.getSize());
    }

    /**
     * Set the resolution, as a {@link Dimension}, to use for the {@link Webcam}.
     */
    public void setResolution(@NotNull final Dimension resolution) {
        if (isRunning()) {
            throw new IllegalStateException("Service must be stopped in order to set resolution");
        }
        this.resolution = resolution;
    }

    private static class DimensionComparator implements Comparator<Dimension>, Serializable {
        private static double area(@NotNull final Dimension d) {
            return d.getWidth() * d.getHeight();
        }

        public int compare(@NotNull final Dimension d1, @NotNull final Dimension d2) {
            return Double.compare(area(d1), area(d2));
        }
    }
}
