package edu.upenn.cis455.xpathengine;

import java.io.InputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

public class XPathEngineImpl implements XPathEngine {
    private String[] xpaths;
  
    public XPathEngineImpl() {
	  xpaths = new String[0];
    }
	
    public void setXPaths(String[] s) {
	  xpaths = s;
    }

    public boolean isValid(int i) {
	  try {
		  String currentXPath = xpaths[i];
		  Parser parser = new Parser(currentXPath);
		  parser.parseXPath(); //This will throw an IllegalStateException if false.
	  } catch (ArrayIndexOutOfBoundsException e) {
		  throw new IllegalArgumentException("The provided index "
		  		+ "is invalid. The array only contains " + xpaths.length + " elements.");
	  }catch (IllegalStateException e0) {
		  return false;
	  }
	  return true;
    }
	
    public boolean[] evaluate(Document domDoc) { 
    	boolean[] results = new boolean[xpaths.length];
    	for (int i = 0; i < xpaths.length; i++) {
    		String currentXPath = xpaths[i];
			Parser parserPath = new Parser(currentXPath);
			try {
				Tree<Token> xpathTree = parserPath.parseXPath();
				results[i] = evaluateSingleXPath(domDoc.getDocumentElement(),xpathTree);
			} catch (Exception e){
				results[i] = false;
			}
    	}
    	return results;
    }
    
    public static boolean evaluateSingleXPath(Node node, Tree<Token> tree) {
    	//if we've run out of xpath without returning false, it has been satisfied
    	if (tree == null)
    		return true;
    	
		Token token = tree.getValue();
		//check to see if the given node has the same value as the xpath tree
		String tokenVal = token.value;
		String nodeVal = node.getNodeName();
		if (tokenVal.equals(nodeVal)) {
			//check that all tests pass, return false if any fail
			ArrayList<Tree<Token>> testChildren = getTestChildren(tree);
			for (Tree<Token> child : testChildren) {
				boolean result = checkTest(node, child);
				if (!result)
					return false;
			}
			
			//advance tree to its child representing the next step
			tree = getNameChild(tree);

			//if the document has run out of node before the xpath is satisfied, return false
			NodeList nodeChildren = node.getChildNodes();	
			if (nodeChildren.getLength() <= 0)
				return false;
			
			//otherwise, try the remaining xpath on all children of the current document node, return true if any pass
			for (int i = 0; i < nodeChildren.getLength(); i++){
				Node nodeChild = nodeChildren.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					boolean result = evaluateSingleXPath(nodeChild, tree);
					if (result)
						return true;
				}
			}	
		}
		return false;
    }
    
    private static boolean checkTest(Node node, Tree<Token> tree) {
    	ProcessedToken token = (ProcessedToken) tree.getValue();
    	ProcessedToken.SubType tokenSubType = token.getSubType();
    	switch(tokenSubType) {
	    	case TEXT:
	    		String text = token.value;
	    		return node.getTextContent().equals(text);
	    	case CONTAINS:
	    		String substring = token.value;
	    		String s = node.getTextContent();
	    		boolean b = node.getTextContent().contains(substring);
	    		return b;
	    	case ATTRIBUTE:
	    		String attributeName = token.getAttName();
	    		String attributeValue = token.getAttValue();
	    		NamedNodeMap attributes = node.getAttributes();
	    		if (attributes.getNamedItem(attributeName) != null){
	    			Node attributeNode = attributes.getNamedItem(attributeName);
	    			String nodeValue = attributeNode.getNodeValue();
	    			return nodeValue.equals(attributeValue);
	    		}
	    		return false;
	    	case INNERAXIS:
				tree = getNameChild(tree);
	    		//if the document has run out of node before the xpath is satisfied, return false
				NodeList nodeChildren = node.getChildNodes();	
				if (nodeChildren.getLength() <= 0)
					return false;
				//otherwise, try the remaining xpath on all children of the current document node, return true if any pass
				for (int i = 0; i < nodeChildren.getLength(); i++){
					Node nodeChild = nodeChildren.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
			    		boolean result = evaluateSingleXPath(nodeChild, tree);
						if (result)
							return true;
					}
				}	
	    		return false;
	    	default:
	    		throw new RuntimeException("A Test node did not match any subTypes.");
    	}
    }

    
    private static ArrayList<Tree<Token>> getTestChildren(Tree<Token> tree) {
    	ArrayList<Tree<Token>> testChildren = new ArrayList<Tree<Token>>();
    	ArrayList<Tree<Token>> children = tree.getChildren();
    	for (Tree<Token> child : children) {
    		if (child.getValue().type == Token.Type.TEST)
    			testChildren.add(child);
    	}
    	return testChildren;
    }
    
    private static Tree<Token> getNameChild(Tree<Token> tree) {
    	ArrayList<Tree<Token>> children = tree.getChildren();
    	for (Tree<Token> child : children) {
    		if (child.getValue().type == Token.Type.NAME)
    			return child;
    	}
    	return null;
    }
    
	@Override
	public boolean isSAX() {
		return false;
	}
	
	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		return null;
	}
        
}

