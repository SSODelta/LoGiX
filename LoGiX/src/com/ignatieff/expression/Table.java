package com.ignatieff.expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Table {
	private Map<Integer, Boolean> tableMap;
	private Map<Integer, String> nameMap;
	
	public Table(){
		tableMap = new HashMap<Integer, Boolean>();
		nameMap = new HashMap<Integer, String>();
	}
	
	public void addEntry(int hashCode){
		tableMap.put(hashCode, false);
	}
	public void addEntry(String s){
		nameMap.put(s.hashCode(),s);
		addEntry(s.hashCode());
	}
	
	public void addEntry(int hashCode, boolean k){
		tableMap.put(hashCode, k);
	}
	
	public void addEntry(String s, boolean k){
		nameMap.put(s.hashCode(),s);
		addEntry(s.hashCode(),k);
	}
	
	private Table getTable(boolean[] values){
		Integer[] keys = getKeys();
		
		if(keys.length != values.length) return null;
		
		Table r = new Table();
		r.nameMap = nameMap;
		
		for(int i=0; i<keys.length; i++)
			r.addEntry(keys[i], values[i]);
		
		return r;
	}
	
	private static String pad(String k, int l){
		while(k.length()<l)
			k="0"+k;
		return k;
	}
	
	private static boolean[] getBool(int num, int length){
		boolean[] booleanArray = new boolean[length];
		String s = pad(Integer.toBinaryString(num), length);
		char[] string = s.toCharArray();
		
		for(int i=0; i<string.length; i++){
			if(string[i] == '1') booleanArray[i] = true;
		}
		
		return booleanArray;
	}
	
	public boolean getEntry(int key){
		return tableMap.get(key);
	}

	private Integer[] getKeys(){
		Set<Integer> keySet = tableMap.keySet();
		return keySet.toArray(new Integer[keySet.size()]);
	}
	
	public Table[] getTables(){
		Integer[] keys = getKeys();
		
		int m = (int)Math.pow(2, keys.length);
		
		Table[] tables = new Table[m];
		
		for(int i=0; i<m; i++){
			tables[i] = getTable(getBool(i,keys.length));
		}
		return tables;
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append('[');
		
		Integer[] keys = getKeys();
		
		for(int i=0; i<keys.length; i++){
			s.append('{');
			
			if(nameMap.containsKey(keys[i])){
				s.append(nameMap.get(keys[i]));
			} else { s.append(keys[i]); }
			
			s.append(':');
			s.append(tableMap.get(keys[i]));
			s.append('}');
			if(i!=keys.length-1)s.append(',');
		}

		s.append(']');
		return s.toString();
	}
	
	
}
