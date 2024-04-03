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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IconFactoryTest {
    @Test
    void whenGettingNullIcon_thenExceptionThrown() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> IconFactory.getImage(null));
    }

    @ParameterizedTest
    @EnumSource(IconFactory.ICON.class)
    void whenGettingAnIcon_thenAnImageIsReturned(IconFactory.ICON icon) {
        assertThat(IconFactory.getImage(icon)).isInstanceOf(Image.class);
    }
}
