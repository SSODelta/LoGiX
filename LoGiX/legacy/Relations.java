package com.ignatieff.proofs;

import java.util.ArrayList;

public class Relations {
	
	ArrayList<Relation> relations;
	ArrayList<String> strings;
	
	public Relations(){
		relations = new ArrayList<Relation>();
		strings = new ArrayList<String>();
	}
	
	public void addRelation(Relation r){
		relations.add(r);
		if(!strings.contains(r.a.toLowerCase()))strings.add(r.a.toLowerCase());
		if(!strings.contains(r.b.toLowerCase()))strings.add(r.b.toLowerCase());
	}
	
	public ArrayList<String> getNeighbors(String s){
		ArrayList<String> neighbors = new ArrayList<String>();
		for(Relation r: relations){
			if(r.a.equalsIgnoreCase(s) && !neighbors.contains(r.b)){
				neighbors.add(r.b);
			}
			if(r.b.equalsIgnoreCase(s) && !neighbors.contains(r.a)){
				neighbors.add(r.a);
			}
		}
		return neighbors;
	}
	
	public ArrayList<String> getNotNeighbors(String s){
		ArrayList<String> notNeighbors = new ArrayList<String>();
		for(Relation r: relations){
			if(!r.a.equalsIgnoreCase(s) && !notNeighbors.contains(r.b)){
				notNeighbors.add(r.b);
			}
			if(!r.b.equalsIgnoreCase(s) && !notNeighbors.contains(r.a)){
				notNeighbors.add(r.a);
			}
		}
		return notNeighbors;
	}
	
	/*public String neighbors(String s){
		ArrayList<String> neighbors = getNeighbors(s);
		ArrayList<String> notNeighbors = getNotNeighbors(s);
	}*/
	
	public boolean contains(Relation r){
		for(Relation q : relations){
			if(q.equals(r))
				return true;
		}
		return false;
	}
		
}
