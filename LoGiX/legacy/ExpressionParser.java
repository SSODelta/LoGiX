package com.ignatieff.proofs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ignatieff.expression.AND;
import com.ignatieff.expression.Expression;
import com.ignatieff.expression.Implication;
import com.ignatieff.expression.Tautology;

public class ExpressionParser {
	private ArrayList<String> constants;
	private ArrayList<Expression> premises;
	private Table t;
	
	private HashMap<String, String> helpMap, listMap, vars;
	
	private ArrayList<String> commandList;
	
	//private static String[] keywordList = new String[]{"exit","reset", "def", "undo", "help", "list", "?", "num","example","true","false"};
	//private static String[] booleanList = new String[]{"with","false","true","and","then","bi-implies","biimplies","implies","or","not","nand","nor","xor","xnor"};
	
	private static String[] protectedList = new String[]{"rpn","exit","reset", "def", "undo", "help", "list", "?", "num","example"};
	private static String[] booleanListNoNot = new String[]{"and","then","implies","or","nand","nor","xor","xnor"};
	
	public ExpressionParser(){
		constants   = new ArrayList<String>();
		commandList = new ArrayList<String>();
		premises    = new ArrayList<Expression>();
		
		helpMap = new HashMap<String, String>();
		listMap = new HashMap<String, String>();
		vars = new HashMap<String, String>();
		
		try {
			String helpFile = readFile("help.txt");
			String[] entries = helpFile.split("\\|");
			for(int i=0; i<entries.length/2; i++){
				helpMap.put(entries[i*2], entries[i*2+1].substring(1));}
		} catch (IOException e) {
			System.out.println("Unable to read 'help.txt'. Has it been moved?");
		}

		try {
			String helpFile = readFile("lists.txt");
			String[] entries = helpFile.split("\\|");
			for(int i=0; i<entries.length/2; i++){
				listMap.put(entries[i*2], entries[i*2+1].substring(1));}
		} catch (IOException e) {
			System.out.println("Unable to read 'lists.txt'. Has it been moved?");
		}
		
		t = new Table();
	}
	
	private void reset(){
		constants = new ArrayList<String>();
		premises = new ArrayList<Expression>();
		t = new Table();
	}
	
	public void addPremise(Expression premise){
		if(premise==null)return;
		premises.add(premise);
	}
	
	public void addConstant(String cons){
		if(constants.contains(cons))
			return;
		
		constants.add(cons);
		t.addEntry(cons);
	}
	
	public Expression getPremises(){
		if(premises.size()==0)return new Tautology(true);
		Expression r = premises.get(0);
		for(int i=0; i<premises.size()-1; i++){
			r = new AND(r,premises.get(i+1));
		}
		return r;
	}
	
	public String readFile(String path) throws IOException{
		return new String(Files.readAllBytes(Paths.get(path)));
	}
	
	public String clearInput(String k){
		String s = k.toLowerCase();
		while(s.startsWith(" "))s = s.substring(1);
		while(s.contains("  ")) s = s.replace("  ", " ");
		s = s.replace(", ", ",");
		while(s.startsWith("if ") && s.indexOf("then")>-1){
			//Preserve CaPiTaL LeTtErS
			String r = k.substring(3);
			
			//Split into two sides
			String[] l = r.split("then");
			
			//Attach again, but with a different position of words (i.e. "If A then B" is the same as "A implies B").
			return l[0] + "implies" + l[1];
		}
		while(s.startsWith("iff ") && s.indexOf("then")>-1){
			//Preserve CaPiTaL LeTtErS
			String r = k.substring(3);
			
			//Split into two sides
			String[] l = r.split("then");
			
			//Attach again, but with a different position of words (i.e. "If A then B" is the same as "A implies B").
			return l[0] + "biimplies" + l[1];
		}
		while(k.startsWith(" "))k = k.substring(1);
		return extend( k.replace("\n","")
						.replace("implise", "implies")
						.replace("adn", "and")
						.replace("bimplise", "biimples")
						.replace("bimplise", "biimples")
						.replace("def def ", "def ")
						.replaceAll("[^A-Za-z0-9()!#<>{'}?;,-. ]", ""));
	}
	
	
	public boolean containsWord(String string, String word){
		Pattern p = Pattern.compile("\\b"+word+"\\b");
	    Matcher m = p.matcher(string);
	    return m.matches();
	}
	
	public String testEqual(Expression p, Expression q){
		Table[] truthTables = t.getTables();
		for(int i=0; i<truthTables.length; i++){
			//if(p.evaluate(truthTables[i]) != q.evaluate(truthTables[i]))
				return "The two expressions are not equal.\nCounter-example: " + truthTables[i];
		}
		return "The two expressions are equal.";
	}
	
	public String isTrue(Expression p){
		Expression prems = getPremises();
		Expression check = new Implication(prems,p);
		
		Table[] truthTables = t.getTables();

		for(int i=0; i<truthTables.length; i++){
			//if(!check.evaluate(truthTables[i]))
				return "false\nCounter-example: "+truthTables[i].toString();
		}
		return "true";
	}
	
	public String getExample(boolean value, int example, Expression p){
		Expression prems = getPremises();
		Expression check = new Implication(prems,p);
		Table[] truthTables = t.getTables();
		int e = example % truthTables.length;
		for(int i=0; i<truthTables.length; i++){
			//if(check.evaluate(truthTables[i])==value){
				if(e>0){e--;continue;}
				return truthTables[i].toString();
			//}
		}
		return "There are no examples of this expression being " + value+".";
	}
	
	public String RPN(String k){
		TokenList l = new TokenList(k);
		return l.toString();
	}
	
	public String isTrueNum(Expression p){
		Expression prems = getPremises();
		Expression check = new Implication(prems,p);
		Table[] truthTables = t.getTables();
		int s = 0;
		boolean isTrue = true;
		for(int i=0; i<truthTables.length; i++){
//if(!check.evaluate(truthTables[i])){
				isTrue = false;
	//		} else s++;
		}
		return s+"/" + truthTables.length + " ("+isTrue+")";
	}
	
	private static boolean arrayContains(String[] arr, String s){
		String[] words = s.split(" ");
		for(int i=0; i<arr.length; i++){
			for(String w : words){
				if(w.equalsIgnoreCase(arr[i])) return true;
			}
		}
		return false;
	}
	
	private void addConstants(TokenList tl){
		Token[] tokens = tl.getTokens();
		for(Token x : tokens){
			if(x.TYPE==Token.TYPE_CONSTANT && !x.DATA.equalsIgnoreCase("false") && !x.DATA.equalsIgnoreCase("true")){
				addConstant(x.DATA);
			}
		}
	}
	
	private Expression getExpression(String s){
		TokenList tl = new TokenList(s);
		addConstants(tl);
		return tl.getExpression();
	}
	
	private static String NULL_STRING = "NULL_STRING";
	private static String STOP_STRING = "STOP_LISTEN";
	
	/*private static boolean isNumber(String k){
		try{
			Integer.parseInt(k);
			return true;
		} catch (NumberFormatException e){ return false;}
	}*/
	
	/*private static boolean duplicateVariable(String k){
		String[] words = k.split(" ");
		boolean hasVariable = false;
		
		for(int i=0; i<words.length; i++){
			if(!arrayContains(booleanList,words[i].replaceAll("[^0-9a-zA-Z]", "")) && !isNumber(words[i])){
				if(hasVariable==true){
					return true;
				} else {
					hasVariable = true;
				}
			} else {
				hasVariable = false;
			}
		}
		return false;
	}*/
	
	private static boolean duplicateConjunction(String k){
		String[] words = k.split(" ");
		boolean hasVariable = false;
		
		for(int i=0; i<words.length; i++){
			if(arrayContains(booleanListNoNot,words[i])){
				if(hasVariable==true){
					return true;
				} else {
					hasVariable = true;
				}
			} else {
				hasVariable = false;
			}
		}
		return false;
	}
	
	private void executeCommands(ArrayList<String> list){
		boolean globalPrint = true;
		for(int i=0; i<list.size(); i++){
			boolean localPrint = globalPrint;
			String nextLine = list.get(i);
			if(nextLine.equals("!!")){
				globalPrint = !globalPrint;
				continue;
			}
			if(nextLine.startsWith("!")){
				nextLine=nextLine.substring(1);
				localPrint = !globalPrint;
			}
			String result = executeCommand(nextLine);
			if(localPrint)System.out.println(result);
		}
	}
	
	/*private static String pasteVars(String q){
		Token[] tokens = TokenList.getTokens(q);
		StringBuilder 
		for(int i=0; i<tokens.length; i++){
			
		}
	}*/
	
	private void undo(int num){
		reset();
		ArrayList<String> newCommands = new ArrayList<String>();
		for(int i=0; i<commandList.size()-num; i++){
			newCommands.add(commandList.get(i));
		}
		commandList = new ArrayList<String>();
		executeCommands(newCommands);
	}
	
	public void startListener(){
		Scanner s = new Scanner(System.in);
		System.out.println("...|- LoGiX v1.0.2 - by SSODelta 2014 -|...");
		boolean con = true;
		boolean globalPrint = true;
		
		while(con){
			System.out.print(":> ");
			String nextLine = clearInput(s.nextLine());
			boolean localPrint = globalPrint;
			
			if(nextLine.equals("!!")){
				globalPrint = !globalPrint;
				continue;
			}
			if(nextLine.startsWith("!")){
				nextLine=nextLine.substring(1);
				localPrint = !localPrint;
			}
			String b = executeCommand(nextLine);
			
			if(!b.equalsIgnoreCase(NULL_STRING) && !b.equalsIgnoreCase(STOP_STRING)){
				if(localPrint) System.out.println(b+"\n");
				continue;
			}
			
			if(b.equalsIgnoreCase(STOP_STRING)){
				con=false;
				continue;
			}
			
		}
		
		s.close();
	}
	
	public String executeCommand(String s){
		return executeCommand(s,true);
	}
	
	public String extendMaximum(String cmd, String arg){
		if(arg.length()==0){
			return "Error - Expected number and expression(s) after 'maximum'.";
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
			
			for(int i=max; i>k; i--){
				string.append("(exactly " + i + " of " + list+")");
				if(i!=k+1)string.append(" or ");
			}
			
			string.append(")");
			
			return string.toString();
		} catch(NumberFormatException e){
			return "Error - Expected integer, but received '"+num+"'.";
		}
	}
	
	public String extendMinimum(String arg){
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
	
	public String extendMaximum(String arg){
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
	
	public String extendExactly(String arg){
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
			
			String list = clearInput(arg.substring(arg.indexOf(' ')+1));
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
	
	public String extend(String string){
		String s = string.replace(", ", ",");
		
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
		
		return s;
	}
	
	public String executeCommand(String s, boolean tryAgain){
		if(s.toLowerCase().contains("error"))
			return s;
		try{
		if(s.contains(";")){
			String prefix = "";
			
			if(s.startsWith("!"))prefix="!";
			
			String[] cmds = s.split(";");
			StringBuilder k = new StringBuilder();
			
			for(int i=0; i<cmds.length; i++){
				String l = executeCommand(prefix+clearInput(cmds[i]));
				if(l.toLowerCase().contains("error"))
					return "Error in expression list at index " + (i+1) + ":\n   "+l.substring(8);
				k.append(l);
				if(i!=cmds.length-1) k.append("\n");
			}
			
			return k.toString();
		}
		if(s.contains("{") || s.contains("}")){
			s=clearInput(s.replace("{", " {"));
			if(numChars('{', s) == numChars('}',s)){
				int delL = s.lastIndexOf('{')+1;
				int delR = s.lastIndexOf('}');

				StringBuilder data = new StringBuilder();

				String commandPrefix = s.substring(0,delL-1);
				String commandSuffix = s.substring(delR+1);
				
				String rawArgs = s.substring(delL,delR);
				String[] args = rawArgs.split(",");
				for(int i=0; i<args.length; i++) {
					String q = commandPrefix + args[i];
					q = clearInput(q + commandSuffix);
					data.append(executeCommand(q, tryAgain));
					if(i!=args.length-1)data.append('\n');
				}
				return data.toString();
			}
			return "Error - mismatched curly brackets.";
		}
		
		commandList.add(s);
		
		String cmd = s, arg = "";
		
		if(s.indexOf(' ')>-1){
			cmd = s.substring(0, s.indexOf(' '));
			arg = s.substring(s.indexOf(' ')+1);
		}

		while(arg.startsWith(" "))arg = arg.substring(1);
		
		if(!cmd.equals("list") && !cmd.equals("help") && arrayContains(protectedList, arg))
			return "Error - can't use protected words as variables (see 'list protected' for full list).";
		/*
		if(duplicateVariable(arg) && numSpaces(arg)>0){
			if(!tryAgain)
			return "Error - unknown command or expression '"+arg+"'";
			return "Error - unable to parse expression; duplicate variables.";
		}*/
		if(duplicateConjunction(arg))
			return "Error - unable to parse expression; duplicate conjunctions (see 'list conjunction' for full list).";
		
		
		if(cmd.equalsIgnoreCase("exit")){
			reset();
			return STOP_STRING;
		}
		
		
		if(cmd.equalsIgnoreCase("load")){
			if(arg.length()==0){
				return "Error - Expected file path after 'load'.";
			}
			try{
				String fileData = readFile(arg);
				String[] lines = fileData.split("\n");
				StringBuilder result = new StringBuilder();
				result.append("[Succesfully loaded file '"+arg+"'!]");
				reset();
				boolean globalPrint = true;
				for(int i=0; i<lines.length; i++){
					boolean localPrint = globalPrint;
					String k = clearInput(lines[i]);
					if(k.equals("!!")){
						globalPrint = !globalPrint;
						continue;
					}
					if(k.startsWith("#") || k.length()==0)continue;
					if(k.startsWith("!")){
						k=k.substring(1);
						localPrint=!localPrint;
					}
					if(k.toLowerCase().startsWith("load"))
						return "Error - Can't load a file from within a file (program has been reset).";
					String r = executeCommand(clearInput(k));
					
					if(r.toLowerCase().startsWith("error"))
						return "Error in file '"+arg+"' at line "+(i+1)+":\n   "+r.substring(8);
					
					if(!localPrint)continue;
					
					result.append("\n"+k+"\n   "+clearInput(r));
					if(i!=lines.length-1)result.append("\n");
				}
				return result.toString();
			} catch(IOException e){
				return "Error - unable to load file '"+arg+"'. Is the path correct?";
			}
		}
		
		if(cmd.equalsIgnoreCase("rpn")){
			if(arg.length()==0){
				return "Error - Expected expression after 'rpn'.";
			}
			return RPN(arg);
		}
		
		if(cmd.equalsIgnoreCase("reset")){
			reset();
			return "Cleared premises!";
		}
		
		if(cmd.equalsIgnoreCase("num")){
			if(arg.length()==0){
				return "Error - Expected expression after '?'.";
			}
			return ""+isTrueNum(getExpression(arg));
		}

		if(cmd.equalsIgnoreCase("test")){
			if(!arg.toLowerCase().contains("with")){
				return "Error - expected a 'with' but received '"+arg+"'.";
			}
			String[] pq = arg.split("with");
			if(pq.length!=2){
				return "Error - expected only two arguments.";
			}
			return testEqual(getExpression(pq[0]),getExpression(pq[1]));
		}
		
		if(cmd.equalsIgnoreCase("example")){
			String arg1 = arg.substring(0,arg.indexOf(" "));
			String arg2 = arg.substring(arg.indexOf(" ")+1);
			int example = 0;
			
			if(arg2.indexOf(' ')>0){
				String num = arg2.substring(0,arg2.indexOf(' '));
				try{
					example = Integer.parseInt(num) - 1;
					arg2 = arg2.substring(arg2.indexOf(' ')+1);
				} catch (NumberFormatException e){};
			}
			
			if(arg1.equalsIgnoreCase("true"))
				return getExample(true,example,getExpression(arg2));
			
			if(arg1.equalsIgnoreCase("false"))
				return getExample(false,example,getExpression(arg2));
		}
		
		if(cmd.equalsIgnoreCase("def")){
			if(arg.length()==0){
				return "Error - Expected expression after 'def'.";
			}
			
			if(arg.contains(":=")){
				String var = arg.substring(0,arg.indexOf(' ')).replace(" ", "");
				String args = arg.substring(arg.indexOf(' ')+1).replace(" ", "");
				if(vars.containsKey(var))
					vars.remove(var);
				vars.put(var,args);
				return "def "+var + " := " + args;
			}

			
			if(arg.contains(">")){
				String var = arg.substring(0,arg.indexOf(' ')).replace(" ", "");
				String args = arg.substring(arg.indexOf(' ')+1).replace(" ", "");
				
				//if(relations.containsKey(var))
				//	vars.remove(var);
				//vars.put(var,args);
				
				return "def "+var + " := " + args;
			}
			
			Token[] tokenList = TokenList.getTokens(arg);
			for(Token t : tokenList){
				if(t.TYPE==Token.TYPE_CONSTANT)
					addConstant(t.DATA);
			}

			Expression e = getExpression(clearInput(arg));
			addPremise(e);
			return "def "+clearInput(arg);
		}
		
		if(cmd.equalsIgnoreCase("?")){
			if(arg.length()==0){
				return "Error - Expected expression after '?'.";
			}
			Expression e = getExpression(arg);
			return isTrue(e);
		}
		
		if(cmd.equalsIgnoreCase("undo")){
			if(arg.length()==0){
				undo(1);
				return "Undid (1) command.";
			}
			try{
				int k = Integer.parseInt(arg);
				if(k<=0){
					return "Error - Argument must be a positive non-zero integer.";
				}
				undo(k);
				return "Undid ("+k+") commands.";
			} catch(NumberFormatException e){
				return "Error - Argument must be a positive non-zero integer.";
			}
		}
		if(cmd.equalsIgnoreCase("list")){
			if(arg.length()==0){
				return "Error - Expecting second argument for command 'list'.";
			}
			if(arg.equalsIgnoreCase("constants")){
				StringBuilder q = new StringBuilder();
				q.append("List of constants:\n   - ");
				
				for(int i=0; i<constants.size(); i++){
					q.append(constants.get(i));
					if(i!=constants.size()-1)
						q.append(", ");
				}
				return q.toString();
			}
			if(listMap.containsKey(arg)){
				return listMap.get(arg);
			}
			
			return "Error - Invalid list module '"+arg+"'. To see a list of list modules, see 'list modules'.";
		}
		
		if(cmd.equalsIgnoreCase("help")){
			if(helpMap.containsKey(arg)){
				return helpMap.get(arg);
			}
			return "Error - Invalid help module '"+arg+"'. To see a list of help modules, see 'list helpmodules'.";
		}
		
		if(tryAgain && numChars(' ',s)>0) {
			String k = executeCommand("def "+s, false);
			if(k.toLowerCase().contains("error")){
				return k;
			}
			return clearInput("def "+k);
		}
		
		return "Error - Unknown command '"+cmd+"'.";
		} catch(NullPointerException e){
			return "Error - Unexpected error. Are you sure it was correctly formatted?";
		}
	}
	
	private static int numChars(char c, String k){
		int r = 0;
		String g = ""+c;
		while(k.contains(g)){
			k=k.substring(k.indexOf(g)+1);
			r++;
		}
		return r;
	}
	
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

	public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
	    List<Set<Integer>> res = new ArrayList<>();
	    getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
	    return res;
	}
	
}
