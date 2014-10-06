package com.ignatieff.proofs;

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

public class TokenList {

	ArrayList<Token> tokenList;
	
	public TokenList(Token[] tokens){
		tokenList = new ArrayList<Token>();
		Stack<Token> stack = new Stack<Token>();
		
		for(int i=0; i<tokens.length; i++){
			
			if(tokens[i].TYPE == Token.TYPE_CONSTANT){
				tokenList.add(tokens[i]);
			}
			
			if(tokens[i].isOperator()){
				while(!stack.empty() && stack.peek().isOperator()){
					if(tokens[i].isLessThanOrEqual(stack.peek())){
						tokenList.add(stack.pop());
					} else {
						break;
					}
				}
				stack.push(tokens[i]);
			}
			
			if(tokens[i].TYPE == Token.TYPE_LEFT_PARANTHESIS){
				stack.push(tokens[i]);
			}
			
			if(tokens[i].TYPE == Token.TYPE_RIGHT_PARANTHESIS){
				while(stack.peek().TYPE != Token.TYPE_LEFT_PARANTHESIS){
					tokenList.add(stack.pop());
				}
				stack.pop();
			}
		}
		
		while(!stack.empty())
			tokenList.add(stack.pop());
	}
	
	public Token[] getTokens(){
		return tokenList.toArray(new Token[tokenList.size()]);
	}
	
	public boolean containsToken(Token k){
		for(Token t : tokenList){
			if(t.TYPE==k.TYPE && t.DATA.equalsIgnoreCase(k.DATA));
				return true;
		}
		return false;
	}
	
	public TokenList(String s){
		this(getTokens(s));
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		
		for(int i=0; i<tokenList.size(); i++){
			s.append(tokenList.get(i).DATA);
			if(i!=tokenList.size()-1)s.append(" ");
		}
		
		return s.toString();
	}
	
	public Expression getExpression(){
		Token[] tokens = getTokens();
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
			
			if(tokens[i].DATA.equalsIgnoreCase("and")){
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
			System.out.println("Error - Unable to parse expression:\n   "+toString());
			return null;
		}
		return stack.pop();
	}

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
				tokens.add(new Token("("));
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
	
}
