package ch.swissqr;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import ch.swissqr.content.ch.Address;

/**
 * Tests for the parsing of addresses
 * 
 * @author pschatzmann
 *
 */
public class TestAddress {
	
	@Test
	public void testAddress1() throws Exception {
		String adressString = "Herr,Phil Schatzmann,lic.oec hsg,Stutzhaldenstrasse 3,8888 Schindellegi SZ";
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Schindellegi SZ", adr.getCity());
	}
	
	@Test
	public void testAddress2() throws Exception {
		String adressString = "Herr,Phil Schatzmann,Stutzhaldenstrasse 3,8888 Schindellegi SZ,Schweiz";
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Phil Schatzmann", adr.getName());
		Assert.assertEquals("Schindellegi SZ", adr.getCity());
		Assert.assertEquals("CH", adr.getCountryISO());
	}

	@Test
	public void testAddress3() throws Exception {
		String adressString = "Herr,Phil Schatzmann,Stutzhaldenstrasse 3,CH-8888 Schindellegi SZ,Switzerland";
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Phil Schatzmann", adr.getName());
		Assert.assertEquals("Schindellegi SZ", adr.getCity());
		Assert.assertEquals("CH", adr.getCountryISO());
	}

	@Test
	public void testAddress4() throws Exception {
		String adressString = "Herr,Phil Schatzmann,8888 Schindellegi SZ,Switzerland";
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Phil Schatzmann", adr.getName());
		Assert.assertEquals("Schindellegi SZ", adr.getCity());
		Assert.assertEquals("CH", adr.getCountryISO());
	}

	@Test
	public void testAddress5() throws Exception {
		String adressString = "Phil Schatzmann,8888 Schindellegi";
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Phil Schatzmann", adr.getName());
		Assert.assertEquals("Schindellegi", adr.getCity());
		Assert.assertEquals("CH", adr.getCountryISO());
	}

	@Test
	public void testAddressAUS() throws Exception {
		String adressString = "Mr S Tan,200 Broadway Av,WEST BEACH SA 5024,AUSTRALIA"; 				
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("Mr S Tan", adr.getName());
		Assert.assertEquals("WEST BEACH SA", adr.getCity());
		Assert.assertEquals("AU", adr.getCountryISO());
	}

	@Test
	public void testAddressUS() throws Exception {
		String adressString = "JOHN DOE,ACME INC,123 MAIN ST NW STE 12,ANYTOWN NY  12345,USA"; 				
		Address adr = Address.createAddress(adressString);
		Assert.assertEquals("JOHN DOE", adr.getName());
		Assert.assertEquals("ANYTOWN NY", adr.getCity());
		Assert.assertEquals("US", adr.getCountryISO());
	}
	
	//@Test
	public void testList() {
		Locale.setDefault(Locale.GERMAN);
		String[] locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);

			System.out.println( obj.getDisplayCountry()
				+ ",  " + obj.getCountry());

		}

		Locale.setDefault(Locale.FRENCH);
		locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);

			System.out.println( obj.getDisplayCountry()
				+ ",  " + obj.getCountry());

		}
		
		Locale.setDefault(Locale.ITALIAN);
		locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);

			System.out.println( obj.getDisplayCountry()
				+ ",  " + obj.getCountry());

		}
		
		Locale.setDefault(Locale.ENGLISH);
		locales = Locale.getISOCountries();

		for (String countryCode : locales) {
			Locale obj = new Locale("", countryCode);

			System.out.println( obj.getDisplayCountry()
				+ ",  " + obj.getCountry());

		}

	}

	
}
