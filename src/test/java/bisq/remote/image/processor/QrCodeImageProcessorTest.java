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

package bisq.remote.image.processor;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class QrCodeImageProcessorTest {
    private final QrCodeImageProcessor qrCodeImageProcessor = new QrCodeImageProcessor();

    @Test
    void whenValidQrCodeInImage_thenValidResult() throws IOException {
        BufferedImage image = ImageIO.read(
                new File(Objects.requireNonNull(getClass().getClassLoader().getResource("images/Test_QR.png")).getFile()));

        Optional<String> result = qrCodeImageProcessor.process(image);

        assertThat(result).isNotEmpty();
        assertThat(result.get()).contains("test");
    }

    @Test
    void whenNoQrCodeInImage_thenEmptyResult() {
        BufferedImage image = new BufferedImage(640, 480, TYPE_INT_RGB);

        Optional<String> result = qrCodeImageProcessor.process(image);

        assertThat(result).isEmpty();
    }
}
