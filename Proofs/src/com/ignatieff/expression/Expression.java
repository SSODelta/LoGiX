package com.ignatieff.expression;

import com.ignatieff.proofs.Table;

public abstract class Expression {
	
	public abstract boolean evaluate(Table t);

	public abstract String toString();
}
