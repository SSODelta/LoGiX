package com.ignatieff.expression;

public class NOR extends BinaryExpression {
	public NOR(Expression a, Expression b){
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean evaluate(Table t) {
		boolean p = a.evaluate(t);
		boolean q = b.evaluate(t);
		
		return !(p | q);
	}
	
	@Override
	public String toString() {
		return "NOR("+a.toString() + ", " + b.toString()+")";
	}
}
