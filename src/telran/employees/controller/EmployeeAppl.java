package telran.employees.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import telran.employees.services.*;
import telran.view.*;

public class EmployeeAppl {

	private static final String CONFIGURATION_NAME = "personDataFile";
	private static final String FILE_FOR_SAVING = "employees.data";
	private static final String WRONG_KEY_VALUE = "Empty configuration";

	public static void main(String[] args) {
		InputOutput io = new ConsoleInputOutput();

		if (args.length < 1) {
			io.writeObjectLine("Usage - argument should contain configurartion file name");
			return;
		}
		// Configuration file contains text line personDataFile = employees.data
		// Apply BufferReader for reading configuration
		// TODO done
		String fileName = getFileName(args[0]);
		if (fileName == null) {
			io.writeObjectLine("No  configuration file");
			return;
		}
		if (fileName == WRONG_KEY_VALUE) {
			io.writeObjectLine("wrong key Value in configuration file");
			return;
		}
		EmployeesMethods employeesMethods = new EmployeesMethodsMapsImpl(fileName);
		employeesMethods.restore();
		HashSet<String> departments = new HashSet<>(Arrays.asList("QA", "Development", "HR", "Management"));
		Menu menu = new Menu("Employees Application", EmployeeActions.getActionItems(employeesMethods, departments));
		menu.perform(io);
	}

	private static String getFileName(String configFile) {
		Properties properties = new Properties();
		String res;
		if (!new File(configFile).exists()) {
			return null;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
			properties.load(br);
			res = properties.getProperty(CONFIGURATION_NAME);
			if (res == null || res.isEmpty() ) {
				return WRONG_KEY_VALUE;
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return res;
		// return properties.getProperty(CONFIGURATION_NAME,FILE_FOR_SAVING);
		// TODO done

	}
}