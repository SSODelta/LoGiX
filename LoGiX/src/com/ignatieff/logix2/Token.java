package com.ignatieff.logix2;

public class Token {
	public String DATA;
	public int TYPE;
	
	public static int TYPE_CONSTANT = 0; 
	public static int TYPE_UNARY    = 1; //not
	public static int TYPE_BINARY_1 = 2; //and, nand
	public static int TYPE_BINARY_2 = 3; //or, nor, xor
	public static int TYPE_BINARY_3 = 4; //implication
	public static int TYPE_BINARY_4 = 5; //bi-implication
	public static int TYPE_LEFT_PARANTHESIS = 6;
	public static int TYPE_RIGHT_PARANTHESIS = 7;
	public static int TYPE_OTHER = 8;
	
	/**
	 * Converts a String representation of a Token into a Token object.
	 * This means assigning priority and data for the given input string.
	 * @param k A String representing a single Token.
	 */
	public Token(String k){
		DATA = k;
		
		if(k.equalsIgnoreCase("not")){
			TYPE = TYPE_UNARY;
		} else if (k.equalsIgnoreCase("and") || k.equalsIgnoreCase("nand")){
			TYPE = TYPE_BINARY_1;
		} else if (k.equalsIgnoreCase("or") || k.equalsIgnoreCase("nor") || k.equalsIgnoreCase("xor")){
			TYPE = TYPE_BINARY_2;
		} else if (k.equalsIgnoreCase("implies") || k.equalsIgnoreCase("then")){
			DATA = "implies";
			TYPE = TYPE_BINARY_3;
		} else if (k.equalsIgnoreCase("bi-implies") || k.equalsIgnoreCase("biimplies") || k.equalsIgnoreCase("iff")){
			DATA = "iff";
			TYPE = TYPE_BINARY_4;
		} else if (k.equalsIgnoreCase("(") || k.equalsIgnoreCase("{")){
			TYPE = TYPE_LEFT_PARANTHESIS;
		} else if (k.equalsIgnoreCase(")") || k.equalsIgnoreCase("}")){
			TYPE = TYPE_RIGHT_PARANTHESIS;
		}
	}
	
	/**
	 * Determines whether or not this Token is an operator.
	 * @return Returns true if this Token is an operator, and false otherwise.
	 */
	public boolean isOperator(){
		return (TYPE >= 1 && TYPE <= 5);
	}
	
	/**
	 * Determines whether or not this Token has lower priority than another Token object.
	 * @param t The Token to compare to.
	 * @return Returns true if this Token object has lower or equal priority as the argument.
	 */
	public boolean isLessThanOrEqual(Token t){
		if(t.TYPE == Token.TYPE_LEFT_PARANTHESIS)return true;
		return TYPE >= t.TYPE;
	}
	
	/**
	 * Determines whether or not this Token's data is equal to some string (case-insensitive).
	 * @param s The data to check if this Token contains
	 * @return The data contained within this Token
	 */
	public boolean isData(String s){
		return s.equalsIgnoreCase(DATA);
	}
	
	@Override
	public String toString(){
		return "[" + DATA + ", " + TYPE + "]";
	}
}
