package mine;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import etc.ComponentScreener;

public class MineFileChooser extends JFileChooser {
	public MineFileChooser() {
		super(System.getProperty("user.dir"));
		for (FileFilter fileFilter : getChoosableFileFilters())
			removeChoosableFileFilter(fileFilter);
		final FileFilter fileFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "PNG-Dateien";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toUpperCase().matches(".*\\." + ComponentScreener.IMG_TYPE);
			}
		};
		setAcceptAllFileFilterUsed(false);
		setFileFilter(fileFilter);
		addChoosableFileFilter(fileFilter);
	}
	
	@Override
	public int showSaveDialog(Component parent) throws HeadlessException {
		final File path = getCurrentDirectory();
		final String file = "MineSweeper." + ComponentScreener.IMG_TYPE;
		final int index = file.lastIndexOf('.');
		final String fileWithoutExtension = index < 0 ? file : file.substring(0, index);
		final String fileExtension = index < 0 ? "" : file.substring(index);
		String numberedFile = file;
		File tempFile;
		for (int i = 2; (tempFile = new File(path, numberedFile)).exists(); i++)
			numberedFile = fileWithoutExtension + " (" + i + ")" + fileExtension;
		setSelectedFile(tempFile);
		return super.showSaveDialog(parent);
	}
	
	@Override
	public void setSelectedFile(final File file) {
		if (!file.getName().toLowerCase().endsWith("." + ComponentScreener.IMG_TYPE))
			super.setSelectedFile(new File(file.getAbsoluteFile() + "." + ComponentScreener.IMG_TYPE));
		else
			super.setSelectedFile(file);
	}
	
	@Override
	public void approveSelection() {
		final File file = getSelectedFile();
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(this, file.getName() + " ist bereits vorhanden." + System.getProperty("line.separator") + "Möchten Sie sie ersetzen?", "Speichern bestätigen", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
				super.approveSelection();
		} else
			super.approveSelection();
	}
}