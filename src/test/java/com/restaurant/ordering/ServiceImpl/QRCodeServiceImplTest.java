package com.restaurant.ordering.ServiceImpl;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceImplTest {

    @InjectMocks
    private QRCodeServiceImpl qrCodeService;

    @Test
    void generateQRCodeImage_ValidInput_ReturnsImage() {
        // Arrange
        String text = "https://example.com";
        int width = 250;
        int height = 250;

        // Act
        BufferedImage result = qrCodeService.generateQRCodeImage(text, width, height);

        // Assert
        assertNotNull(result);
        assertEquals(width, result.getWidth());
        assertEquals(height, result.getHeight());
    }

    @Test
    void generateQRCodeImage_EmptyText_ThrowsException() {
        // Arrange
        String text = "";
        int width = 250;
        int height = 250;

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            qrCodeService.generateQRCodeImage(text, width, height);
        });
    }

    @Test
    void generateQRCodeImage_SmallDimensions_ReturnsImage() {
        // Arrange
        String text = "https://example.com";
        int width = 50;
        int height = 50;

        // Act
        BufferedImage result = qrCodeService.generateQRCodeImage(text, width, height);

        // Assert
        assertNotNull(result);
        assertEquals(width, result.getWidth());
        assertEquals(height, result.getHeight());
    }

    @Test
    void generateQRCodeImage_LargeDimensions_ReturnsImage() {
        // Arrange
        String text = "https://example.com";
        int width = 500;
        int height = 500;

        // Act
        BufferedImage result = qrCodeService.generateQRCodeImage(text, width, height);

        // Assert
        assertNotNull(result);
        assertEquals(width, result.getWidth());
        assertEquals(height, result.getHeight());
    }
}
