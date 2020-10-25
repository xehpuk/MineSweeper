package mine;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public class MineLabel extends JLabel {
	private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
	private static final int MIN_VALUE = -99;
	private static final int MAX_VALUE = 999;
	
	private int value;
	
	public MineLabel(int value) {
		setBackground(Color.BLACK);
		setForeground(Color.RED);
		setOpaque(true);
		setFont(FONT);
		setValue(value);
	}
	
	public void setValue(final int value) {
		this.value = value;
		if (value < 0)
			if (value < MIN_VALUE)
				setText(String.valueOf(MIN_VALUE));
			else
				if (value > -10)
					setText(String.valueOf("-0" + Math.abs(value)));
				else
					setText(String.valueOf(value));
		else
			if (value > MAX_VALUE)
				setText(String.valueOf(MAX_VALUE));
			else
				if (value < 100)
					if (value < 10)
						setText("00" + String.valueOf(value));
					else
						setText("0" + String.valueOf(value));
				else
					setText(String.valueOf(value));
	}
	
	public void incValue() {
		setValue(value + 1);
	}
	
	public void decValue() {
		setValue(value - 1);
	}
	
	public final int getValue() {
		return value;
	}
}