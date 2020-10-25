package mine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ButtonModel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mine.gfx.MineGraphics;
import mine.gfx.MineGraphics.Emoticon;

public class MineButton extends JToggleButton {
	private final static Insets MARGIN = new Insets(0, 0, 0, 0);
	private final static Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
	private final static Color[] FOREGROUNDS = {
		new Color(0, true),
		Color.BLUE,
		Color.GREEN.darker(),
		Color.RED,
		Color.BLUE.darker(),
		Color.RED.darker(),
		Color.CYAN.darker(),
		Color.BLACK,
		Color.GRAY
	};
	private final static ChangeListener CHANGE_LISTENER = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (MineFrame.getInstance().isGameOver())
				return;
			final ButtonModel model = (ButtonModel) e.getSource();
			if (model.isPressed() && model.isArmed())
				MineFrame.getInstance().getButton().setIcon(MineGraphics.get(Emoticon.O_O));
			else
				MineFrame.getInstance().getButton().setIcon(MineGraphics.get(Emoticon.SMILE));;
		}
	};
	private final static MouseListener MOUSE_LISTENER = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if (MineFrame.getInstance().isGameOver())
				return;
			final MineButton button = (MineButton) e.getComponent();
			switch (e.getButton()) {
			case MouseEvent.BUTTON3:
				if (button.isSelected())
					button.revealSurroundingButtons();
				else
					button.changeStatus();
				button.requestFocusInWindow();
				break;
			case MouseEvent.BUTTON1:
				if (e.getClickCount() == 2)
					button.revealSurroundingButtons();
				break;
			}
		};
	};
	
	private final int x;
	private final int y;
	
	private Status status = Status.NORMAL;
	private int value = -1;
	
	public MineButton(final int x, final int y) {
		this.x = x;
		this.y = y;
		
		final ButtonModel model = new ToggleButtonModel() {
			@Override
			public void setArmed(boolean b) {
				super.setArmed(b && !isSelected() && getStatus() != Status.FLAG && !MineFrame.getInstance().isGameOver());
			}
			
			@Override
			public void setPressed(boolean b) {
				super.setPressed(b && !isSelected() && !MineFrame.getInstance().isGameOver());
			}
			
			@Override
			public void setSelected(boolean b) {
				if (!b || MineFrame.getInstance().isGameOver())
					return;
				if (getStatus() != Status.FLAG) {
					if (b != isSelected())
						setText(null);
					super.setSelected(b);
				}
			}
			
			@Override
			public void setRollover(boolean b) {
				super.setRollover(b && !MineFrame.getInstance().isGameOver());
			}
		};
		setModel(model);
		setMargin(MARGIN);
		setFont(FONT);
		setForeground(Color.MAGENTA);
		addMouseListener(MOUSE_LISTENER);
		model.addChangeListener(CHANGE_LISTENER);
	}
	
	public static Color foreground(final int i) {
		return FOREGROUNDS[i];
	}
	
	public void revealSurroundingButtons() {
		final Collection<MineButton> buttons = getField().fetchSurroundingButtons(x, y);
		int flags = 0;
		for (final Iterator<MineButton> iterator = buttons.iterator(); iterator.hasNext();) {
			final MineButton button = iterator.next();
			if (button.isSelected())
				iterator.remove();
			else if (button.getStatus() == Status.FLAG) {
				flags++;
				iterator.remove();
			}
		}
		if (flags == value && !buttons.isEmpty()) {
			for (MineButton mineButton : buttons) {
				mineButton.setSelected(true);
			}
		} else {
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}
	
	private MineField getField() {
		return (MineField) getParent();
	}
	
	public int getValue() {
		return value ;
	}
	
	public void setValue(final int surroundingBombs) {
		setForeground(FOREGROUNDS[surroundingBombs]);
		this.value = surroundingBombs;
		setText(String.valueOf(surroundingBombs));
	}

	public Status changeStatus() {
		if (!(isSelected() || MineFrame.getInstance().isGameOver())) {
			if (MineFrame.getInstance().hasMarker()) {
				switch (getStatus()) {
					case NORMAL:
						setStatus(Status.FLAG);
						break;
					case FLAG:
						setStatus(Status.MARKER);
						break;
					case MARKER:
						setStatus(Status.NORMAL);
						break;
				}
			} else {
				switch (getStatus()) {
					case NORMAL:
						setStatus(Status.FLAG);
						break;
					case FLAG:
						setStatus(Status.NORMAL);
						break;
				}
			}
			switch (getStatus()) {
				case NORMAL:
					setText(null);
					break;
				case FLAG:
					setText("!");
					break;
				case MARKER:
					setText("?");
					break;
			}
			MineFrame.getInstance().getStatistics().refresh(getField());
		}
		return getStatus();
	}
	
	public final Status getStatus() {
		return status;
	}
	
	public final void setStatus(Status status) {
		if (this.status == status)
			return;
		final MineLabel bombsLabel = MineFrame.getInstance().getBombsLabel();
		if (this.status == Status.FLAG)
			bombsLabel.incValue();
		this.status = status;
		if (status == Status.FLAG)
			bombsLabel.decValue();
	}
	
	public enum Status {
		NORMAL, FLAG, MARKER;
	}
	
	public final int getButtonX() {
		return x;
	}
	
	public final int getButtonY() {
		return y;
	}

	@Override
	public String toString() {
		return "MineButton [x=" + x + ", y=" + y + ", status=" + status + ", isSelected()=" + isSelected() + "]";
	}
}