package com.ocr;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.apache.tomcat.util.http.fileupload.FileUtils;

public class CharacterExtractor {

	private static int num = 0;
	private static File outputDir = null;
	private static InputStream inputImage = null;
	private static int std_width;
	private static int std_height;
	private static int areaW = 0;;
	private static int areaH = 0;
	private static Image img = null;

	public static void slice(InputStream input) {
		//inputImage = new FileInputStream(input);
		
		
		std_width = 0;
		std_height = 0;
	inputImage = input;
		
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PixelImage pixelImage = new PixelImage(img);

		pixelImage.toGrayScale(false);
		pixelImage.filter();

		scan(pixelImage, 0, 0, pixelImage.width, pixelImage.height);
	}
	

	public static BufferedImage resizeImage(final Image image, int width, int height) {
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		// below three lines are for RenderingHints for better image quality at
		// cost of higher processing time
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return bufferedImage;
	}

	@SuppressWarnings("null")
	public static void processChar(PixelImage pixelImage, int x1, int y1, int x2, int y2, int rowY1,int rowY2) {
		{
			int areaW1 = x2 - x1;
			int areaH1 = y2 - y1;
			areaW = areaW1;
			areaH = areaH1;

			// =areaH1;
			// =Math.max(areaH1,areaH);

			System.out.println("Width :" + areaW + "  Hight : " + areaH);
			System.out.println("y1 :" + y1 + "  y2 : " + y2);

			System.out.println("x1 :" + x1 + "  x2 : " + x2);

			std_height = 60;
			std_width = 60;
			// Extract the character
			
			
			
			BufferedImage characterImage=null;
			BufferedImage characterImage1=null;
			
			
			
			
			
			
		/**	
			try {
				 characterImage1=javax.imageio.ImageIO.read(inputImage);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			**/
			
			characterImage1=(BufferedImage) img;
			characterImage = characterImage1.getSubimage(x1, y1, areaW, areaH);
			int w = characterImage.getWidth();
			int h = characterImage.getHeight();

			int w1 = characterImage1.getWidth();
			int h1 = characterImage1.getHeight();
			
			System.out.println();

			if (characterImage.getWidth() != std_width) {
				// Make image always std_width wide
				double scaleAmount = (double) std_width / (double) characterImage.getWidth();
				AffineTransform tx = new AffineTransform();
				tx.scale(scaleAmount, scaleAmount);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				characterImage = op.filter(characterImage, null);
			}
			int gg = characterImage.getWidth();
			int kk = characterImage.getHeight();
			if (characterImage.getHeight() != std_height) {
				// Make image always std_height tall
				double scaleAmount = (double) std_height / (double) characterImage.getHeight();
				AffineTransform tx = new AffineTransform();
				tx.scale(scaleAmount, scaleAmount);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				characterImage = op.filter(characterImage, null);
			}

			BufferedImage ggg = resizeImage(characterImage, 16, 21);

			int hh = 0;
			w = characterImage.getWidth();
			h = characterImage.getHeight();
			BufferedImage normalizedImage = new BufferedImage(std_width, std_height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = normalizedImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, std_width, std_height);
			// Center scaled image on new canvas
			int x_offset = (std_width - pixelImage.width) / 2;
			int y_offset = (std_height - pixelImage.height) / 2;

			g.drawImage(characterImage, x_offset, y_offset, null);
			g.dispose();

			double matrix[][] = new double[1001][1001];

			PixelImage p = new PixelImage(normalizedImage);
			p.toGrayScale(true);
			p.filter();

			for (int f1 = 0; f1 < ggg.getHeight() - 1; f1++) {
				for (int f = 0; f < ggg.getWidth() - 1; f++) {
					if (ggg.getRGB(f, f1) < -10921639) {

						System.out.print("X");
						matrix[f][f1] = 1;
					}

					else {
						System.out.print(".");
						matrix[f][f1] = 0;
					}

				}

				System.out.println();

			}
			PixelImage gggg = new PixelImage(ggg);
			gggg.toGrayScale(true);
			gggg.filter();
			for (int f1 = 0; f1 < gggg.height; f1++) {
				for (int f = 0; f < gggg.width; f++) {

					if (gggg.getPixel(f, f1) > 100) {
						System.out.print(" ");
						// matrix[f][f1]= 1;

					} else {
						System.out.print("X");
						// matrix[f][f1]= 0;

					}
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
			System.out.println();

			Main.test(matrix);

			num++;
		}

	}

	public static void processSpace1(PixelImage pi, int cx1, int y1, int sx2, int y2) {
		System.out.println("processSpace");
		Main.AddToArray(' ');
	}

	

	protected static float shortRowFraction = 0.125f;
	protected static float liberalPolicyAreaWhitespaceFraction = 0.95f;
	protected static float minSpaceWidthAsFractionOfRowHeight = 0.6f;
	protected static float minCharWidthAsFractionOfRowHeight = 0.15f;
	protected static float minCharBreakWidthAsFractionOfRowHeight = 0.05f;

	protected static int whiteThreshold = 128;

	public float getShortRowFraction() {
		return shortRowFraction;
	}

	public void setShortRowFraction(float shortRowFraction) {
		this.shortRowFraction = shortRowFraction;
	}

	public float getLiberalPolicyAreaWhitespaceFraction() {
		return liberalPolicyAreaWhitespaceFraction;
	}

	public void setLiberalPolicyAreaWhitespaceFraction(float liberalPolicyAreaWhitespaceFraction) {
		this.liberalPolicyAreaWhitespaceFraction = liberalPolicyAreaWhitespaceFraction;
	}

	public float getMinSpaceWidthAsFractionOfRowHeight() {
		return minSpaceWidthAsFractionOfRowHeight;
	}

	public void setMinSpaceWidthAsFractionOfRowHeight(float minSpaceWidthAsFractionOfRowHeight) {
		this.minSpaceWidthAsFractionOfRowHeight = minSpaceWidthAsFractionOfRowHeight;
	}

	public float getMinCharWidthAsFractionOfRowHeight() {
		return minCharWidthAsFractionOfRowHeight;
	}

	public void setMinCharWidthAsFractionOfRowHeight(float minCharWidthAsFractionOfRowHeight) {
		this.minCharWidthAsFractionOfRowHeight = minCharWidthAsFractionOfRowHeight;
	}

	public float getMinCharBreakWidthAsFractionOfRowHeight() {
		return minCharBreakWidthAsFractionOfRowHeight;
	}

	public void setMinCharBreakWidthAsFractionOfRowHeight(float minCharBreakWidthAsFractionOfRowHeight) {
		this.minCharBreakWidthAsFractionOfRowHeight = minCharBreakWidthAsFractionOfRowHeight;
	}

	public int getWhiteThreshold() {
		return whiteThreshold;
	}

	public void setWhiteThreshold(int whiteThreshold) {
		this.whiteThreshold = whiteThreshold;
	}

	public final static void scan(PixelImage pixelImage, int blockX1, int blockY1, int blockX2, int blockY2) {
//	public final static void scan() {

		int[] pixels = pixelImage.pixels;
		int w = pixelImage.width;
		int h = pixelImage.height;

		if (blockX1 < 0) {
			blockX1 = 0;
		} else if (blockX1 >= w) {
			blockX1 = w - 1;
		}
		if (blockY1 < 0) {
			blockY1 = 0;
		} else if (blockY1 >= h) {
			blockY1 = h - 1;
		}
		if ((blockX2 <= 0) || (blockX2 >= w)) {
			blockX2 = w - 1;
		}
		if ((blockY2 <= 0) || (blockY2 >= h)) {
			blockY2 = h - 1;
		}

		blockX2++;
		blockY2++;

		boolean whiteLine = true;
		ArrayList<Integer> al = new ArrayList<Integer>();
		int y1 = 0;
		for (int y = blockY1; y < blockY2; y++) {
			boolean isWhiteSpace = true;
			for (int x = blockX1, idx = (y * w) + blockX1; x < blockX2; x++, idx++) {
				if (pixels[idx] < whiteThreshold) {
					isWhiteSpace = false;
					break;
				}
			}
			if (isWhiteSpace) {
				if (!whiteLine) {
					whiteLine = true;
					al.add(new Integer(y1));
					al.add(new Integer(y));
				}
			} else {
				if (whiteLine) {
					whiteLine = false;
					y1 = y;
				}
			}
		}
		if (!whiteLine) {
			al.add(new Integer(y1));
			al.add(new Integer(blockY2));
		}
		// Now for each row that looks unreasonably short
		// compared to the previous row, merge the short row into
		// the previous row. This accommodates characters such as
		// underscores.
		for (int i = 0; (i + 4) <= al.size(); i += 2) {
			int bY0 = (al.get(i)).intValue();
			int bY1 = (al.get(i + 1)).intValue();
			int bY2 = (al.get(i + 2)).intValue();
			int bY3 = (al.get(i + 3)).intValue();
			int row0H = bY1 - bY0;
			int whiteH = bY2 - bY1;
			int row1H = bY3 - bY2;
			if (((row1H <= (int) ((float) row0H * shortRowFraction)) || (row1H < 6))
					&& ((whiteH <= (int) ((float) row0H * shortRowFraction)) || (whiteH < 6))) {
				al.remove(i + 2);
				al.remove(i + 1);
				i -= 2;
			}
		}
		if (al.size() == 0) {
			al.add(new Integer(blockY1));
			al.add(new Integer(blockY2));
		}
		// Process the rows.
		for (int i = 0; (i + 1) < al.size(); i += 2) {
			int bY1 = (al.get(i)).intValue();
			int bY2 = (al.get(i + 1)).intValue();

			///
			/// System.err.println("process row: "+blockX1+","+bY1+"
			/// "+blockX2+","+bY2);
			processRow(pixelImage, pixels, w, h, blockX1, bY1, blockX2, bY2);
		}
	}

	private static void processRow(PixelImage pixelImage, int[] pixels, int w, int h, int x1, int y1, int x2, int y2) {

		int rowHeight = y2 - y1;
		int minCharBreakWidth = Math.max(1, (int) ((float) rowHeight * minCharBreakWidthAsFractionOfRowHeight));
		int liberalWhitspaceMinWhitePixelsPerColumn = (int) ((float) rowHeight * liberalPolicyAreaWhitespaceFraction);
		// First store beginning and ending character
		// X positions and calculate average character spacing.
		ArrayList<Integer> al = new ArrayList<Integer>();
		boolean inCharSeparator = true;
		int charX1 = 0, prevCharX1 = -1;
		boolean liberalWhitespacePolicy = false;
		int numConsecutiveWhite = 0;
		for (int x = x1 + 1; x < (x2 - 1); x++) {
			if ((!liberalWhitespacePolicy) && (numConsecutiveWhite == 0) && ((x - charX1) >= rowHeight)) {
				// Something's amiss. No whitespace.
				// Try again but do it with the liberal whitespace
				// detection algorithm.
				x = charX1;
				liberalWhitespacePolicy = true;
			}
			int numWhitePixelsThisColumn = 0;
			boolean isWhiteSpace = true;
			for (int y = y1, idx = (y1 * w) + x; y < y2; y++, idx += w) {
				if (pixels[idx] >= whiteThreshold) {
					numWhitePixelsThisColumn++;
				} else {
					if (!liberalWhitespacePolicy) {
						isWhiteSpace = false;
						break;
					}
				}
			}
			if ((liberalWhitespacePolicy) && (numWhitePixelsThisColumn < liberalWhitspaceMinWhitePixelsPerColumn)) {
				isWhiteSpace = false;
			}
			if (isWhiteSpace) {
				numConsecutiveWhite++;
				if (numConsecutiveWhite >= minCharBreakWidth) {
					if (!inCharSeparator) {
						inCharSeparator = true;
						al.add(new Integer(charX1));
						al.add(new Integer(x - (numConsecutiveWhite - 1)));
					}
				}
			} else {
				numConsecutiveWhite = 0;
				if (inCharSeparator) {
					inCharSeparator = false;
					prevCharX1 = charX1;
					charX1 = x;
					liberalWhitespacePolicy = false;
				}
			}
		}
		if (numConsecutiveWhite == 0) {
			al.add(new Integer(charX1));
			al.add(new Integer(x2));
		}
		int minSpaceWidth = (int) ((float) rowHeight * minSpaceWidthAsFractionOfRowHeight);
		int minCharWidth = (int) ((float) rowHeight * minCharWidthAsFractionOfRowHeight);
		if (minCharWidth < 1) {
			minCharWidth = 1;
		}
		for (int i = 0; (i + 4) < al.size(); i += 2) {
			int thisCharWidth = (al.get(i + 2)).intValue() - (al.get(i)).intValue();
			if ((thisCharWidth < minCharWidth) || (thisCharWidth < 6)) {
				al.remove(i + 2);
				al.remove(i + 1);
				i -= 2;
			}
		}
		for (int i = 0; (i + 1) < al.size(); i += 2) {
			if (i >= 2) {
				int cx1 = (al.get(i - 1)).intValue();
				int cx2 = (al.get(i)).intValue();
				while ((cx2 - cx1) >= minSpaceWidth) {
					int sx2 = Math.min(cx1 + minSpaceWidth, cx2);
					processSpace1(pixelImage, cx1, y1, sx2, y2);
					cx1 += minSpaceWidth;
				}
			}
			int cx1 = (al.get(i)).intValue();
			int cx2 = (al.get(i + 1)).intValue();
			int cy1 = y1;
			while (cy1 < y2) {
				boolean isWhiteSpace = true;
				for (int x = cx1, idx = (cy1 * w) + cx1; x < cx2; x++, idx++) {
					if (pixels[idx] < whiteThreshold) {
						isWhiteSpace = false;
						break;
					}
				}
				if (!isWhiteSpace) {
					break;
				}
				cy1++;
			}
			int cy2 = y2;
			while (cy2 > cy1) {
				boolean isWhiteSpace = true;
				for (int x = cx1, idx = ((cy2 - 1) * w) + cx1; x < cx2; x++, idx++) {
					if (pixels[idx] < whiteThreshold) {
						isWhiteSpace = false;
						break;
					}
				}
				if (!isWhiteSpace) {
					break;
				}
				cy2--;
			}
			if (cy1 >= cy2) {
				processSpace1(pixelImage, cx1, y1, cx2, y2);
			} else {
				processChar(pixelImage, cx1, cy1, cx2, cy2, y1, y2);

			}
		}
	}

	static void processSpace(PixelImage pixelImage, int cx1, int y1, int sx2, int y2) {
		// TODO Auto-generated method stub
		System.out.println("space");
	}

	/**
	 * public void processChar(PixelImage pixelImage, int x1, int y1, int x2,
	 * int y2, int rowY1, int rowY2) { try { int areaW = x2 - x1; int areaH = y2
	 * - y1; int std_width=15; int std_height=20; // File inputImage = new
	 * File(pixelImage); String outputDir ="C:\\Users\\User\\Documents\\pics\\";
	 * 
	 * //Extract the character BufferedImage characterImage =
	 * ImageIO.read(pixelImage); characterImage = characterImage.getSubimage(x1,
	 * y1, areaW, areaH);
	 * 
	 * //Scale image so that both the height and width are less than std size if
	 * (pixelImage.width > std_width) { //Make image always std_width wide
	 * double scaleAmount = (double) std_width / (double) pixelImage.width;
	 * AffineTransform tx = new AffineTransform(); tx.scale(scaleAmount,
	 * scaleAmount); AffineTransformOp op = new AffineTransformOp(tx,
	 * AffineTransformOp.TYPE_BILINEAR); characterImage =
	 * op.filter(characterImage, null); }
	 * 
	 * if (pixelImage.height > std_height) { //Make image always std_height tall
	 * double scaleAmount = (double) std_height / (double) pixelImage.height;
	 * AffineTransform tx = new AffineTransform(); tx.scale(scaleAmount,
	 * scaleAmount); AffineTransformOp op = new AffineTransformOp(tx,
	 * AffineTransformOp.TYPE_BILINEAR); characterImage =
	 * op.filter(characterImage, null); }
	 * 
	 * //Paint the scaled image on a white background BufferedImage
	 * normalizedImage = new BufferedImage(std_width, std_height,
	 * BufferedImage.TYPE_INT_RGB); Graphics2D g =
	 * normalizedImage.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0,
	 * 0, std_width, std_height); //Center scaled image on new canvas int
	 * x_offset = (std_width - pixelImage.width) / 2; int y_offset = (std_height
	 * - pixelImage.height) / 2;
	 * 
	 * g.drawImage(characterImage, x_offset, y_offset, null); g.dispose();
	 * 
	 * //Save new image to file File outputfile = new File(outputDir +
	 * File.separator + "char_" + num + ".png"); ImageIO.write(normalizedImage,
	 * "png", outputfile);
	 * 
	 * 
	 * double matrix[][] = new double[20][20]; Image img1 =
	 * ImageIO.read(outputfile); PixelImage pixelImage1 = new PixelImage(img1);
	 * pixelImage1.toGrayScale(true); pixelImage1.filter();
	 * 
	 * for (int f1=0;f1<pixelImage1.height;f1++){ for (int
	 * f=0;f<pixelImage1.width;f++){
	 * 
	 * if (pixelImage1.getPixel(f,f1 )>120){ System.out.print("O");
	 * matrix[f][f1]= 0; } else{ System.out.print("X"); matrix[f][f1]= 0.5;
	 * 
	 * } } System.out.println(); } System.out.println(); System.out.println();
	 * System.out.println();
	 * 
	 * Main.test(matrix);
	 * 
	 * num++; } catch(IOException e) { ; } } public void processSpace(PixelImage
	 * pixelImage, int x1, int y1, int x2, int y2) { try { //
	 * bfImageGraphics.setStroke(new BasicStroke(4)); //
	 * bfImageGraphics.setColor(Color.yellow); // bfImageGraphics.drawRect(x1,
	 * y1, x2 - x1, y2 - y1); } catch (Exception ex) { } }
	 **/
}
			
		
