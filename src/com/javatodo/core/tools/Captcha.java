/**
 * Copyright (c) 2017, Wang Wenyuan 王文渊 (827287829@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javatodo.core.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import com.javatodo.config.C;

public class Captcha {
	private int width = C.CaptchaWidth;//图片的宽度
	private int height = C.CaptchaHeight;//图片的高度
	private int codeNum = C.CaptchaCodeNum;//验证码字符数
	private int lineNum = C.CaptchaLineNum;//干扰线条数目
	private String code = null;//验证码内容
	private BufferedImage buffImg = null;//图片buffer

	private char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8',
			'9' };

	public Captcha() {
		this.createCode();
	}

	/**
	 * 
	 * @param width
	 *            图片宽
	 * @param height
	 *            图片高
	 */
	public Captcha(int width, int height) {
		this.width = width;
		this.height = height;
		this.createCode();
	}

	/**
	 * 
	 * @param width
	 *            图片宽
	 * @param height
	 *            图片高
	 * @param codeCount
	 *            字符个数
	 * @param lineCount
	 *            干扰线条数
	 */
	public Captcha(int width, int height, int codeNum, int lineNum) {
		this.width = width;
		this.height = height;
		this.codeNum = codeNum;
		this.lineNum = lineNum;
		this.createCode();
	}

	public void createCode() {
		int x = 0;
		int fontHeight = 0;
		int codeY = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		x = width / (codeNum + 2);
		fontHeight = height - 2;
		codeY = height - 4;

		buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		Random random = new Random();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		Font font = new Font(C.CaptchaFont, Font.BOLD, fontHeight);
		g.setFont(font);
		for (int i = 0; i < lineNum; i++) {
			int xs = random.nextInt(width);
			int ys = random.nextInt(height);
			int xe = xs + random.nextInt(width / 8);
			int ye = ys + random.nextInt(height / 8);
			red = random.nextInt(255);
			green = random.nextInt(255);
			blue = random.nextInt(255);
			g.setColor(new Color(red, green, blue));
			g.drawLine(xs, ys, xe, ye);
		}
		StringBuffer randomCode = new StringBuffer();
		for (int i = 0; i < codeNum; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			red = random.nextInt(255);
			green = random.nextInt(255);
			blue = random.nextInt(255);
			g.setColor(new Color(red, green, blue));
			g.drawString(strRand, (i + 1) * x, codeY);
			randomCode.append(strRand);
		}
		code = randomCode.toString();
	}

	public void write(String img_path) throws IOException {
		OutputStream outputStream = new FileOutputStream(img_path);
		this.write(outputStream);
	}

	public void write(OutputStream outputStream) throws IOException {
		ImageIO.write(buffImg, "png", outputStream);
		outputStream.close();
	}

	public BufferedImage getBuffImg() {
		return buffImg;
	}

	public String getCode() {
		return code;
	}
}
