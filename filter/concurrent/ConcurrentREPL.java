package cs131.pa2.filter.concurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import cs131.pa2.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop).
 * It reads commands from the user, parses them, executes them and displays the result.
 * @author cs131a
 *
 */
public class ConcurrentREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;
	/**
	 * The main method that will execute the REPL loop
	 * @param args not used
	 */
	static Map<String, ArrayList<Thread>> map;
	static Map<Integer, String> backGroundCommands;
	static int currNo;
	
	public static void main(String[] args){
		currNo = 1;
		map = new HashMap<>();
		backGroundCommands = new TreeMap<>();
		//backGroundFilters = new TreeMap<>();
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		String command; 
		
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			String[] temp = command.trim().split("\\s+");
			if(command.equals("exit")) {
				break;
			} else if(temp[temp.length - 1].equals("&")) {
				command = command.split("&")[0];
				backGroundCommands.put(currNo, command);
				//backGroundFlag = true;
				runCommand2(command);
				//backGroundFlag = false;
				currNo++;
			} else if(command.equals("repl_jobs")) {
				repljobs();
			} else if(temp[0].equals("kill")) {
				kill(command, temp);
			} else if(!command.trim().equals("")) {
				//building the filters list from the command
				runCommand1(command);
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}
	
	public static void runCommand1(String command) {
		ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
		while(filterlist != null) {
			Thread t = new Thread(filterlist);
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			filterlist = (ConcurrentFilter) filterlist.getNext();
		}
	}
	
	
	public static void runCommand2(String command) {
		ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
		map.put(command, new ArrayList<>());
		while(filterlist != null) {
			Thread t = new Thread(filterlist);
			t.start();
			map.get(command).add(t);
			filterlist = (ConcurrentFilter) filterlist.getNext();
		}
	}
	
	
	public static void kill(String command, String[] temp) {
		if(temp.length < 2) {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
			return;
		}
		try {
			int i = Integer.valueOf(temp[1]);
			ArrayList<Thread> threads = map.get(backGroundCommands.get(i));
			for(Thread thread: threads) {
				if(thread.isAlive()) {
					thread.interrupt();
				}
			}
			map.remove(backGroundCommands.get(i));
			backGroundCommands.remove(i);
		} catch (NumberFormatException e) {
			System.out.printf(Message.INVALID_PARAMETER.toString(), command);
			return;
		}
	}
	
	public static void repljobs() {
		List<Integer> deadCommandNo = new ArrayList<>();
		//System.out.println("size " + backGroundCommands.keySet().size());
		for(int i: backGroundCommands.keySet()) {
			String command = backGroundCommands.get(i);
			//System.out.println("enter keyset traverse ");
			boolean flag = false;
			for(Thread thread: map.get(command)) {
				//System.out.println("enter thread traverse ");
				if(thread.isAlive()) {
					System.out.println("\t" + i + ". " + backGroundCommands.get(i) + "&");
					flag = true;
					break;
				}
			}
			if(flag == false) {
				deadCommandNo.add(i);
			}		
		}
		//System.out.println("dead size" + deadCommandNo.size());
		for(int i: deadCommandNo) {
			String str = backGroundCommands.get(i);
			backGroundCommands.remove(i);
			map.remove(str);
		}
		//System.out.println("back size" + backGroundCommands.keySet().size());
		//System.out.println("map size" + map.size());
//		Map<Integer, String> temp1 = new TreeMap<>();
//		Map<String, ArrayList<Thread>> temp2 = new HashMap<>();
//		for(int i: liveCommandNo) {
//			temp1.put(i, backGroundCommands.get(i));
//		}
//		backGroundCommands = temp1;
//		for(int i: liveCommandNo) {
//			temp2.put(backGroundCommands.get(i), map.get(backGroundCommands.get(i)));
//		}
//		map = temp2;
	}
}
