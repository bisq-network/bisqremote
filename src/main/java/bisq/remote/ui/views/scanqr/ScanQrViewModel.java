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

package bisq.remote.ui.views.scanqr;

import bisq.remote.image.capture.ImageCaptureService;
import com.github.sarxos.webcam.Webcam;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Getter
@Component
public class ScanQrViewModel {
    private final ObservableList<WebcamListItem> availableWebcams = FXCollections.observableArrayList();
    private final ObjectProperty<WebcamListItem> selectedWebcam = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> webcamImage = new SimpleObjectProperty<>();

    public ScanQrViewModel(ImageCaptureService imageCaptureService) {
        imageCaptureService
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue == null) {
                                webcamImage.setValue(null);
                                return;
                            }
                            webcamImage.setValue(SwingFXUtils.toFXImage(newValue, null));
                        });
    }

    public void setSelectedWebcam(@NotNull final WebcamListItem webcamListItem) {
        selectedWebcam.setValue(webcamListItem);
    }

    public void clearWebcamImage() {
        webcamImage.setValue(null);
    }

    public void discoverAvailableWebcams() {
        availableWebcams.clear();
        for (Webcam webcam : Webcam.getWebcams()) {
            availableWebcams.add(new WebcamListItem(webcam));
        }
    }
}
