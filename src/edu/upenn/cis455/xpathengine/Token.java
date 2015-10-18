package edu.upenn.cis455.xpathengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class Token.
 */
public class Token {
    static enum Type { KEYWORD, NAME, SYMBOL, QUOTE, ERROR, EOF, STEP, TEST }
    private static final Pattern NAME_REGEX = Pattern.compile("([a-zA-Z_]+|\\w+|\\s+|\\t+)+");
    private static final Pattern QUOTE_REGEX = Pattern.compile("\"");
    private static final Pattern SYMBOL_REGEX = Pattern.compile("(\\(|\\)|\\[|\\]|=|@|/|,){1}");
    public static Set<String> KEYWORDS = new HashSet<String>();
   
    final Type type;
    final String value;

    /**
     * Constructor. Takes only a value and determines the type then calls other constructor.
     * @param value - String value.
     */
    public Token(String value) {
        this(typeOf(value), removeAllWhitespace(value));
    }
    
    public Token(String value, boolean protectQuotes) {
    	this(typeOf(value), value);
    }
    
    /**
     * Constructor. Takes the Token.Type and a value. Adds keywords to the set. 
     * @param type - Token.Type of this token.
     * @param value - String value.
     */
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
        KEYWORDS.add("text");
        KEYWORDS.add("contains");
    }

    /**
     * Finds the token type of a String.
     * @param next The next String to be evaluated.
     * @return The type of the string.
     */
    public static Token.Type typeOf(String next) {
        if (next == null) 
        	return Token.Type.EOF;
        if (SYMBOL_REGEX.matcher(next).matches()) 
        	return Token.Type.SYMBOL;
        if (QUOTE_REGEX.matcher(next).matches())
        	return Token.Type.QUOTE;
        if (NAME_REGEX.matcher(next).matches()) {
            if (Token.KEYWORDS.contains(removeAllWhitespace(next))) 
            	return Token.Type.KEYWORD;
            return Token.Type.NAME;
        }
        return Token.Type.ERROR;
    }
    
    /**
     * Removes all the whitespace from a token (NAME or String).
     * @param potentialKeyword
     * @return
     */
    private static String removeAllWhitespace(String potentialKeyword) {
    	return potentialKeyword.replaceAll("\\s","");
    }

    /**
     * Override of equals. Compares two Tokens by Type and value.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Token) {
            Token that = (Token) o;
            return this.type == that.type && this.value.equals(that.value);
        }
        return false;
    }

    /**
     * Overrride of hashcode. Simple returns the hashcode of the value contained in this Token.
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Override of toString().
     */
    @Override
    public String toString() {
        return type + ":" + value;
    }

}

