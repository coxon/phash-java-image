package com.coxon.phash.core;

import org.jtransforms.dct.FloatDCT_2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * 感知hash
 *
 * @author coxon
 */
public class PerceptualHash implements ImageHash {

    private static final int SCALED_IMAGE_SIZE = 32;
    private static final FloatDCT_2D DCT = new FloatDCT_2D(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE);


    @Override
    public long getPerceptualHash(final Image image) {

        //scale , we want grey picture
        final BufferedImage scaledImage = new BufferedImage(SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, BufferedImage.TYPE_BYTE_GRAY);
        final Graphics2D graphics = scaledImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(image, 0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, null);
        graphics.dispose();


        //now get 2D discrete cosine for scaled image
        final float[] dct = new float[SCALED_IMAGE_SIZE * SCALED_IMAGE_SIZE];
        scaledImage.getData().getPixels(0, 0, SCALED_IMAGE_SIZE, SCALED_IMAGE_SIZE, dct);
        DCT.forward(dct, false);

        //get 8*8 pixel from left-top and get average
        float lowFrequencyDctAverage = -dct[0];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                lowFrequencyDctAverage += dct[x + (y * SCALED_IMAGE_SIZE)];
            }
        }

        lowFrequencyDctAverage /= 64;


        // gt avg set bit-val to 1 else 0
        long hash = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                hash <<= 1;

                if (dct[x + (y * SCALED_IMAGE_SIZE)] > lowFrequencyDctAverage) {
                    hash |= 1;
                }
            }
        }

        return hash;
    }

    public static void main(String[] args) throws IOException {

        PerceptualHash perceptualHash = new PerceptualHash();

        long hash1 = perceptualHash.getPerceptualHash(ImageIO.read(new URL("https://kara-image.asiainfo.com/scale/channel/d4eb1b82-e9ad-4285-bacc-7283e998be32.png")));
        long hash = perceptualHash.getPerceptualHash(ImageIO.read(new URL("https://kara-image.asiainfo.com/scale/channel/d3c3c8b0-adab-4aa0-acae-3259db0caae3.png")));

        CompareImageHash.getInstance().compare(Long.toBinaryString(hash1), Long.toBinaryString(hash));
    }
}
