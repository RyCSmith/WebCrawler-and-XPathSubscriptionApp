package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.xpathengine.Parser;
import edu.upenn.cis455.xpathengine.Token;
import edu.upenn.cis455.xpathengine.Tree;

public class ParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParseXPath() {
		//An error will be thrown and test will fail if any of the following are not valid xpaths
		Parser parser = new Parser("/foo/bar/xyz");
		parser.parseXPath();
		parser = new Parser("/foo/bar[@att=\"xx\"]");
		parser.parseXPath();
		parser = new Parser("/xyz/abc[contains(text(),\"someSubstring\")]");
		parser.parseXPath();
		parser = new Parser("/a/b/c[text()=\"theEntireText\"]");
		parser.parseXPath();
		parser = new Parser("/a/b/c[text() = \"whiteSpacesShouldNotMatter\"]");
		parser.parseXPath();
		parser = new Parser("/d/e/f[foo[text()=\"something\"]][bar]");
		parser.parseXPath();
		
		//the following tests will fail if an error is not thrown when parsing invalid xpaths
		try {
			parser = new Parser("foo/bar/xy][z");
			parser.parseXPath();
			fail();
		}catch (Exception e){}
		try {
			parser = new Parser("foo/bar/xyz");
			parser.parseXPath();
			fail();
		}catch (Exception w) {}
	}

}
