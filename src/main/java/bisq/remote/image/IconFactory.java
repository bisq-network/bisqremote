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

package bisq.remote.image;

import javafx.scene.image.Image;
import lombok.NonNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provides a means for retrieving icons used within the application.
 * For example, {@code IconFactory.getImage(IconFactory.ICON.APP)}.
 */
public class IconFactory {
    private static final Map<ICON, Image> images = new EnumMap<>(ICON.class);

    public enum ICON {
        APP,
        COPY,
        TRASH
    }

    public static Image getImage(@NonNull final ICON icon) {
        switch (icon) {
            case APP -> images.putIfAbsent(
                    icon,
                    new Image(
                            Objects.requireNonNull(
                                    IconFactory.class
                                            .getClassLoader()
                                            .getResourceAsStream("icons/push_notification.png"))));
            case COPY -> images.putIfAbsent(
                    icon,
                    new Image(
                            Objects.requireNonNull(
                                    IconFactory.class
                                            .getClassLoader()
                                            .getResourceAsStream("icons/copy.png"))));
            case TRASH -> images.putIfAbsent(
                    icon,
                    new Image(
                            Objects.requireNonNull(
                                    IconFactory.class
                                            .getClassLoader()
                                            .getResourceAsStream("icons/trash.png"))));
        }
        return images.get(icon);
    }
}
