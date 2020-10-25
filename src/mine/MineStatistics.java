package mine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mine.MineButton.Status;
import mine.gfx.MineGraphics;
import mine.gfx.MineGraphics.Picture;

public class MineStatistics extends JDialog implements ActionListener {
	private final MineTableModel model;
	
	private class MineDataSet {
		private int found;
		private int total;
	}

	private class MineTableModel extends AbstractTableModel {
		private final MineDataSet[] data = new MineDataSet[10];
		private final String[] columnNames = {"", "Ist", "Soll", "Rest", "%"};
		
		public MineTableModel() {
			for (int i = 0; i < data.length; i++)
				data[i] = new MineDataSet();
		}
		
		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public int getRowCount() {
			return data.length + 1;
		}

		@Override
		public Object getValueAt(int row, int column) {
			final MineDataSet mds;
			if (row == data.length) {
				mds = new MineDataSet();
				for (MineDataSet m : data) {
					mds.found += m.found;
					mds.total += m.total;
				}
			} else {
				mds = data[row];
			}
			switch (column) {
			case 0:
				return row;
			case 1:
				return mds.found;
			case 2:
				return mds.total;
			case 3:
				return mds.total - mds.found;
			case 4:
				return mds.total == 0 ? 100f : mds.found * 100f / mds.total;
			default:
				throw new IllegalArgumentException();
			}
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
			case 3:
				return Integer.class;
			case 4:
				return Float.class;
			default:
				return super.getColumnClass(columnIndex);
			}
		}
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

	public MineStatistics() {
		super(MineFrame.getInstance(), "Statistik");
		
		final JPanel panel = new JPanel(new BorderLayout());
		final JTable table = new JTable(model = new MineTableModel());
		table.setRowMargin(0);
		table.setRowHeight(MineGraphics.Picture.DIMENSION);
		final TableColumnModel columnModel = table.getColumnModel();
		final JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setReorderingAllowed(false);
		tableHeader.setResizingAllowed(false);
		columnModel.setColumnMargin(0);
		columnModel.getColumn(0).setPreferredWidth(MineGraphics.Picture.DIMENSION);
		columnModel.getColumn(0).setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				final MineButton b = new MineButton(-1, -1);
				final int i = (Integer) value;
				b.setPreferredSize(new Dimension(MineGraphics.Picture.DIMENSION, MineGraphics.Picture.DIMENSION));
				if (i < 9)
					b.setValue(i);
				else if (i == 9)
					b.setIcon(new ImageIcon(MineGraphics.get(Picture.BOMB)));
				else
					b.setText("=");
				return b;
			}
		});
		final TableCellRenderer colorRenderer = new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				final Component c = table.getDefaultRenderer(table.getColumnClass(column)).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				final float f = (Float) table.getValueAt(row, table.getColumnCount() - 1);
				final Font font;
				final Color color;
				if (f <= 100f) {
					font = c.getFont().deriveFont(Font.BOLD);
					color = new Color(Color.HSBtoRGB(f / 300, 1f, 1f));
				} else {
					font = c.getFont().deriveFont(Font.BOLD | Font.ITALIC);
					color = Color.RED;
				}
				c.setFont(font);
				c.setForeground(color);
				if (!isSelected)
					c.setBackground(Color.GRAY);
				return c;
			}
		};
		for (int i = 1, c = columnModel.getColumnCount(); i < c; i++) {
			final TableColumn column = columnModel.getColumn(i);
			column.setPreferredWidth(55);
			column.setCellRenderer(colorRenderer);
		}
		panel.add(table, BorderLayout.CENTER);
		panel.add(tableHeader, BorderLayout.NORTH);
		setContentPane(panel);
		getRootPane().registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		setResizable(false);
		pack();
		setLocationRelativeTo(MineFrame.getInstance());
	}

	public void refresh(MineField field) {
		int[] progress = new int[10];
		int[] total = new int[10];
		synchronized (field.getTreeLock()) {
			for(int i = 0, c = field.getComponentCount(); i < c; i++) {
				MineButton button = field.getComponent(i);
				if (button.getStatus() == Status.FLAG)
					progress[9]++;
				if (field.isMine(button.getButtonX(), button.getButtonY())) {
					total[9]++;
				} else {
					final int bombs = field.countSurroundingBombs(button.getButtonX(), button.getButtonY());
					total[bombs]++;
					if (button.isSelected())
						progress[bombs]++;
				}
			}
		}
		
		for (int i = 0; i < model.data.length; i++) {
			final MineDataSet j = model.data[i];
			j.found = progress[i];
			j.total = total[i];
		}
		
		model.fireTableDataChanged();
	}
	
	private MineTableModel getModel() {
		return model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		setVisible(false);
	}
}