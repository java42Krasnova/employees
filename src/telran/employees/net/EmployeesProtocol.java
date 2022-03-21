package telran.employees.net;

import telran.employees.dto.Employee;
import telran.employees.dto.ReturnCode;
import telran.employees.services.EmployeesMethods;
import telran.net.ApplProtocol;
import telran.net.dto.Request;
import telran.net.dto.Response;
import telran.net.dto.ResponseCode;
import java.util.*;
import static telran.employees.net.dto.ApiConstants.*
;

import java.io.Serializable;
import java.lang.reflect.Method;public class EmployeesProtocol implements ApplProtocol {
public EmployeesProtocol(EmployeesMethods employees) {
		this.employees = employees;
	}

private EmployeesMethods employees;

	@Override
	public Response getResponse(Request request) {
		//TODO DONE
		String requestComand = request.requestType.replaceAll("/", "_");
		try {
			Method method = EmployeesProtocol.class.getDeclaredMethod(requestComand, Serializable.class);
			method.setAccessible(true);
			return	 (Response) method.invoke(this, request.requestData);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return  new Response(ResponseCode.UNKNOWN_REQUEST,
					request.requestType + " not implemented");
	}
	

	private Response _salary_update(Serializable requestData) {
		try {
			Map<String, Object> map = (Map<String,Object>)requestData;
			ReturnCode code = employees.updateSalary((Long)map.get(ID), (Integer)map.get(SALARY));
			
			return new Response(ResponseCode.OK, (Serializable)code);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}


	private Response _department_update(Serializable requestData) {
		try {
			Map<String, Object> map = (Map<String,Object>)requestData;
			ReturnCode code = employees.updateDepartment((Long)map.get(ID), (String)map.get(DEPARTMENT));
			
			return new Response(ResponseCode.OK, (Serializable)code);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}


	private Response _employee_remove(Serializable requestData) {
		try {
			Long id = (Long)requestData;
			ReturnCode code = employees.removeEmployee(id);
			
			return new Response(ResponseCode.OK, (Serializable)code);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}


	private Response _department_salary_get(Serializable requestData) {
		try {
			Map<String, Object> map = (Map<String,Object>)requestData;
			
			Integer[] fromTo = (Integer[])map.get(FROM_TO);
			String department = (String)map.get(DEPARTMENT);
			List<Employee> responseData = new ArrayList<>();
			employees.getEmployeesByDepartmentAndSalary(department, fromTo[0], fromTo[1]).forEach(responseData::add);
			
			return new Response(ResponseCode.OK, (Serializable)responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _department_get(Serializable requestData) {
		try {
			String department = (String)requestData;
			List<Employee> responseData = new ArrayList<>();
			employees.getEmployeesByDepartment(department).forEach(responseData::add);
			
			return new Response(ResponseCode.OK, (Serializable)responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _salary_get(Serializable requestData) {
		try {
			Integer[] fromTo = (Integer[])requestData;
			List<Employee> responseData = new ArrayList<>();
			employees.getEmployeesBySalary(fromTo[0], fromTo[1]).forEach(responseData::add);
			
			return new Response(ResponseCode.OK, (Serializable)responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _age_get(Serializable requestData) {
		try {
			Integer[] fromTo = (Integer[])requestData;
			List<Employee> responseData = new ArrayList<>();
			employees.getEmployeesByAge(fromTo[0], fromTo[1]).forEach(responseData::add);
			
			return new Response(ResponseCode.OK, (Serializable)responseData);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _employee_get(Serializable requestData) {
		try {
			Long id = (Long)requestData;
			Employee empl = employees.getEmployee(id);
			
			return new Response(ResponseCode.OK, (Serializable)empl);
		} catch (Exception e) {
			return new Response(ResponseCode.WRONG_REQUEST_DATA, e.getMessage());
		}
	}

	private Response _get(Serializable requestData) {
		try {
			List<Employee> responseData = new ArrayList<>();
			employees.getAllEmployees().forEach(responseData::add);
			
			return new Response(ResponseCode.OK, (Serializable)responseData);
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