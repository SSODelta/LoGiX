package com.ignatieff.expression;

import com.ignatieff.proofs.Table;

public class Constant extends Expression {

	private int ID;
	
	public Constant(int ID){
		this.ID = ID;
	}
	
	public Constant(Object o){
		this.ID = o.hashCode();
	}
	
	@Override
	public boolean evaluate(Table t) {
		return t.getEntry(ID);
	}
	
	@Override
	public String toString() {
		return ""+ID;
	}

}
