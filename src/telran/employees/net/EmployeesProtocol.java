package telran.employees.net;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;
import telran.employees.services.EmployeesMethods;
import telran.net.ApplProtocol;
import telran.net.dto.Request;
import telran.net.dto.Response;
import telran.net.dto.ResponseCode;
import java.util.*;
import static telran.employees.net.dto.ApiConstants.*;

import java.io.Serializable;
import java.security.KeyStore.Entry;

public class EmployeesProtocol implements ApplProtocol {
	public EmployeesProtocol(EmployeesMethods employees) {
		this.employees = employees;
	}

	private EmployeesMethods employees;

	@Override
	public Response getResponse(Request request) {
		switch (request.requestType) {
		case ADD_EMPLOYEE:
			return _employee_add(request.requestData);
		case GET_EMPLOYEES:
			return _get(request.requestData);
		case REMOVE_EMPLOYEE:
			return _employee_remove(request.requestData);
		case UPDATE_SALARY:
			return _employee_salary_update(request.requestData);
		case UPDATE_DEPARTMENT:
			return _employee_department_update(request.requestData);
		case DISPLAY_EMPLOYEE:
			return _employee_get(request.requestData);
		case GET_EMPLOYEES_BY_AGE:
			return _employee_age_get(request.requestData);
		case GET_EMPLOYEES_BY_SALARY:
			return _employee_salary_get(request.requestData);
		case GET_EMPLOYEES_BY_DEPARTMENT:
			return _employee_department_get(request.requestData);
		case GET_EMPLOYEES_BY_SALARY_IN_DEPARTMENT:
			return _employee_department_salary_get(request.requestData);
		// TODO done
		default:
			return new Response(ResponseCode.UNKNOWN_REQUEST, request.requestType + " not implemented");
		}

	}

	private Response _employee_department_salary_get(Serializable requestData) {
		try {
			// TODO done
			HashMap<String, Object> depSalaryRange = (HashMap<String, Object>) requestData;
			String dep = getDepartmentName(depSalaryRange);
			int[] salRange = (int[]) depSalaryRange.get(dep);
			List<Employee> responseData = new LinkedList<>();
			employees.getEmployeesByDepartmentAndSalary(dep, salRange[0], salRange[1]).forEach(responseData::add);
			return new Response(ResponseCode.OK, (Serializable) responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private String getDepartmentName(HashMap<String, Object> dataHash) {
		return dataHash.keySet().stream().findFirst().get();
	}

	private Response _employee_department_get(Serializable requestData) {
		// TODO done
		try {
			String department = (String) requestData;
			List<Employee> responseData = new LinkedList<>();
			employees.getEmployeesByDepartment(department).forEach(responseData::add);
			return new Response(ResponseCode.OK, (Serializable) responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}

	}

	private Response _employee_salary_get(Serializable requestData) {
		// TODO done
		try {
			int[] salaryRange = (int[]) requestData;
			List<Employee> responseData = new LinkedList<>();
			employees.getEmployeesBySalary(salaryRange[0], salaryRange[1]).forEach(responseData::add);
			return new Response(ResponseCode.OK, (Serializable) responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _employee_age_get(Serializable requestData) {
		// TODO done
		try {
			int[] ageRange = (int[]) requestData;
			List<Employee> responseData = new LinkedList<>();
			employees.getEmployeesByAge(ageRange[0], ageRange[1]).forEach(responseData::add);
			return new Response(ResponseCode.OK, (Serializable) responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _employee_get(Serializable requestData) {
		// TODO done
		try {
			long id = (long) requestData;
			return new Response(ResponseCode.OK, employees.getEmployee(id));
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}

	}

	private Response _employee_department_update(Serializable requestData) {
		// TODO done
		try {
			HashMap<String, Object> emplToUpdate = (HashMap<String, Object>) requestData;
			String dep = getDepartmentName(emplToUpdate);
			long id = (long) emplToUpdate.get(dep);
			ReturnCode responseData = employees.updateDepartment(id, dep);
			return new Response(ResponseCode.OK, responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _employee_salary_update(Serializable requestData) {
		// TODO Done
		try {
			HashMap<Long, Integer> salayaRange = (HashMap<Long, Integer>) requestData;
			long id = salayaRange.entrySet().stream().map(k -> k.getKey()).findFirst().get();
			int salary = salayaRange.get(id);
			ReturnCode responseData = employees.updateSalary(id, salary);
			return new Response(ResponseCode.OK, responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}

	}

	private Response _employee_remove(Serializable requestData) {
		// TODO Done
		try {
			long id = (long) requestData;
			ReturnCode responseData = employees.removeEmployee(id);
			return new Response(ResponseCode.OK, responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _get(Serializable requestData) {
		try {
			List<Employee> responseData = new ArrayList<>();
			employees.getAllEmployees().forEach(responseData::add);

			return new Response(ResponseCode.OK, (Serializable) responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _employee_add(Serializable requestData) {
		try {
			Employee employee = (Employee) requestData;
			ReturnCode responseData = employees.addEmployee(employee);
			return new Response(ResponseCode.OK, responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

}