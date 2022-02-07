package telran.employees.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import telran.employees.dto.Employee;
import telran.employees.services.EmployeesMethods;
import telran.employees.services.EmployeesMethodsMapsImpl;
import telran.view.InputOutput;
import telran.view.Item;

public class EmployeeActions {
private static final int MAX_AGE = 120;
private static final HashSet<String> DEPARTMENTS = new HashSet<>(Arrays.asList("qa", "developer", "manager", "tester"));
private static EmployeesMethods employees;
private EmployeeActions(){
 
}
static public ArrayList<Item> getEmployeesMenuItems(EmployeesMethods employees){
	
	EmployeeActions.employees = employees;
	ArrayList<Item> items = new ArrayList<>();
	
	items.add(Item.of("Add new Employee", EmployeeActions::addEmployee));
	items.add(Item.of("Remove Employee", EmployeeActions::removeEmployee));
	items.add(Item.of("Get Employee by ID", EmployeeActions::getEmployee));
	items.add(Item.of("Get all Employees", EmployeeActions::getAllEmployees));
	items.add(Item.of("Get Employees by age", EmployeeActions::getEmployeesByAge));
	items.add(Item.of("Get Employees by salary", EmployeeActions::getEmployeesBySalary));
	items.add(Item.of("Get Employees by department", EmployeeActions::getEmployeesByDepartment));
	items.add(Item.of("Get Employees by depatment and salary", EmployeeActions::getEmployeesByDepartmentAndSalary));
	items.add(Item.of("Update salary", EmployeeActions::updateSalary));
	items.add(Item.of("Update department", EmployeeActions::updateDepartment));
	items.add(Item.exit());

	return items;
}
static private Employee enterEmployee(InputOutput io) {
			long id = io.readLong("Enter ID", 0, Long.MAX_VALUE -1);
			String name = io.readStringPredicate("Enter name", "Name may contain only letters with first capital",
					str -> str.matches("[A-Z][a-z]+"));
			LocalDate birthDate = io.readDate("Enter birthdate in the yyyy-MM-dd format");
			int salary = io.readInt("Enter Salary", 0, Integer.MAX_VALUE-1);
			String department = io.readStringOption("Enter department from list" + DEPARTMENTS, DEPARTMENTS).toLowerCase();
			//String department = io.readString("enter department").toLowerCase();
			return  new Employee(id, name, birthDate, salary, department);
	
}

static private void addEmployee(InputOutput io) {
	io.writeObjectLine(employees.addEmployee(EmployeeActions.enterEmployee(io)));
}
static private  void removeEmployee(InputOutput io) {
	io.writeObjectLine(employees.removeEmployee(io.readInt("enter Id")));
}
static private  void getAllEmployees(InputOutput io) {
	 io.writeObjectLine(employees.getAllEmployees());
}
static private  void getEmployee(InputOutput io) {
	io.writeObjectLine(employees.getEmployee(io.readInt("enter id")));
}

static private  void getEmployeesByAge(InputOutput io) {
	int ageFrom = io.readInt("enter age range FROM", 0, MAX_AGE-1);
	int ageTo = io.readInt("enter age range TO",ageFrom+1, MAX_AGE);
	
	io.writeObjectLine((employees.getEmployeesByAge(ageFrom, ageTo)));
}

static private  void getEmployeesBySalary(InputOutput io) {
	int minSalary = io.readInt("Enter min range for salary", 0, Integer.MAX_VALUE-1);
	int maxSalary = io.readInt("Enter max range for salary", minSalary+1, Integer.MAX_VALUE);
	
	io.writeObjectLine(employees.getEmployeesBySalary(minSalary, maxSalary));
}

static private  void getEmployeesByDepartment(InputOutput io) {
	String department = io.readStringOption("Enter department " + DEPARTMENTS, DEPARTMENTS).toLowerCase();
	io.writeObjectLine(employees.getEmployeesByDepartment(department));
}

static private  void getEmployeesByDepartmentAndSalary(InputOutput io) {
	String department = io.readStringOption("Enter department from list" + DEPARTMENTS, DEPARTMENTS).toLowerCase();
	
	int minSalary = io.readInt("Enter min range for salary",0, Integer.MAX_VALUE-1);
	int maxSalary = io.readInt("Enter max range for salary", minSalary+1, Integer.MAX_VALUE);
	io.writeObjectLine(employees.getEmployeesByDepartmentAndSalary(department, minSalary, maxSalary));
}
static private  void updateSalary(InputOutput io) {
	
	long emplID = io.readLong("enter employees id for update salary");
	int newSalary = io.readInt("Enter new Salary", 0, Integer.MAX_VALUE);

	io.writeObjectLine(employees.updateSalary(emplID, newSalary));
}
static private  void updateDepartment(InputOutput io) {
	long emplID = io.readLong("enter employees id for update department");
	io.writeObjectLine("Now works in " + employees.getEmployee(emplID).department);
	String newDepartment = io.readStringOption("enter new department from list" + DEPARTMENTS, DEPARTMENTS).toLowerCase();
	io.writeObjectLine(employees.updateDepartment(emplID, newDepartment));

}

}
