package edu.upenn.cis455.xpathengine;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Stack;

import edu.upenn.cis455.xpathengine.Token.Type;

public class Parser {

    /** The tokenizer used by this Parser. */
    StreamTokenizer tokenizer = null;

    /**
     * The stack used for holding Trees as they are created.
     */
    public Stack<Tree<Token>> stack = new Stack<>();
    public boolean inQuotes;
    
    public Parser(String text) {
        Reader reader = new StringReader(text);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.parseNumbers();
        tokenizer.eolIsSignificant(true);
        tokenizer.slashStarComments(true);
        tokenizer.slashSlashComments(true);
        tokenizer.lowerCaseMode(false);
        tokenizer.ordinaryChars(33, 47);
        tokenizer.ordinaryChars(58, 64);
        tokenizer.ordinaryChars(91, 96);
        tokenizer.ordinaryChars(123, 126);
        tokenizer.wordChars(' ', ' ');
        inQuotes = false;
    }
    
    public Tree<Token> parseXPath() throws IllegalStateException {
    	if (!isAxis(0))
    		throw new IllegalStateException("Invalid syntax");
    	return stack.pop();	
    }
    
    public boolean isAxis(int count) throws IllegalStateException {
    	if (nextTokenMatches(Token.Type.SYMBOL, "/")) {
    		stack.pop();
    		if (!isStep())
    			return false;
    		isAxis(count + 1);
    		if (count > 0)
    			makeTree(2, 1);
    		return true;
    	}
    	return false;
    }
    
    /**
     * The only difference is that innerAxis allows for optional starting "/" character.
     * @param count
     * @return
     * @throws IllegalStateException
     */
    public boolean isInnerAxis(int count) throws IllegalStateException {
    	if (nextTokenMatches(Token.Type.SYMBOL, "/"))
    		stack.pop();
    	if (!isStep())
    		return false;
    	isAxis(count + 1);
    	if (count > 0)
    		makeTree(2, 1);
    	return true;
    }
    
    public boolean isStep() throws IllegalStateException {
    	if (nextTokenMatches(Token.Type.NAME)) {
    		while (isTest())
    			makeTree(2, 1);
    		return true;
    	}
    	return false;
    }
    
    public boolean isTest() throws IllegalStateException {
    	boolean innerAxis = false;
    	if (nextTokenMatches(Token.Type.SYMBOL, "[")) {
    		if(!isText())
    			if(!isAttribute())
    				if(!isContains())
    					if(isInnerAxis(0))
    						innerAxis = true;
    					else
    						throw new IllegalStateException("Invalid syntax");
    		if (!nextTokenMatches(Token.Type.SYMBOL, "]")) 
    			throw new IllegalStateException("Invalid syntax");
	    	stack.pop();
	    	Tree<Token> savedTestNode = stack.pop();
	    	stack.pop();
	    	if (innerAxis) {
	    		ProcessedToken token = new ProcessedToken(Token.Type.TEST, "");
	    		token.setSubType(ProcessedToken.SubType.INNERAXIS);
	    		Tree<Token> wrapperNode = new Tree<Token>(token);
	    		wrapperNode.addChild(savedTestNode);
	    		stack.push(wrapperNode);
	    	}
	    	else {
	    		stack.push(savedTestNode);
	    	}
    		return true;
    	}
    	return false;
    }
    
    public boolean isText() throws IllegalStateException {
    	if (nextTokenMatches(Token.Type.KEYWORD, "text")) {
    		if (!(nextTokenMatches(Token.Type.SYMBOL, "(")))
    			throw new IllegalStateException("Invalid syntax");
    		if (!(nextTokenMatches(Token.Type.SYMBOL, ")")))
    			throw new IllegalStateException("Invalid syntax");
    		if (!(nextTokenMatches(Token.Type.SYMBOL, "=")))
    			throw new IllegalStateException("Invalid syntax");
    		String fullName = parseQuoteExpression();
    		for (int i = 0; i < 4; i++) {
    			stack.pop();
    		}
    		ProcessedToken token = new ProcessedToken(Token.Type.TEST, fullName);
    		token.setSubType(ProcessedToken.SubType.TEXT);
    		Tree<Token> newNode = new Tree<Token>(token);
    		stack.push(newNode);
    		return true;
    	}
    	return false;
    }
    
    public boolean isAttribute() throws IllegalStateException {
    	//checking general syntax of attribute
    	if (nextTokenMatches(Token.Type.SYMBOL, "@")) {
    		if (!nextTokenMatches(Token.Type.NAME))
    			throw new IllegalStateException("Invalid syntax");
    		if (!nextTokenMatches(Token.Type.SYMBOL, "="))
    			throw new IllegalStateException("Invalid syntax");
    		String fullName = parseQuoteExpression();
    		//build a new node from the obtained information
    		stack.pop();
    		String name = stack.pop().getValue().value;
    		stack.pop();
    		ProcessedToken token = new ProcessedToken(ProcessedToken.Type.TEST, "");
    		token.setSubType(ProcessedToken.SubType.ATTRIBUTE);
    		token.setAttName(name);
    		token.setAttValue(fullName);
    		Tree<Token> newNode = new Tree<Token>(token);
    		stack.push(newNode);
    		return true;
    	}
    	return false;
    }
    
	/**
	 * Attempts to build a string of NAME Tokens that may include inner quotes.
	 * @return fullName - Parsed string.
	 */
    private String parseQuoteExpression() {
    	int count = 0;
		String fullName = "";
		while (nextTokenMatches(Token.Type.NAME) || nextTokenMatches(Token.Type.QUOTE)) {
			//settings inQuotes tells the tokenizer to create tokens using the constructor that does not remove whitespace
			inQuotes = true;
			count++;
			fullName += stack.pop().getValue().value;
		}
		//check that the word includes at least one item in quotes and that the quotes are properly opened and closed
		inQuotes = false;
		if (count < 3 || !(fullName.charAt(fullName.length() - 1) == '\"') || !(fullName.charAt(0) == '\"'))
			throw new IllegalStateException("Invalid syntax");
		//remove quotes from beginning and end of word
		fullName = fullName.substring(0, fullName.length() - 1).substring(1);
		return fullName;
    }
    
    public boolean isContains() throws IllegalStateException {
    	if (nextTokenMatches(Token.Type.KEYWORD, "contains")) {
    		if (!nextTokenMatches(Token.Type.SYMBOL, "("))
    			throw new IllegalStateException("Invalid syntax");
    		if (!nextTokenMatches(Token.Type.KEYWORD, "text"))
    			throw new IllegalStateException("Invalid syntax");
    		if (!(nextTokenMatches(Token.Type.SYMBOL, "(")))
    			throw new IllegalStateException("Invalid syntax");
    		if (!(nextTokenMatches(Token.Type.SYMBOL, ")")))
    			throw new IllegalStateException("Invalid syntax");
    		if (!(nextTokenMatches(Token.Type.SYMBOL, ",")))
    			throw new IllegalStateException("Invalid syntax");
    		String fullName = parseQuoteExpression();
    		if (!(nextTokenMatches(Token.Type.SYMBOL, ")")))
    			throw new IllegalStateException("Invalid syntax");
    		for (int i = 0; i < 7; i++) {
    			stack.pop();
    		}
    		ProcessedToken token = new ProcessedToken(Token.Type.TEST, fullName);
    		token.setSubType(ProcessedToken.SubType.CONTAINS);
    		Tree<Token> newNode = new Tree<Token>(token);
    		stack.push(newNode);
    		return true;
    	}
    	return false;
    }
    
    
    private boolean nextTokenMatches(Token.Type type) {
        Token t = nextToken();
        if (t.type == type) {
            stack.push(new Tree<>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    private boolean nextTokenMatches(Token.Type type, String value) {
        Token t = nextToken();
        if (type == t.type && value.equals(t.value)) {
            stack.push(new Tree<>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    /**
     * Returns the next Token.
     * @return The next Token.
     */
    private Token nextToken() {
        int code;
        try { 
        	code = tokenizer.nextToken(); 
        	}
        catch (IOException e) { 
        	throw new Error(e); 
        }
        switch (code) {
            case StreamTokenizer.TT_WORD:
            	if (inQuotes)
            		return new Token(tokenizer.sval, true);
            	else
            		return new Token(tokenizer.sval);
            case StreamTokenizer.TT_EOF:
                return new Token(Token.Type.EOF, "EOF");
            default:
            	if (inQuotes)
            		return new Token(((char) code) + "", true);
            	else
            		return new Token(((char) code) + "");
        }
    }

    private void makeTree(int rootIndex, int... childIndices) {
        Tree<Token> root = getStackItem(rootIndex);
        for (int i = 0; i < childIndices.length; i++) {
            root.addChild(getStackItem(childIndices[i]));
        }
        for (int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        stack.push(root);
    }
    
    private Tree<Token> getStackItem(int n) {
        return stack.get(stack.size() - n);
    }
}

