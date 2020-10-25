package mine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import etc.LimitedJTextField;

public class MineCustomStarter extends JDialog implements ActionListener {
	private final static int MIN_HEIGHT = 9;
	private final static int MAX_HEIGHT = 24;
	private final static int MIN_WIDTH = 9;
	private final static int MAX_WIDTH = 30;
	private final static int MIN_BOMBS = 10;
	private final static int MAX_BOMBS = (MAX_HEIGHT - 1) * (MAX_WIDTH - 1);
	
	public MineCustomStarter() {
		super(MineFrame.getInstance(), true);
		setTitle("Benutzerdefiniert");
		
		final JComponent contentPane = Box.createVerticalBox();
		
		final JComponent highPanel = Box.createHorizontalBox();
		final JLabel highLabel = new JLabel("Höhe (" + MIN_HEIGHT + "-" + MAX_HEIGHT + "):");
		final LimitedJTextField highField = new LimitedJTextField(3);
		highField.setMaximumSize(highField.getPreferredSize());
		highField.setText(String.valueOf(MineFrame.getInstance().getRows()));
		highPanel.add(Box.createHorizontalStrut(5));
		highPanel.add(highLabel);
		highPanel.add(Box.createHorizontalGlue());
		highPanel.add(highField);
		highPanel.add(Box.createHorizontalStrut(5));
		
		final JComponent widthPanel = Box.createHorizontalBox();
		final JLabel widthLabel = new JLabel("Breite (" + MIN_WIDTH + "-" + MAX_WIDTH + "):");
		final LimitedJTextField widthField = new LimitedJTextField(3);
		widthField.setMaximumSize(widthField.getPreferredSize());
		widthField.setText(String.valueOf(MineFrame.getInstance().getColumns()));
		widthPanel.add(Box.createHorizontalStrut(5));
		widthPanel.add(widthLabel);
		widthPanel.add(Box.createHorizontalGlue());
		widthPanel.add(widthField);
		widthPanel.add(Box.createHorizontalStrut(5));
		
		final JComponent minePanel = Box.createHorizontalBox();
		final JLabel mineLabel = new JLabel("Minen (" + MIN_BOMBS + "-" + MAX_BOMBS + "):");
		final LimitedJTextField mineField = new LimitedJTextField(3);
		mineField.setMaximumSize(mineField.getPreferredSize());
		mineField.setText(String.valueOf(MineFrame.getInstance().getNumberOfBombs()));
		minePanel.add(Box.createHorizontalStrut(5));
		minePanel.add(mineLabel);
		minePanel.add(Box.createHorizontalGlue());
		minePanel.add(mineField);
		minePanel.add(Box.createHorizontalStrut(5));
		
		final JPanel buttonPanel = new JPanel();
		final JButton okButton = new JButton("OK");
		final JButton cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(this);
		getRootPane().registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		final ActionListener submitListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rows = highField.getValue();
				rows = rows < MIN_HEIGHT ? MIN_HEIGHT : rows > MAX_HEIGHT ? MAX_HEIGHT : rows;
				highField.setValue(rows);
				int columns = widthField.getValue();
				columns = columns < MIN_WIDTH ? MIN_WIDTH : columns > MAX_WIDTH ? MAX_WIDTH : columns;
				widthField.setValue(columns);
				int numberOfBombs = mineField.getValue();
				final int dimension = (rows - 1) * (columns - 1);
				numberOfBombs = numberOfBombs < MIN_BOMBS ? MIN_BOMBS : Math.min(Math.min(numberOfBombs, MAX_BOMBS), dimension);
				mineField.setValue(numberOfBombs);
				MineFrame.getInstance().getJMenuBar().setOldMode(MineFrame.getInstance().getJMenuBar().getCurrentMode());
				MineFrame.getInstance().newGame(MineFrame.Mode.CUSTOM);
				MineFrame.getInstance().newGame(columns, rows, numberOfBombs);
				dispose();
			}
		};
		okButton.addActionListener(submitListener);
		highField.addActionListener(submitListener);
		widthField.addActionListener(submitListener);
		mineField.addActionListener(submitListener);
		
		contentPane.add(Box.createVerticalStrut(5));
		contentPane.add(highPanel);
		contentPane.add(Box.createVerticalStrut(5));
		contentPane.add(widthPanel);
		contentPane.add(Box.createVerticalStrut(5));
		contentPane.add(minePanel);
		contentPane.add(buttonPanel);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				MineFrame.getInstance().getJMenuBar().restoreMode();
			}
		});
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setContentPane(contentPane);
		setResizable(false);
		pack();
	}
	
	@Override
	public void setVisible(boolean b) {
		setLocationRelativeTo(MineFrame.getInstance());
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();
	}
}