package com.coxon.phash.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * 均值hash
 *
 * @author coxon
 */
public class AverageHash implements ImageHash{


    @Override
    public long getPerceptualHash(final Image image) {
        final BufferedImage scaledImage = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);
        {
            final Graphics2D graphics = scaledImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            graphics.drawImage(image, 0, 0, 8, 8, null);

            graphics.dispose();
        }

        final int[] pixels = new int[64];
        scaledImage.getData().getPixels(0, 0, 8, 8, pixels);

        final int average;
        {
            int total = 0;

            for (int pixel : pixels) {
                total += pixel;
            }

            average = total / 64;
        }

        long hash = 0;

        for (final int pixel : pixels) {
            hash <<= 1;

            if (pixel > average) {
                hash |= 1;
            }
        }

        return hash;
    }


    public static void main(String[] args) throws IOException {
        AverageHash perceptualHash = new AverageHash();
        long hash1 = perceptualHash.getPerceptualHash(ImageIO.read(new URL("https://kara-image.asiainfo.com/scale/channel/d4eb1b82-e9ad-4285-bacc-7283e998be32.png")));
        long hash = perceptualHash.getPerceptualHash(ImageIO.read(new URL("https://kara-image.asiainfo.com/scale/channel/d3c3c8b0-adab-4aa0-acae-3259db0caae3.png")));

        CompareImageHash.getInstance().compare(Long.toHexString(hash1), Long.toHexString(hash));
    }
}
