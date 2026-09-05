package fgp.engine.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/*-
 * To use this class, Create a JLabel on your JFrame, then change your 
 * JLabel name = new JLabel("text"); 
 * to
 * PictureBox name = new PictureBox("folder", "filename.jpg"); 
 * 
 * @author Mr. Hapke
 */
public class PictureBox extends JLabel {

	private static final long serialVersionUID = 1381812448276589893L;

	private Image img;
	private Image original;

	private int originalW;
	private int originalH;
	private double originalAspect;

	private ImageCache2 cache = ImageCache2.getInstance();

	private int scaledW;
	private int scaledH;

	private int frameNumber;

	public PictureBox(String folder, String filename) {
		setImage(folder, filename);
	}

	public void setImage(String folder, String filename) {
		try {
			original = cache.getImage(folder, filename);
			if (original == null)
				return;
			originalW = original.getWidth(null);
			originalH = original.getHeight(null);

			originalAspect = ((double) originalW) / originalH;

			scaleImage();
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void scaleImage() {
		int boxWidth = getWidth();
		int boxHeight = getHeight();
		double boxAspect = ((double) boxWidth) / boxHeight;
		int w, h;
		if (originalAspect > boxAspect) {
			// image wider, so scale height
			w = boxWidth;
			h = (int) (boxWidth / originalAspect);
		} else {
			w = (int) (boxHeight * originalAspect);
			h = boxHeight;
		}
		img = cache.getScaled(original, w, h);
	}

	@Override
	protected void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);
		gfx.setColor(getBackground());
		gfx.fillRect(0, 0, getWidth(), getHeight());
		if (img != null) {
			gfx.drawImage(img, 0, 0, null);
		}

		if (ImageCache2.PICTURE_DEBUG) {
			gfx.setColor(Color.black);
			gfx.drawString("Frame #" + frameNumber++, 10, 10);
		}
	}

	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		scaleImage();
		outputResize();
	}

	@Override
	public void setSize(int w, int h) {
		super.setSize(w, h);
		scaleImage();
		outputResize();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		scaleImage();
		outputResize();
	}

	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		scaleImage();
		outputResize();
	}

	private void outputResize() {
		if (ImageCache2.PICTURE_DEBUG)
			System.out.println("New size is: " + getBounds());
	}

	@Override
	public String toString() {
		return "PictureBox [" + "<" + originalW + "," + originalH + "> => [" + scaledW + "," + scaledH + "]]";
	}
}
