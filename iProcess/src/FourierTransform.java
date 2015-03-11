import java.awt.image.BufferedImage;

public class FourierTransform {
	
	/* 保存图像傅里叶正变换后的复数矩阵F(u, v) */
	private static Complex[][] fourierMatrix = null;
	
	/**
	 * 获取fourierMatrix
	 */
	public static Complex[][] getFourierMatrix() { return fourierMatrix; }
	
	/**
	 * 2维图像的傅里叶变换
	 * @param srcImage 原始图像缓存
	 * @param height 图像高度
	 * @param width 图像宽度
	 * @param flag 判断是进行正变换(flag = 1)还是逆变换(flag = 0)
     * @return 变换后的复数矩阵
	 */
	public static Complex[][] dft2D(double[][] srcImage, int height, int width, int flag) {
		Complex[][] srcComplex = new Complex[height][width];	
		Complex[][] resComplex = new Complex[height][width];
		Complex[][]	tempComplex = new Complex[height][width];
		
		/* initialization of srcComplex, tempComplex, resComplex */
		if (flag == 1) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					resComplex[i][j] = new Complex();
					srcComplex[i][j] = new Complex();
					tempComplex[i][j] = new Complex();
					double value = srcImage[i][j];
					srcComplex[i][j].setValue(value * Math.pow((-1), i+j), 0);
				}
			}
		} else if (flag == 0 && fourierMatrix != null) {
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					tempComplex[i][j] = new Complex();
					resComplex[i][j] = new Complex();
					srcComplex[i][j] = fourierMatrix[i][j].conjugate();
				}
			}
		}
		
		/* get the value of tempComplex */
		for (int x = 0; x < height; x++) {
			for (int v = 0; v < width; v++) {
				Complex res = new Complex();
				for (int y = 0; y < width; y++) {
					double real = Math.cos(2.0 * Math.PI * v * y / (double)width);
					double img = Math.sin(2.0 * Math.PI * v * y / (double)width) * (-1);
					Complex c = new Complex(real, img);
					res = res.add(c.multiply(srcComplex[x][y]));
				}
				tempComplex[x][v].setValue(res);
			}
		}
		
		/* get the value of resComplex */
		double rate = 1 / (double)height;
		for (int u = 0; u < height; u++) {
			for (int v = 0; v < width; v++) {
				Complex res = new Complex();
				for (int x = 0; x < height; x++) {
					double real = Math.cos(2.0 * Math.PI * u * x / (double)height);
					double img = Math.sin(2.0 * Math.PI * u * x / (double)height) * (-1);
					Complex c = new Complex(real, img);
					res = res.add(c.multiply(tempComplex[x][v]));
				}
				if (flag == 1)
					resComplex[u][v].setValue(res.multiply(rate));
				else
					resComplex[u][v].setValue(res.multiply(width));
			}
		}
		
		fourierMatrix = flag == 1 ? resComplex : null;
		
		return resComplex;
	}
	
	
	/**
	 * 在频域对图像进行滤波处理
	 * @param srcImage 原始图像缓存
	 * @param mask 滤波模板
	 * @param size 模板大小
     * @return 滤波处理后的图像缓存
	 */
	public static BufferedImage filter2d_freq(BufferedImage srcImage, double[][] mask, int size) {
		int height = srcImage.getHeight();
		int width = srcImage.getWidth();
		BufferedImage dstImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		/* calculate the average of the srcImage matrix */
		double sum = 0;
		double[][] srcRGB = new double[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = srcImage.getRGB(j, i);
				int gray = Mylib.getGray(rgb);
				srcRGB[i][j] = gray;
				sum += gray;
			}
		}
		double ave = sum / (height * width);
		
		/* expand srcImage matrix and mask to P*Q with ave and zero respectively */
		int P = height + size - 1;
		int Q = width + size - 1;
		double[][] expandRGB = Mylib.getExpandMatrix(srcRGB, height, width, P, Q, ave);
		double[][] expandMask = Mylib.getExpandMatrix(mask, size, size, P, Q, 0);
		
		/* calculate the FFT of expandRGB and expandMask */
		Complex[][] fftedRGB = fft2D(expandRGB, P, Q, 1);
		Complex[][] fftedMask = fft2D(expandMask, P, Q, 1);
		
		/* calculate Guv = Array multiply of fftedRGB with fftedMask */
		int H = fftedRGB.length;
		int W = fftedRGB[0].length;
		Complex[][] Guv = new Complex[H][W];
		for (int i = 0; i < H; i++) {
			for (int j = 0; j < W; j++) {
				Complex temp = fftedRGB[i][j].multiply(fftedMask[i][j]);
				Guv[i][j] = temp;
			}
		}
		
		/* calculate the IFFT of Guv */
		fourierMatrix = Guv;
		Complex[][] guv = fft2D(null, H, W, 0);
		
		int[][] gxy = Mylib.getResultArray(guv, height, width, 0);
		dstImage = Mylib.drawImage(gxy, height, width);
		
		return dstImage;
	}

	
	/**
	 * 2维图像的快速傅里叶变换
	 * @param srcImage 原始图像缓存
	 * @param height 图像高度
	 * @param width 图像宽度
	 * @param flag 判断是进行正变换(flag = 1)还是逆变换(flag = 0)
     * @return 变换后的复数矩阵
	 */
	public static Complex[][] fft2D(double[][] srcImage, int height, int width, int flag) {
		double r1 = Math.log10(width)/Math.log10(2.0) - 
				(int)(Math.log10(width)/Math.log10(2.0));
		double r2 = Math.log10(height)/Math.log10(2.0) - 
				(int)(Math.log10(height)/Math.log10(2.0));
		Boolean isPowerOf2 = true;
		int H = height;
		int W = width;
		
		/* judge if the height and width is the power of 2 */
		if (flag == 1) {
			if (r1 != 0) {
				for (int i = 0; ; i++) {
					if (Math.pow(2, i) > width) {
						W = (int)Math.pow(2, i);
						break;
					}
				}
				isPowerOf2 = false;
			}
			if (r2 != 0) {
				for (int i = 0; ; i++) {
					if (Math.pow(2, i) > height) {
						H = (int)Math.pow(2, i);
						break;
					}
				}
				isPowerOf2 = false;
			}
		}
		
		/* expand the srcImage matrix to H*W with the last value in matrix */
		double[][] expandImage = new double[H][W];
		if (!isPowerOf2)
			expandImage = Mylib.getExpandMatrix(srcImage, height, width, H, W,
					srcImage[height-1][width-1]);
		else
			expandImage = srcImage;

		/* initialization of srcComplex */
		Complex[][] srcComplex = new Complex[H][W];
		if (flag == 1) {
			for (int i = 0; i < H; i++) {
				for (int j = 0; j < W; j++) {
					srcComplex[i][j] = new Complex();
					double value = expandImage[i][j];
					srcComplex[i][j].setValue(value * Math.pow((-1), i+j), 0);
				}
			}
		} else if (fourierMatrix != null) {
			srcComplex = fourierMatrix;
		}
		
		/* get the result after FFT or IFFT */
		double rate = 1.0 / H;
		Complex[][] resComplex = new Complex[H][W];
		for (int x = 0; x < H; x++) {
			if (flag == 1)
				resComplex[x] = fft(srcComplex[x]);
			else
				resComplex[x] = ifft(srcComplex[x]);
		}
		Complex[] temp = new Complex[H];
		for (int v = 0; v < W; v++) {
			for (int u = 0; u < H; u++) {
				temp[u] = resComplex[u][v];
			}
			if (flag == 1)
				temp = fft(temp);
			else
				temp = ifft(temp);
			for (int u = 0; u < H; u++) {
				if (flag == 1)
					resComplex[u][v] = temp[u].multiply(rate);
				else
					resComplex[u][v] = temp[u].multiply(W);
			}
		}
		
		fourierMatrix = flag == 1 ? resComplex : null;
		
		return resComplex;
	}
	
	/**
	 * 1维矩阵的快速傅里叶正变换
	 * @param x 原始复数矩阵
     * @return 变换后的复数矩阵
	 */
	public static Complex[] fft(Complex[] x) {
        int len = x.length;

        // base case
        if (len == 1) return new Complex[] { x[0] };

        // fft of even terms
        Complex[] even = new Complex[len/2];
        for (int k = 0; k < len / 2; k++) {
            even[k] = x[2*k];
        }
        Complex[] resEven = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < len / 2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] resOdd = fft(odd);

        // combine
        Complex[] y = new Complex[len];
        for (int k = 0; k < len / 2; k++) {
            double temp = -2 * k * Math.PI / (double)len;
            Complex c = new Complex(Math.cos(temp), Math.sin(temp));
            y[k] = resEven[k].add(c.multiply(resOdd[k]));
            y[k + len/2] = resEven[k].minus(c.multiply(resOdd[k]));
        }
        
        return y;
    }

	/**
	 * 1维矩阵的快速傅里叶逆变换
	 * @param x 原始复数矩阵
     * @return 变换后的复数矩阵
	 */
    public static Complex[] ifft(Complex[] x) {
        int len = x.length;
        Complex[] y = new Complex[len];

        // take conjugate
        for (int i = 0; i < len; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < len; i++) {
            y[i] = y[i].conjugate();
            y[i] = y[i].multiply(1.0 / len);
        }

        return y;
    }
    
}
