//package Client;

/**
* CaptureScreen.java
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class CaptureScreen extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton start, cancel;
	private JPanel c;
	private BufferedImage get;
	private JTabbedPane jtp;
	private int index;
	private JRadioButton java, system;

	/** Creates a new instance of CaptureScreen */
	public CaptureScreen() {
		super("Screenshot");
		this.setResizable(false);
		initWindow();
		initOther();
	}

	private void initOther() {
		jtp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	private void initWindow() {
		start = new JButton("Screenshot started");
		start.setFocusPainted(false);
		start.addActionListener(this);
		cancel = new JButton("Exit");
		cancel.setFocusPainted(false);
		cancel.addActionListener(this);
		JPanel buttonJP = new JPanel();
		buttonJP.setBackground(new Color(135, 206, 235));
		c = new JPanel(new BorderLayout());
		c.setBackground(new Color(135, 206, 235));
		c.setBounds(500, 300, 100, 100);
		JLabel jl = new JLabel("Screenshot Program", JLabel.CENTER);
		JLabel jl1 = new JLabel("Double click on the area to save.", JLabel.CENTER);
		jl.setFont(new Font("Times New Roman", Font.BOLD, 40));
		jl1.setFont(new Font("Times New Roman", Font.BOLD, 14));
		jl.setForeground(Color.BLACK);
		jl1.setForeground(Color.BLACK);
		c.add(jl, BorderLayout.CENTER);
		c.add(jl1, BorderLayout.SOUTH);
		buttonJP.add(start);
		buttonJP.add(cancel);
		ButtonGroup bg = new ButtonGroup();
		bg.add(java);
		bg.add(system);
		JPanel all = new JPanel();
		all.add(buttonJP);
		all.setBackground(new Color(135, 206, 235));
		this.getContentPane().add(c, BorderLayout.CENTER);
		this.getContentPane().add(all, BorderLayout.SOUTH);
		this.setSize(400, 244);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void updates() {
		this.setVisible(true);
		if (get != null) {
			if (index == 0) {
				c.removeAll();
				c.add(jtp, BorderLayout.CENTER);
			} else {

			}
			PicPanel pic = new PicPanel(get);
			jtp.addTab("Picture" + (++index), pic);
			jtp.setSelectedComponent(pic);
			SwingUtilities.updateComponentTreeUI(c);
		}
	}

	private void doStart() {
		try {
			this.setVisible(false);
			Thread.sleep(500);
			Robot ro = new Robot();
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension di = tk.getScreenSize();
			Rectangle rec = new Rectangle(0, 0, di.width, di.height);
			BufferedImage bi = ro.createScreenCapture(rec);
			JFrame jf = new JFrame();
			Temp temp = new Temp(jf, bi, di.width, di.height);
			jf.getContentPane().add(temp, BorderLayout.CENTER);
			jf.setUndecorated(true);
			jf.setSize(di);
			jf.setVisible(true);
			jf.setAlwaysOnTop(true);
		} catch (Exception exe) {
			exe.printStackTrace();
		}
	}

	public void doSave(BufferedImage get) {
		try {
			if (get == null) {
				JOptionPane.showMessageDialog(this, "Image should be specified!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JFileChooser jfc = new JFileChooser(".");
			jfc.addChoosableFileFilter(new GIFfilter());
			jfc.addChoosableFileFilter(new BMPfilter());
			jfc.addChoosableFileFilter(new JPGfilter());
			jfc.addChoosableFileFilter(new PNGfilter());
			int i = jfc.showSaveDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				String about = "PNG";
				String ext = file.toString().toLowerCase();
				javax.swing.filechooser.FileFilter ff = jfc.getFileFilter();
				if (ff instanceof JPGfilter) {
					if (!ext.endsWith(".jpg")) {
						String ns = ext + ".jpg";
						file = new File(ns);
						about = "JPG";
					}
				} else if (ff instanceof PNGfilter) {
					if (!ext.endsWith(".png")) {
						String ns = ext + ".png";
						file = new File(ns);
						about = "PNG";
					}
				} else if (ff instanceof BMPfilter) {
					if (!ext.endsWith(".bmp")) {
						String ns = ext + ".bmp";
						file = new File(ns);
						about = "BMP";
					}
				} else if (ff instanceof GIFfilter) {
					if (!ext.endsWith(".gif")) {
						String ns = ext + ".gif";
						file = new File(ns);
						about = "GIF";
					}
				}
				if (ImageIO.write(get, about, file)) {
					JOptionPane.showMessageDialog(this, "Saved successfully!");
				} else
				JOptionPane.showMessageDialog(this, "Failed to save!");
			}
		} catch (Exception exe) {
			exe.printStackTrace();
		}
	}

	private void doClose(Component c) {
		jtp.remove(c);
		c = null;
		System.gc();
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == start) {
			doStart();
		} else if (source == cancel) {
			this.dispose();
		}
	}

	private class PicPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1L;
		JButton save, close;
		BufferedImage get;

		public PicPanel(BufferedImage get) {
			super(new BorderLayout());
			this.get = get;
			initPanel();
		}

		private void initPanel() {
			save = new JButton("Save (S)");
			save.setFocusPainted(false);

			close = new JButton("Exit (X)");
			close.setFocusPainted(false);
			close.setMnemonic('S');
			close.setMnemonic('X');
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(save);
			buttonPanel.add(close);
			buttonPanel.setBackground(new Color(135, 206, 235));
			JLabel icon = new JLabel(new ImageIcon(get));
			this.add(new JScrollPane(icon), BorderLayout.CENTER);
			this.add(buttonPanel, BorderLayout.SOUTH);
			save.addActionListener(this);
			close.addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == save) {
				doSave(get);
			} else if (source == close) {
				get = null;
				doClose(this);
			}
		}
	}

	private class BMPfilter extends javax.swing.filechooser.FileFilter {
		public BMPfilter() {
		}

		public boolean accept(File file) {
			if (file.toString().toLowerCase().endsWith(".bmp") || file.isDirectory()) {
				return true;
			} else
			return false;
		}

		public String getDescription() {
			return "*.BMP(BMP Image)";
		}
	}

	private class JPGfilter extends javax.swing.filechooser.FileFilter {
		public JPGfilter() {
		}

		public boolean accept(File file) {
			if (file.toString().toLowerCase().endsWith(".jpg") || file.isDirectory()) {
				return true;
			} else
			return false;
		}

		public String getDescription() {
			return "*.JPG(JPG Image)";
		}
	}

	private class GIFfilter extends javax.swing.filechooser.FileFilter {
		public GIFfilter() {
		}

		public boolean accept(File file) {
			if (file.toString().toLowerCase().endsWith(".gif") || file.isDirectory()) {
				return true;
			} else
			return false;
		}

		public String getDescription() {
			return "*.GIF(GIF Image)";
		}
	}

	private class PNGfilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			if (file.toString().toLowerCase().endsWith(".png") || file.isDirectory()) {
				return true;
			} else
			return false;
		}

		public String getDescription() {
			return "*.PNG(PNG Image)";
		}
	}

	private class Temp extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1L;
		private BufferedImage bi;
		private int width, height;
		private int startX, startY, endX, endY, tempX, tempY;
		private JFrame jf;
		private Rectangle select = new Rectangle(0, 0, 0, 0);
		private Cursor cs = new Cursor(Cursor.CROSSHAIR_CURSOR);
		private States current = States.DEFAULT;
		private Rectangle[] rec;
		public static final int START_X = 1;
		public static final int START_Y = 2;
		public static final int END_X = 3;
		public static final int END_Y = 4;
		private int currentX, currentY;
		private Point p = new Point();
		private boolean showTip = true;

		public Temp(JFrame jf, BufferedImage bi, int width, int height) {
			this.jf = jf;
			this.bi = bi;
			this.width = width;
			this.height = height;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			initRecs();
		}

		private void initRecs() {
			rec = new Rectangle[8];
			for (int i = 0; i < rec.length; i++) {
				rec[i] = new Rectangle();
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(bi, 0, 0, width, height, this);
			g.setColor(Color.RED);
			g.drawLine(startX, startY, endX, startY);
			g.drawLine(startX, endY, endX, endY);
			g.drawLine(startX, startY, startX, endY);
			g.drawLine(endX, startY, endX, endY);
			int x = startX < endX ? startX : endX;
			int y = startY < endY ? startY : endY;
			select = new Rectangle(x, y, Math.abs(endX - startX), Math.abs(endY - startY));
			int x1 = (startX + endX) / 2;
			int y1 = (startY + endY) / 2;
			g.fillRect(x1 - 2, startY - 2, 5, 5);
			g.fillRect(x1 - 2, endY - 2, 5, 5);
			g.fillRect(startX - 2, y1 - 2, 5, 5);
			g.fillRect(endX - 2, y1 - 2, 5, 5);
			g.fillRect(startX - 2, startY - 2, 5, 5);
			g.fillRect(startX - 2, endY - 2, 5, 5);
			g.fillRect(endX - 2, startY - 2, 5, 5);
			g.fillRect(endX - 2, endY - 2, 5, 5);
			rec[0] = new Rectangle(x - 5, y - 5, 10, 10);
			rec[1] = new Rectangle(x1 - 5, y - 5, 10, 10);
			rec[2] = new Rectangle((startX > endX ? startX : endX) - 5, y - 5, 10, 10);
			rec[3] = new Rectangle((startX > endX ? startX : endX) - 5, y1 - 5, 10, 10);
			rec[4] = new Rectangle((startX > endX ? startX : endX) - 5, (startY > endY ? startY : endY) - 5, 10, 10);
			rec[5] = new Rectangle(x1 - 5, (startY > endY ? startY : endY) - 5, 10, 10);
			rec[6] = new Rectangle(x - 5, (startY > endY ? startY : endY) - 5, 10, 10);
			rec[7] = new Rectangle(x - 5, y1 - 5, 10, 10);
			if (showTip) {
				g.setColor(Color.CYAN);
				g.fillRect(p.x, p.y, 170, 20);
				g.setColor(Color.RED);
				g.drawRect(p.x, p.y, 170, 20);
				g.setColor(Color.BLACK);
				g.drawString("Hold the cursor to select the zone of the screenshot!", p.x, p.y + 15);
			}
		}

		private void initSelect(States state) {
			switch (state) {
				case DEFAULT:
				currentX = 0;
				currentY = 0;
				break;
				case EAST:
				currentX = (endX > startX ? END_X : START_X);
				currentY = 0;
				break;
				case WEST:
				currentX = (endX > startX ? START_X : END_X);
				currentY = 0;
				break;
				case NORTH:
				currentX = 0;
				currentY = (startY > endY ? END_Y : START_Y);
				break;
				case SOUTH:
				currentX = 0;
				currentY = (startY > endY ? START_Y : END_Y);
				break;
				case NORTH_EAST:
				currentY = (startY > endY ? END_Y : START_Y);
				currentX = (endX > startX ? END_X : START_X);
				break;
				case NORTH_WEST:
				currentY = (startY > endY ? END_Y : START_Y);
				currentX = (endX > startX ? START_X : END_X);
				break;
				case SOUTH_EAST:
				currentY = (startY > endY ? START_Y : END_Y);
				currentX = (endX > startX ? END_X : START_X);
				break;
				case SOUTH_WEST:
				currentY = (startY > endY ? START_Y : END_Y);
				currentX = (endX > startX ? START_X : END_X);
				break;
				default:
				currentX = 0;
				currentY = 0;
				break;
			}
		}

		public void mouseMoved(MouseEvent me) {
			doMouseMoved(me);
			initSelect(current);
			if (showTip) {
				p = me.getPoint();
				repaint();
			}
		}

		private void doMouseMoved(MouseEvent me) {
			if (select.contains(me.getPoint())) {
				this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				current = States.MOVE;
			} else {
				States[] st = States.values();
				for (int i = 0; i < rec.length; i++) {
					if (rec[i].contains(me.getPoint())) {
						current = st[i];
						this.setCursor(st[i].getCursor());
						return;
					}
				}
				this.setCursor(cs);
				current = States.DEFAULT;
			}
		}

		public void mouseExited(MouseEvent me) {
		}

		public void mouseEntered(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent me) {
			int x = me.getX();
			int y = me.getY();
			if (current == States.MOVE) {
				startX += (x - tempX);
				startY += (y - tempY);
				endX += (x - tempX);
				endY += (y - tempY);
				tempX = x;
				tempY = y;
			} else if (current == States.EAST || current == States.WEST) {
				if (currentX == START_X) {
					startX += (x - tempX);
					tempX = x;
				} else {
					endX += (x - tempX);
					tempX = x;
				}
			} else if (current == States.NORTH || current == States.SOUTH) {
				if (currentY == START_Y) {
					startY += (y - tempY);
					tempY = y;
				} else {
					endY += (y - tempY);
					tempY = y;
				}
			} else if (current == States.NORTH_EAST || current == States.NORTH_EAST || current == States.SOUTH_EAST
			|| current == States.SOUTH_WEST) {
				if (currentY == START_Y) {
					startY += (y - tempY);
					tempY = y;
				} else {
					endY += (y - tempY);
					tempY = y;
				}
				if (currentX == START_X) {
					startX += (x - tempX);
					tempX = x;
				} else {
					endX += (x - tempX);
					tempX = x;
				}
			} else {
				startX = tempX;
				startY = tempY;
				endX = me.getX();
				endY = me.getY();
			}
			this.repaint();
		}

		public void mousePressed(MouseEvent me) {
			showTip = false;
			tempX = me.getX();
			tempY = me.getY();
		}

		public void mouseReleased(MouseEvent me) {
			if (me.isPopupTrigger()) {
				if (current == States.MOVE) {
					showTip = true;
					p = me.getPoint();
					startX = 0;
					startY = 0;
					endX = 0;
					endY = 0;
					repaint();
				} else {
					jf.dispose();
					updates();
				}
			}
		}

		public void mouseClicked(MouseEvent me) {
			if (me.getClickCount() == 2) {
				Point p = me.getPoint();
				if (select.contains(p)) {
					if (select.x + select.width < this.getWidth() && select.y + select.height < this.getHeight()) {
						get = bi.getSubimage(select.x, select.y, select.width, select.height);
						jf.dispose();
						updates();
					} else {
						int wid = select.width, het = select.height;
						if (select.x + select.width >= this.getWidth()) {
							wid = this.getWidth() - select.x;
						}
						if (select.y + select.height >= this.getHeight()) {
							het = this.getHeight() - select.y;
						}
						get = bi.getSubimage(select.x, select.y, wid, het);
						jf.dispose();
						updates();
					}
				}
			}
		}
	}
}

enum States {
	NORTH_WEST(new Cursor(Cursor.NW_RESIZE_CURSOR)),
	NORTH(new Cursor(Cursor.N_RESIZE_CURSOR)), NORTH_EAST(new Cursor(Cursor.NE_RESIZE_CURSOR)), EAST(
	new Cursor(Cursor.E_RESIZE_CURSOR)), SOUTH_EAST(new Cursor(Cursor.SE_RESIZE_CURSOR)), SOUTH(
	new Cursor(Cursor.S_RESIZE_CURSOR)), SOUTH_WEST(new Cursor(Cursor.SW_RESIZE_CURSOR)), WEST(
	new Cursor(Cursor.W_RESIZE_CURSOR)), MOVE(new Cursor(Cursor.MOVE_CURSOR)), DEFAULT(
	new Cursor(Cursor.DEFAULT_CURSOR));

	private Cursor cs;

	States(Cursor cs) {
		this.cs = cs;
	}

	public Cursor getCursor() {
		return cs;
	}
}
