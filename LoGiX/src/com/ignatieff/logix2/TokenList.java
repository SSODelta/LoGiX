package com.ignatieff.logix2;

import java.util.ArrayList;
import java.util.Stack;

import com.ignatieff.expression.AND;
import com.ignatieff.expression.Biimplication;
import com.ignatieff.expression.Constant;
import com.ignatieff.expression.Expression;
import com.ignatieff.expression.Implication;
import com.ignatieff.expression.NAND;
import com.ignatieff.expression.NOR;
import com.ignatieff.expression.NOT;
import com.ignatieff.expression.OR;
import com.ignatieff.expression.Tautology;
import com.ignatieff.expression.XOR;
import com.ignatieff.logix2.Token;

public class TokenList {
	
	private Token[] tokens;
	private boolean RPN = false;
	private String label;
	
	/**
	 * Instantiates a new TokenList from a String.
	 * The string will get Tokenized, which means splitting up the String into components.
	 * For example, tokenizing "A + (B * C)" gives 'A, +, (, B, *, C, )'.
	 * @param k The cleaned input String to tokenize.
	 */
	public TokenList(String k){
		this.label = k;
		tokens = getTokens(k);
	}
	
	/**
	 * Converts this TokenList into Reverse Polish Notation.
	 */
	public void convertToRPN(){
		RPN = true;
		tokens = getReversePolishNotation(tokens);
	}
	
	/**
	 * Converts a Token array into Reverse Polish Notation rather than the standard infix notation.
	 * Uses the Shunting-Yard algorithm.
	 * @param t The Token array to convert.
	 * @return The input array in Reverse Polish Notation.
	 */
	public static Token[] getReversePolishNotation(Token[] t){
		ArrayList<Token> tokenList = new ArrayList<Token>();
		Stack<Token> stack = new Stack<Token>();
		
		for(int i=0; i<t.length; i++){
			
			if(t[i].TYPE == Token.TYPE_CONSTANT){
				tokenList.add(t[i]);
			}
			
			if(t[i].isOperator()){
				while(!stack.empty() && stack.peek().isOperator()){
					if(t[i].isLessThanOrEqual(stack.peek())){
						tokenList.add(stack.pop());
					} else {
						break;
					}
				}
				stack.push(t[i]);
			}
			
			if(t[i].TYPE == Token.TYPE_LEFT_PARANTHESIS){
				stack.push(t[i]);
			}
			
			if(t[i].TYPE == Token.TYPE_RIGHT_PARANTHESIS){
				while(stack.peek().TYPE != Token.TYPE_LEFT_PARANTHESIS){
					tokenList.add(stack.pop());
				}
				stack.pop();
			}
		}
		
		while(!stack.empty())
			tokenList.add(stack.pop());
		
		return tokenList.toArray(new Token[tokenList.size()]);
	}
	
	/**
	 * Determines the position of the first occurrence of a specific Token.
	 * @param t The Token to find.
	 * @return An integer representing the position of the specified Token. 0 is the first position, and -1 means it doesn't exist.
	 */
	public int indexOf(Token t){
		for(int i=0; i<tokens.length; i++){
			Token k = tokens[i];
			if(k.DATA.equalsIgnoreCase(t.DATA) && k.TYPE == t.TYPE) return i;
		}
		return -1;
	}
	
	/**
	 * Determines whether or not this TokenList contains some specific Token.
	 * @param t The Token to find.
	 * @return Returns true if some element of this TokenList has the same TYPE and DATA as the input Token.
	 */
	public boolean contains(Token t){
		for(Token k : tokens){
			if(k.DATA.equalsIgnoreCase(t.DATA) && k.TYPE == t.TYPE) return true;
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		
		for(int i=0; i<tokens.length; i++){
			s.append(tokens[i].DATA);
			if(i!=tokens.length-1)s.append(" ");
		}
		
		return s.toString();
	}
	
	/**
	 * Returns the token at position i.
	 * @param i The position to get the Token from.
	 * @return The token at position i.
	 */
	public Token get(int i){
		return tokens[i];
	}
	
	/**
	 * Returns this TokenList as a recursive expression tree.
	 * Parses the tokens in this TokenList interpreted as Reverse Polish Notation.
	 * Will automatically convert this TokenList to Reverse Polish Notation if not already.
	 * @return An Expression representing this TokenList as a recursive expression tree.
	 * @throws LogixException Will return a LogixException if the input string was badly formatted.
	 */
	public Expression getExpression() throws LogixException{
		if(!RPN)convertToRPN();
		
		Stack<Expression> stack = new Stack<Expression>();
		
		for(int i=0; i<tokens.length; i++){
			if(tokens[i].TYPE == Token.TYPE_CONSTANT){
				if(tokens[i].DATA.equalsIgnoreCase("false")){
					stack.push(new Tautology(false));
					continue;
				} else if (tokens[i].DATA.equalsIgnoreCase("true")){
					stack.push(new Tautology(true));
					continue;
				}
				stack.push(new Constant(tokens[i].DATA));
				continue;
			}
			if(tokens[i].TYPE == Token.TYPE_UNARY){
				Expression k = new NOT(stack.pop());
				stack.push(k);
				continue;
			}
			Expression q = stack.pop();
			Expression p = stack.pop();
			
			if(tokens[i].isData("and")){
				Expression k = new AND(p,q);
				stack.push(k);
				continue;
			}

			if(tokens[i].DATA.equalsIgnoreCase("iff")){
				Expression k = new Biimplication(p,q);
				stack.push(k);
				continue;
			}

			if(tokens[i].DATA.equalsIgnoreCase("implies")){
				Expression k = new Implication(p,q);
				stack.push(k);
				continue;
			}
			
			if(tokens[i].DATA.equalsIgnoreCase("nand")){
				Expression k = new NAND(p,q);
				stack.push(k);
				continue;
			}
			
			if(tokens[i].DATA.equalsIgnoreCase("nor")){
				Expression k = new NOR(p,q);
				stack.push(k);
				continue;
			}
			
			if(tokens[i].DATA.equalsIgnoreCase("or")){
				Expression k = new OR(p,q);
				stack.push(k);
				continue;
			}

			if(tokens[i].DATA.equalsIgnoreCase("xor")){
				Expression k = new XOR(p,q);
				stack.push(k);
				continue;
			}
		}
		if(stack.size()!=1){
			throw new LogixException("Can't convert to recursive expression tree. Invalid expression '"+label+"'.");
		}
		return stack.pop();
	}
	
	/**
	 * Tokenizes a String s, which means splitting up the String into components.
	 * For example, tokenizing "A + (B * C)" gives 'A, +, (, B, *, C, )'.
	 * @param s The String to tokenize.
	 * @return An array of Token-objects which represents the input String.
	 */
	public static Token[] getTokens(String s){
		ArrayList<Token> tokens = new ArrayList<Token>();
		String token = "";
		
		char[] chars = s.toCharArray();
		
		for(int i=0; i<chars.length; i++){
			if(chars[i]==' '){
				if(token.length()==0)continue;
				tokens.add(new Token(token));
				token = "";
				continue;
			}
			if(chars[i]=='0' && token.length()==0){
				tokens.add(new Token("false"));
				continue;
			}
			if(chars[i]=='1' && token.length()==0){
				tokens.add(new Token("true"));
				continue;
			}
			if(chars[i]=='('){
				if(token.length()>0){
					tokens.add(new Token(token));
				}
				tokens.add(new Token("("));
				token = "";
				continue;
			}
			if(chars[i]==','){
				if(token.length()>0){
					tokens.add(new Token(token));
				}
				tokens.add(new Token(","));
				token = "";
				continue;
			}
			if(chars[i]==')'){
				if(token.length()>0){
					tokens.add(new Token(token));
				}
				tokens.add(new Token(")"));
				token = "";
				continue;
			}
			token += chars[i];
		}
		if(token.length() != 0)
			tokens.add(new Token(token));
		return tokens.toArray(new Token[tokens.size()]);
	}
	
	public Token[] getTokens(){
		return tokens;
	}
	
	/**
	 * Converts a String to a recursive expression tree.
	 * @param s The string to convert
	 * @return An Expression representing the String.
	 * @throws LogixException
	 */
	public static Expression convertToExpression(String s) throws LogixException{
		TokenList tl = new TokenList(s);
		return tl.getExpression();
	}
	
}
