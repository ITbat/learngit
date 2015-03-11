import java.awt.EventQueue;

import javax.swing.JFrame;

/* Run the program */
public class iProcess {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ImageViewFrame frame = new ImageViewFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}
