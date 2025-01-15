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

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides various methods for interacting with {@link BufferedImage}'s.
 */
public class ImageUtil {
    private ImageUtil() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Compares two {@link BufferedImage}'s pixel by pixel.
     *
     * @param imgA the first image.
     * @param imgB the second image.
     * @return whether the images are both the same or not.
     */
    public static boolean isImagesIdentical(@NotNull final BufferedImage imgA, @NotNull final BufferedImage imgB) {
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width = imgA.getWidth();
        int height = imgA.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Generates a {@link BufferedImage} consisting of centered white text on a black background.
     * @param text The text content to shown within the generated {@link BufferedImage}.
     * @param width The width of the generated {@link BufferedImage}.
     * @param height The height of the generated {@link BufferedImage}.
     * @return The generated {@link BufferedImage}.
     */
    public static BufferedImage generateTextImage(@NotNull final String text, final int width, final int height) {
        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = bufferedImage.getGraphics();

        final Rectangle rect = new Rectangle(width, height);
        drawCenteredString(graphics, text, rect, new Font("Courier New", Font.PLAIN, 16));

        return bufferedImage;
    }

    /**
     * Draw a {@link String} centered in the middle of a {@link Rectangle}.
     *
     * @param g The ${@link Graphics} instance.
     * @param text The ${@link String} to draw.
     * @param rect The ${@link Rectangle} to center the text in.
     * @param font The ${@link Font} to use to draw the text.
     */
    private static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // For the Y coordinate of the text, add the ascent since in Java 2d 0 is top of the screen
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
}
