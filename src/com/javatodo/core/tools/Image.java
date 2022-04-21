package com.javatodo.core.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	/**
	 * 调整图片尺寸
	 * 
	 * @param src,原图片路径
	 * @param dist,新图片路径
	 * @param width,新图片的宽度
	 * @return
	 */
	public static Boolean resizeImage(String src, String dist, float width) {
		try {
			BufferedImage image = ImageIO.read(new File(src));
			double ratio = width / image.getWidth();
			int newWidth = (int) (image.getWidth() * ratio);
			int newHeight = (int) (image.getHeight() * ratio);
			BufferedImage bfImage = new BufferedImage(newWidth, newHeight, image.getType());
			bfImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
			FileOutputStream os = new FileOutputStream(dist);
			ImageIO.write(bfImage, getType(src), os);
			os.close();
			os = null;
			return true;
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 调整图片尺寸
	 * 
	 * @param src,原图片路径
	 * @param dist,新图片路径
	 * @param width,新图片的宽度
	 * @param height,新图片的高度
	 * @return
	 */
	public static Boolean resizeImage(String src, String dist, float width, float height) {
		try {
			BufferedImage image = ImageIO.read(new File(src));
			int newWidth = (int) width;
			int newHeight = (int) height;
			BufferedImage bfImage = new BufferedImage(newWidth, newHeight, image.getType());
			bfImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
			FileOutputStream os = new FileOutputStream(dist);
			ImageIO.write(bfImage, getType(src), os);
			os.close();
			os = null;
			return true;
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 裁剪图片
	 * 
	 * @param src,原图片路径
	 * @param dist,新图片路径
	 * @param startX,裁剪起始位置的X坐标
	 * @param startY,裁剪起始位置的Y坐标
	 * @param endX,裁剪结束位置的X坐标
	 * @param endY,裁剪起始位置的Y坐标
	 * @return
	 */
	public static Boolean cropImage(String src, String dist, int startX, int startY, int endX, int endY) {
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(src));
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			if (startX < 0) {
				startX = 0;
			}
			if (startY < 0) {
				startY = 0;
			}
			if (startX > width) {
				startX = width;
			}
			if (startY > height) {
				startY = height;
			}

			if (endX < 0 || endX < startX) {
				endX = startX;
			}
			if (endY < 0 || endY < startY) {
				endY = startY;
			}
			if (endX > width) {
				endX = width;
			}
			if (endY > height) {
				endY = height;
			}
			BufferedImage result = new BufferedImage(endX - startX, endY - startY, bufferedImage.getType());
			for (int x = startX; x < endX; ++x) {
				for (int y = startY; y < endY; ++y) {
					int rgb = bufferedImage.getRGB(x, y);
					result.setRGB(x - startX, y - startY, rgb);
				}
			}
			FileOutputStream os = new FileOutputStream(dist);
			ImageIO.write(result, getType(src), os);
			os.close();
			os = null;
			return true;
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 合并图片
	 * 
	 * @param src,原图片路径
	 * @param logosrc,LOGO的路径
	 * @param dist,合并后的图片路径
	 * @param x,logo在原图片的x坐标
	 * @param y,logo在原图片的y坐标
	 * @return
	 */
	public static boolean overlapImage(String src, String logosrc, String dist, float x, float y) {
		try {
			BufferedImage big = ImageIO.read(new File(src));
			BufferedImage logo = ImageIO.read(new File(logosrc));
			Graphics2D g = big.createGraphics();
			g.drawImage(logo, (int) x, (int) y, logo.getWidth(), logo.getHeight(), null);
			g.dispose();
			if (!new File(dist).exists()) {
				new File(dist).createNewFile();
			}
			return ImageIO.write(big, dist.split("\\.")[1], new File(dist));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 在图片上写字
	 * 
	 * @param src,原图片路径
	 * @param dist,合并后的图片路径
	 * @param text,文字内容
	 * @param left,文字左边距离
	 * @param top,文字顶部距离
	 * @param color,文字颜色
	 * @param fontSize,文字大小
	 * @return
	 */
	public static void drawText(String src, String dist, String text, int left, int top, String color, int fontSize) throws IOException {
		BufferedImage bimage = ImageIO.read(new File(src));
		Graphics2D g = bimage.createGraphics();
		Font font = new Font("宋体", Font.BOLD, fontSize); // 定义字体
		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
		top = top + fm.getHeight();
		color = color.replace("#", "");
		int _color = (int) Long.parseLong(color, 16);
		int _r = (_color >> 16) & 0xFF;
		int _g = (_color >> 8) & 0xFF;
		int _b = (_color >> 0) & 0xFF;
		g.setPaint(new Color(_r, _g, _b));
		g.setFont(font);
		g.drawString(text, left, top);
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(dist);
			ImageIO.write(bimage, "png", out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件的mimeType
	 * 
	 * @param filename
	 * @return
	 */
	public static String getMimeType(String filename) {
		try {
			String mimeType = getType(filename);
			return String.format("image/%s", mimeType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取图片类型
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String getType(String filename) throws IOException {
		FileInputStream fis = null;
		File f = new File(filename);
		if (!f.exists() || f.isDirectory() || f.length() < 8) {
			return "";
		}
		fis = new FileInputStream(f);
		byte[] bufHeaders = readInputStreamAt(fis, 0, 8);
		if (isJPEGHeader(bufHeaders)) {
			long skiplength = f.length() - 2 - 8; // 第一次读取时已经读了8个byte,因此需要减掉
			byte[] bufFooters = readInputStreamAt(fis, skiplength, 2);
			if (isJPEGFooter(bufFooters)) {
				return "jpeg";
			}
		}
		if (isPNG(bufHeaders)) {
			return "png";
		}
		if (isGIF(bufHeaders)) {
			return "gif";
		}
		if (isWEBP(bufHeaders)) {
			return "webp";
		}
		if (isBMP(bufHeaders)) {
			return "bmp";
		}
		if (isICON(bufHeaders)) {
			return "ico";
		}
		return "";
	}

	/**
	 * 标示一致性比较
	 * 
	 * @param buf     待检测标示
	 * @param markBuf 标识符字节数组
	 * @return 返回false标示标示不匹配
	 */
	private static boolean compare(byte[] buf, byte[] markBuf) {
		for (int i = 0; i < markBuf.length; i++) {
			byte b = markBuf[i];
			byte a = buf[i];

			if (a != b) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param fis        输入流对象
	 * @param skiplength 跳过位置长度
	 * @param length     要读取的长度
	 * @return 字节数组
	 * @throws IOException
	 */
	private static byte[] readInputStreamAt(FileInputStream fis, long skiplength, int length) throws IOException {
		byte[] buf = new byte[length];
		fis.skip(skiplength); //
		int read = fis.read(buf, 0, length);
		return buf;
	}

	private static boolean isBMP(byte[] buf) {
		byte[] markBuf = "BM".getBytes(); // BMP图片文件的前两个字节
		return compare(buf, markBuf);
	}

	private static boolean isICON(byte[] buf) {
		byte[] markBuf = { 0, 0, 1, 0, 1, 0, 32, 32 };
		return compare(buf, markBuf);
	}

	private static boolean isWEBP(byte[] buf) {
		byte[] markBuf = "RIFF".getBytes(); // WebP图片识别符
		return compare(buf, markBuf);
	}

	private static boolean isGIF(byte[] buf) {
		byte[] markBuf = "GIF89a".getBytes(); // GIF识别符
		if (compare(buf, markBuf)) {
			return true;
		}
		markBuf = "GIF87a".getBytes(); // GIF识别符
		if (compare(buf, markBuf)) {
			return true;
		}
		return false;
	}

	private static boolean isPNG(byte[] buf) {
		byte[] markBuf = { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A }; // PNG识别符
		// new String(buf).indexOf("PNG")>0 //也可以使用这种方式
		return compare(buf, markBuf);
	}

	private static boolean isJPEGHeader(byte[] buf) {
		byte[] markBuf = { (byte) 0xff, (byte) 0xd8 }; // JPEG开始符
		return compare(buf, markBuf);
	}

	private static boolean isJPEGFooter(byte[] buf)// JPEG结束符
	{
		byte[] markBuf = { (byte) 0xff, (byte) 0xd9 };
		return compare(buf, markBuf);
	}
}
