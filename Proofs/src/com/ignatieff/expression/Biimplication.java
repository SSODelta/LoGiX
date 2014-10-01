package com.ignatieff.expression;

import com.ignatieff.proofs.Table;

public class Biimplication extends BinaryExpression {
	public Biimplication(Expression a, Expression b){
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean evaluate(Table t) {
		boolean p = a.evaluate(t);
		boolean q = b.evaluate(t);
		return (p==q);
	}
	
	@Override
	public String toString() {
		return "IFF("+a.toString() + ", " + b.toString()+")";
	}
	
}
