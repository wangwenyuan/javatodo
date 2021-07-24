package com.javatodo.core.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QR {
	private static final int BLACK = 0xFF000000;// 用于设置图案的颜色
	private static final int WHITE = 0xFFFFFFFF; // 用于背景色

	/**
	 * 设置 logo
	 * 
	 * @param matrixImage源二维码图片
	 * @param logo_img_path     LOGO图片路径
	 * @return 返回带有logo的二维码图片
	 * @throws IOException
	 */
	private static BufferedImage LogoMatrix(BufferedImage matrixImage, String logo_img_path) throws IOException {
		// 读取二维码图片，并构建绘图对象
		Graphics2D g2 = matrixImage.createGraphics();
		int matrixWidth = matrixImage.getWidth();
		int matrixHeigh = matrixImage.getHeight();

		// 读取Logo图片
		BufferedImage logo = ImageIO.read(new File(logo_img_path));
		// 开始绘制图片
		g2.drawImage(logo, matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, null);// 绘制
		BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);// 设置笔画对象
		// 指定弧度的圆角矩形
		RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth / 5 * 2, matrixHeigh / 5 * 2, matrixWidth / 5, matrixHeigh / 5, 20, 20);
		g2.setColor(Color.white);
		g2.draw(round);// 绘制圆弧矩形
		// 设置logo 有一道灰色边框
		BasicStroke stroke2 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke2);// 设置笔画对象
		RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth / 5 * 2 + 2, matrixHeigh / 5 * 2 + 2, matrixWidth / 5 - 4, matrixHeigh / 5 - 4, 20, 20);
		g2.setColor(new Color(128, 128, 128));
		g2.draw(round2);// 绘制圆弧矩形
		g2.dispose();
		matrixImage.flush();
		return matrixImage;
	}

	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, (matrix.get(x, y) ? BLACK : WHITE));
			}
		}
		return image;
	}

	private static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		} else {
			System.out.println("图片生成成功！");
		}
	}

	private static void writeToFile(BitMatrix matrix, String logo_img_path, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		// 设置logo图标
		image = QR.LogoMatrix(image, logo_img_path);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		} else {
			System.out.println("图片生成成功！");
		}
	}

	private static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		// 设置logo图标
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

	private static void writeToStream(BitMatrix matrix, String logo_img_path, String format, OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		// 设置logo图标
		image = QR.LogoMatrix(image, logo_img_path);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

	private static BitMatrix createQrBitMatrix(String contents) throws WriterException {
		int width = 430; // 二维码图片宽度430
		int height = 430; // 二维码图片高度430
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 内容所使用字符集编码
		hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.toString());
		hints.put(EncodeHintType.MARGIN, 1);// 设置二维码边的空度，非负数
		BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, // 要编码的内容
				BarcodeFormat.QR_CODE, width, // 条形码的宽度
				height, // 条形码的高度
				hints);// 生成条形码时的一些配置,此项可选
		return bitMatrix;
	}

	// 输出二维码到文件
	public static void EncodeToFile(String contents, String output_file_path) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		String format = "png";// 二维码的图片格式 png
		// 生成二维码
		File outputFile = new File(output_file_path);// 指定输出路径
		QR.writeToFile(bitMatrix, format, outputFile);
	}

	// 输出二维码到文件
	public static void EncodeToFile(String contents, String logo_img_path, String output_file_path) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		String format = "png";// 二维码的图片格式 png
		// 生成二维码
		File outputFile = new File(output_file_path);// 指定输出路径
		QR.writeToFile(bitMatrix, logo_img_path, format, outputFile);
	}

	// 输出二维码到流
	public static void EncodeToStream(String contents, OutputStream stream) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		String format = "png";// 二维码的图片格式 png
		// 生成二维码
		QR.writeToStream(bitMatrix, format, stream);
	}

	// 输出二维码到流
	public static void EncodeToStream(String contents, String logo_img_path, OutputStream stream) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		String format = "png";// 二维码的图片格式 png
		// 生成二维码
		QR.writeToStream(bitMatrix, logo_img_path, format, stream);
	}

	// 输出二维码到base64
	public static String EncodeToBase64(String contents, OutputStream stream) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		BufferedImage image = toBufferedImage(bitMatrix);
		String imageString = null;
		String type = "png";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = (new Base64()).encodeToString(imageBytes);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	// 输出带logo的二维码到base64
	public static String EncodeToBase64(String contents, String logo_img_path, OutputStream stream) throws IOException, WriterException {
		BitMatrix bitMatrix = QR.createQrBitMatrix(contents);
		BufferedImage image = toBufferedImage(bitMatrix);
		image = QR.LogoMatrix(image, logo_img_path);
		String imageString = null;
		String type = "png";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = (new Base64()).encodeToString(imageBytes);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}
}
