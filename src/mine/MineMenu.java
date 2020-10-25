package mine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.InsetsUIResource;

import mine.MineFrame.Mode;
import etc.ComponentScreener;

public class MineMenu extends JMenuBar implements ItemListener {
	private JRadioButtonMenuItem oldMode;
	private JRadioButtonMenuItem currentMode;
	private final MineFileChooser fileChooser = new MineFileChooser();

	public MineMenu() {
		super();
		
		final JMenu gameMenu = new JMenu("Spiel");
		gameMenu.setMnemonic(KeyEvent.VK_S);
		final JMenuItem newGame = new JMenuItem("Neu");
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().newGame();
			}
		});
		gameMenu.add(newGame);
		gameMenu.addSeparator();
		final ButtonGroup levelGroup = new ButtonGroup();
		final JRadioButtonMenuItem beginners = new JRadioButtonMenuItem("Anfänger");
		levelGroup.add(beginners);
		beginners.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().newGame(Mode.BEGINNER);
			}
		});
		gameMenu.add(beginners);
		final JRadioButtonMenuItem advanced = new JRadioButtonMenuItem("Fortgeschrittene");
		levelGroup.add(advanced);
		advanced.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().newGame(Mode.ADVANCED);
			}
		});
		gameMenu.add(advanced);
		final JRadioButtonMenuItem pros = new JRadioButtonMenuItem("Profis");
		levelGroup.add(pros);
		pros.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().newGame(Mode.PROS);
			}
		});
		gameMenu.add(pros);
		final JRadioButtonMenuItem custom = new JRadioButtonMenuItem("Benutzerdefiniert…");
		levelGroup.add(custom);
		gameMenu.add(custom);
		gameMenu.addSeparator();
		final JCheckBoxMenuItem wrapField = new JCheckBoxMenuItem("Wrapfield");
		wrapField.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				MineFrame.getInstance().setWrapField(e.getStateChange() == ItemEvent.SELECTED);
				MineFrame.getInstance().newGame();
			}
		});
		gameMenu.add(wrapField);
		final JCheckBoxMenuItem marker = new JCheckBoxMenuItem("Merker (?)");
		marker.setSelected(true);
		marker.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				MineFrame.getInstance().setMarker(e.getStateChange() == ItemEvent.SELECTED);
				MineFrame.getInstance().newGame();
			}
		});
		gameMenu.add(marker);
		final JCheckBoxMenuItem sound = new JCheckBoxMenuItem("Sound");
		sound.setEnabled(false); // TODO implement sound
		gameMenu.add(sound);
		gameMenu.addSeparator();
		final JMenuItem highscore = new JMenuItem("Bestzeiten…");
		highscore.setEnabled(false); // TODO implement highscore
		gameMenu.add(highscore);
		gameMenu.addSeparator();
		final JMenuItem quit = new JMenuItem("Beenden");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().dispose();
			}
		});
		gameMenu.add(quit);
		
		beginners.addItemListener(this);
		advanced.addItemListener(this);
		pros.addItemListener(this);
		custom.addItemListener(this);
		beginners.setSelected(true);
		setOldMode(beginners);
		custom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MineFrame.getInstance().getDialog().setVisible(true);
			}
		});
		
		add(gameMenu);
		
		final JMenu viewMenu = new JMenu("Ansicht");
		viewMenu.setMnemonic(KeyEvent.VK_A);
		final JMenuItem screen = new JMenuItem("Screenshot");
		screen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final BufferedImage image = ComponentScreener.screen(MineFrame.getInstance(), false);
				if (fileChooser.showSaveDialog(MineFrame.getInstance()) == JFileChooser.APPROVE_OPTION)
					try {
						if (!ImageIO.write(image, ComponentScreener.IMG_TYPE, fileChooser.getSelectedFile()))
							JOptionPane.showMessageDialog(MineFrame.getInstance(), "Ihr System unterstützt das Format " + ComponentScreener.IMG_TYPE + " nicht.", "Speichern nicht möglich", JOptionPane.WARNING_MESSAGE);
					} catch (IOException ioe) {
						ioe.printStackTrace();
						JOptionPane.showMessageDialog(MineFrame.getInstance(), ioe.getStackTrace(), "Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
					}
			}
		});
		screen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		viewMenu.add(screen);
		final JMenuItem stats = new JMenuItem("Statistik");
		stats.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final MineFrame mineFrame = MineFrame.getInstance();
				final MineStatistics statistics = mineFrame.getStatistics();
				statistics.setLocationRelativeTo(mineFrame);
				statistics.setVisible(true);
			}
		});
		stats.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		viewMenu.add(stats);
		viewMenu.addSeparator();
		final JCheckBoxMenuItem color = new JCheckBoxMenuItem("Farbe");
		color.setSelected(true);
		color.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				MineFrame.getInstance().setColored(color.isSelected());
			}
		});
		viewMenu.add(color);
		final JMenu lookAndFeelMenu = new JMenu("Look and Feel");
		final ButtonGroup lookAndFeelGroup = new ButtonGroup();
		final LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		final LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
		final ItemListener lookAndFeelListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					try {
						final String actionCommand = ((AbstractButton) e.getSource()).getActionCommand();
						UIManager.setLookAndFeel(actionCommand);
						if (actionCommand.contains("Nimbus"))
							UIManager.getLookAndFeelDefaults().put("ToggleButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
						SwingUtilities.updateComponentTreeUI(MineFrame.getInstance());
						SwingUtilities.updateComponentTreeUI(MineFrame.getInstance().getDialog());
						SwingUtilities.updateComponentTreeUI(MineFrame.getInstance().getStatistics());
						SwingUtilities.updateComponentTreeUI(getFileChooser());
						MineFrame.getInstance().pack();
						MineFrame.getInstance().getDialog().pack();
						MineFrame.getInstance().getStatistics().pack();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		for (LookAndFeelInfo lookAndFeelInfo : installedLookAndFeels) {
			final JRadioButtonMenuItem lookAndFeelRadioButton = new JRadioButtonMenuItem(lookAndFeelInfo.getName());
			final String className = lookAndFeelInfo.getClassName();
			lookAndFeelRadioButton.setActionCommand(className);
			lookAndFeelGroup.add(lookAndFeelRadioButton);
			lookAndFeelRadioButton.setSelected(currentLookAndFeel != null && className.equals(currentLookAndFeel.getClass().getName()));
			lookAndFeelRadioButton.addItemListener(lookAndFeelListener);
			lookAndFeelMenu.add(lookAndFeelRadioButton);
		}
		
		viewMenu.add(lookAndFeelMenu);
		add(viewMenu);
		
		final JMenu helpMenu = new JMenu("?");
		helpMenu.setMnemonic('?');
		final JMenuItem help = new JMenuItem("Hilfe");
		help.setEnabled(false); // TODO implement content
		help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(help);
		helpMenu.addSeparator();
		final JMenuItem info = new JMenuItem("Info…");
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MineFrame.getInstance(), "MineSweeper v0.4.1 by xehpuk", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		// TODO update info (JEditorPane?)
		helpMenu.add(info);
		add(helpMenu);
	}
	
	public final MineFileChooser getFileChooser() {
		return fileChooser;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED)
			setOldMode((JRadioButtonMenuItem) e.getItemSelectable());
		else
			setCurrentMode((JRadioButtonMenuItem) e.getItemSelectable());;
	}
	
	public final JRadioButtonMenuItem getCurrentMode() {
		return currentMode;
	}

	public final void setCurrentMode(JRadioButtonMenuItem currentMode) {
		this.currentMode = currentMode;
	}
	
	public void setOldMode(JRadioButtonMenuItem itemSelectable) {
		oldMode = itemSelectable;
	}
	
	public void restoreMode() {
		getOldMode().setSelected(true);
	}
	
	private final JRadioButtonMenuItem getOldMode() {
		return oldMode;
	}
}