
package telran.employees.services;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.StreamSupport;

public class EmployeesMethodsMapsImpl implements EmployeesMethods {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmployeesMethodsMapsImpl(String fileName) {
		this.fileName = fileName;
	}
	private transient String fileName;// field
	private HashMap<Long, Employee> mapEmployees; //= new HashMap<>(); // key employee's id, value - employee
	private TreeMap<Integer, List<Employee>> employeesAge; //= new TreeMap<>(); // key - age, value - list of employees
																				// with the same age
	private TreeMap<Integer, List<Employee>> employeesSalary; //= new TreeMap<>(); // key - salary,
	// value - list of employees with the same salary
	private HashMap<String, List<Employee>> employeesDepartment; //= new HashMap<>();

	@Override
	public ReturnCode addEmployee(Employee empl) {
		if (mapEmployees.containsKey(empl.id)) {
			return ReturnCode.EMPLOYEE_ALREADY_EXISTS;
		}
		Employee emplS = copyOneEmployee(empl);
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
		Employee empl = mapEmployees.remove(id);
		if (empl == null) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		employeesAge.get(getAge(empl)).remove(empl);
		employeesDepartment.get(empl.department).remove(empl);
		employeesSalary.get(empl.salary).remove(empl);
		return ReturnCode.OK;
	}

	@Override
	public Iterable<Employee> getAllEmployees() {

		return copyEmployees(mapEmployees.values());
	}

	private Iterable<Employee> copyEmployees(Collection<Employee> employees) {

		return employees.stream().map(empl -> copyOneEmployee(empl)).toList();
	}

	private Employee copyOneEmployee(Employee empl) {
		return new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department);
	}

	@Override
	public Employee getEmployee(long id) {
		Employee empl = mapEmployees.get(id);
		return empl == null ? null : copyOneEmployee(empl);
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		Collection<List<Employee>> lists = employeesAge.subMap(ageFrom, true, ageTo, true).values();
		List<Employee> employeesList = getCombinedList(lists);
		return copyEmployees(employeesList);
	}

	private List<Employee> getCombinedList(Collection<List<Employee>> lists) {

		return lists.stream().flatMap(List::stream).toList();
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		Collection<List<Employee>> lists = employeesSalary.subMap(salaryFrom, true, salaryTo, true).values();
		List<Employee> employeesList = getCombinedList(lists);
		return copyEmployees(employeesList);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		List<Employee> employees = employeesDepartment.getOrDefault(department, Collections.emptyList());

		return employees.isEmpty() ? employees : copyEmployees(employees);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		Iterable<Employee> employeesByDepartment = getEmployeesByDepartment(department);
		HashSet<Employee> employeesBySalary = new HashSet<>(
				(List<Employee>) getEmployeesBySalary(salaryFrom, salaryTo));

		return StreamSupport.stream(employeesByDepartment.spliterator(), false)
				// .filter(employeesBySalary::contains).toList();
				.filter(c -> employeesBySalary.contains(c)).toList();
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		Employee empl = mapEmployees.get(id);
		if (empl == null) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		if (empl.salary == newSalary) {
			return ReturnCode.SALARY_NOT_UPDATED;
		}
		employeesSalary.get(empl.salary).remove(empl);
		empl.salary = newSalary;
		employeesSalary.computeIfAbsent(empl.salary, k -> new LinkedList<Employee>()).add(empl);
		return ReturnCode.OK;
	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		Employee empl = mapEmployees.get(id);
		if (empl == null) {
			return ReturnCode.EMPLOYEE_NOT_FOUND;
		}
		if (empl.department.equals(newDepartment)) {
			return ReturnCode.DEPARTMENT_NOT_UPDATED;
		}
		employeesDepartment.get(empl.department).remove(empl);
		empl.department = newDepartment;
		employeesDepartment.computeIfAbsent(empl.department, k -> new LinkedList<Employee>()).add(empl);
		return ReturnCode.OK;
	}

	@Override
	public void save() {
		// TODO done
		File outputFile = new File(fileName);
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outputFile));){
			outputStream.writeObject(this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void restore() {
		// TODO done
		File inputFile = new File(fileName);
		System.out.println(fileName);
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(inputFile));) {
			EmployeesMethodsMapsImpl restoredMaps = (EmployeesMethodsMapsImpl) inputStream.readObject();
			this.employeesAge = restoredMaps.employeesAge;
			this.employeesDepartment = restoredMaps.employeesDepartment;
			this.employeesSalary = restoredMaps.employeesSalary;
			this.mapEmployees = restoredMaps.mapEmployees;
		} catch (Exception e) {
			this.mapEmployees = new HashMap<>();
			this.employeesAge = new TreeMap<>();
			this.employeesSalary = new TreeMap<>();
			this.employeesDepartment = new HashMap<>();
		}
	}

}