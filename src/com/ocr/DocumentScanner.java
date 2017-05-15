package com.ocr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

public class DocumentScanner {
	private int num = 0;

	protected float shortRowFraction = 0.125f;
	protected float liberalPolicyAreaWhitespaceFraction = 0.95f;
	protected float minSpaceWidthAsFractionOfRowHeight = 0.6f;
	protected float minCharWidthAsFractionOfRowHeight = 0.15f;
	protected float minCharBreakWidthAsFractionOfRowHeight = 0.05f;

	protected int whiteThreshold = 128;

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

	/**
	 * @param pixelImage
	 *            The <code>PixelImage</code> object to be scanned.
	 * @param main
	 *            The <code>DocumentScannerListener</code> to receive
	 *            notifications during the scanning process.
	 * @param blockX1
	 *            The leftmost pixel position of the area to be scanned, or
	 *            <code>0</code> to start scanning at the left boundary of the
	 *            image.
	 * @param blockY1
	 *            The topmost pixel position of the area to be scanned, or
	 *            <code>0</code> to start scanning at the top boundary of the
	 *            image.
	 * @param blockX2
	 *            The rightmost pixel position of the area to be scanned, or
	 *            <code>0</code> to stop scanning at the right boundary of the
	 *            image.
	 * @param blockY2
	 *            The bottommost pixel position of the area to be scanned, or
	 *            <code>0</code> to stop scanning at the bottom boundary of the
	 *            image.
	 */
	public final void scan(PixelImage pixelImage, int blockX1, int blockY1, int blockX2, int blockY2) {

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

		/*
		 * int origBlockX1 = blockX1, origBlockY1 = blockY1, origBlockX2 =
		 * blockX2, origBlockY2 = blockY2;
		 * 
		 * // Narrow the block until there are no remaining dark edges. /// int
		 * thresh = Math.min(255, whiteThreshold+(whiteThreshold/4)); int thresh
		 * = whiteThreshold; float blackElimFraction = 0.1f;
		 * ///System.out.println("thresh="+thresh); for (boolean reduced = true;
		 * reduced;) { reduced = false; // Left edge int blackCount = 0; int idx
		 * = (blockY1*w)+blockX1; int maxBlack = Math.max(1,
		 * (int)((float)((blockY2+1)-blockY1)*blackElimFraction)); for (int y =
		 * blockY1; y <= blockY2; y++, idx += w) { if (pixels[idx] < thresh)
		 * blackCount++; }
		 * ///System.out.println("left blackCount="+blackCount+" maxBlack="
		 * +maxBlack+" blockY1="+blockY1+" blockY2="+blockY2); if (blackCount >=
		 * maxBlack) { ///System.out.println("    reduce left"); reduced = true;
		 * blockX1++; if (blockX1 >= blockX2) break; } // Right edge blackCount
		 * = 0; idx = (blockY1*w)+blockX2; maxBlack = Math.max(1,
		 * (int)((float)((blockY2+1)-blockY1)*blackElimFraction)); for (int y =
		 * blockY1; y <= blockY2; y++, idx += w) {
		 * ///System.out.print("["+pixels[idx]+"]"); if (pixels[idx] < thresh)
		 * blackCount++; } ///System.out.println();
		 * ///System.out.println("right blackCount="+blackCount+" maxBlack="
		 * +maxBlack+" blockY1="+blockY1+" blockY2="+blockY2); if (blackCount >=
		 * maxBlack) { ///System.out.println("    reduce right"); reduced =
		 * true; blockX2--; if (blockX1 >= blockX2) break; } // Top edge
		 * blackCount = 0; idx = (blockY1*w)+blockX1; maxBlack = Math.max(1,
		 * (int)((float)((blockX2+1)-blockX1)*blackElimFraction)); for (int x =
		 * blockX1; x <= blockX2; x++, idx++) { if (pixels[idx] < thresh)
		 * blackCount++; }
		 * ///System.out.println("top blackCount="+blackCount+" maxBlack="
		 * +maxBlack+" blockX1="+blockX1+" blockX2="+blockX2); if (blackCount >=
		 * maxBlack) { ///System.out.println("    reduce top"); reduced = true;
		 * blockY1++; if (blockY1 >= blockY2) break; } // Bottom edge blackCount
		 * = 0; idx = (blockY2*w)+blockX1; maxBlack = Math.max(1,
		 * (int)((float)((blockX2+1)-blockX1)*blackElimFraction)); for (int x =
		 * blockX1; x <= blockX2; x++, idx++) { if (pixels[idx] < thresh)
		 * blackCount++; }
		 * ///System.out.println("bottom blackCount="+blackCount+" maxBlack="
		 * +maxBlack+" blockX1="+blockX1+" blockX2="+blockX2); if (blackCount >=
		 * maxBlack) { ///System.out.println("    reduce bottom"); reduced =
		 * true; blockY2--; if (blockY1 >= blockY2) break; } }
		 * 
		 * if ( (blockX1 >= blockX2) || (blockY1 >= blockY2) ) { // Reduction
		 * failed; restore to original values. blockX1 = origBlockX1; blockY1 =
		 * origBlockY1; blockX2 = origBlockX2; blockY2 = origBlockY2; }
		 */

		blockX2++;
		blockY2++;

		boolean whiteLine = true;
		// listener.beginDocument(pixelImage);
		// First build list of rows of text.
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

	private void processRow(PixelImage pixelImage, int[] pixels, int w, int h, int x1, int y1, int x2, int y2) {

		// listener.beginRow(pixelImage, y1, y2);
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
		// Next combine concecutive supposed character cells where their
		// leftmost X positions are too close together.
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
		// Process the remaining character cells.
		for (int i = 0; (i + 1) < al.size(); i += 2) {
			if (i >= 2) {
				int cx1 = (al.get(i - 1)).intValue();
				int cx2 = (al.get(i)).intValue();
				while ((cx2 - cx1) >= minSpaceWidth) {
					int sx2 = Math.min(cx1 + minSpaceWidth, cx2);
					processSpace(pixelImage, cx1, y1, sx2, y2);
					cx1 += minSpaceWidth;
				}
			}
			int cx1 = (al.get(i)).intValue();
			int cx2 = (al.get(i + 1)).intValue();
			int cy1 = y1;
			// Adjust cy1 down to point to the the top line which is not all
			// white.
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
			// Adjust cy2 up to point to the the line after the last line
			// which is not all white.
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
				processSpace(pixelImage, cx1, y1, cx2, y2);
			} else {
				processChar(pixelImage, cx1, cy1, cx2, cy2, y1, y2);

			}
		}
		// listener.endRow(pixelImage, y1, y2);
	}

	public void processChar(PixelImage pixelImage, int x1, int y1, int x2, int y2, int rowY1, int rowY2) {
		try {
			int areaW = x2 - x1;
			int areaH = y2 - y1;
			int std_width = 15;
			int std_height = 20;
			// File inputImage = new File(pixelImage);
			String outputDir = "C:\\Users\\User\\Documents\\pics\\";

			// Extract the character
			BufferedImage characterImage = null;
			characterImage = characterImage.getSubimage(x1, y1, areaW, areaH);

			// Scale image so that both the height and width are less than std
			// size
			if (pixelImage.width > std_width) {
				// Make image always std_width wide
				double scaleAmount = (double) std_width / (double) pixelImage.width;
				AffineTransform tx = new AffineTransform();
				tx.scale(scaleAmount, scaleAmount);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				characterImage = op.filter(characterImage, null);
			}

			if (pixelImage.height > std_height) {
				// Make image always std_height tall
				double scaleAmount = (double) std_height / (double) pixelImage.height;
				AffineTransform tx = new AffineTransform();
				tx.scale(scaleAmount, scaleAmount);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				characterImage = op.filter(characterImage, null);
			}

			// Paint the scaled image on a white background
			BufferedImage normalizedImage = new BufferedImage(std_width, std_height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = normalizedImage.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, std_width, std_height);
			// Center scaled image on new canvas
			int x_offset = (std_width - pixelImage.width) / 2;
			int y_offset = (std_height - pixelImage.height) / 2;

			g.drawImage(characterImage, x_offset, y_offset, null);
			g.dispose();

			// Save new image to file
			File outputfile = new File(outputDir + File.separator + "char_" + num + ".png");
			ImageIO.write(normalizedImage, "png", outputfile);

			double matrix[][] = new double[20][20];
			Image img1 = ImageIO.read(outputfile);
			PixelImage pixelImage1 = new PixelImage(img1);
			pixelImage1.toGrayScale(true);
			pixelImage1.filter();

			for (int f1 = 0; f1 < pixelImage1.height; f1++) {
				for (int f = 0; f < pixelImage1.width; f++) {

					if (pixelImage1.getPixel(f, f1) > 120) {
						System.out.print("O");
						matrix[f][f1] = 0;
					} else {
						System.out.print("X");
						matrix[f][f1] = 0.5;

					}
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();
			System.out.println();

			Main.test(matrix);

			num++;
		} catch (IOException e) {
			;
		}
	}

	public void processSpace(PixelImage pixelImage, int x1, int y1, int x2, int y2) {
		try {
			// bfImageGraphics.setStroke(new BasicStroke(4));
			// bfImageGraphics.setColor(Color.yellow);
			// bfImageGraphics.drawRect(x1, y1, x2 - x1, y2 - y1);
		} catch (Exception ex) {
		}
	}

	public void beginRow(PixelImage pixelImage, int y1, int y2) {
		// TODO Auto-generated method stub
		
	}
}
