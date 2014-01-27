package net.beskyttet.mjpeg;

import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public class MjpegViewer implements Runnable {
	private boolean running = true;
	private int x = 10, y = 10;
	private Dimension size;
	private JFrame frame;
	private URL url;

	public MjpegViewer(URL url) {
		this.url = url;
	}

	public void run() {
		frame = new JFrame("MjpegViewer");
		frame.setAlwaysOnTop(true);

		ImagePanel ip = new ImagePanel(null);
		frame.getContentPane().add("Center", ip);
		frame.setLocation(x, y);
		frame.setSize(size);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});

		while (running) {
			try {
				MjpegInputStream m = new MjpegInputStream(url.openStream());
				MjpegFrame f;
				while ((f = m.readMjpegFrame()) != null) {
					ip.setImage((BufferedImage) f.getImage());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setSize(int width, int height) {
		size = new Dimension(width, height);
		if (frame != null)
			frame.setSize(size);
	}

	private void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		if (frame != null)
			frame.setLocation(x, y);
	}

	public static void main(String[] args) {
		String url = null;
		String username = null;
		String password = null;

		int x = 0, y = 0, width = 640, height = 510;

		for (String arg : args) {
			if (arg.startsWith("--url=")) {
				url = arg.substring(6);
			} else if (arg.startsWith("--username=")) {
				username = arg.substring(11);
			} else if (arg.startsWith("--password=")) {
				password = arg.substring(11);
			} else if (arg.startsWith("--position=")) {
				String pos = arg.substring(11);
				if (pos.indexOf('x') < 0) {
					System.err.println("Please specify position as: 50x30");
					System.exit(1);
				}
				x = Integer.valueOf(pos.substring(0, pos.indexOf('x')));
				y = Integer.valueOf(pos.substring(pos.indexOf('x') + 1));
			} else if (arg.startsWith("--size=")) {
				String size = arg.substring(7);
				if (size.indexOf('x') < 0) {
					System.err.println("Please specify position as: 50x30");
					System.exit(1);
				}
				width = Integer.valueOf(size.substring(0, size.indexOf('x')));
				height = Integer.valueOf(size.substring(size.indexOf('x') + 1));
			}
		}

		if (url == null) {
			System.out.println("You need to specify --url=");
			System.exit(1);
		}
		if (username == null) {
			System.out.println("You need to specify --username=");
			System.exit(1);
		}
		if (password == null) {
			System.out.println("You need to specify --password=");
			System.exit(1);
		}

		Authenticator.setDefault(new HTTPAuthenticator(username, password));
		try {
			MjpegViewer viewer = new MjpegViewer(new URL(url));
			viewer.setLocation(x, y);
			viewer.setSize(width, height);
			viewer.run();
		} catch (MalformedURLException e) {
			System.err.println("Malformed URL provided.");
		}
	}

	static class HTTPAuthenticator extends Authenticator {
		private String username, password;

		public HTTPAuthenticator(String user, String pass) {
			username = user;
			password = pass;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password.toCharArray());
		}
	}
}
