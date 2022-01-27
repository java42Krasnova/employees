package telran.employees.services;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EmployeesMethodsMapsImpl implements EmployeesMethods {
	private HashMap<Long, Employee> mapEmployees = new HashMap<>(); // key employee's id, value - employee
	private TreeMap<Integer, List<Employee>> employeesAge = new TreeMap<>(); // key - age, value - list of employees
																				// with the same age
	private TreeMap<Integer, List<Employee>> employeesSalary = new TreeMap<>(); // key - salary,
	// value - list of employees with the same salary
	private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();

	@Override
	public ReturnCode addEmployee(Employee empl) {
		if (mapEmployees.containsKey(empl.id)) {
			return ReturnCode.EMPLOYEE_ALREADY_EXISTS;
		}
		Employee emplS = new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department);
		mapEmployees.put(emplS.id, emplS);
		employeesAge.computeIfAbsent(getAge(emplS), k -> new LinkedList<Employee>()).add(emplS);
		employeesSalary.computeIfAbsent(emplS.salary, k -> new LinkedList<Employee>()).add(emplS);
		employeesDepartment.computeIfAbsent(emplS.department, k -> new LinkedList<Employee>()).add(emplS);

		return ReturnCode.OK;
	}

	private Integer getAge(Employee emplS) {
		return (int) ChronoUnit.YEARS.between(emplS.birthDate, LocalDate.now());
	}

	@Override
	public ReturnCode removeEmployee(long id) {
		if (!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee emplForRemove = mapEmployees.remove(id);
		removingFromMaps(emplForRemove);
		return ReturnCode.OK;
	}

	private void removingFromMaps(Employee emplForRemove) {
		List<Employee> valueListTmp = employeesAge.get(getAge(emplForRemove));
		if (isNeedRemoveKeyFromMap(valueListTmp, emplForRemove)) {
			employeesAge.remove(getAge(emplForRemove));
		}
		valueListTmp = employeesSalary.get(emplForRemove.salary);
		if (isNeedRemoveKeyFromMap(valueListTmp, emplForRemove)) {
			employeesSalary.remove(emplForRemove.salary);
		}
		valueListTmp = employeesDepartment.get(emplForRemove.department);
		if (isNeedRemoveKeyFromMap(valueListTmp, emplForRemove)) {
			employeesDepartment.remove(emplForRemove.department);
		}
	}

	private boolean isNeedRemoveKeyFromMap(List<Employee> valueListTmp, Employee emplForRemove) {
		valueListTmp.remove(emplForRemove);
		return valueListTmp.isEmpty();
	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		List<Employee> allEmployeesList = mapEmployees.isEmpty()?Collections.emptyList():
				 mapEmployees.values().stream().toList();
		return allEmployeesList.isEmpty() ? allEmployeesList
				: getEmployeesFromList(allEmployeesList);
	}

	@Override
	public Employee getEmployee(long id) {
		return  mapEmployees.get(id);
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		if(ageTo < ageFrom || employeesAge.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Integer, List<Employee>> mapEmplByAge = employeesAge.subMap(ageFrom, ageTo);
		List<Employee> listEmplByAge = mapEmplByAge.isEmpty() ? Collections.emptyList():
			mapEmplByAge.values().stream().flatMap(c -> c.stream()).toList();
		return listEmplByAge.isEmpty() ? listEmplByAge : getEmployeesFromList(listEmplByAge);
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		if(salaryTo < salaryFrom || employeesSalary.isEmpty()) {
			return Collections.emptyList();
		}
		Map<Integer, List<Employee>> mapEmplBySalary = employeesSalary.subMap(salaryFrom, salaryTo);
		List<Employee> listEmplBySalary = mapEmplBySalary.isEmpty()? Collections.emptyList():
			mapEmplBySalary.values().stream().flatMap(s -> s.stream()).toList();

		return listEmplBySalary.isEmpty()? Collections.emptyList():getEmployeesFromList(listEmplBySalary); 
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		List<Employee> listEmployeesInDepartment = employeesDepartment.getOrDefault(department, Collections.emptyList());
		return listEmployeesInDepartment.isEmpty() ? listEmployeesInDepartment : getEmployeesFromList(listEmployeesInDepartment);
	}

	private Iterable<Employee> getEmployeesFromList(List<Employee> employees) {
		return employees.stream()
				.map(e -> new Employee(e.id, e.name, e.birthDate, e.salary, e.department)).toList();
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		if(employeesDepartment.isEmpty()|| !employeesDepartment.containsKey(department) || salaryTo < salaryFrom) {
			return Collections.emptyList();
		}
		List<Employee> listEmployeesInSallaryRange = employeesDepartment.get(department).stream()
			.filter(e -> e.salary >= salaryFrom && e.salary < salaryTo).toList();
		
		return listEmployeesInSallaryRange.isEmpty()? listEmployeesInSallaryRange: getEmployeesFromList(listEmployeesInSallaryRange);
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		if (!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee emplToUpdate = getEmployee(id);
		List<Employee> tmpListEmpl = employeesSalary.get(emplToUpdate.salary);
		if(isNeedRemoveKeyFromMap(tmpListEmpl, emplToUpdate)){
			employeesSalary.remove(emplToUpdate.salary);
		}
		emplToUpdate.salary = newSalary;
		employeesSalary.computeIfAbsent(newSalary, k -> new LinkedList<Employee>()).add(emplToUpdate);
		return ReturnCode.OK;
	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		if (!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee emplToUpdate = getEmployee(id);
		List<Employee> tmpListEmpl = employeesDepartment.get(emplToUpdate.department);
		if(isNeedRemoveKeyFromMap(tmpListEmpl, emplToUpdate)){
			employeesDepartment.remove(emplToUpdate.department);
		}
		emplToUpdate.department = newDepartment;
		employeesDepartment.computeIfAbsent(newDepartment, k -> new LinkedList<Employee>()).add(emplToUpdate);
		return ReturnCode.OK;
	}

}