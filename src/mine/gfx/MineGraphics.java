package mine.gfx;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class MineGraphics {
	private MineGraphics() {}
	
	public static ImageIcon get(Emoticon emo) {
		if (emo == null)
			return null;
		return emo.getImage();
	}
	
	public static BufferedImage get(Picture pic) {
		if (pic == null)
			return null;
		return pic.getImage();
	}
	
	public enum Emoticon {
		GRIN, O_O, SMILE, WANT, X_X;
		
		public final static int DIMENSION = 40;
		
		private ImageIcon image;
		
		public final ImageIcon getImage() {
			if (image == null) {
				try {
					setImage(new ImageIcon(ImageIO.read(MineGraphics.class.getResourceAsStream(name().toLowerCase() + ".png")).getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return image;
		}
		
		private final void setImage(ImageIcon image) {
			this.image = image;
		}
	}
	
	public enum Picture {
		BOMB;
		
		public final static int DIMENSION = 24;
		
		private BufferedImage image;
		
		public final BufferedImage getImage() {
			if (image == null) {
				try {
					final BufferedImage rawImage = ImageIO.read(MineGraphics.class.getResourceAsStream(name().toLowerCase() + ".png"));
					final int imageType = rawImage.getType();
					final BufferedImage newImage = new BufferedImage(DIMENSION, DIMENSION, imageType == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : imageType);
					newImage.createGraphics().drawImage(rawImage, 0, 0, DIMENSION, DIMENSION, null);
					setImage(newImage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return image;
		}
		
		private final void setImage(BufferedImage image) {
			this.image = image;
		}
	}
}