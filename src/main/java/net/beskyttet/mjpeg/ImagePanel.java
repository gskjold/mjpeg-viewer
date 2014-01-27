package net.beskyttet.mjpeg;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	public ImagePanel(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			double winAspect = (double) getWidth() / (double) getHeight();
			double aspect = (double) image.getWidth() / (double) image.getHeight();

			int x, y, w, h;
			if (winAspect < aspect) {
				w = getWidth();
				h = (int) (w / aspect);
				x = 0;
				y = (getHeight() - h) / 2;
				h += y;
			} else {
				h = getHeight();
				w = (int) (h * aspect);
				y = 0;
				x = (getWidth() - w) / 2;
				w += x;
			}
			g.drawImage(image, x, y, w, h, 0, 0, image.getWidth(), image.getHeight(), this);
		}
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
}
