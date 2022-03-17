package telran.employees.services;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

	private transient String fileName; //field won't be serialized
 private HashMap<Long, Employee> mapEmployees = new HashMap<>(); //key employee's id, value - employee
 private TreeMap<Integer, List<Employee>> employeesAge= new TreeMap<>(); //key - age, value - list of employees with the same age
 private TreeMap<Integer, List<Employee>> employeesSalary = new TreeMap<>(); //key - salary,
 //value - list of employees with the same salary
 private HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
 static ReadWriteLock lockEmployees = new ReentrantReadWriteLock();
 static Lock readLockEmployees = lockEmployees.readLock();
 static Lock writeLockEmployees = lockEmployees.writeLock();
 static ReadWriteLock lockAge = new ReentrantReadWriteLock();
 static Lock readLockAge = lockAge.readLock();
 static Lock writeLockAge = lockAge.writeLock();
 static ReadWriteLock lockDepartment = new ReentrantReadWriteLock();
 static Lock readLockDepartment = lockDepartment.readLock();
 static Lock writeLockDepartment = lockDepartment.writeLock();
 static ReadWriteLock lockSalary = new ReentrantReadWriteLock();
 static Lock readLockSalary = lockSalary.readLock();
 static Lock writeLockSalary = lockSalary.writeLock();
 private void lockAllWrite() {
	 writeLockEmployees.lock();
	 writeLockAge.lock();
	 writeLockDepartment.lock();
	 writeLockSalary.lock();
 }
 private void unLockAllWrite() {
	 writeLockEmployees.unlock();
	 writeLockAge.unlock();
	 writeLockDepartment.unlock();
	 writeLockSalary.unlock();
 }
 private void lockDepartmentSalary() {
	 readLockDepartment.lock();
	 readLockSalary.lock();
 }
 private void unLockDepartmentSalary() {
	 readLockDepartment.unlock();
	 readLockSalary.unlock();
 }
 private void updateSalaryLock() {
	 readLockEmployees.lock();
	 writeLockSalary.lock();
 }
 private void updateSalaryUnlock() {
	 readLockEmployees.unlock();
	 writeLockSalary.unlock();
 }
 private void updateDepartmentLock() {
	 readLockEmployees.lock();
	 writeLockDepartment.lock();
 }
 private void updateDepartmentUnlock() {
	 readLockEmployees.unlock();
	 writeLockDepartment.unlock();
 }
	@Override
	public ReturnCode addEmployee(Employee empl) {
		try {
			lockAllWrite();
			if (mapEmployees.containsKey(empl.id)) {
				return ReturnCode.EMPLOYEE_ALREADY_EXISTS;
			}
			Employee emplS = copyOneEmployee(empl);
			mapEmployees.put(emplS.id, emplS);
			employeesAge.computeIfAbsent(getAge(emplS), k -> new LinkedList<Employee>()).add(emplS);
			employeesSalary.computeIfAbsent(emplS.salary, k -> new LinkedList<Employee>()).add(emplS);
			employeesDepartment.computeIfAbsent(emplS.department, k -> new LinkedList<Employee>()).add(emplS);
			return ReturnCode.OK;
		} finally {
			unLockAllWrite();
		}
	}

	private Integer getAge(Employee emplS) {
		
		return (int)ChronoUnit.YEARS.between(emplS.birthDate, LocalDate.now());
	}

	@Override
	public ReturnCode removeEmployee(long id) {
		try {
			lockAllWrite();
			Employee empl = mapEmployees.remove(id);
			if (empl == null) {
				return ReturnCode.EMPLOYEE_NOT_FOUND;
			}
			employeesAge.get(getAge(empl)).remove(empl);
			employeesDepartment.get(empl.department).remove(empl);
			employeesSalary.get(empl.salary).remove(empl);
			return ReturnCode.OK;
		} finally {
			unLockAllWrite();
		}
	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		
		try {
			readLockEmployees.lock();
			return copyEmployees(mapEmployees.values());
		} finally {
			readLockEmployees.unlock();
		}
	}

	private Iterable<Employee> copyEmployees(Collection<Employee> employees) {
		
		return employees.stream()
				.map(empl -> copyOneEmployee(empl))
				.toList();
	}

	private Employee copyOneEmployee(Employee empl) {
		return new Employee(empl.id, empl.name, empl.birthDate, empl.salary, empl.department);
	}

	@Override
	public Employee getEmployee(long id) {
		try {
			readLockEmployees.lock();
			Employee empl = mapEmployees.get(id);
			return empl == null ? null : copyOneEmployee(empl);
		} finally {
			readLockEmployees.unlock();
		}
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		try {
			readLockAge.lock();
			Collection<List<Employee>> lists = employeesAge.subMap(ageFrom, true, ageTo, true).values();
			List<Employee> employeesList = getCombinedList(lists);
			return copyEmployees(employeesList);
		} finally {
			readLockAge.unlock();
		}
	}

	private List<Employee> getCombinedList(Collection<List<Employee>> lists) {
		
		return lists.stream().flatMap(List::stream).toList();
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		try {
			readLockSalary.lock();
			Collection<List<Employee>> lists = employeesSalary.subMap(salaryFrom, true, salaryTo, true).values();
			List<Employee> employeesList = getCombinedList(lists);
			return copyEmployees(employeesList);
		} finally {
			readLockSalary.unlock();
		}
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		try {
			readLockDepartment.lock();
			List<Employee> employees = employeesDepartment.getOrDefault(department, Collections.emptyList());
			return employees.isEmpty() ? employees : copyEmployees(employees);
		} finally {
			readLockDepartment.unlock();
		}
	}

	

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom,
			int salaryTo) {
		try {
			lockDepartmentSalary();
			Iterable<Employee> employeesByDepartment = getEmployeesByDepartment(department);
			HashSet<Employee> employeesBySalary = new HashSet<>(
					(List<Employee>) getEmployeesBySalary(salaryFrom, salaryTo));
			return StreamSupport.stream(employeesByDepartment.spliterator(), false).filter(employeesBySalary::contains)
					.toList();
		} finally {
			unLockDepartmentSalary();
		}
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		try {
			updateSalaryLock();
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
		} finally {
			updateSalaryUnlock();
		}
	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		try {
			updateDepartmentLock();
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
		} finally {
			updateDepartmentUnlock();
		}
	}

	@Override
	public void restore() {
		File inputFile = new File(fileName);
		if (inputFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(inputFile))) {
				EmployeesMethodsMapsImpl employeesFromFile = (EmployeesMethodsMapsImpl) input.readObject();
				this.employeesAge = employeesFromFile.employeesAge;
				this.employeesDepartment =  employeesFromFile.employeesDepartment;
				this.employeesSalary = employeesFromFile.employeesSalary;
				this.mapEmployees = employeesFromFile.mapEmployees;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			} 
		}
		
	}

	@Override
	public void save() {
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
			output.writeObject(this);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
		
	}

}