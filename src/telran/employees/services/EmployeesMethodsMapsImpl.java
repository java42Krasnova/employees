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
		updateMapAges(emplForRemove);
		updateMapSalary(emplForRemove);
		updateMapDepartments(emplForRemove);
		return ReturnCode.OK;
	}

	private void updateMapDepartments(Employee emplForRemove) {
		List<Employee> listEmplByDepartment = employeesDepartment.get(emplForRemove.department);
		listEmplByDepartment.remove(emplForRemove);
		if (listEmplByDepartment.isEmpty()) {
			employeesDepartment.remove(emplForRemove.department);
		}
	}

	private void updateMapSalary(Employee emplForRemove) {
		List<Employee> listEmplByDep = employeesSalary.get(emplForRemove.salary);
		listEmplByDep.remove(emplForRemove);
		if (listEmplByDep.isEmpty()) {
			employeesSalary.remove(emplForRemove.salary);
		}
	}

	private void updateMapAges(Employee emplForRemove) {
		List<Employee> listEmplByAge = employeesAge.get(getAge(emplForRemove));
		listEmplByAge.remove(emplForRemove);
		if (listEmplByAge.isEmpty()) {
			employeesAge.remove(getAge(emplForRemove));
		}
	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		return getEmployeesFromList(mapEmployees.values().stream().toList());
	}

	@Override
	public Employee getEmployee(long id) {
		// return mapEmployees.containsKey(id)? new Employee(id,
		// mapEmployees.get(id).name, mapEmployees.get(id).birthDate,
		// mapEmployees.get(id).salary, mapEmployees.get(id).department) : null ;

		return mapEmployees.containsKey(id) ? new Employee(mapEmployees.get(id)) : null;
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		List<Employee> listEmplByAge;
		try {
			listEmplByAge = employeesAge.subMap(ageFrom, true, ageTo, true).values().stream()
					.flatMap(c -> c.stream()).toList();
		} catch (IllegalArgumentException e) {
			return Collections.emptyList();
		}
		return getEmployeesFromList(listEmplByAge);
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		List<Employee> listEmplBySalary;
		try {
			listEmplBySalary = employeesSalary.subMap(salaryFrom, true, salaryTo, true).values().stream()
					.flatMap(s -> s.stream()).toList();
		} catch (IllegalArgumentException e) {
			return Collections.emptyList();
		}
		return  getEmployeesFromList(listEmplBySalary);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		List<Employee> listEmployeesInDepartment = employeesDepartment.getOrDefault(department,
				Collections.emptyList());
		return listEmployeesInDepartment.isEmpty() ? listEmployeesInDepartment
				: getEmployeesFromList(listEmployeesInDepartment);
	}

	private Iterable<Employee> getEmployeesFromList(List<Employee> employees) {
		return employees.stream()
				// .map(e -> new Employee(e.id, e.name, e.birthDate, e.salary,
				// e.department)).toList();
				.map(e -> new Employee(e)).toList();
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		List<Employee> listEmployeesInSallaryRange = employeesDepartment.get(department).stream()
				.filter(e -> e.salary >= salaryFrom && e.salary <= salaryTo).toList();
		return getEmployeesFromList(listEmployeesInSallaryRange);
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		if (!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee emplToUpdate = mapEmployees.get(id);
		updateMapSalary(emplToUpdate);
		emplToUpdate.salary = newSalary;
		employeesSalary.computeIfAbsent(newSalary, k -> new LinkedList<Employee>()).add(emplToUpdate);
		return ReturnCode.OK;
	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		if (!mapEmployees.containsKey(id)) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		Employee emplToUpdate = mapEmployees.get(id);
		updateMapDepartments(emplToUpdate);
		emplToUpdate.department = newDepartment;
		employeesDepartment.computeIfAbsent(newDepartment, k -> new LinkedList<Employee>()).add(emplToUpdate);
		return ReturnCode.OK;
	}

}