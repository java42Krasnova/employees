package telran.employees.controller;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;
import telran.employees.services.EmployeesMethods;
import telran.net.Sender;
import static telran.employees.net.dto.ApiConstants.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
public class EmployeesMethodsTcpProxy implements EmployeesMethods {
private Sender sender;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmployeesMethodsTcpProxy(Sender sender) {
		this.sender = sender;
	}

	@Override
	public ReturnCode addEmployee(Employee empl) {
		
		return sender.send(ADD_EMPLOYEE, empl);
	}

	@Override
	public ReturnCode removeEmployee(long id) {
		//TODO done
		return sender.send(REMOVE_EMPLOYEE, id);
	}

	@Override
	public Iterable<Employee> getAllEmployees() {
		
		return sender.send(GET_EMPLOYEES, "");
	}

	@Override
	public Employee getEmployee(long id) {
		// TODO done
		return sender.send(DISPLAY_EMPLOYEE, id);
	}

	@Override
	public Iterable<Employee> getEmployeesByAge(int ageFrom, int ageTo) {
		//TOO done
		int[] ageRange = {ageFrom, ageTo};
		return sender.send(GET_EMPLOYEES_BY_AGE, ageRange);
	}

	@Override
	public Iterable<Employee> getEmployeesBySalary(int salaryFrom, int salaryTo) {
		//TOO done
		int[] salaryRange = {salaryFrom, salaryTo};
		return sender.send(GET_EMPLOYEES_BY_SALARY, salaryRange);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartment(String department) {
		// TODO done
		return sender.send(GET_EMPLOYEES_BY_DEPARTMENT, department);
	}

	@Override
	public Iterable<Employee> getEmployeesByDepartmentAndSalary(String department, int salaryFrom, int salaryTo) {
		// TODO Done
		HashMap<String, Object> employeesBySalaryInDepInfo = new HashMap<>();
		int[] salaryRange = {salaryFrom, salaryTo};
		employeesBySalaryInDepInfo.put(department, salaryRange);
		return sender.send(GET_EMPLOYEES_BY_SALARY_IN_DEPARTMENT, employeesBySalaryInDepInfo);
	}

	@Override
	public ReturnCode updateSalary(long id, int newSalary) {
		// TODO Done
		HashMap<Long,Integer> salaryUpdateInfo = new HashMap<>();
		salaryUpdateInfo.put(id, newSalary);
		return sender.send(UPDATE_SALARY, salaryUpdateInfo);
	}

	@Override
	public ReturnCode updateDepartment(long id, String newDepartment) {
		// TODO Done
		HashMap<String,Object> departmentUpdateInfo = new HashMap<>();
		departmentUpdateInfo.put(newDepartment,id);
		return sender.send(UPDATE_DEPARTMENT, departmentUpdateInfo);
	}

	@Override
	public void restore() {
		// TODO Auto-generated method stub
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

}