package com.ignatieff.expression;

public class Implication extends BinaryExpression {

	public Implication(Expression a, Expression b){
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean evaluate(Table t) {
		boolean p = a.evaluate(t);
		boolean q = b.evaluate(t);
		if(p && !q) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "IMPLIES("+a.toString() + ", " + b.toString()+")";
	}
	
}
