package ch.swissqr;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * Test for PNG imgage file format
 * 
 * @author pschatzmann
 *
 */
public class TestWritePng {
	@Test
	public void testWriteTest() throws Exception {
		File file = new File("./src/main/resources/icons/test.png");
		file.delete();
		String key = "Test";
		BufferedImage bufferedImage = new BufferedImage(70, 70, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImage.getGraphics();
		graphics.setColor(Color.YELLOW);
		graphics.fillRect(0, 0, 70, 70);
		graphics.setColor(Color.BLUE);
		graphics.fillRect(8, 8, 54, 54);
		graphics.setColor(Color.YELLOW);
		graphics.setFont(new Font("Arial", Font.BOLD, 20));
		graphics.drawString(key, 14, 40);
		ImageIO.write(bufferedImage, "png", file);
	}

	@Test
	public void testWriteError() throws Exception {
		File file = new File("./src/main/resources/icons/error.png");
		file.delete();
		String key = "Error";
		BufferedImage bufferedImage = new BufferedImage(70, 70, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImage.getGraphics();
		graphics.setColor(Color.RED);
		graphics.fillRect(0, 0, 70, 70);
		graphics.setColor(Color.DARK_GRAY);
		graphics.fillRect(8, 8, 54, 54);
		graphics.setColor(Color.YELLOW);
		graphics.setFont(new Font("Arial", Font.BOLD, 20));
		graphics.drawString(key, 10, 40);
		ImageIO.write(bufferedImage, "png", file);
	}

}
