package com.ignatieff.logix2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ignatieff.expression.AND;
import com.ignatieff.expression.Expression;
import com.ignatieff.expression.Implication;
import com.ignatieff.expression.Tautology;
import com.ignatieff.expression.Table;

public class CoreEngine {
	
	private ArrayList<String> names, variables, dataTypes, objects, relations;
	private ArrayList<Expression> premises;
	private Map<String, String> objectTypes;
	private Map<String, ArrayList<String>> relationLinks;
	
	private static String[] booleanList = new String[]{"iff","with","false","true","and","then","bi-implies","biimplies","implies","or","not","nand","nor","xor","xnor"};
	
	
	private Table table;
	
	/**
	 * Instantiates a new CoreEngine for use in LoGiX
	 * Version: v1.1.0
	 */
	public CoreEngine(){
		names     = new ArrayList<String>();
		variables = new ArrayList<String>();

		dataTypes = new ArrayList<String>();
		objects   = new ArrayList<String>();

		relations = new ArrayList<String>();
		
		premises  = new ArrayList<Expression>();
		
		objectTypes   = new HashMap<String, String>();
		relationLinks = new HashMap<String, ArrayList<String>>();
		
		table = new Table();
	}
	
	public static boolean isAlphaNumeric(String k){
		String l = k.replaceAll("[^a-zA-Z0-9_]", "");
		return l.equalsIgnoreCase(k);
	}
	
	public static boolean isVariable(String k){
		if(k.length()==0)return false;
		if(!isAlphaNumeric(k))return false;
		for(String l : booleanList){
			if(l.equalsIgnoreCase(k))return false;
		}
		return true;
	}
	
	private boolean firstLetterCapitalized(String s){
		return s.charAt(0) == s.toUpperCase().charAt(0);
	}
	
	/**
	 * Adds a variable to this CoreEngine's list of variables.
	 * @param variable The variable to add.
	 */
	private void addVariable(String variable){
		if(!isVariable(variable))return;
		if(variables.contains(variable))return;
		
		variables.add(variable);
		table.addEntry(variable);
	}
	
	public String getExample(Expression p, boolean falseTrue){
		Expression prems = getPremises();
		Expression check = new Implication(prems,p);
		Table[] truthTables = table.getTables();

		for(int i=0; i<truthTables.length; i++){
			if(!check.evaluate(truthTables[i]))
				return truthTables[i].toString();
		}
		return "There are no examples of this expression being "+falseTrue+".";
	}
	
	/**
	 * Adds a premise to this CoreEngine's list of variables.
	 * @param e The Expression to add.
	 */
	private void addPremise(Expression e){
		if(!premises.contains(e))
			premises.add(e);
	}
	
	/**
	 * Extends a command String to only containing primitives instead of complex shorthand expression.
	 * @param s The command to extend
	 * @return An extended command
	 */
	private String extendCommand(String string){
		String s = string;
		
		
		
		return Extender.extend(s);
	}
	
	public String processCommand(Command c) throws LogixException{
		return processCommand(c,true);
	}
	
	private boolean containsRelation(Command c){
		TokenList tl = new TokenList(c.toString());
		for(String relName : relations){
			if(tl.contains(new Token(relName)))return true;
		}
		return false;
	}
	
	private String getFirstRelation(Command c){
		TokenList tl = new TokenList(c.toString());
		for(String relName : relations){
			if(tl.contains(new Token(relName)))return relName;
		}
		return null;
	}
	
	private void addConstants(String q){
		TokenList tl = new TokenList(q);
		Token[] tokens = tl.getTokens();
		for(Token t : tokens){
			if(t.TYPE==Token.TYPE_CONSTANT)
				addVariable(t.DATA);
		}
	}
	
	private String[] getObjectsForType(String type) throws LogixException {
		if(!dataTypes.contains(type)) throw new LogixException("Undefined data type '"+type+"'.");
		ArrayList<String> obj = new ArrayList<String>();
		for(String s : objects){
			if(!objectTypes.containsKey(s)) throw new LogixException("Completely unexpected error occurred.");
			if(objectTypes.get(s).equals(type))
				obj.add(s);
		}
		return obj.toArray(new String[obj.size()]);
	}
	
	private Command[] extendRelations(Command c) throws LogixException{
		String s = c.toString();
		String rel = getFirstRelation(c);
		
		String m = null;
		int pos = -1;

		String q = new String(s);
		while(m==null){
			if(q.indexOf(rel)==-1){
				throw new LogixException("Unable to parse command '"+c.toString()+"'.");
			};
			
			q = q.substring(q.indexOf(rel)+rel.length());
			String rawArgs = q.substring(1,q.indexOf(")"));
			String[] listArgs = rawArgs.split(",");
			for(int i=0; i<listArgs.length; i++){
				if(!listArgs[i].toLowerCase().equals(listArgs[i]))continue;
				pos = i;
				m = listArgs[i];
			}
		}
		
		ArrayList<String> links = relationLinks.get(rel);
		
		String dataType = links.get(pos);
		
		String[] objects = getObjectsForType(dataType);
		
		ArrayList<Command> commands = new ArrayList<Command>();
		Token[] tokens = TokenList.getTokens(s);
		
		for(String v : objects){
			StringBuilder newCommand = new StringBuilder();
			for(int i=0; i<tokens.length; i++){
				String k = tokens[i].DATA;
				if(k.equals(m)) k = v;
				
				newCommand.append(k);
				if(i!=tokens.length-1) newCommand.append(' ');
			}
			String k = Command.cleanString(newCommand.toString());
			commands.add(new Command(k));
		}
		
		return commands.toArray(new Command[commands.size()]);
		
	}
	
	private static String[] protectedList = new String[]{"rpn","exit","reset", "def", "undo", "help", "list", "?", "num","example","rel","obj","iff","with","false","true","and","then","bi-implies","biimplies","implies","or","not","nand","nor","xor","xnor"};

	private static boolean arrayContains(String[] arr, String s){
		String[] words = s.split(" ");
		for(int i=0; i<arr.length; i++){
			for(String w : words){
				if(w.equalsIgnoreCase(arr[i])) return true;
			}
		}
		return false;
	}
	
	private boolean containsPlaceholders(Command k){
		String checkString = k.toString();
		if(k.isCommand("question"))
			checkString = k.getArgument(1).toString();
		Token[] tokens = TokenList.getTokens(checkString);
		for(Token t : tokens){
			if(!arrayContains(protectedList,t.DATA) && isAlphaNumeric(t.DATA) && t.DATA.hashCode() == t.DATA.toLowerCase().hashCode())
				return true;
		}
		return false;
	}
	
	private Command extendFunctions(Command s){
		Command c = s;
		while(containsRelation(c)){
			boolean inside = false;
			String rel = getFirstRelation(c);
			String k = c.toString();
			
			StringBuilder sb = new StringBuilder();
			
			Token[] tokens = TokenList.getTokens(k);
			for(int i=0; i<tokens.length; i++){
				if(inside){
					if(tokens[i].DATA.equals("(")){
						sb.append('_');
						continue;
					}
					if(tokens[i].DATA.equals(",")){
						sb.append('_');
						continue;
					}
					if(tokens[i].DATA.equals(")")){
						inside=false;
						continue;
					}
					sb.append(tokens[i].DATA);
					continue;
				}
				if(tokens[i].DATA.equals(rel)){
					sb.append(tokens[i].DATA);
					inside = true;continue;};
				sb.append(" "+tokens[i].DATA+" ");
			}
			
			c = new Command(sb.toString());
		}
		
		return c;
	}
	
	private String[] relMods = new String[]{"ref","strict","sym","asym","trans","total","euclidean","eq"};
	private boolean isRelModifier(String k){
		for(String s : relMods){
			if(k.equalsIgnoreCase(s))return true;
		}
		return false;
	}
	
	/**
	 * Processes a given command in this CoreEngine
	 * @param c The command to process
	 * @return The return message from whatever command is executed
	 * @throws LogixException
	 */
	public String processCommand(Command c, boolean tryAgain) throws LogixException{
		
		if(containsRelation(c)){
			if(containsPlaceholders(c)){
				Command[] newLines = extendRelations(c);
				for(Command k : newLines){
					processCommand(k);
				}
				return c.toString();
			}
			return processCommand(extendFunctions(c));
		}
		
		if(c.isCommand("rel")){

			ArrayList<String> types = new ArrayList<String>();
			
			ArrayList<String> mods = new ArrayList<String>();
			String exp = c.getArgument(1);
			
			int i = 1;
			
			while(isRelModifier(exp)){
				mods.add(exp);
				i++;
				exp = c.getArgument(i);
			}
			
			
			if(!exp.contains("(") || !exp.contains(")")) throw new LogixException("Mismatched parentheses.");
			
			String relName = exp.substring(0,exp.indexOf('('));
			if(!firstLetterCapitalized(relName)) throw new LogixException("Expected first letter of '"+relName+"' to be capitalized.");
			if(names.contains(relName)) throw new LogixException("Variable identifier '"+relName+"' isn't assign-able - maybe it's already in use?");
			
			String rawLinks = exp.substring(exp.indexOf('(')+1, exp.indexOf(')'));
			String[] links = rawLinks.split(",");
			
			for(String link : links){
				if(!dataTypes.contains(link)) throw new LogixException("Can't define relation with undefined type '"+link+"'.");
				types.add(link);
			}
			
			relations.add(relName);
			names.add(relName);
			relationLinks.put(relName, types);
			
			return "Successfully defined new relation '"+relName+"'.";
		}
		
		if(c.isCommand("obj")){
			
			ArrayList<String> newDataTypes = new ArrayList<String>(dataTypes);
			ArrayList<String> newObjects = new ArrayList<String>(objects);
			ArrayList<String> newNames = new ArrayList<String>(names);
			Map<String, String> newObjectTypes = new HashMap<String,String>(objectTypes);
			
			String objectName  = c.getArgument(1);
			String[] vars = c.getArgumentAsArray(2);

			if(!firstLetterCapitalized(objectName)) throw new LogixException("Expected first letter of '"+objectName+"' to be capitalized.");
			
			newDataTypes.add(objectName);
			
			if(names.contains(objectName) || newNames.contains(objectName)) throw new LogixException("Variable identifier '"+objectName+"' isn't assign-able - maybe it's already in use?");

			newNames.add(objectName);
			
			for(int i=0; i<vars.length; i++){
				if(!firstLetterCapitalized(vars[i])) throw new LogixException("Expected first letter of '"+vars[i]+"' to be capitalized.");
				if(names.contains(vars[i]) || newNames.contains(vars[i])) throw new LogixException("Variable identifier '"+vars[i]+"' isn't assign-able - maybe it's already in use?");
				
				newObjects.add(vars[i]);
				newNames.add(vars[i]);
				
				newObjectTypes.put(vars[i], objectName);
			}
			
			objectTypes = newObjectTypes;
			names = newNames;
			dataTypes = newDataTypes;
			objects = newObjects;
			
			return "Defined new object type '"+objectName+"'.";
		}
		
		if(c.isCommand("def")){
			
			String arg = c.getArguments(1);
			
			arg = extendCommand(arg);
			
			addConstants(arg);
			
			Expression e = TokenList.convertToExpression(arg);
			addPremise(e);
			
			return c.toString();
		}
		
		if(c.isCommand("example")){
			
			boolean falseTrue = false;
			
			String falseTrueStr = c.getArgument(1);
			
			if(falseTrueStr.equalsIgnoreCase("true")) falseTrue = true;
			
			String exp = c.getArguments(2);
			
			exp = extendCommand(exp);

			addConstants(exp);
			
			Expression e = TokenList.convertToExpression(exp);
			
			return getExample(e, falseTrue);
		}
		
		if(c.isCommand("question")){
			
			if(containsRelation(c)) return processCommand(extendFunctions(c));
			
			String exp = c.getArgument(1);
		
			
			String message = c.getArguments(2);
			
			exp = extendCommand(exp);
			
			if(message == null || message.length()==0 || message.toString().equals("null")) message = "true";
			
			addConstants(exp);
			
			Expression e = TokenList.convertToExpression(exp);
			
			if(isTrue(e)) return message;
			return "false";
		}
		
		if(tryAgain && c.arguments()>0){
			try{
				return processCommand(c.pushHeader("def"), false);
			} catch(LogixException e){
				throw e;
			}
		}
		
		throw new LogixException("Invalid command or identifier '"+c.getHeader()+"'.");
	}
	
	/**
	 * Determines whether or not a given expression is a semantic consequence of the premises.
	 * @param e The expression to check.
	 * @return Returns true if the input follows from the premises.
	 */
	private boolean isTrue(Expression p){
		Expression prems = getPremises();
		Expression check = new Implication(prems,p);
		Table[] truthTables = table.getTables();

		for(int i=0; i<truthTables.length; i++){
			if(!check.evaluate(truthTables[i]))
				return false;
		}
		return true;
	}
	
	/**
	 * Gets the list of premises AND'ed together.
	 * @return An Expression representing all premises.
	 */
	private Expression getPremises(){
		if(premises.size()==0)return new Tautology(true);
		Expression r = premises.get(0);
		for(int i=0; i<premises.size()-1; i++){
			r = new AND(r,premises.get(i+1));
		}
		return r;
	}
}
