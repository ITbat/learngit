import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/* Set the frame of the program */
@SuppressWarnings("serial")
public class ImageViewFrame extends JFrame {
	/* set the default size of the frame */
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	private FileDialog fileDia1 = new FileDialog(this, "Choose a picture",
			FileDialog.LOAD);
	private FileDialog fileDia2 = new FileDialog(this, "Save picture",
			FileDialog.SAVE);
	private BufferedImage image = null;
	private ImageIcon icon = null;
	private JLabel label;
	private JMenuBar menubar;
	private Dimension winSize;
	
	public ImageViewFrame() {
		super("iProcess");
		init();
		
		JMenu File = new JMenu("File ");
		JMenu Edit = new JMenu("Edit ");
		JMenu View = new JMenu("View");
		menubar.add(File);
		menubar.add(Edit);
		menubar.add(View);
		
		JMenuItem open = new JMenuItem("Open ");
		JMenuItem save = new JMenuItem("Save ");
		JMenuItem exit = new JMenuItem("exit ");
		File.add(open);
		File.add(save);
		File.add(exit);
		
		JMenuItem smooth = new JMenuItem("Smooth");
		JMenuItem fsmooth = new JMenuItem("Fourier Smooth");
		JMenuItem sharpen = new JMenuItem("Sharpen");
		JMenuItem fsharpen = new JMenuItem("Fourier Sharpen");
		JMenuItem soble = new JMenuItem("Soble");
		JMenuItem harmonic = new JMenuItem("Harmonic mean");
		JMenuItem contraharmonic = new JMenuItem("Contraharmonic mean");
		JMenuItem arithmetic = new JMenuItem("Arithmetic men");
		JMenuItem geometric = new JMenuItem("Geometric mean");
		JMenuItem min = new JMenuItem("Min");
		JMenuItem max = new JMenuItem("Max");
		JMenuItem median = new JMenuItem("Median");
		JMenu statisticalFilter = new JMenu("Statistical");
		statisticalFilter.add(min);
		statisticalFilter.add(max);
		statisticalFilter.add(median);
		JMenu filter = new JMenu("Filter");
		filter.add(smooth);
		filter.add(fsmooth);
		filter.add(sharpen);
		filter.add(fsharpen);
		filter.add(soble);
		filter.add(harmonic);
		filter.add(contraharmonic);
		filter.add(arithmetic);
		filter.add(geometric);
		filter.add(statisticalFilter);
		
		JMenuItem gaussianNoise = new JMenuItem("Add Gaussian Noise");
		JMenuItem saltNoise = new JMenuItem("Add Salt Noise");
		JMenuItem pepperNoise = new JMenuItem("Add Pepper Noise");
		JMenuItem saltAndpepperNoise = new JMenuItem("Add Salt-and-Pepper Noise");
		JMenu addNoise = new JMenu("Add Noise");
		addNoise.add(gaussianNoise);
		addNoise.add(saltNoise);
		addNoise.add(pepperNoise);
		addNoise.add(saltAndpepperNoise);
		
		JMenuItem scale = new JMenuItem("Scale");
		JMenuItem quantize = new JMenuItem("Quantize");
		JMenuItem equalizeGray = new JMenuItem("EqualizeGray");
		JMenuItem equalizeRGB = new JMenuItem("EqualizeRGB");
		JMenuItem equalizeAve = new JMenuItem("Equalize Average");
		JMenu equalize = new JMenu("Equalize");
		equalize.add(equalizeGray);
		equalize.add(equalizeRGB);
		equalize.add(equalizeAve);
		JMenuItem recovery = new JMenuItem("Recovery");
		Edit.add(scale);
		Edit.add(quantize);
		Edit.add(equalize);
		Edit.add(filter);
		Edit.add(addNoise);
		Edit.add(recovery);
		
		JMenuItem histogram = new JMenuItem("Draw histogram");
		JMenuItem patch = new JMenuItem("Get patchImage");
		JMenu fourierTrans = new JMenu("Fourier Transform");
		JMenuItem dft = new JMenuItem("DFT");
		JMenuItem idft = new JMenuItem("IDFT");
		JMenuItem fft = new JMenuItem("FFT");
		JMenuItem ifft = new JMenuItem("IFFT");
		fourierTrans.add(dft);
		fourierTrans.add(idft);
		fourierTrans.add(fft);
		fourierTrans.add(ifft);
		View.add(histogram);
		View.add(patch);
		View.add(fourierTrans);
		
		/* open a picture */
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fileDia1.setVisible(true);
					Image srcImage = ImageIO.read(new File(fileDia1.getDirectory() +
							fileDia1.getFile()));
					image = new BufferedImage(srcImage.getWidth(null), srcImage.getHeight(null),
							BufferedImage.TYPE_INT_RGB);
					image.flush();
					Graphics g = image.getGraphics();
					g.drawImage(srcImage, 0, 0, null);
					g.dispose();
					imShow(image);
					label.setHorizontalAlignment(SwingConstants.CENTER);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});
		
		/* save a picture */
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (icon != null) {
					try {
						fileDia2.setVisible(true);
						ImageIO.write(getCurrentImage(), "png", new File(fileDia2.getDirectory()
								+fileDia2.getFile()));
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No picture found!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* close the program */
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		/* recovery the image */
		recovery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
						imShow(image);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "No picture found!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* scale image */
		scale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					int w = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the width"));
					int h = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the height"));
					try {
					    BufferedImage dstImage = ImageProcess.scale(getCurrentImage(), w, h);
					    imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		/* reduce gray level and show the result */
		quantize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					int level = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the gray levels"));
					try {
						BufferedImage dstImage = ImageProcess.quantize(image, level);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		/* draw the histogram of the current image */
		histogram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
						JFrame frame = new JFrame();
						JLabel lab = new JLabel();
						frame.setBounds(winSize.width/4,winSize.height/7,  //location
								570, 600); 
						frame.add(lab, BorderLayout.CENTER);
						ImageIcon ic = new ImageIcon(ImageProcess.getHistogram(getCurrentImage()));
						lab.setIcon(ic);
						frame.setVisible(true);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* equalize the current image and show the result */
		equalizeGray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
						int[][] srcMatrix = Mylib.toArray(getCurrentImage());
						int h = srcMatrix.length;
						int w = srcMatrix[0].length;
					    int[][] dstMatrix = ImageProcess.equalize_hist(srcMatrix, h, w);
					    imShow(Mylib.drawImage(dstMatrix, h, w));
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		equalizeRGB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (image != null) {
					try {
					    BufferedImage dstImage = ImageProcess.equalize_histRGB(getCurrentImage());
					    imShow(dstImage);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		equalizeAve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					try {
					    BufferedImage dstImage = ImageProcess.equalize_histAverage(getCurrentImage());
					    imShow(dstImage);
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* get the patch of the original image randomly */
		patch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int patch_width = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the width of the patch"));
					int patch_height = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the height of the patch"));
					try {
						Dimension patch_size = new Dimension(patch_width,
								patch_height);
						
						int x = (int)(Math.random() * (getCurrentImage().getWidth()
								- patch_width - 1));
						int y = (int)(Math.random() * (getCurrentImage().getHeight()
								- patch_height - 1));
						
						BufferedImage patchImage = ImageProcess.view_as_window(
								getCurrentImage(), patch_size, x, y);
						imShow(patchImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* smooth filter */
		smooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "smooth", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		/* sharpen filter by a 3×3 matrix */
		sharpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					float[][] mask = {{-1, -1, -1},
									  {-1, 8, -1},
									  {-1, -1, -1}};
					BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
							mask, "sharpen", 3);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		/* Soble filter by two 3×3 matrixes */
		soble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
							null, "soble", 3);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		harmonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the Harmonic-mean filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "harmonic", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		contraharmonic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the Contraharmonic-mean filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "contraharmonic", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		arithmetic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the Arithmetic mean filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "arithmetic", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		geometric.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the Geometric mean filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "geometric", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		min.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "min", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		max.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "max", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		median.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the filter mask"));
					try {
						BufferedImage dstImage = ImageProcess.filter2D(getCurrentImage(),
								null, "median", size);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		gaussianNoise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int mean = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the mean of GaussianNoise"));
					int stdVariance = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the standard variance of GaussianNoise"));
					try {
						int[][] srcMatrix = Mylib.toArray(getCurrentImage());
						int h = srcMatrix.length;
						int w = srcMatrix[0].length;
						int[][] dstMatrix = ImageProcess.generateGaussianNoise(
								srcMatrix, h, w, mean, stdVariance);
						BufferedImage dstImage = Mylib.drawImage(dstMatrix, h, w);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		saltNoise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					double p = Double.valueOf(JOptionPane.showInputDialog(
							 "Please input the probability of SaltNoise (p < 1)"));
					try {
						int[][] matrix = Mylib.toArray(getCurrentImage());
						int h = matrix.length;
						int w = matrix[0].length;
						ImageProcess.generateSaltNoise(matrix, h, w, p);
						BufferedImage dstImage = Mylib.drawImage(matrix, h, w);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		pepperNoise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					double p = Double.valueOf(JOptionPane.showInputDialog(
							 "Please input the probability of PepperNoise (p < 1)"));
					try {
						int[][] matrix = Mylib.toArray(getCurrentImage());
						int h = matrix.length;
						int w = matrix[0].length;
						ImageProcess.generatePepperNoise(matrix, h, w, p);
						BufferedImage dstImage = Mylib.drawImage(matrix, h, w);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		saltAndpepperNoise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					double p1 = Double.valueOf(JOptionPane.showInputDialog(
							 "Please input the probability of SaltNoise (p < 1)"));
					double p2 = Double.valueOf(JOptionPane.showInputDialog(
							 "Please input the probability of PepperNoise (p < 1)"));
					try {
						int[][] matrix = Mylib.toArray(getCurrentImage());
						int h = matrix.length;
						int w = matrix[0].length;
						ImageProcess.generateSaltAndPepperNoise(matrix, h, w, p1, p2);
						BufferedImage dstImage = Mylib.drawImage(matrix, h, w);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});

		/* get the Spectrogram through DFT */
		dft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int[][] matrix = Mylib.toArray(getCurrentImage());
					int height = matrix.length;
					int width = matrix[0].length;
					double[][] srcRGB = new double[height][width];
					for (int i = 0; i < height; i++) {
						for (int j = 0; j < width; j++) {
							srcRGB[i][j] = (double)matrix[i][j];
						}
					}
					Complex[][] resRGB = FourierTransform.dft2D(srcRGB, height,
							width, 1);
					int[][] dstMatrix = Mylib.getResultArray(resRGB,
							height, width, 1);
					BufferedImage dstImage = Mylib.drawImage(dstMatrix, height, width);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		idft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (FourierTransform.getFourierMatrix() != null) {
					int height = getCurrentImage().getHeight();
					int width = getCurrentImage().getWidth();
					Complex[][] resRGB = FourierTransform.dft2D(null, height, width, 0);
					int[][] matrix = Mylib.getResultArray(resRGB, height, width, 0);
					BufferedImage dstImage = Mylib.drawImage(matrix, height, width);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please make DFT firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		fft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					BufferedImage srcImage = getCurrentImage();
					int height = srcImage.getHeight();
					int width = srcImage.getWidth();
					double[][] srcRGB = new double[height][width];
					for (int i = 0; i < height; i++) {
						for (int j = 0; j < width; j++) {
							int value = Mylib.getGray(srcImage.getRGB(j, i));
							srcRGB[i][j] = value;
						}
					}
					
					Complex[][] resRGB = FourierTransform.fft2D(srcRGB, height, width, 1);
					int[][] matrix = Mylib.getResultArray(resRGB, height, width, 11);
					
					BufferedImage dstImage = Mylib.drawImage(matrix, height, width);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		ifft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (FourierTransform.getFourierMatrix() != null) {
					int height = getCurrentImage().getHeight();
					int width = getCurrentImage().getWidth();
					Complex[][] resRGB = FourierTransform.fft2D(null, height, width, 0);
					int[][] matrix = Mylib.getResultArray(resRGB, height, width, 0);
					BufferedImage dstImage = Mylib.drawImage(matrix, height, width);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please make FFT firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		/* smooth filter in the frequency domain */
		fsmooth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					int size = Integer.parseInt(JOptionPane.showInputDialog(
							 "Please input the size of the Arithmetic mean filter mask"));
					try {
						double[][] mask = new double[11][11];
						for (int i = 0; i < size; i++) {
							for (int j = 0; j < size; j++) {
								mask[i][j] = (double) (1.0 / (size * size));
							}
						}
						BufferedImage dstImage = FourierTransform.filter2d_freq(
								getCurrentImage(), mask, 11);
						imShow(dstImage);
					} catch (NumberFormatException nfe) {
						System.out.println("format error");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		
		fsharpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (getCurrentImage() != null) {
					double[][] mask = {{-1, -1, -1},
							  		  {-1, 8, -1},
							  		  {-1, -1, -1}};
					BufferedImage dstImage = FourierTransform.filter2d_freq(
							getCurrentImage(), mask, 3);
					imShow(dstImage);
				} else {
					JOptionPane.showMessageDialog(null, "Please open a picture firstly!",
							 "Message", JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
	}
	
	/**
	 * 初始化窗口，设置窗口位置，大小等
	 */
	public void init() {
		Toolkit kit = this.getToolkit();
		winSize = kit.getScreenSize();
		this.setBounds(winSize.width/4,winSize.height/4,  //location
				winSize.width/2,winSize.height/2); //size
		/* use a label to show a picture */
		label =new JLabel();
		this.add(label, BorderLayout.CENTER);
		
		/* set the menu bar */
		menubar = new JMenuBar();
		setJMenuBar(menubar);
	}
	
	/**
	 * 获取当前图像缓存
	 * @return 当前图像缓存
	 */
	public BufferedImage getCurrentImage() {
		Image curImg = icon.getImage();
		BufferedImage curBuf = new BufferedImage(curImg.getWidth(null), 
				curImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
		curBuf.flush();
		Graphics g = curBuf.getGraphics();
		g.drawImage(curImg, 0, 0, null);
		g.dispose();
		return curBuf;
	}
	
	/**
	 * 在窗口中显示图像
	 * @param dstImage 要显示的图像缓存 
	 */
	private void imShow(BufferedImage dstImage) {
		icon = new ImageIcon(dstImage);
		label.setIcon(icon);
	}
}