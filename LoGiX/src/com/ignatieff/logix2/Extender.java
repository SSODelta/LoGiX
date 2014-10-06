package com.ignatieff.logix2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Extender {
	
	/**
	 * Extend shorthand expressions into primitive types (expands 'maximum', 'minimum' etc.)
	 * @param string
	 * @return
	 */
	public static String extend(String string){
		String s = string;
		
			while(s.contains("minimum")){
			
			int min = s.indexOf("minimum");
			int del = 0;
			
			String prefix = s.substring(0,min);
			String after = s.substring(min+8);
			after = after.replace(", ", ",");
			
			for(int i=5; i<after.length(); i++){
				if(after.charAt(i)==' ' || after.charAt(i)==')' || after.charAt(i)=='}'){
					del=i;
					break;
				}
				del = after.length();
			}
			
			String arg = extendMinimum(s.substring(min+8,min+8+del));
			if(arg.toLowerCase().contains("error"))
				return arg;
			String suffix = s.substring(min+8+del);
			
			s = prefix + arg + suffix;
			
		}
		
		while(s.contains("maximum")){
			
			int min = s.indexOf("maximum");
			int del = 0;
			
			String prefix = s.substring(0,min);
			String after = s.substring(min+8);
			after = after.replace(", ", ",");
			
			for(int i=5; i<after.length(); i++){
				if(after.charAt(i)==' ' || after.charAt(i)==')' || after.charAt(i)=='}'){
					del=i;
					break;
				}
				del = after.length();
			}
			
			String arg = extendMaximum(s.substring(min+8,min+8+del));
			if(arg.toLowerCase().contains("error"))
				return arg;
			String suffix = s.substring(min+8+del);
			
			s = prefix + arg + suffix;
			
		}
		
		while(s.contains("exactly")){
			
			int min = s.indexOf("exactly");
			int del = 0;
			
			String prefix = s.substring(0,min);
			String after = s.substring(min+8);
			after = after.replace(", ", ",");
			
			for(int i=5; i<after.length(); i++){
				if(after.charAt(i)==' ' || after.charAt(i)==')' || after.charAt(i)=='}'){
					del=i;
					break;
				}
				del = after.length();
			}
			
			String arg = extendExactly(s.substring(min+8,min+8+del));
			if(arg.toLowerCase().contains("error"))
				return arg;
			String suffix = s.substring(min+8+del);
			s = prefix + arg + suffix;
			
		}
		
		s = extendIFs(s);
		
		return s;
	}

	
	public static String extendIFs(String arg){
		String s = arg;
		
		while(s.startsWith("if ") && s.indexOf("then")>-1){
			//Preserve CaPiTaL LeTtErS
			String r = s.substring(3);
			
			//Split into two sides
			String[] l = r.split("then");
			
			//Attach again, but with a different position of words (i.e. "If A then B" is the same as "A implies B").
			return l[0] + "implies" + l[1];
		}
		while(s.startsWith("iff ") && s.indexOf("then")>-1){
			//Preserve CaPiTaL LeTtErS
			String r = s.substring(3);
			
			//Split into two sides
			String[] l = r.split("then");
			
			//Attach again, but with a different position of words (i.e. "If A then B" is the same as "A implies B").
			return l[0] + "biimplies" + l[1];
		}
		
		return s.replace("then", "implies");
	}
	
	/**
	 * Extends the minimum shorthand expression
	 * @param arg The argument of the command
	 * @return An extended string
	 */
	public static String extendMinimum(String arg){
		if(arg.length()==0){
			return "Error - Expected number and expression(s) after 'minimum'.";
		}
		if(!arg.contains(" ")){
			return "Error - Expected at least two arguments.";
		}
		
		String num = "null";
		
		try{
			num = arg.substring(0,arg.indexOf(' '));
			int k = Integer.parseInt(num);
			String list = arg.substring(arg.indexOf(' ')+4);
			
			int max = list.split(",").length;

			if(k>max || k<0)
				return "Error - Expected integer in range [0,"+max+"], but received '"+k+"'.";
			
			StringBuilder string = new StringBuilder();
			string.append("(");
			
			for(int i=k; i<max+1; i++){
				string.append("(exactly " + i + " of " + list+")");
				if(i!=max)string.append(" or ");
			}
			
			string.append(")");
			
			return string.toString();
		} catch(NumberFormatException e){
			return "Error - Expected integer, but received '"+num+"'.";
		}
	}
	
	/**
	 * Extends the 'maximum' shorthand expression.
	 * @param arg The argument
	 * @return An extended String
	 */
	public static String extendMaximum(String arg){
		if(arg.length()==0){
			return "Error - Expected number and expression(s) after 'minimum'.";
		}
		if(!arg.contains(" ")){
			return "Error - Expected at least two arguments.";
		}
		
		String num = "null";
		
		try{
			num = arg.substring(0,arg.indexOf(' '));
			int k = Integer.parseInt(num);
			String list = arg.substring(arg.indexOf(' ')+4);
			
			int max = list.split(",").length;

			if(k>max || k<0)
				return "Error - Expected integer in range [0,"+max+"], but received '"+k+"'.";
			
			StringBuilder string = new StringBuilder();
			string.append("(");
			
			for(int i=0; i<max; i++){
				string.append("(exactly " + i + " of " + list+")");
				if(i!=max-1)string.append(" or ");
			}
			string.append(")");
			
			return string.toString();
		} catch(NumberFormatException e){
			return "Error - Expected integer, but received '"+num+"'.";
		}
	}
	
	/**
	 * Extend the 'exactly' shorthand expression.
	 * @param arg The argument of the command.
	 * @return An extended string consisting of primitive types.
	 */
	public static String extendExactly(String arg){
		if(arg.length()==0){
			return "Error - Expected number and expression(s) after 'exactly'.";
		}
		if(!arg.contains(" ")){
			return "Error - Expected at least two arguments.";
		}
		
		String num = "null";
		
		try{
			num = arg.substring(0,arg.indexOf(' '));
			int k = Integer.parseInt(num);
			
			String list = arg.substring(arg.indexOf(' ')+1);
			if(!list.toLowerCase().startsWith("of ")){
				return "Error - Expected 'of' after '"+num+"'.";
			}
			list = list.substring(3);
			String[] expressions = list.replace(", ",",").split(",");

			if(k>expressions.length || k<0)
				return "Error - Expected integer in range [0,"+expressions.length+"], but received '"+k+"'.";
			
			ArrayList<Integer> ints = new ArrayList<Integer>();
			
			for(int i=0; i<expressions.length; i++)
				ints.add(i);

			List<Set<Integer>> subsets = getSubsets(ints,k);

			StringBuilder newString = new StringBuilder();
			
			for(Set<Integer> set : subsets){
				String localString = "(";
				for(int i=0; i<expressions.length; i++){

					if(i!=0)localString+=" and ";
					if(set.contains(i)){
						localString += "("+expressions[i] + ")";
					} else localString += "not ("+expressions[i] + ")";
					
				}
				newString.append(localString + ") or "); 
			}
			
			String f = newString.toString();
			f = f.substring(0,f.length()-4);
			
			return f;
			
		} catch(NumberFormatException e){
			return "Error - Expected integer, but received '"+num+"'.";
		}
	}
	
	/**
	 * Get all subsets of length k in a set with n elements
	 * @param superSet The set to find subsets of.
	 * @param k The length of each subset.
	 * @return The solution.
	 */
	public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
	    List<Set<Integer>> res = new ArrayList<>();
	    getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
	    return res;
	}
	
	/**
	 * Get all subsets of length k in a set with n elements.
	 * @param superSet The set to find subsets of.
	 * @param k The length of subsets
	 * @param idx Initial guess
	 * @param current Current set
	 * @param solution The solution
	 */
	private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current,List<Set<Integer>> solution) {
	    //successful stop clause
	    if (current.size() == k) {
	        solution.add(new HashSet<>(current));
	        return;
	    }
	    //unseccessful stop clause
	    if (idx == superSet.size()) return;
	    Integer x = superSet.get(idx);
	    current.add(x);
	    //"guess" x is in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	    current.remove(x);
	    //"guess" x is not in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	}
}