package telran.employees.services;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import java.io.*;

public class EmployeesMethodsMapsImpl implements EmployeesMethods {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmployeesMethodsMapsImpl(String fileName) {
		this.fileName = fileName;
	}

	private transient String fileName; // field won't be serialized
	private HashMap<Long, Employee> mapEmployees = new HashMap<>(); // key employee's id, value - employee
	private TreeMap<Integer, List<Employee>> employeesAge = new TreeMap<>(); // key - age, value - list of employees
																				// with the same age
	private TreeMap<Integer, List<Employee>> employeesSalary = new TreeMap<>(); // key - salary,
	// value - list of employees with the same salary
	private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();

	static private ReadWriteLock lock1 = new ReentrantReadWriteLock();
	static private Lock readLock1Employees = lock1.readLock();
	static private Lock writeLock1Employee = lock1.writeLock();

	static private ReadWriteLock lock2 = new ReentrantReadWriteLock();
	static private Lock readLock2EmployeesAge = lock2.readLock();
	static private Lock writeLock2EmployeeAge = lock2.writeLock();

	static private ReadWriteLock lock3 = new ReentrantReadWriteLock();
	static private Lock readLock3EmployeesSalary = lock3.readLock();
	static private Lock writeLock3EmployeeSalary = lock3.writeLock();

	static private ReadWriteLock lock4 = new ReentrantReadWriteLock();
	static private Lock readLock4EmployeesDepartment = lock4.readLock();
	static private Lock writeLock4EmployeeDepartment = lock4.writeLock();
	
	 /* 
	  static private List<ReentrantReadWriteLock> readRwriteLockList =
	  getLockList(); static private List<Lock> readLockList = getReadLockList();
	  static private List<Lock> writeLocksList = getwriteLockList();
	 * 
	 * private static List<ReentrantReadWriteLock> getLockList() { return
	 * IntStream.range(0, 4).mapToObj(i -> new ReentrantReadWriteLock()).toList(); }
	 * 
	 * private static List<Lock> getwriteLockList() { List<Lock> res = new
	 * ArrayList<>(); readRwriteLockList.forEach(rw -> res.add(rw.writeLock())); //
	 * readRwriteLockList.stream().map(rw -> rw.writeLock()).toList(); return res; }
	 * private static List<Lock> getReadLockList() { List<Lock> res = new
	 * ArrayList<>(); readRwriteLockList.forEach(rw -> res.add(rw.readLock()));
	 * return res; }
	 */

	@Override
	public ReturnCode addEmployee(Employee empl) {
		try {
			readLock1Employees.lock();
			if (mapEmployees.containsKey(empl.id)) {
				return ReturnCode.EMPLOYEE_ALREADY_EXISTS;
			}
			readLock1Employees.unlock();

			Employee emplS = copyOneEmployee(empl);

			writeLock1Employee.lock();
			mapEmployees.put(emplS.id, emplS);
			writeLock1Employee.unlock();

			writeLock2EmployeeAge.lock();
			employeesAge.computeIfAbsent(getAge(emplS), k -> new LinkedList<Employee>()).add(emplS);
			writeLock2EmployeeAge.unlock();

			writeLock3EmployeeSalary.lock();
			employeesSalary.computeIfAbsent(emplS.salary, k -> new LinkedList<Employee>()).add(emplS);
			writeLock3EmployeeSalary.unlock();

			writeLock4EmployeeDepartment.lock();
			employeesDepartment.computeIfAbsent(emplS.department, k -> new LinkedList<Employee>()).add(emplS);
			return ReturnCode.OK;

		} finally {
			readLock1Employees.unlock();
			writeLock1Employee.unlock();
			writeLock2EmployeeAge.unlock();
			writeLock3EmployeeSalary.unlock();
			writeLock4EmployeeDepartment.unlock();
		}
	}

	private Integer getAge(Employee emplS) {

		return (int) ChronoUnit.YEARS.between(emplS.birthDate, LocalDate.now());
	}

	@Override
	public ReturnCode removeEmployee(long id) {

		try {
			writeLock1Employee.lock();
			Employee empl = mapEmployees.remove(id);
			if (empl == null) {
				return ReturnCode.EMPLOYEE_NOT_FOUND;
			}
			writeLock1Employee.unlock();

			writeLock2EmployeeAge.lock();
			employeesAge.get(getAge(empl)).remove(empl);
			writeLock2EmployeeAge.unlock();

			writeLock3EmployeeSalary.lock();
			employeesSalary.get(empl.salary).remove(empl);
			writeLock3EmployeeSalary.unlock();

			writeLock4EmployeeDepartment.lock();
			employeesDepartment.get(empl.department).remove(empl);
			return ReturnCode.OK;
		} finally {
			writeLock1Employee.unlock();
			writeLock2EmployeeAge.unlock();
			writeLock3EmployeeSalary.unlock();
			writeLock4EmployeeDepartment.unlock();
		}

	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		try {
			readLock1Employees.lock();
			return copyEmployees(mapEmployees.values());
		} finally {
			readLock1Employees.unlock();
		}
	}

	private Iterable<Employee> copyEmployees(Collection<Employee> employees) {
		return employees.stream().map(empl -> copyOneEmployee(empl)).toList();
	}

	private Employee copyOneEmployee(Employee empl) {
		return new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department);
	}

	@Override
	public Employee getEmployee(long id) {

		try {
			readLock1Employees.lock();
			Employee empl = mapEmployees.get(id);
			return empl == null ? null : copyOneEmployee(empl);
		} finally {
			readLock1Employees.unlock();
		}
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {

		try {
			readLock2EmployeesAge.lock();
			Collection<List<Employee>> lists = employeesAge.subMap(ageFrom, true, ageTo, true).values();
			List<Employee> employeesList = getCombinedList(lists);
			// V.R. It is good place for unlock(). getCombinedList() returns the copy
			return copyEmployees(employeesList);
		} finally {
			readLock2EmployeesAge.unlock();
		}

	}

	private List<Employee> getCombinedList(Collection<List<Employee>> lists) {

		return lists.stream().flatMap(List::stream).toList();
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		try {
			readLock3EmployeesSalary.lock();
			Collection<List<Employee>> lists = employeesSalary.subMap(salaryFrom, true, salaryTo, true).values();
			List<Employee> employeesList = getCombinedList(lists);
			// V.R. It is good place for unlock(). getCombinedList() returns the copy
			return copyEmployees(employeesList);
		} finally {
			readLock3EmployeesSalary.unlock();
		}

	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {

		try {
			readLock4EmployeesDepartment.lock();
			List<Employee> employees = employeesDepartment.getOrDefault(department, Collections.emptyList());
			return employees.isEmpty() ? employees : copyEmployees(employees);
		} finally {
			readLock4EmployeesDepartment.unlock();
		}

	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		Iterable<Employee> employeesByDepartment;
		HashSet<Employee> employeesBySalary;
		try {
			readLock3EmployeesSalary.lock();
			readLock4EmployeesDepartment.lock();
			employeesByDepartment = getEmployeesByDepartment(department);
			// V.R. The better place for readLock3EmployeesSalary.lock();
			employeesBySalary = new HashSet<>((List<Employee>) getEmployeesBySalary(salaryFrom, salaryTo));
			return StreamSupport.stream(employeesByDepartment.spliterator(), false).filter(employeesBySalary::contains)
					.toList();
		} finally {
			readLock3EmployeesSalary.unlock();
			readLock4EmployeesDepartment.unlock();
		}

	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		Employee empl;
		try {
			readLock1Employees.lock();
			empl = mapEmployees.get(id);
			if (empl == null) {
				return ReturnCode.EMPLOYEE_NOT_FOUND;
			}
			if (empl.salary == newSalary) {
				return ReturnCode.SALARY_NOT_UPDATED;
			}

			writeLock3EmployeeSalary.lock();
			employeesSalary.get(empl.salary).remove(empl);
			empl.salary = newSalary;
			employeesSalary.computeIfAbsent(empl.salary, k -> new LinkedList<Employee>()).add(empl);
			return ReturnCode.OK;
		} finally {
			readLock1Employees.unlock();
			writeLock3EmployeeSalary.unlock();
		}

	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		try {
			readLock1Employees.lock();
			Employee empl = mapEmployees.get(id);
			if (empl == null) {
				return ReturnCode.EMPLOYEE_NOT_FOUND;
			}
			if (empl.department.equals(newDepartment)) {
				return ReturnCode.DEPARTMENT_NOT_UPDATED;
			}

			writeLock4EmployeeDepartment.lock();
			employeesDepartment.get(empl.department).remove(empl);
			empl.department = newDepartment;
			employeesDepartment.computeIfAbsent(empl.department, k -> new LinkedList<Employee>()).add(empl);
			return ReturnCode.OK;
		} finally {
			readLock1Employees.unlock();
			writeLock4EmployeeDepartment.unlock();
		}

	}

	@Override
	public void restore() {
		File inputFile = new File(fileName);
		if (inputFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(inputFile))) {
				writeLock1Employee.lock();
				writeLock2EmployeeAge.lock();
				writeLock3EmployeeSalary.lock();
				writeLock4EmployeeDepartment.lock();
				EmployeesMethodsMapsImpl employeesFromFile = (EmployeesMethodsMapsImpl) input.readObject();
				this.employeesAge = employeesFromFile.employeesAge;
				this.employeesDepartment = employeesFromFile.employeesDepartment;
				this.employeesSalary = employeesFromFile.employeesSalary;
				this.mapEmployees = employeesFromFile.mapEmployees;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			} finally {
				writeLock1Employee.unlock();
				writeLock2EmployeeAge.unlock();
				writeLock3EmployeeSalary.unlock();
				writeLock4EmployeeDepartment.unlock();
			}
		}

	}

	@Override
	public void save() {
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
			// V.R. Where are lockers?
			output.writeObject(this);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}

	}

}