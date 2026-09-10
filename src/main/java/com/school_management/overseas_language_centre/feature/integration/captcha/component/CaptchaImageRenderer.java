package com.school_management.overseas_language_centre.feature.integration.captcha.component;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;                 // write BufferedImage → PNG bytes
import java.awt.*;                            // Graphics2D, Color, Font, …
import java.awt.image.BufferedImage;          // in-memory RGB bitmap
import java.io.ByteArrayOutputStream;         // hold PNG bytes in memory
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.SecureRandom;            // strong RNG for noise / jitter
import java.util.Base64;                      // encode PNG bytes for JSON

/**
 * Draws a captcha image from a plaintext code and returns it as Base64 PNG.
 * Used by CaptchaServiceImpl.generate().
 */
@Component
public class CaptchaImageRenderer {

    // Canvas size in pixels (wide enough for ~6 glyphs)
    private static final int WIDTH = 160;
    private static final int HEIGHT = 56;

    // Cryptographically strong RNG for noise lines, dots, and glyph jitter
    private final SecureRandom random = new SecureRandom();

    /**
     * Render {@code code} onto a noisy background and return Base64 PNG
     * (no {@code data:} prefix — the client adds that if needed).
     */
    public String render(String code) {

        // Allocate an RGB bitmap we will paint on
        BufferedImage image =
                new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // Graphics context for drawing shapes / text onto the image
        Graphics2D g = image.createGraphics();

        try {
            // Smoother edges when characters are rotated
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Light green-ish fill so noise stands out
            g.setColor(new Color(245, 248, 244));
            // Paint the entire canvas with that background color
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Random strokes behind the text (harder for bots / OCR)
            drawNoiseLines(g);

            // Draw each captcha character with random tilt / color
            drawCharacters(g, code);

            // Speckle dots on top of the text
            drawNoiseDots(g);

        } finally {
            // Always free native graphics resources
            g.dispose();
        }

        // Encode the finished PNG as a Base64 string for the JSON response
        return toBase64(image);
    }

    /** Draw a few random strokes across the image (behind the text). */
    private void drawNoiseLines(Graphics2D g) {

        // Line thickness in pixels
        g.setStroke(new BasicStroke(1.4f));

        // Five independent noise lines
        for (int i = 0; i < 5; i++) {

            // Muted random color per line (not pure black — softer noise)
            g.setColor(new Color(
                    150 + random.nextInt(80),   // R: 150–229
                    160 + random.nextInt(70),   // G: 160–229
                    150 + random.nextInt(80))); // B: 150–229

            // Line from a random point A → random point B
            g.drawLine(
                    random.nextInt(WIDTH),   // x1
                    random.nextInt(HEIGHT),  // y1
                    random.nextInt(WIDTH),   // x2
                    random.nextInt(HEIGHT)); // y2
        }
    }

    /** Draw each character with slight rotation and position jitter. */
    private void drawCharacters(Graphics2D g, String code) {

        // Horizontal slot width per character (+1 leaves side padding)
        int charWidth = WIDTH / (code.length() + 1);

        // One glyph at a time
        for (int i = 0; i < code.length(); i++) {

            // Bold monospace; size varies slightly (34–39) so glyphs look uneven
            g.setFont(new Font(
                    Font.MONOSPACED,
                    Font.BOLD,
                    34 + random.nextInt(6)));

            // Dark / mid random color (readable on the light background)
            g.setColor(new Color(
                    random.nextInt(90),         // R: 0–89
                    90 + random.nextInt(70),    // G: 90–159
                    random.nextInt(90)));       // B: 0–89

            // Small random rotation in radians (~ ±0.25 rad ≈ ±14°)
            double angle = (random.nextDouble() - 0.5) * 0.5;

            // Baseline position: slot center + tiny horizontal/vertical jitter
            int x = charWidth / 2 + charWidth * i + random.nextInt(6);
            int y = 38 + random.nextInt(8);

            // Rotate around the glyph, draw it, then rotate back
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-angle, x, y); // undo so the next character starts upright
        }
    }

    /** Scatter random 1×1 pixels as noise (on top of the text). */
    private void drawNoiseDots(Graphics2D g) {

        // Sixty speckles across the canvas
        for (int i = 0; i < 60; i++) {

            // Fully random RGB per dot
            g.setColor(new Color(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)));

            // 1×1 filled rectangle = one noise pixel
            g.fillRect(
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT),
                    1,
                    1);
        }
    }

    /** Write the image as PNG into memory and Base64-encode the bytes. */
    private String toBase64(BufferedImage image) {

        // Auto-closes the stream when the try block ends
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Encode the BufferedImage as PNG into `out`
            ImageIO.write(image, "png", out);

            // Turn raw PNG bytes into a Base64 string (safe for JSON)
            return Base64.getEncoder()
                    .encodeToString(out.toByteArray());

        } catch (IOException e) {

            // ImageIO failures are rare; wrap as unchecked for the service layer
            throw new UncheckedIOException(
                    "Failed to render captcha image",
                    e);
        }
    }
}
