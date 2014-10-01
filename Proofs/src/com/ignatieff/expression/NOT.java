package com.ignatieff.expression;

import com.ignatieff.proofs.Table;

public class NOT extends Expression {

	Expression a;
	
	public NOT(Expression a){
		this.a=a;
	}
	
	@Override
	public boolean evaluate(Table t) {
		return !a.evaluate(t);
	}
	
	@Override
	public String toString() {
		return "NOT " + a.toString();
	}
}
