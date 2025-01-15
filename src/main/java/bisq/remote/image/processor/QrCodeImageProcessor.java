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

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;

/**
 * Processes {@link BufferedImage}'s to detect and decode QR codes within the image.
 */
@Component
public class QrCodeImageProcessor implements ImageProcessor<String> {
    private static final Map<DecodeHintType, Object> HINTS = Map.of(
            DecodeHintType.TRY_HARDER, Boolean.TRUE,
            DecodeHintType.PURE_BARCODE, Boolean.FALSE
    );

    /**
     * Processes the given {@link BufferedImage} to detect and decode any QR codes present.
     *
     * @param image The {@link BufferedImage} to be processed.
     * @return An {@link Optional<String>} containing the decoded QR code text if a
     * QR code is detected, or {@link Optional#empty()} if no QR code is found.
     */
    @Override
    public Optional<String> process(@NotNull final BufferedImage image) {
        final LuminanceSource source = new BufferedImageLuminanceSource(image);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            final Result result = new QRCodeReader().decode(bitmap, HINTS);
            return Optional.of(result.getText());
        } catch (NotFoundException | ChecksumException | FormatException ignored) {
            // There is no QR code in the image
            return Optional.empty();
        }
    }
}
