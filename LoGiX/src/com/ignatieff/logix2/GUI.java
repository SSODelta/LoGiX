package com.ignatieff.logix2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class GUI {
	
	private ArrayList<String> commandList;
	private boolean running;
	private CoreEngine engine;
	
	public GUI(){
		commandList = new ArrayList<String>();
		running = true;
		engine = new CoreEngine();
	}
	
	public String loadDataFromFile(String path) throws IOException{
		Path p = Paths.get(path);
		return new String(Files.readAllBytes(p));
	}
	
	public String loadFile(String path) throws IOException, LogixException{
		String data = loadDataFromFile(path);
		String[] lines = data.split("\n");
		StringBuilder sb = new StringBuilder();
		
		sb.append("[File loaded successfully!]\n");
		
		boolean globalPrint = true;
		
		for(int i=0; i<lines.length; i++){
			
			String line = lines[i];
			boolean localPrint = globalPrint;
			
			if(Command.cleanString(line).equals("!!")){
				globalPrint = !globalPrint;
				continue;
			}

			if(Command.isEmpty(line) || line.startsWith("#"))
				continue;
			if(line.startsWith("!")){
				line = line.substring(1);
				localPrint = !localPrint;}
			
			try {
				String msg = engine.processCommand(new Command(line));
				if(msg.length()==0)continue;
				if(localPrint)sb.append(msg+"\n");
				
			} catch (LogixException e) {
				throw new LogixException("An error occurred at line " + i + ":\n   "+e.getData().substring(9) +"\n      Input: "+line);
			} catch (Exception e){
				e.printStackTrace();
				throw new LogixException("An unexpected error occured at line "+i+".\n");
				
			}
		}
		
		return sb.toString();
	}
	
	public void start(){
		running=true;
		
		Scanner s = new Scanner(System.in);
		System.out.println("...|- LoGiX v1.1.0 - by SSODelta 2014 -|...");
		
		while(running){
			System.out.print(":> ");
			String rawCommand = s.nextLine();
			Command command = new Command(rawCommand);
			
			if(command.isCommand("load")){
				String file = command.getArgument(1);
				
				String k = null;
				
				try {
					k = loadFile(file);
					commandList.add(command.toString());
				} catch (IOException e) {
					System.out.println("[ERROR]: Unable to read file '"+file+"'. Maybe it's been moved?");
					continue;
				} catch (LogixException e){
					System.out.println(e.getData());
					continue;
				}
				
				System.out.println(k);
				continue;
			}
			
			try{
				String msg = engine.processCommand(command);
				System.out.println(msg+"\n");
				commandList.add(command.toString());
			} catch(LogixException e){
				System.out.println(e.getData()+"\n");
			}
		}
		
		s.close();
	}
}
