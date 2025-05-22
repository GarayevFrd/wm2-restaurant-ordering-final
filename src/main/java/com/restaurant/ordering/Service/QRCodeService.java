package com.restaurant.ordering.Service;

import java.awt.image.BufferedImage;

public interface QRCodeService {
    BufferedImage generateQRCodeImage(String text, int width, int height);
}