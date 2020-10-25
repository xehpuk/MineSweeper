package mine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import mine.gfx.MineGraphics;
import mine.gfx.MineGraphics.Emoticon;
import mine.gfx.MineGraphics.Picture;

public class MineFrame extends JFrame {
	public static boolean DEBUG = false;
	
	private static MineFrame INSTANCE;
	
	private int rows;
	private int columns;
	private int numberOfBombs;
	
	private Mode mode;
	
	private boolean gameOver = false;
	private boolean marker = true;
	private boolean colored = true;
	private boolean firstMove = true;
	private boolean wrapField = false;
	
	private final MineLabel bombsLabel;
	private final MineLabel timeLabel;
	private final JButton button;
	private MineCustomStarter dialog;
	private MineStatistics stats;

	private MineField field;
	
	private final Timer timer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getTimeLabel().incValue();
		}
	});
	
	public static MineFrame getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MineFrame("MineSweeper");
		}
		return INSTANCE;
	}
	
	public final void setColored(boolean colored) {
		this.colored = colored;
		repaint();
	}
	
	private MineFrame(String title) {
		super(title);
		
		final MineMenu menu = new MineMenu();
		setJMenuBar(menu);
		final JPanel contentPane = new JPanel(new BorderLayout()) {
			private BufferedImage image;
			
			@Override
			public void paint(Graphics g) {
				if (colored)
					super.paint(g);
				else {
					if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
						image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_GRAY);
					}
					Graphics2D imgGraphics = image.createGraphics();
					super.paint(imgGraphics);
					imgGraphics.dispose();
					g.drawImage(image, 0, 0, null);
				}
			}
		};
		final JComponent headerCenter = Box.createHorizontalBox();
		headerCenter.add(Box.createHorizontalStrut(5));
		headerCenter.add(bombsLabel = new MineLabel(numberOfBombs));
		headerCenter.add(Box.createHorizontalGlue());
		button = new JButton();
		button.setPreferredSize(new Dimension(MineGraphics.Emoticon.DIMENSION, MineGraphics.Emoticon.DIMENSION));
		button.setPressedIcon(MineGraphics.get(Emoticon.WANT));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		headerCenter.add(button);
		headerCenter.add(Box.createHorizontalGlue());
		headerCenter.add(timeLabel = new MineLabel(0));
		headerCenter.add(Box.createHorizontalStrut(5));
		
		final JComponent header = Box.createVerticalBox();
		header.add(Box.createVerticalStrut(5));
		header.add(headerCenter);
		header.add(Box.createVerticalStrut(5));
		header.setBorder(BorderFactory.createLoweredBevelBorder());
		
		contentPane.add(header, BorderLayout.NORTH);
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		
		setContentPane(contentPane);
		setResizable(false);
		setIconImage(MineGraphics.get(Picture.BOMB));
		newGame(9, 9, 10);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	@Override
	public void dispose() {
		stopTimer();
		super.dispose();
	}
	
	public void newGame() {
		newGame(columns, rows, numberOfBombs);
	}
	
	public void newGame(Mode mode) {
		setMode(mode);
		switch (mode) {
			case BEGINNER:
				newGame(9, 9, 10);
				break;
			case ADVANCED:
				newGame(16, 16, 40);
				break;
			case PROS:
				newGame(30, 16, 99);
				break;
		}
	}
	
	public void newGame(final int width, final int height, final int bombCount) {
		final MineField newField = new MineField(columns = width, rows = height, numberOfBombs = bombCount);
		if (field != null)
			getContentPane().remove(field);
		stopTimer();
		setFirstMove(true);
		setGameOver(false);
		getContentPane().add(field = newField, BorderLayout.CENTER);
		getBombsLabel().setValue(bombCount);
		getTimeLabel().setValue(0);
		getButton().setIcon(MineGraphics.get(Emoticon.SMILE));
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getStatistics().refresh(field);
			}
		});
		if (DEBUG) {
			System.out.println(field.toString());
			System.out.println();
		}
		validate();
		pack();
		newField.getComponent(0).requestFocusInWindow();
	}
	
	@Override
	public void pack() {
		super.pack();
		relocate();
	}
	
	private void relocate() {
		final Point location = getLocation();
		final Dimension size = getSize();
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = toolkit.getScreenSize();
		final Insets insets = toolkit.getScreenInsets(getGraphicsConfiguration());
		final int x;
		final int diffX = screenSize.width - size.width - insets.left - insets.right;
		if (location.x > diffX)
			x = Math.max(diffX, 0);
		else
			x = location.x;
		final int y;
		final int diffY = screenSize.height - size.height - insets.top - insets.bottom;
		if (location.y > diffY)
			y = Math.max(diffY, 0);
		else
			y = location.y;
		setLocation(x, y);
	}
	
	public final int getRows() {
		return rows;
	}
	
	public final int getColumns() {
		return columns;
	}
	
	public final int getNumberOfBombs() {
		return numberOfBombs;
	}
	
	public final MineCustomStarter getDialog() {
		if (dialog == null) {
			dialog = new MineCustomStarter();
		}
		return dialog;
	}
	
	public final MineStatistics getStatistics() {
		if (stats == null) {
			stats = new MineStatistics();
		}
		return stats;
	}
	
	public final JButton getButton() {
		return button;
	}

	public final MineLabel getBombsLabel() {
		return bombsLabel;
	}

	public final MineLabel getTimeLabel() {
		return timeLabel;
	}
	
	@Override
	public MineMenu getJMenuBar() {
		return (MineMenu) super.getJMenuBar();
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public final void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	public final boolean hasMarker() {
		return marker;
	}
	
	public final void setMarker(boolean marker) {
		this.marker = marker;
	}
	
	public final boolean isFirstMove() {
		return firstMove;
	}
	
	public final void setFirstMove(boolean firstMove) {
		this.firstMove = firstMove;
	}
	
	public final boolean isWrapField() {
		return wrapField;
	}

	public final void setWrapField(boolean wrapField) {
		this.wrapField = wrapField;
	}

	public final Mode getMode() {
		return mode;
	}
	
	public final void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public enum Mode {
		BEGINNER, ADVANCED, PROS, CUSTOM;
	}
	
	public void stopTimer() {
		timer.stop();
	}
	
	public void startTimer() {
		if (!timer.isRunning())
			timer.start();
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				RepaintManager.setCurrentManager(new RepaintManager() {
					@Override
					public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
						JComponent r = c.getRootPane();
						if (r != null) {
							super.addDirtyRegion(r, 0, 0, r.getWidth(), r.getHeight());
						} else {
							super.addDirtyRegion(c, 0, 0, w, h);
						}
					}
				});
				final MineFrame frame = getInstance();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}