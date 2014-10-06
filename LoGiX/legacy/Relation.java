package com.ignatieff.proofs;

public class Relation {
	public String a,b;
	
	public Relation(String relA, String relB){
		a=relA;
		b=relB;
	}
	
	public boolean equals(Relation r){
		return this.a.equalsIgnoreCase(r.a) && this.b.equalsIgnoreCase(r.b);
	}
}