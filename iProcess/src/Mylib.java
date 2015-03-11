import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * ��װһЩ��������
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
	 * ���ݾ�������ͼ���ϵ�����ֵ
	 * @param matrix ���;���
	 * @param height ͼ��߶�
	 * @param width ͼ����
     * @return ���ú��ͼ�񻺴�
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
	 * ��ȡ��չ��ľ���
	 * @param srcMatrix ԭʼ����
	 * @param h ԭʼ����߶�
	 * @param w ԭʼ������
	 * @param disH ��չ�����߶�
	 * @param disW ��չ�������
	 * @param value �����ֵ
     * @return ��չ��ľ���
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
	 * �������ڵ���ֵ��С�궨��[0-255]֮��
	 * @param matrix ԭʼ����
	 * @param h ����߶�
	 * @param w ������
     * @return �궨��ľ���
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
	 * ������Ҷ�任��õ��ĸ���������вü����õ�������Ҫ���ֵ����ؾ���(��С��ԭʼͼ����ͬ)
	 * ��flag=0��1����ȡ�����������Ͻǲ��֣���flag=11����ȡ���������м䲿��
	 * @param complex ����Ҷ�任��ĸ�������
	 * @param height Ŀ�����ؾ���ĸ�
	 * @param width Ŀ�����ؾ���Ŀ�
	 * @param flag �жϸø��������������任����(flag = 1��flag = 11(����))������任����(flag = 0)	
     * @return Ŀ�����ؾ���
	 */
    public static int[][] getResultArray(Complex[][] complex, int height, int width, int flag) {
		double[][] matrix = new double[height][width];
		int x = 0, y = 0;
		int H = complex.length;		//the height of the complex matrix
		int W = complex[0].length;	//the width of the complex matrix
		
		/**
		 * ͼ�񾭹����ٸ���Ҷ�������任�������Ŵ�����
		 * �������ȡ�м䲿�֣���x, y��Ϊ��ʼ��ȡ�������
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
