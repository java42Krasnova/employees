package telran.employees.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.StreamSupport;

import telran.employees.dto.Employee;
import telran.employees.services.EmployeesMethods;
import telran.employees.services.EmployeesMethodsMapsImpl;
import telran.view.InputOutput;
import telran.view.Item;

public class EmployeeActions {
private static final int MAX_AGE = 120;
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
			long id = io.readLong("Enter ID");
			String name = io.readStringPredicate("Enter name", "Name may contain only letters with first capital",
					str -> str.matches("[A-Z][a-z]+"));
			LocalDate birthDate = io.readDate("Enter birthdate in the yyyy-MM-dd format");
			int salary = io.readInt("Enter Salary");
			String department = io.readString("enter department").toLowerCase();
			return  new Employee(id, name, birthDate, salary, department);
}
 private static HashSet<String> getListDepartments(){
	List <Employee> allEmpl =  (List<Employee>) employees.getAllEmployees();
	 return new HashSet<>(allEmpl.stream().map(e -> e.department).toList());
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
	int ageFrom = io.readInt("enter age ranfe FROM");
	int ageTo = io.readInt("enter age ranfe TO",ageFrom+1, MAX_AGE);
	
	io.writeObjectLine((employees.getEmployeesByAge(ageFrom, ageTo)));
}

static private  void getEmployeesBySalary(InputOutput io) {
	int minSalary = io.readInt("Enter min range for salary");
	int maxSalary = io.readInt("Enter max range for salary", minSalary+1, Integer.MAX_VALUE);
	
	io.writeObjectLine(employees.getEmployeesBySalary(minSalary, maxSalary));
}

static private  void getEmployeesByDepartment(InputOutput io) {
	HashSet<String> departments = getListDepartments();
	String department = io.readStringOption("Enter department " + departments, departments).toLowerCase();
	io.writeObjectLine(employees.getEmployeesByDepartment(department));
}

static private  void getEmployeesByDepartmentAndSalary(InputOutput io) {
	HashSet<String> departments = getListDepartments();
	String department = io.readStringOption("Enter department from list" + departments, departments).toLowerCase();
	
	int minSalary = io.readInt("Enter min range for salary");
	int maxSalary = io.readInt("Enter max range for salary", minSalary+1, Integer.MAX_VALUE);

	io.writeObjectLine(employees.getEmployeesByDepartmentAndSalary(department, minSalary, maxSalary));
}
static private  void updateSalary(InputOutput io) {
	
	int emplID = io.readInt("enter employees id for update salary");
	int newSalary = io.readInt("Enter new Salary");
	if(newSalary < 0) {
		throw new IllegalArgumentException("salary must be greather then zerro");
	}
	io.writeObjectLine(employees.updateSalary(emplID, newSalary));
}
static private  void updateDepartment(InputOutput io) {
	int emplID = io.readInt("enter employees id for update department");
	io.writeObjectLine("Now works in " + employees.getEmployee(emplID).department);
	String newDepartment = io.readString("enter new department").toLowerCase();
	io.writeObjectLine(employees.updateDepartment(emplID, newDepartment));

}

}
