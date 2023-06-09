package com.example.servak;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {
    public String qr_code(long order){
        int width = 350;//350
        int height = 350;//350
        String fileType = "png";
        String filePath = "Data\\Images\\QR_CODE\\"+order + ".jpg";//order+".png"

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(String.valueOf(order), BarcodeFormat.QR_CODE, width, height);

            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, fileType, path);
            System.out.println("QR Code generated successfully.");
        } catch (WriterException | IOException e) {
            System.out.println("Error while generating QR Code: " + e.getMessage());
        }
        return filePath;
    }
}