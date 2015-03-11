import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import javax.swing.JOptionPane;

public class ImageProcess {
	
	/**
	 * 缩放函数
	 * @param srcImage 原始图像缓存 
	 * @param disWidth 缩放后的宽度
	 * @param dstHeight 缩放后的高度
     * @return 缩放后的图像缓存
	 */
	public static BufferedImage scale(BufferedImage srcImage, int dstWidth, int dstHeight) {
		BufferedImage dstImg = new BufferedImage(dstWidth, dstHeight,
				BufferedImage.TYPE_INT_RGB);
		int srcWidth = srcImage.getWidth();
		int srcHeight = srcImage.getHeight();
		float widRat = (float)srcWidth / dstWidth;
		float heiRat = (float)srcHeight / dstHeight;
		
		/* get the gray value of every pixel in the picture */
		int[][] gray = new int[srcWidth][srcHeight];
		for (int i = 0; i < srcWidth; i++)
			for (int j = 0; j < srcHeight; j++) {
				gray[i][j] = Mylib.getGray(srcImage.getRGB(i, j));
			}
		
		int y1, y2, x1, x2;    //coordinates
		float x0, y0, u1, u2, v1, v2;
		
		for (int i = 0; i < dstHeight; i++) {
			y0 = (float) (i * heiRat);
			y1 = (int) y0;				//get the integer part
			if (y1 == srcHeight - 1)	//border issues
				y2 = y1;
			else
				y2 = y1 + 1;
			
			v1 = y0 - y1;				//obtain the fractional part
			v2 = 1.0f - v1;
			
			for (int j = 0; j < dstWidth; j++) {
				x0 = (float) (j * widRat);
				x1 = (int) x0;
				if (x1 == srcWidth - 1)
					x2 = x1;
				else
					x2 = x1 + 1;
				
				u1 = x0 - x1;
				u2 = 1.0f - u1;
				
				float s1 = u2 * v2; 
				float s2 = u2 * v1;
				float s3 = u1 * v2;
				float s4 = u1 * v1;
				
				/* Formula: f(i+u,j+v) = (1-u)(1-v)f(i,j) + (1-u)vf(i,j+1) +
				 *  u(1-v)f(i+1,j) + uvf(i+1,j+1) */
				int rgb = (int) (s1 * gray[x1][y1] + s2 * gray[x1][y2] +
						s3 * gray[x2][y1] + s4 * gray[x2][y2]);
				int rgb_ = new Color(rgb, rgb, rgb).getRGB();
				dstImg.setRGB(j, i, rgb_);
			}
		}
		
		return dstImg;
	}
	
	/**
	 * 灰度级别调整函数
	 * @param srcImage 原始图像缓存 
	 * @param level 调整后的灰度级别
     * @return 调整后的图像缓存
	 */
	public static BufferedImage quantize(BufferedImage srcImage, int level) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		/* get the gray value of every pixel in the picture */
		int[][] gray = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				gray[i][j] = Mylib.getGray(srcImage.getRGB(i, j));
			}
		}
		
		/* divide the interval according to the level */
		BufferedImage dstImg = new BufferedImage(width,
				height, BufferedImage.TYPE_INT_RGB);
		float[] newGray = new float[level];
		newGray[0] = 0;
		for (int i = 1; i < level; i++) {
			newGray[i] = newGray[i-1] + (float)(255 / (level - 1));
		}

		/* reset the value of RGB */
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (gray[x][y] >= newGray[level - 1]) {
					int rgb = new Color(255, 255, 255).getRGB();
					dstImg.setRGB(x, y, rgb);
				} else {
					for (int i = 0; i < level - 1; i++) {
						float sw = (newGray[i] + newGray[i+1]) / 2;
						if (gray[x][y] >= newGray[i] && gray[x][y] < sw) {
							int rgb = new Color((int)newGray[i], (int)newGray[i],
									(int)newGray[i]).getRGB();
							dstImg.setRGB(x, y, rgb);
							break;
						} else if (gray[x][y] >= sw && gray[x][y] <= newGray[i+1]) {
							int rgb = new Color((int)newGray[i+1], (int)newGray[i+1],
									(int)newGray[i+1]).getRGB();
							dstImg.setRGB(x, y, rgb);
							break;
						}
					}
				}
			}
		}
		
		return dstImg;
	}
	
	/**
	 * 获取图像直方图
	 * @param srcImage 原始图像缓存 
     * @return 读进的图像的直方图缓存
	 */
	public static BufferedImage getHistogram(BufferedImage srcImage) {
		int size = 550;
		BufferedImage histImage = new BufferedImage(size,size,
				BufferedImage.TYPE_INT_RGB);
        int[] Brightness = new int[256]; 	//store the number of each gray value
        
        for (int i = 0; i < Brightness.length; i++) {
        	Brightness[i] = 0;
        }
        
        /* get the number of each gray value */
        for (int i = 0; i < srcImage.getWidth(); i++)
        	for (int j = 0; j < srcImage.getHeight(); j++) {
        		int gray = Mylib.getGray(srcImage.getRGB(i, j));
        		Brightness[gray]++;
        	}
        
        /* find the max */
        int max = -1;
		for(int i = 0; i < Brightness.length; i++) {
			if(max < Brightness[i]) {
				max = Brightness[i];
			}
		}
        
        /* draw X and Y Axis lines */
        Graphics2D g2d = histImage.createGraphics();
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(20, 20, size, size);
        g2d.setPaint(Color.WHITE);
        g2d.drawLine(25, 500, 535, 500);
        g2d.drawLine(25, 500, 25, 25);
        g2d.setPaint(Color.RED);
        g2d.drawString("Histgram", 220, 520);
        
        /* scale to 400 */
        g2d.setPaint(Color.GREEN);
        float rate = 400.0f / ((float)max);
        int offset = 5;
        for (int i = 0; i < Brightness.length; i++) {
        	int frequency = (int)(Brightness[i] * rate);
        	g2d.drawLine(25 + offset + 2 * i, 500, 25 + offset + 2 * i, 500-frequency);
        }
        
		return histImage;
	}
	
	/**
	 * 灰度图直方图均衡化处理
	 * @param srcMatrix 原始图像矩阵
	 * @param height 图像高
	 * @param width 图像宽
     * @return 均衡化后的图像矩阵
	 */
	public static int[][] equalize_hist(int[][] srcMatrix, int height, int width) {
		int[][] dstMatrix = new int[height][width];
		int[] histogram = getHistogramArray(srcMatrix, height, width);
		
		/* make s = T(r), and set the RGB of the dstImage */
		double rate = (double)255 / (width * height);
		double[] s = new double [256];
		s[0] = (rate * histogram[0]);
		for (int i = 1; i < 256; i++) {
			s[i] = s[i-1] + rate * histogram[i];
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int gray = srcMatrix[i][j];
				dstMatrix[i][j] = (int) s[gray];
			}
		}
	      
	    return dstMatrix;
	}
	
	/**
	 * 彩色图使用RGB模式进行直方图均衡化处理
	 * @param srcImage 原始图像缓存
     * @return 均衡化后的图缓存
	 */
	public static BufferedImage equalize_histRGB(BufferedImage srcImage) {
		int height = srcImage.getHeight();
		int width = srcImage.getWidth();
		int[][] R = new int[height][width];
		int[][] G = new int[height][width];
		int[][] B = new int[height][width];
		getRGB(srcImage, R, G, B);
		R = equalize_hist(R, height, width);
		G = equalize_hist(G, height, width);
		B = equalize_hist(B, height, width);
		BufferedImage dstImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = new Color(R[i][j], G[i][j], B[i][j]).getRGB();
				dstImage.setRGB(j, i, rgb);
			}
		}
		return dstImage;
	}
	
	/**
	 * 彩色图使用均值模式进行直方图均衡化处理
	 * @param srcImage 原始图像缓存
     * @return 均衡化后的图缓存
	 */
	public static BufferedImage equalize_histAverage(BufferedImage srcImage) {
		int height = srcImage.getHeight();
		int width = srcImage.getWidth();
		int[][] R = new int[height][width];
		int[][] G = new int[height][width];
		int[][] B = new int[height][width];
		getRGB(srcImage, R, G, B);
		
		int[] hisR = getHistogramArray(R, height, width);
		int[] hisG = getHistogramArray(G, height, width);
		int[] hisB = getHistogramArray(B, height, width);
		int[] ave = new int[256];
		for (int i = 0; i < 256; i++) {
			ave[i] = (hisR[i] + hisG[i] +hisB[i]) / 3;
		}
		
		BufferedImage dstImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		double rate = (double)255 / (width * height);
		double[] s = new double [256];
		s[0] = (rate * ave[0]);
		for (int i = 1; i < 256; i++) {
			s[i] = s[i-1] + rate * ave[i];
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int r = (int) s[R[i][j]];
				int g = (int) s[G[i][j]];
				int b = (int) s[B[i][j]];
				int rgb = new Color(r, g, b).getRGB();
				dstImage.setRGB(j, i, rgb);
			}
		}
		return dstImage;
	}
	
	/**
	 * 获取彩色图像的R、G、B三个平面
	 * @param srcImage 输入图像缓存
	 * @param R 储存R平面的二维矩阵
	 * @param G 储存G平面的二维矩阵
	 * @param B 储存B平面的二维矩阵
	 */
	private static void getRGB(BufferedImage srcImage, int[][] R, int[][] G, int[][] B) {
		int height = srcImage.getHeight();
		int width = srcImage.getWidth();
		int[][] srcMatrix = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				srcMatrix[i][j] = srcImage.getRGB(j, i);
			}
		}
		ColorModel colorModel = ColorModel.getRGBdefault();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				R[i][j] = colorModel.getRed(srcMatrix[i][j]);
				G[i][j] = colorModel.getGreen(srcMatrix[i][j]);
				B[i][j] = colorModel.getBlue(srcMatrix[i][j]);
			}
		}
	}
	
	/**
	 * 获取图像矩阵灰度值的直方图分布
	 * @param matrix 输入图像矩阵
	 * @param height 图像高
	 * @param width 图像宽
     * @return 结果数组
	 */
	private static int[] getHistogramArray(int[][] matrix, int height, int width) {
		int[] histogram = new int[256];
		for (int i = 0; i < histogram.length; i++) {
        	histogram[i] = 0;
        }
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int gray = matrix[i][j];
				histogram[gray]++;
			}
		}
		return histogram;
	}
	
	/**
	 * 裁剪图像
	 * @param srcImage 原始图像缓存 
	 * @param patch_size 裁剪后的图像大小
	 * @param x 裁剪点开始的x坐标
	 * @param y 裁剪点开始的y坐标
     * @return 裁剪后的图像缓存
	 */
	public static BufferedImage view_as_window(BufferedImage srcImage,
			Dimension patch_size, int x, int y) {
		int patchWidth = patch_size.width;
		int patchHeight = patch_size.height;
		BufferedImage patchImage = new BufferedImage(patchWidth, patchHeight,
				BufferedImage.TYPE_INT_RGB);
		
		/* get a patch of the srcImage */
		for (int i = 0; i < patchHeight; i++) {
			for (int j = 0; j < patchWidth; j++) {
				int rgb = srcImage.getRGB(x+j, y+i);
				patchImage.setRGB(j, i, rgb);
			}
		}
		
		return patchImage;
	}
	
	/**
	 * 滤波处理
	 * @param srcImage 原始图像缓存 
	 * @param mask 滤波模板
	 * @param type 滤波类型
	 * @param size 模板大小
     * @return 滤波处理后的图像缓存
	 */
	public static BufferedImage filter2D(BufferedImage srcImage, float[][] mask, String type, int size) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		BufferedImage dstImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		/* Soble matrixes */
		float[][] mask1 = {{-1, -2, -1},
		  		   		   {0, 0, 0},
		  		   		   {1, 2, 1}};
		float[][] mask2 = {{-1, 0, 1},
		  		   		   {-2, 0, 2},
		  		   		   {-1, 0, 1}};
		
		/* RGB to gray value and get the average */
		int sumPix = 0;
		int[][] gray = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				gray[i][j] = Mylib.getGray(srcImage.getRGB(j, i));
				sumPix += gray[i][j];
			}
		}
		int average = sumPix / (width * height);
		
		double Q = 0;
		if (type.equals("contraharmonic")) {
			Q = Double.valueOf(JOptionPane.showInputDialog(
					 "Please input the value of Q"));
		}
		
		/* fixed with the average of all the pixels */
		int H = height + size / 2 * 2;
		int W = width + size / 2 * 2;
		int[][] filledPix = new int[H][W];
		int m = 0;
		int n = 0;
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < W; j++) {
				if ((i < size / 2) || (i >= height + size / 2)
						|| (j < size / 2) || (j >= width + size / 2)) {
					filledPix[i][j] = average;
				} else {
					filledPix[i][j] = gray[m][n];
					n++;
					if (n == width) {
						m++;
						n = 0;
					}
				}
			}
		}
		
		/* get the result matrix after filter */
		int value = 0;
		double[][] resultPix = new double[height][width];
		for (int i = 0; i <= H - size; i++) {
			for (int j = 0; j <= W - size; j++) {
				int[][] patch = new int[size][size];
				for (int k = 0; k < size; k++) {
					for (int l = 0; l < size; l++) {
						patch[k][l] = filledPix[i+k][j+l];
					}
				}
				
				if (type.equals("smooth") || type.equals("sharpen")) {
					//value = getSum(patch, mask, size);
					value = getArithmeticMean(patch, size);
				} else if (type.equals("soble")) {
					int s1 = getSum(patch, mask1, size);
					int s2 = getSum(patch, mask2, size);
					value = Math.abs(s1) + Math.abs(s2);
				} else if (type.equals("harmonic")) {
					value = getHarmonicMean(patch, size);
				} else if (type.equals("contraharmonic")) {
					value = getContraharmonicMean(patch, size, Q);
				} else if (type.equals("min")) {
					value = getMin(patch, size);
				} else if (type.equals("max")) {
					value = getMax(patch, size);
				} else if (type.equals("median")) {
					value = getMedian(patch, size);
				} else if (type.equals("geometric")) {
					value = getGeometricMean(patch, size);
				} else if (type.equals("arithmetic")) {
					value = getArithmeticMean(patch, size);
				}
				/* demarcating the value between 0 and 255 */
				resultPix[i][j] = value;
			}
		}
		
		int[][] matrix = Mylib.demarcate(resultPix, height, width);
		/* set the RGB for the dstImage */
		dstImage = Mylib.drawImage(matrix, height, width);
		
		return dstImage;
	}
	
	/**
	 * 获取两个矩阵阵列乘后的矩阵的和
	 * @param patch 矩阵1(从图像裁剪下来一块矩阵)
	 * @param mask 矩阵2(滤波模板)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getSum(int[][] patch, float[][] mask, int size) {
		float sum = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum += ((patch[i][j]) * (mask[i][j]));
			}
		}
		return (int)sum;
	}
	
	/**
	 * 获取调和均值
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getHarmonicMean (int[][] patch, int size) {
		double result = 0;
		double sum = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum += 1.0 / patch[i][j];
			}
		}
		result = size * size / sum;
		return (int)result;
	}
	
	/**
	 * 获取逆调和均值
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
	 * @param Q 参数Q
     * @return 结果
	 */
	private static int getContraharmonicMean(int[][] patch, int size, double Q) {
		double result = 0;
		double sum1 = 0;
		double sum2 = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum1 += Math.pow(patch[i][j], Q+1);
				sum2 += Math.pow(patch[i][j], Q);
			}
		}
		result = sum1 / sum2;
		return (int)result;
	}
	
	/**
	 * 获取几何均值
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getGeometricMean(int[][] patch, int size) {
		double result = 0;
		double product = 1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				product *= patch[i][j];
			}
		}
		result = Math.pow(product, 1.0 / (size * size));
		return (int)result;
	}
	
	/**
	 * 获取算术均值
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getArithmeticMean(int[][] patch, int size) {
		double result = 0;
		double sum = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				sum += patch[i][j];
			}
		}
		result = sum / (size * size);
		return (int)result;
	}
	
	/**
	 * 获取矩阵中的最小值（最小值滤波）
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getMin(int[][] patch, int size) {
		int min = patch[0][0];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (min > patch[i][j])
					min = patch[i][j];
			}
		}
		return min;
	}
	
	/**
	 * 获取矩阵中的最大值（最大值滤波）
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getMax(int[][] patch, int size) {
		int max = patch[0][0];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (max < patch[i][j])
					max = patch[i][j];
			}
		}
		return max;
	}
	
	/**
	 * 获取矩阵中的中值（中值滤波）
	 * @param patch 矩阵(从图像裁剪下来一块矩阵)
	 * @param size 矩阵大小
     * @return 结果
	 */
	private static int getMedian(int[][] patch, int size) {
		int[] array = new int[size * size];
		int len = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				array[len] = patch[i][j];
				len++;
			}
		}
		for (int i = 1; i < len; i++) {
	        int temp = array[i];
	        int j = i;
	        for (; j > 0 && temp < array[j-1]; j--)
	            array[j] = array[j-1];
	        array[j] = temp;
	    }
		return array[len / 2];
	}
	
	/**
	 * 产生高斯噪声
	 * @param srcMatrix 输入图像矩阵
	 * @param h 图像高
	 * @param w 图像宽
	 * @param mean 高斯函数均值
	 * @param stdVariance 高斯函数标准差
     * @return 添加高斯噪声后的图像矩阵
	 */
	public static int[][] generateGaussianNoise(int[][] srcMatrix, int h, int w,
			int mean, int stdVariance) {
		double[][] matrix = new double[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				double rand1 = Math.random(), rand2 = Math.random();
				double g = Math.sqrt((-2) * Math.log(rand1)) * Math.cos(2 * Math.PI * rand2);
				double noise = stdVariance * g + mean;
				matrix[i][j] = srcMatrix[i][j] + noise;
			}
		}
		int[][] dstMatrix = Mylib.demarcate(matrix, h, w);
		return dstMatrix;
	}
	
	/**
	 * 产生椒盐噪声
	 * @param srcMatrix 输入图像矩阵
	 * @param h 图像高
	 * @param w 图像宽
	 * @param saltP 产生盐点的概率
	 * @param pepperP 产生椒点的概率
     * @return 添加椒盐噪声后的图像矩阵
	 */
	public static void generateSaltAndPepperNoise(int[][] srcMatrix, int h, int w,
			double saltP, double pepperP) {
		int N = w * h;
		int saltNum = (int) (saltP * N);
		int pepperNum = (int) (pepperP * N);
		for (int i = 0; i < saltNum; i++) {
			double x = Math.random() * h;
			double y = Math.random() * w;
			srcMatrix[(int)x][(int)y] = 255;
		}
		for (int i = 0; i < pepperNum; i++) {
			double x = Math.random() * h;
			double y = Math.random() * w;
			srcMatrix[(int)x][(int)y] = 0;
		}
	}
	
	/**
	 * 在输入图像上产生盐点噪声
	 * @param srcMatrix 输入图像矩阵
	 * @param h 图像高
	 * @param w 图像宽
	 * @param p 产生盐点的概率
	 */
	public static void generateSaltNoise(int[][] srcMatrix, int h, int w,
			double p) {
		int N = w * h;
		int saltNum = (int) (p * N);
		for (int i = 0; i < saltNum; i++) {
			double x = Math.random() * h;
			double y = Math.random() * w;
			srcMatrix[(int)x][(int)y] = 255;
		}
	}
	
	/**
	 * 在输入图像上产生椒点噪声
	 * @param srcMatrix 输入图像矩阵
	 * @param h 图像高
	 * @param w 图像宽
	 * @param p 产生椒点的概率
	 */
	public static void generatePepperNoise(int[][] srcMatrix, int h, int w,
			double p) {
		int N = w * h;
		int saltNum = (int) (p * N);
		for (int i = 0; i < saltNum; i++) {
			double x = Math.random() * h;
			double y = Math.random() * w;
			srcMatrix[(int)x][(int)y] = 0;
		}
	}

}