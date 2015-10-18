package test.edu.upenn.cis455;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import edu.upenn.cis455.servlet.ServletSupport;

public class ServletSupportTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBuildHTMLDocument() {
		String testHtmlPage = "<html>	<head>		<title>XPath Evaluator</title><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>	</head>	<body>		<div>			<span>Ryan Smith</span>			<br>			<span>rysmit</span>		</div>		<center>			<form action=\"/servlet/xpath\" method=\"post\">			  XPath:<br>			<div id=\"emptydiv\"value=\"0\">			  <input type=\"text\" name=\"xpath0\" style=\"width:300px;\">			  <br>			</div>  Document URL:<br>			  <input type=\"text\" name=\"url\" style=\"width:300px;\">			  <br><br>			  <button type=\"button\" id=\"add-button\">Add Another XPath</button>			  <input type=\"submit\" value=\"Submit\" style=\"width:100px;\">			</form>		</center>	</body>	<script>		$('#add-button').click(function() {			var indexText = document.getElementById('emptydiv').getAttribute('value');			var nextIndex = parseInt(indexText) + 1;			document.getElementById('emptydiv').setAttribute('value', nextIndex);			var newHTML = \"<input type=\"text\" name=\"xpath\" + nextIndex.toString() + \"\" style=\"width:300px;\"><br>\";			$('#emptydiv').append(newHTML);		});	</script></html>";
		Document doc = ServletSupport.buildDocument(testHtmlPage, false);
		assertFalse(doc == null);
		assertTrue(doc instanceof Document);
		Node node = doc.getDocumentElement();
		assertEquals("html", node.getNodeName());
	}
	
	@Test
	public void testBuildXMLDocument() {
		String testXmlPage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bookstore><book category=\"cooking\"><title lang=\"en\">Everyday Italian</title><author>Giada De Laurentiis</author><year>2005</year><price>30.00</price></book></bookstore>";
		Document doc = ServletSupport.buildDocument(testXmlPage, false);
		assertFalse(doc == null);
		assertTrue(doc instanceof Document);
		Node node = doc.getDocumentElement();
		assertEquals("bookstore", node.getNodeName());
	}

}
