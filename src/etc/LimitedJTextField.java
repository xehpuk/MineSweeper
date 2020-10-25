package etc;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedJTextField extends JTextField {
	public LimitedJTextField(final int maxCharacters) {
		super(new LimitedPlainDocument(maxCharacters), null, maxCharacters);
	}
	
	public LimitedPlainDocument getLimitedDocument() {
		return (LimitedPlainDocument) super.getDocument();
	}
	
	public int getMaxCharacters() {
		return getLimitedDocument().getMaxCharacters();
	}
	
	public void setMaxCharacters(int maxCharacters) {
		getLimitedDocument().setMaxCharacters(maxCharacters);
	}
	
	public int getValue() {
		try {
			return Integer.parseInt(getText());
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	public void setValue(int value) {
		setText(String.valueOf(value));
	}
	
	public static class LimitedPlainDocument extends PlainDocument {
		private int maxCharacters;
		
		public final int getMaxCharacters() {
			return maxCharacters;
		}
		
		public final void setMaxCharacters(int maxCharacters) {
			this.maxCharacters = maxCharacters;
		}
		
		public LimitedPlainDocument(int maxCharacters) {
			this.maxCharacters = maxCharacters;
		}
		
		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (((getLength() + str.length()) <= maxCharacters) && str.matches("\\d+"))
				super.insertString(offset, str, attr);
			else
				Toolkit.getDefaultToolkit().beep();
		}
	}
}
