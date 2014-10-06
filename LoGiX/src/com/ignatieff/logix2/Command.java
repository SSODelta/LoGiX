package com.ignatieff.logix2;

public class Command {
	
	private String[] arguments;
	
	/**
	 * Returns the header of the command - the first argument.
	 * @return The header for this command.
	 */
	public String getHeader(){
		return arguments[0];
	}
	
	/**
	 * Gets an argument
	 * @param i
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public String getArgument(int i) throws ArrayIndexOutOfBoundsException{
		if(i<0 || i>arguments.length-1) throw new ArrayIndexOutOfBoundsException("Expected index with value 0 < i < " + (arguments.length-1) + ".");
		return arguments[i];
	}
	
	/**
	 * Gets an argument as an Array split by comma
	 * @param i The index
	 * @return Returns an argument as an array
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public String[] getArgumentAsArray(int i) throws ArrayIndexOutOfBoundsException{
		if(i<0 || i>arguments.length-1) throw new ArrayIndexOutOfBoundsException("Expected index with value 0 < i < " + (arguments.length-1) + ".");
		return arguments[i].split(",");
	}
	
	/**
	 * Gets an argument as an integer
	 * @param i The index
	 * @return An integer representing an argument
	 * @throws ArrayIndexOutOfBoundsException
	 * @throws NumberFormatException
	 */
	public int getArgumentAsInteger(int i) throws ArrayIndexOutOfBoundsException, NumberFormatException{
		if(i<0 || i>arguments.length-1) throw new ArrayIndexOutOfBoundsException("Expected index with value 0 < i < " + (arguments.length-1) + ".");
		return Integer.parseInt(arguments[i]);
	}
	
	/**
	 * Cleans raw data and converts it into cleaned data.
	 * @param string The raw data
	 * @return The cleaned data
	 */
	public static String cleanString(String string){
		String k = string;
		
		while(k.contains("  "))k = k.replace("  ", " ");
		if(k.startsWith(" "))  k = k.substring(1);
		
		k = k.replace(", ", ",");
		k = k.replace(" ,", ",");
		k = k.replace(" (", "(");
		k = k.replace("( ", "(");
		k = k.replace(" )", ")");
		k = k.replace("[ ", "[");
		k = k.replace(" ]", "]");
		k = k.replace("{ ", "{");
		k = k.replace(" }", "}");
		
		
		return k.replaceAll("[^A-Za-z0-9()!#<>{'}?;,-_. ]", "");
	}
	
	public static boolean isEmpty(String k){
		Command c = new Command(k);
		return c.toString().length()==0;
	}
	
	/**
	 * Determines whether or not this Command object is a certain command type.
	 * @param s The command type to test.
	 * @return True if it matches the input string (case insensitive), false otherwise.
	 */
	public boolean isCommand(String s){
		return s.equalsIgnoreCase(getHeader());
	}
	
	public int arguments(){
		return arguments.length-1;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<arguments.length; i++){
			sb.append(arguments[i]);
			if(i!=arguments.length-1)sb.append(" ");
		}
		return cleanString(sb.toString());
	}
	
	/**
	 * Returns all arguments after startIndex concatenated together.
	 * @param startIndex The start index.
	 * @return
	 */
	public String getArguments(int startIndex){
		StringBuilder sb = new StringBuilder();
		for(int i=startIndex; i<arguments.length; i++){
			sb.append(arguments[i]);
			if(i!=arguments.length-1)sb.append(' ');
		}
		return cleanString(sb.toString());
	}
	
	public void addHeader(String newHeader){
		String[] newArgs = new String[arguments.length+1];
		newArgs[0] = newHeader;
		for(int i=0; i<arguments.length; i++){
			newArgs[i+1] = arguments[i];
		}
		arguments = newArgs;
	}
	
	public Command pushHeader(String newHeader){
		Command k = this;
		k.addHeader(newHeader);
		return k;
	}
	
	/**
	 * Instantiates a new Command instance from a given command string
	 * @param cmd The cleaned input to create a new command from
	 */
	public Command(String cmd){
		String cleaned = cleanString(cmd);
		
		if(!cleaned.contains("?")){
			arguments = cleaned.split(" ");
		} else {
			arguments = new String[3];
			arguments[0] = "question";
			
			String[] args = cleaned.split("\\?");
			arguments[1] = args[0];

			if(args.length==1)return;
			
			arguments[2] = args[1].replace("\"", "");
		}
	}
}
