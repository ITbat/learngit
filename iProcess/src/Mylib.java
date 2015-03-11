import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * 封装一些辅助函数
 */
public class Mylib {
	
	public static int getGray(int rgb) {
		return (int) (rgb & 0x00ff0000) >> 16;
	}
	
	public static int[][] toArray(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[][] matrix = new int[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				matrix[i][j] = getGray(image.getRGB(j, i));
			}
		}
		return matrix;
	}
	
	/**
	 * 根据矩阵设置图像上的像素值
	 * @param matrix 整型矩阵
	 * @param height 图像高度
	 * @param width 图像宽度
     * @return 设置后的图像缓存
	 */
	public static BufferedImage drawImage(int[][] matrix, int height, int width) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int gray = matrix[i][j];
				int rgb = new Color(gray, gray, gray).getRGB();
				image.setRGB(j, i, rgb);
			}
		}
		return image;
	}
	
	/**
	 * 获取扩展后的矩阵
	 * @param srcMatrix 原始矩阵
	 * @param h 原始矩阵高度
	 * @param w 原始矩阵宽度
	 * @param disH 扩展后矩阵高度
	 * @param disW 扩展后矩阵宽度
	 * @param value 补充的值
     * @return 扩展后的矩阵
	 */
	public static double[][] getExpandMatrix(double[][] srcMatrix, int h, int w,
			int dstH, int dstW, double value) {
		double[][] expandMatrix = new double[dstH][dstW];
		for (int x = 0; x < dstH; x++) {
			for (int y = 0; y < dstW; y++) {
				if ((x >= 0 && x <= h - 1) && (y >= 0 && y <= w - 1)) {
					expandMatrix[x][y] = srcMatrix[x][y];
				} else {
					expandMatrix[x][y] = value;
				} 
			}
		}
		return expandMatrix;
	}
	
	/**
	 * 将矩阵内的数值大小标定在[0-255]之间
	 * @param matrix 原始矩阵
	 * @param h 矩阵高度
	 * @param w 矩阵宽度
     * @return 标定后的矩阵
	 */
	public static int[][] demarcate(double[][] matrix, int h, int w) { 
		int[][] newMatrix = new int[h][w];
		double max = matrix[0][0];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				if (max < matrix[i][j])
					max = matrix[i][j];
			}
		}
		double rate = 255 / max;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				double temp = matrix[i][j] * rate;
				newMatrix[i][j] = (int)temp;
				newMatrix[i][j] = newMatrix[i][j] < 0 ? 0 : newMatrix[i][j];
			}
		}
		return newMatrix;
	}

	/**
	 * 将傅里叶变换后得到的复数矩阵进行裁剪，得到我们想要呈现的像素矩阵(大小与原始图像相同)
	 * 若flag=0或1，裁取复数矩阵左上角部分，若flag=11，裁取复数矩阵中间部分
	 * @param complex 傅里叶变换后的复数矩阵
	 * @param height 目标像素矩阵的高
	 * @param width 目标像素矩阵的宽
	 * @param flag 判断该复数矩阵是由正变换得来(flag = 1或flag = 11(快速))还是逆变换得来(flag = 0)	
     * @return 目标像素矩阵
	 */
    public static int[][] getResultArray(Complex[][] complex, int height, int width, int flag) {
		double[][] matrix = new double[height][width];
		int x = 0, y = 0;
		int H = complex.length;		//the height of the complex matrix
		int W = complex[0].length;	//the width of the complex matrix
		
		/**
		 * 图像经过快速傅里叶快速正变换后会整体放大，所以
		 * 我这里裁取中间部分，令x, y作为开始采取点的坐标
		 */
		if (flag == 11) {
			x = (H - height) / 2;
			y = (W - width) / 2;
		}
		
		for (int i = x; i < height + x; i++) {
			for (int j = y; j < width + y; j++) {
				if (flag == 1 || flag == 11) {
					double mold = complex[i][j].model();
					int value = 0;
					if (mold != 0)
						value = 1 + (int)Math.log(mold);
					matrix[i-x][j-y] = value;
				} else {
					matrix[i-x][j-y] = complex[i][j].getReal();
					matrix[i-x][j-y] *= Math.pow((-1), (i-x)+(j-y));
				}
			}
		}
		
		// get the matrix after being demarcated
		int[][] newMatrix = Mylib.demarcate(matrix, height, width);
		
		return newMatrix;
	}
}
