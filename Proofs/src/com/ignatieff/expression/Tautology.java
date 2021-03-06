package com.ignatieff.expression;

import com.ignatieff.proofs.Table;

public class Tautology extends Expression {

	boolean value;
	
	public Tautology(boolean val){
		value = val;
	}
	
	public Tautology(){
		this(true);
	}

	@Override
	public boolean evaluate(Table t) {
		return value;
	}

	@Override
	public String toString() {
		return ""+value;
	}
	
}
