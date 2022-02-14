package telran.employees.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

import telran.employees.services.*;
import telran.view.*;

public class EmployeeAppl {

	public static void main(String[] args) /*throws IOException*/ {
		InputOutput io = new ConsoleInputOutput();

		if (args.length < 1) {
			io.writeObjectLine("Usage - argument should contain configurartion file name");
			return;
		}
		// Configuration file contains text line personDataFile = employees.data
		// Apply BufferReader for reading configuration
		//TODO done
		String fileName = getFileName(args[0]);
		if(fileName == null) {
			io.writeObjectLine("Wrong configuration file");
			return;
		}
		EmployeesMethods employeesMethods = new EmployeesMethodsMapsImpl(fileName);
		employeesMethods.restore();
		HashSet<String> departments = new HashSet<>(Arrays.asList("QA", "Development", "HR", "Management"));
		Menu menu = new Menu("Employees Application", EmployeeActions.getActionItems(employeesMethods, departments));
		menu.perform(io);
	}

	private static String getFileName(String configFile)  /*throws IOException*/ {
		// TODO done
		/*
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		return reader.readLine();
		*/
		
		try(BufferedReader reader = new BufferedReader(new FileReader(configFile));) {
			// V.R. Properties?
			return reader.readLine();
		// V.R. FileNotFoundException	
		} catch (Exception e) {
			return null;
		}
		
	}
}