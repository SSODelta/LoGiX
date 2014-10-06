package com.ignatieff.proofs;

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
	
	public boolean isOperator(){
		return (TYPE >= 1 && TYPE <= 5);
	}
	
	public boolean isLessThanOrEqual(Token t){
		if(t.TYPE == Token.TYPE_LEFT_PARANTHESIS)return true;
		return TYPE >= t.TYPE;
	}
	
	@Override
	public String toString(){
		return "[" + DATA + ", " + TYPE + "]";
	}
}
