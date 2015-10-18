package edu.upenn.cis455.xpathengine;

import edu.upenn.cis455.xpathengine.Token.Type;

/**
 * This is a subclass of Token. It is basically used to add additional 
 * information to Token structure when storing TEST constraints.
 */
public class ProcessedToken extends Token {
	static enum SubType { ATTRIBUTE, TEXT, CONTAINS, INNERAXIS }
	private SubType subType;
	private String info;
	private String attributeName;
	private String attributeValue;
	
	/**
	 * Calls superclass constructor to assign a type (will be TEST when this is used)
	 * and a value when applicable.
	 * @param type - Token.Type
	 * @param value - String value
	 */
	public ProcessedToken(Token.Type type, String value) {
		super(type, value);
	}
	
    public void setSubType(SubType subType) {
    	this.subType = subType;
    }
    public SubType getSubType() {
    	return subType;
    }
    public void setAttName(String attName) {
    	attributeName = attName;
    }
    public String getAttName() {
    	return attributeName;
    }
    public void setAttValue(String attValue) {
    	attributeValue = attValue;
    }
    public String getAttValue() {
    	return attributeValue;
    }

}