package ch.swissqr;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ch.swissqr.utils.StringUtils;

/**
 * Tests for the custom string functions
 * 
 * @author pschatzmann
 *
 */
public class TestStringUtil {

	@Test
	public void isEmpty() {
		Assert.assertTrue(StringUtils.isEmpty(""));
		Assert.assertTrue(StringUtils.isEmpty(null));
		Assert.assertTrue(StringUtils.isEmpty(" "));
		Assert.assertFalse(StringUtils.isEmpty("abc"));

		Assert.assertEquals("",StringUtils.str(null));
	}

	
	@Test
	public void formatGroup() {
		String in = "RF18 5390 0754 7034 12";
		String out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);
			
	}
	
	@Test
	public void formatGroup4() {
		String in = "CH43 3199 9000 0012 6578";
		String out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);

		in = "CH43 3199 9000 0012 6578 9";
		out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);

		in = "CH43 3199 9000 0012 6578 98";
		out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);

		in = "CH43 3199 9000 0012 6578 987";
		out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);

		in = "CH43 3199 9000 0012 6578 9876";
		out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);

		in = " CH43 3199 9000 0012 6578 9876 ";
		out = StringUtils.formatInGroups(4,in.replaceAll(" ",""));
		Assert.assertEquals(in.trim(),out);

	}
	
	@Test
	public void formatGroupRight() {
		String in = "21 00000 00003 13947 14300 09017";
		String out = StringUtils.formatInGroupsRight(5,in.replaceAll(" ",""));
		Assert.assertEquals(in,out);
	}
	
	
	@Test
	public void testSplit() {
		String line = "word1 word2 word3 word4";
		
		List<String> result = StringUtils.splitLines(line,10,5);

		result = StringUtils.splitLines(line,10,10);
		System.out.println(result);
		
	}
	
}
