package com.javatodo.core.tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class Image {
	//调整图片大小
	public static OutputStream resizeImage(InputStream is, OutputStream os, String format) throws IOException {
		int size=100;
		BufferedImage prevImage = ImageIO.read(is);
        double width = prevImage.getWidth();
        double height = prevImage.getHeight();
        if(width<300){
        	size=250;
        }else if(width>750){
        	size=750;
        }else {
        	size=(int)width;
        }
        double percent = size/width;
        int newWidth = (int)(width * percent);
        int newHeight = (int)(height * percent);
        BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
        ImageIO.write(image, format, os);
        os.flush();
        is.close();
        os.close();
        //ByteArrayOutputStream b = (ByteArrayOutputStream) os;
        return os;
    }
	//裁剪图片
	public static BufferedImage cropImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int startX=0;
        int startY=0;
        int endX=width;
        int endY=height-20;
        if (startX == -1) {
            startX = 0;
        }
        if (startY == -1) {
            startY = 0;
        }
        if (endX == -1) {
            endX = width - 1;
        }
        if (endY == -1) {
            endY = height - 1;
        }
        BufferedImage result = new BufferedImage(endX - startX, endY - startY, 4);
        for (int x = startX; x < endX; ++x) {
            for (int y = startY; y < endY; ++y) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(x - startX, y - startY, rgb);
            }
        }
        return result;
    }
	//合并图片
	public static final void overlapImage(String bigPath, String outFile) { 
        try { 
            BufferedImage big = ImageIO.read(new File(bigPath));
            String smallPath="D:/001.png";
            if(big.getWidth()<300){
            	smallPath="D:/001.png";
            }else{
            	smallPath="D:/002.png";
            }
            BufferedImage small = ImageIO.read(new File(smallPath)); 
            Graphics2D g = big.createGraphics(); 
            int x = (big.getWidth() - small.getWidth()) / 2; 
            int y = (big.getHeight() - small.getHeight()) / 2; 
            g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null); 
            g.dispose(); 
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile)); 
        } catch (Exception e) { 
            throw new RuntimeException(e); 
        } 
    }
}
