package mine;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

import mine.gfx.MineGraphics;
import mine.gfx.MineGraphics.Emoticon;
import mine.gfx.MineGraphics.Picture;

public class MineField extends JPanel implements ItemListener, KeyListener {
	private final boolean[][] field;
	
	public MineField(final int width, final int height, final int bombCount) {
		super(new GridLayout(height, width));
		
		if (width <= 0)
			throw new IllegalArgumentException("Width too small! (" + width + " <= " + 0 + ")");
		if (height <= 0)
			throw new IllegalArgumentException("Height too small! (" + height + " <= " + 0 + ")");
		final int dimension = (width - 1) * (height - 1);
		if (bombCount > dimension)
			throw new IllegalArgumentException("Too many bombs! (" + bombCount + " > (" + width + " - 1) * (" + height + " - 1) = " + dimension + ")");
		
		field = new boolean[height][width];
		
		final Border border = BorderFactory.createLoweredBevelBorder();
		setBorder(border);
		final Insets insets = border.getBorderInsets(this);
		setPreferredSize(new Dimension(insets.left + width * MineGraphics.Picture.DIMENSION + insets.right, insets.top + height * MineGraphics.Picture.DIMENSION + insets.bottom));
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				final MineButton button = new MineButton(w, h);
				button.addItemListener(this);
				button.addKeyListener(this);
				add(button);
			}
		}
	}
	
	private void initBombs(final int x, final int y) {
		final int width = getFieldWidth();
		final int height = getFieldHeight();
		final int dimension = width * height;
		final List<Integer> places = new ArrayList<Integer>(dimension);
		for (int i = 0; i < dimension; i++)
			places.add(i);
		final Collection<MineButton> buttons = fetchSurroundingButtons(x, y);
		final List<Integer> list = new ArrayList<Integer>(buttons.size() + 1);
		list.add(x + y * width);
		for (MineButton mineButton : buttons)
			list.add(mineButton.getButtonX() + mineButton.getButtonY() * width);
		Collections.sort(list, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}});
		for (final Integer surroundingIndex : list)
			places.remove(surroundingIndex);
		final Random random = new Random();
		for (int i = 0, b = MineFrame.getInstance().getNumberOfBombs(); i < b; i++) {
			final int rnd = random.nextInt(places.size());
			final int nextBomb = places.remove(rnd);
			field[nextBomb / width][nextBomb % width] = true;
			if (MineFrame.DEBUG)
				getButtonAt(nextBomb % width, nextBomb / width).setText("+");
		}
	}
	
	public boolean isMine(final int x, final int y) {
		return field[y][x];
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			final MineButton mineButton = (MineButton) e.getSource();
			final int x = mineButton.getButtonX();
			final int y = mineButton.getButtonY();
			final int numberOfComponents = getComponentCount();
			final int width = getFieldWidth();
			if (MineFrame.getInstance().isFirstMove()) {
				initBombs(x, y);
				if (MineFrame.DEBUG) {
					System.out.println(MineField.this);
					System.out.println();
				}
				MineFrame.getInstance().setFirstMove(false);
				MineFrame.getInstance().startTimer();
			}
			if (field[y][x]) {
				MineFrame.getInstance().setGameOver(true);
				MineFrame.getInstance().stopTimer();
				MineFrame.getInstance().getButton().setIcon(MineGraphics.get(Emoticon.X_X));
				for (int i = 0; i < numberOfComponents; i++) {
					final MineButton button = getComponent(i);
					if (field[i / width][i % width]) {
						button.setText(null);
						button.setIcon(new ImageIcon(MineGraphics.get(Picture.BOMB)));
					}
				}
				MineFrame.getInstance().getStatistics().refresh(this);
			} else {
				final int surroundingBombs = countSurroundingBombs(x, y);
				mineButton.setValue(surroundingBombs);
				if (surroundingBombs == 0) {
					if (!autoSelect)
						selectSurroundingButtons(x, y);
				}
				MineFrame.getInstance().getStatistics().refresh(this);
				for (int i = 0; i < numberOfComponents; i++) {
					final MineButton button = getComponent(i);
					if (!field[i / width][i % width] &&
						!button.isSelected())
						return;
				}
				MineFrame.getInstance().setGameOver(true);
				MineFrame.getInstance().stopTimer();
				MineFrame.getInstance().getButton().setIcon(MineGraphics.get(Emoticon.GRIN));
			}
		}
	}
	
	private MineButton getButtonAt(final int x, final int y) {
		final int width = getFieldWidth();
		final int height = getFieldHeight();
		if (MineFrame.getInstance().isWrapField()) {
			int newX = x;
			while (newX < 0)
				newX += width;
			newX %= width;
			int newY = y;
			while (newY < 0)
				newY += height;
			newY %= height;
			return getComponent(newY * width + newX);
		} else {
			if (x >= 0 && x < width && y >= 0 && y < height)
				return getComponent(y * width + x);
			return null;
		}
	}
	
	public Collection<MineButton> fetchSurroundingButtons(final int x, final int y) {
		final Collection<MineButton> result = new HashSet<MineButton>();
		MineButton button;
		int x2, y2;
		
		y2 = y - 1;
		x2 = x - 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		x2 = x;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		x2 = x + 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		
		y2 = y;
		x2 = x - 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		x2 = x + 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		
		y2 = y + 1;
		x2 = x - 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		x2 = x;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		x2 = x + 1;
		button = getButtonAt(x2, y2);
		if (button != null)
			result.add(button);
		return result;
	}
	
	private boolean autoSelect = false;
	
	public void selectSurroundingButtons(final int x, final int y) {
		autoSelect = true;
		final Collection<MineButton> buttonsToSelect = new HashSet<MineButton>();
		Collection<MineButton> surroundingButtons = fetchSurroundingButtons(x, y);
		Collection<MineButton> pendingButtons;
		do {
			pendingButtons = new HashSet<MineButton>();
			for (MineButton mineButton : surroundingButtons) {
				if (buttonsToSelect.add(mineButton)) {
					if (countSurroundingBombs(mineButton.getButtonX(), mineButton.getButtonY()) == 0) {
						pendingButtons.add(mineButton);
					}
				}
			}
			surroundingButtons = new HashSet<MineButton>();
			for (MineButton mineButton : pendingButtons)
				surroundingButtons.addAll(fetchSurroundingButtons(mineButton.getButtonX(), mineButton.getButtonY()));
		} while (!surroundingButtons.isEmpty());
		for (MineButton mineButton : buttonsToSelect)
			mineButton.setSelected(true);
		autoSelect = false;
	}
	
	public int countSurroundingBombs(final int x, final int y) {
		final int width = getFieldWidth();
		final int height = getFieldHeight();
		final boolean wrapField = MineFrame.getInstance().isWrapField();
		
		int result = 0;
		int x2, y2;
		
		y2 = y - 1;
		if (wrapField)
			while (y2 < 0)
				y2 += height;
		if (y2 >= 0) {
			x2 = x - 1;
			if (wrapField)
				while (x2 < 0)
					x2 += width;
			if (x2 >= 0 && field[y2][x2])
				result++;
			x2 = x;
			if (field[y2][x2])
				result++;
			x2 = x + 1;
			if (wrapField)
				x2 %= width;
			if (x2 < width && field[y2][x2])
				result++;
		}
		
		y2 = y;
		x2 = x - 1;
		if (wrapField)
			while (x2 < 0)
				x2 += width;
		if (x2 >= 0 && field[y2][x2])
			result++;
		x2 = x + 1;
		if (wrapField)
			x2 %= width;
		if (x2 < width && field[y2][x2])
			result++;
		
		y2 = y + 1;
		if (wrapField)
			y2 %= height;
		if (y2 < height) {
			x2 = x - 1;
			if (wrapField)
				while (x2 < 0)
					x2 += width;
			if (x2 >= 0 && field[y2][x2])
				result++;
			x2 = x;
			if (field[y2][x2])
				result++;
			x2 = x + 1;
			if (wrapField)
				x2 %= width;
			if (x2 < width && field[y2][x2])
				result++;
		}
		return result;
	}
	
	@Override
	public MineButton getComponent(int n) {
		return (MineButton) super.getComponent(n);
	}
	
	@Override
	public GridLayout getLayout() {
		return (GridLayout) super.getLayout();
	}
	
	public int getFieldHeight() {
		return field.length;
	}
	
	public int getFieldWidth() {
		return field[0].length;
	}
	
	@Override
	public String toString() {
		final int height = getFieldHeight();
		final int width = getFieldWidth();
		
		final StringBuilder result = new StringBuilder(height * (width + 1) - 1);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				result.append(field[i][j] ? '+' : '-');
			result.append('\n');
		}
		return result.toString();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		final MineButton button = (MineButton) e.getSource();
		final MineButton destination;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				destination = getButtonAt(button.getButtonX(), button.getButtonY() - 1);
				break;
			case KeyEvent.VK_DOWN:
				destination = getButtonAt(button.getButtonX(), button.getButtonY() + 1);
				break;
			case KeyEvent.VK_LEFT:
				destination = getButtonAt(button.getButtonX() - 1, button.getButtonY());
				break;
			case KeyEvent.VK_RIGHT:
				destination = getButtonAt(button.getButtonX() + 1, button.getButtonY());
				break;
			case KeyEvent.VK_SHIFT:
				if (button.isSelected())
					button.revealSurroundingButtons();
				else
					button.changeStatus();
			default:
				return;
		}
		if (destination != null)
			destination.requestFocusInWindow();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {}
}