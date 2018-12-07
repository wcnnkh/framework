package shuchaowen.common.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;

import javax.imageio.ImageIO;

public final class ImageUtils {
	private ImageUtils(){};
	
	/**
	 * 图片大小调整
	 * 
	 * @param imagePath
	 *            原图片路径
	 * @param newImagePath
	 *            新图片路径
	 * @param newWidth
	 *            新图片大小
	 * @param newHeight
	 *            新图片大小
	 */
	public static void scale(String imagePath, String newImagePath, int newWidth, int newHeight) {
		BufferedImage oldImg;
		try {
			// File oldFile = new File(imagePath);
			oldImg = ImageIO.read(new File(imagePath));
			Image image = oldImg.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);

			BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			ImageIO.write(newImg, getFormatName(imagePath), new File(newImagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getFormatName(String imagePath) {
		return imagePath.substring(imagePath.lastIndexOf(".") + 1).toUpperCase();
	}

	/**
	 * 切割图片
	 * 
	 * @param oldImagePath
	 * @param newImagePath
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param isFill
	 *            当切割的高或宽大于图片本身的时候是否填充
	 */
	public static void cut(String oldImagePath, String newImagePath, int x, int y, int width, int height,
			boolean isFill) {
		try {
			BufferedImage oldImage = ImageIO.read(new File(oldImagePath));
			int oldwidth = oldImage.getWidth();
			int oldHeight = oldImage.getHeight();
			if (oldwidth == 0 || oldHeight == 0) {
				return;
			}

			if (isFill) {
				if (width > oldwidth) {
					oldwidth = width;
				}

				if (height > oldHeight) {
					oldHeight = height;
				}
			}

			Image image = oldImage.getScaledInstance(oldwidth, oldHeight, Image.SCALE_DEFAULT);
			BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImage.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();

			ImageIO.write(newImage, getFormatName(oldImagePath), new File(newImagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将图片切成几行几列
	 * 
	 * @param imgPath
	 * @param descDir
	 *            生成的图片目录
	 * @param rows
	 * @param cols
	 */
	public static void cut2(String imgPath, String descDir, int rows, int cols) {
		try {
			if (rows <= 0 || cols <= 0) {
				return;
			}

			BufferedImage img = ImageIO.read(new File(imgPath));
			int width = img.getWidth();
			int height = img.getHeight();

			if (width <= 0 || height <= 0) {
				return;
			}

			if (rows > height || cols > width) {
				return;
			}

			Image image;
			ImageFilter cropFilter;
			Image tempImage = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			int destWidth = width / cols;
			int destHeight = height / rows;

			String formateName = getFormatName(imgPath);
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					int newImageW = destWidth;
					int newImageH = destHeight;

					if (j == cols - 1) {
						newImageW = width - destWidth * j;
					}

					if (i == rows - 1) {
						newImageH = height - destHeight * i;
					}

					cropFilter = new CropImageFilter(j * destWidth, i * destHeight, newImageW, newImageH);
					image = Toolkit.getDefaultToolkit()
							.createImage(new FilteredImageSource(tempImage.getSource(), cropFilter));
					BufferedImage tag = new BufferedImage(newImageW, newImageH, BufferedImage.TYPE_INT_RGB);
					Graphics g = tag.getGraphics();
					g.drawImage(image, 0, 0, null);
					g.dispose();
					ImageIO.write(tag, formateName, new File(descDir + i + "_" + j + "." + formateName.toLowerCase()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片格式转换
	 * 
	 * @param oldImagePath
	 * @param newImagePath
	 */
	public static void convert(String oldImagePath, String newImagePath) {
		try {
			BufferedImage img = ImageIO.read(new File(oldImagePath));
			ImageIO.write(img, getFormatName(newImagePath), new File(newImagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片转黑白
	 * 
	 * @param imagePath
	 * @param newImagePath
	 */
	public static void gray(String imagePath, String newImagePath) {
		try {
			BufferedImage oldBi = ImageIO.read(new File(imagePath));
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp ccop = new ColorConvertOp(cs, null);
			oldBi = ccop.filter(oldBi, null);
			ImageIO.write(oldBi, getFormatName(imagePath), new File(newImagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pressText(String pressText, String imagePath, String newImagePath, String fontName,
			int fontStyle, Color color, int fontSize, int x, int y, float alpha) {
		try {
			Image img = ImageIO.read(new File(imagePath));
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(img, 0, 0, width, height, null);
			g.setColor(color);
			g.setFont(new Font(fontName, fontStyle, fontSize));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 在指定坐标绘制水印文字
			g.drawString(pressText, (width - getLength(pressText) * fontSize) / 2 + x, (height - fontSize) / 2 + y);
			g.dispose();
			ImageIO.write(image, getFormatName(imagePath), new File(newImagePath));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pressImage(String pressImg, String srcImageFile, String destImageFile, int x, int y,
			float alpha) {
		try {
			File img = new File(srcImageFile);
			Image src = ImageIO.read(img);
			int wideth = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, wideth, height, null);
			// 水印文件
			Image src_biao = ImageIO.read(new File(pressImg));
			int wideth_biao = src_biao.getWidth(null);
			int height_biao = src_biao.getHeight(null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			g.drawImage(src_biao, (wideth - wideth_biao) / 2, (height - height_biao) / 2, wideth_biao, height_biao,
					null);
			// 水印文件结束
			g.dispose();
			ImageIO.write((BufferedImage) image, getFormatName(srcImageFile), new File(destImageFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}

	public static String getBase64ImageUrl(String filePath) {
		StringBuilder sb = new StringBuilder("data:image/jpg;base64,");
		sb.append("data:image/");
		sb.append(FileUtils.getFileSuffix(filePath));
		sb.append(";base64,");
		return FileUtils.toBase64(filePath, sb);
	}
}
