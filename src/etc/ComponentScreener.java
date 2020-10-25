package etc;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;

public final class ComponentScreener {
	public final static String IMG_TYPE = "png";

	private ComponentScreener() {
		throw new UnsupportedOperationException("this utility class must not be instantiated");
	}

	/**
	 * Screens a component and saves the image to a file.
	 * Should be called from the <code>EventDispatchThread</code>.
	 * 
	 * @param component
	 *            a {@link Component} to be screened.
	 * @param output
	 *            a {@link File} to be written to.
	 * @param withRobot
	 *            determines if a {@link Robot} should be used
	 * @return <code>false</code> if no appropriate writer is found.
	 * @throws IOException
	 *             if an error occurs during writing.
	 * @throws UnsupportedOperationException
	 *             if <code>withRobot</code> is <code>true</code> and the platform configuration does not allow low-level input control.
	 *             This exception is always thrown when {@link GraphicsEnvironment#isHeadless()} returns true (encapsulates an {@link AWTException}).
	 */
	public final static boolean screen(Component component, File output, boolean withRobot) throws IOException, UnsupportedOperationException {
		return ImageIO.write(screen(component, withRobot), IMG_TYPE, output);
	}
	
	public final static BufferedImage screen(Component component, boolean withRobot) throws UnsupportedOperationException {
		final BufferedImage image;
		if (withRobot) {
			try {
				final Robot robot = new Robot();
				image = robot.createScreenCapture(component.getBounds());
			} catch (AWTException e) {
				throw new UnsupportedOperationException(e);
			}
		} else {
			if (component instanceof RootPaneContainer) {
				final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
				if (!(JFrame.isDefaultLookAndFeelDecorated() && lookAndFeel != null
						&& UIManager.getCrossPlatformLookAndFeelClassName().equals(lookAndFeel.getClass().getName())))
					component = ((RootPaneContainer) component).getContentPane();
			}
			image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
			component.print(image.createGraphics());
		}
		return image;
	}
}