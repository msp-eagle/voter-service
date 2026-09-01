package com.example.regclient_newVersion.Service;


import com.example.regclient_newVersion.dto.SignatureResponse;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class WacomSignatureService {

    private static final String JAR_CLASSPATH =
            "Wacom-0.0.1-SNAPSHOT.jar;wgssSTU.jar";

    private static final String MAIN_CLASS =
            "com.exampleDemoButtons.DemoButtons";

    public SignatureResponse captureSignature() throws Exception {

        File signatureFile = new File("signature.jpg");

        // Remove previous signature
        Files.deleteIfExists(signatureFile.toPath());

        /*
         * Start Wacom SDK application.
         *
         * This should open the Wacom Signature Pad
         * and wait for the user to sign.
         */
        Process process = new ProcessBuilder(
                "java",
                "-cp",
                JAR_CLASSPATH,
                MAIN_CLASS
        )
        .inheritIO()
        .start();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            return new SignatureResponse(
                    false,
                    "Wacom signature capture failed.",
                    null
            );
        }

        if (!signatureFile.exists()) {
            return new SignatureResponse(
                    false,
                    "Signature was not captured.",
                    null
            );
        }

        // Read captured image
        BufferedImage jpgImage =
                ImageIO.read(signatureFile);

        if (jpgImage == null) {
            return new SignatureResponse(
                    false,
                    "Unable to read captured signature.",
                    null
            );
        }

        BufferedImage cleanedImage =
                makeBackgroundTransparent(jpgImage);

        BufferedImage centeredImage =
                cropAndCenter(cleanedImage);

        // Convert PNG to Base64
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(
                centeredImage,
                "png",
                outputStream
        );

        byte[] pngBytes =
                outputStream.toByteArray();

        String base64Signature =
                Base64.getEncoder()
                        .encodeToString(pngBytes);

        return new SignatureResponse(
                true,
                "Signature captured successfully.",
                base64Signature
        );
    }

    private BufferedImage makeBackgroundTransparent(
            BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage result =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB
                );

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int pixel = image.getRGB(x, y);

                int alpha =
                        (pixel == Color.WHITE.getRGB())
                                ? 0
                                : 255;

                int rgb =
                        pixel & 0x00FFFFFF;

                result.setRGB(
                        x,
                        y,
                        (alpha << 24) | rgb
                );
            }
        }

        return result;
    }

    private BufferedImage cropAndCenter(
            BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        int minX = width;
        int minY = height;
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int alpha =
                        (image.getRGB(x, y) >> 24)
                                & 0xff;

                if (alpha > 0) {

                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        // No signature detected
        if (minX >= maxX || minY >= maxY) {
            return image;
        }

        int signatureWidth =
                maxX - minX + 1;

        int signatureHeight =
                maxY - minY + 1;

        BufferedImage cropped =
                image.getSubimage(
                        minX,
                        minY,
                        signatureWidth,
                        signatureHeight
                );

        BufferedImage centered =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D graphics =
                centered.createGraphics();

        int xOffset =
                (width - signatureWidth) / 2;

        int yOffset =
                (height - signatureHeight) / 2;

        graphics.drawImage(
                cropped,
                xOffset,
                yOffset,
                null
        );

        graphics.dispose();

        return centered;
    }
}

