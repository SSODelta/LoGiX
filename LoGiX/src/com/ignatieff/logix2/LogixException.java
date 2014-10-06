package com.ignatieff.logix2;

public class LogixException extends Exception {

	private static final long serialVersionUID = 61238741234L;

	private String DATA;

	/**
	 * Instantiates a new LoGiX-exception with a given message.
	 * @param message The error message to display.
	 */
	public LogixException(String message){
		DATA = "[ERROR]: "+message;
	}
	
	/**
	 * Gets the data contained in this LoGiX-exception
	 * @return The data contained in this object.
	 */
	public String getData(){
		return DATA;
	}
}
