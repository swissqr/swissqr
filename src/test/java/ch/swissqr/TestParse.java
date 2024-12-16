package ch.swissqr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;

import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.AllBarcodeTypes;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.formats.CSVFormat;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.content.ch.formats.IFormat;
import ch.swissqr.errors.BarcodeException;

/**
 * Tests for parsing barcode images
 * 
 * @author pschatzmann
 *
 */
public class TestParse {
	private final static Logger LOG = Logger.getLogger(TestParse.class);

	@Test
	public void parseStandard() throws Exception {
		File img = new File("src/test/resources/qr-codes/swissref.png");
		Assert.assertTrue(img.exists());

		BufferedImage bi = ImageIO.read(img ); 

		QRBarcode bc = new QRBarcode();
		String str = bc.readImage(new FileInputStream(img));
		LOG.info("---");
		LOG.info(str);

		ContentBarcodeCH ch = new ContentBarcodeCH();
		ch.parse(str);		
		Assert.assertEquals("210000000003139471430009017", ch.getPaymentReference().getReference());

	}
	
	@Test
	public void parseShort() throws NotFoundException, ChecksumException, com.google.zxing.FormatException,
			FileNotFoundException, IOException, BarcodeException {
		
		QRBarcode bc = new QRBarcode();
		String str = bc.readImage(new FileInputStream(new File("src/test/resources/qr-codes/swissref.png")));
		ContentBarcodeCH ch = new ContentBarcodeCH();
		ch.parse(str);		
		Assert.assertEquals("210000000003139471430009017", ch.getPaymentReference().getReference());

	}
	
	
	@Test
	public void parsePayment() throws Exception {
		File img = new File("src/test/resources/qr-codes/payment.jpg");
		Assert.assertTrue(img.exists());

		BufferedImage bi = ImageIO.read(img ); 

		QRBarcode bc = new QRBarcode();
		String str = bc.readImage(new FileInputStream(img));
		LOG.info("---");
		LOG.info(str);

		ContentBarcodeCH ch = new ContentBarcodeCH();
		ch.parse(str);		

	}
	
	@Test
	public void parsePascal() throws Exception {
		File img = new File("src/test/resources/qr-codes/swisspascal.png");
		Assert.assertTrue(img.exists());

		BufferedImage bi = ImageIO.read(img ); 

		QRBarcode bc = new QRBarcode();
		String str = bc.readImage(new FileInputStream(img));
		LOG.info("---");
		LOG.info(str);

		ContentBarcodeCH ch = new ContentBarcodeCH();
		ch.parse(str);		

	}
	
	
	@Test
	public void parseWrite() throws Exception {
		File img = new File("src/test/resources/write.png");
		Assert.assertTrue(img.exists());

		BufferedImage bi = ImageIO.read(img ); 

		QRBarcode bc = new QRBarcode();
		String str = bc.readImage(new FileInputStream(img));
		LOG.info("---");
		LOG.info(str);

		ContentBarcodeCH ch = new ContentBarcodeCH();
		ch.parse(str);		

	}
	
	@Test
	public void testBarcodeFromCSV() throws Exception {
		String contentString = new String(Files.readAllBytes(Paths.get("src/test/resources/barcodesCH.csv")));
		IFormat fmt = new CSVFormat();
		for (IContent c : fmt.read(contentString)) {
			 BufferedImage img = c.toBarcode("PNG", null, null);
			 File file = new File("src/test/resources/generated/"+c.getProperties().getProperty("filename")+".PNG");
		     ImageIO.write(img, "PNG", file);
			 LOG.info(file.getName());
			 Assert.assertTrue(file.length()>0);
		}

	}


}
