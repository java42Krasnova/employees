package telran.employees.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import telran.employees.dto.*;

class EmployeesMethodsTests {
	private static final long ID1 = 123;
	private static final String NAME = "name";
	private static final LocalDate BIRTHDATE1 = LocalDate.of(2000, 1, 1);
	private static final int SALARY1 = 5000;
	private static final String DEPARTMENT1 = "department1";
	private static final long ID2 = 124;
	private static final long ID3 = 125;
	private static final long ID4 = 126;
	private static final long ID5 = 127;
	private static final long ID6 = 128;
	private static final LocalDate BIRTHDATE2 = LocalDate.of(1995, 1, 1);
	private static final LocalDate BIRTHDATE3 = LocalDate.of(1997, 1, 1);
	private static final LocalDate BIRTHDATE4 = LocalDate.of(1970, 1, 1);
	private static final LocalDate BIRTHDATE5 = LocalDate.of(1971, 1, 1);
	private static final LocalDate BIRTHDATE6 = LocalDate.of(1980, 1, 1);
	private static final String DEPARTMENT2 = "department2";
	private static final int SALARY2 = 6000;
	private static final int SALARY3 = 1000;
	EmployeesMethods employees;
	Employee empl1 = new Employee(ID1, NAME, BIRTHDATE1, SALARY1, DEPARTMENT1);
	Employee empl2 = new Employee(ID2, NAME, BIRTHDATE2, SALARY1, DEPARTMENT1);
	Employee empl3 = new Employee(ID3, NAME, BIRTHDATE3, SALARY2, DEPARTMENT1);
	Employee empl4 = new Employee(ID4, NAME, BIRTHDATE4, SALARY2, DEPARTMENT2);
	Employee empl5 = new Employee(ID5, NAME, BIRTHDATE5, SALARY3, DEPARTMENT2);
	Employee empl6 = new Employee(ID6, NAME, BIRTHDATE6, SALARY3, DEPARTMENT2);
	List<Employee> employeesList = Arrays.asList(empl1, empl2, empl3, empl4, empl5, empl6);

	@BeforeEach
	void setUp() throws Exception {
		employees = new EmployeesMethodsMapsImpl();
		employeesList.forEach(employees::addEmployee);

	}

	@Test
	void testAddEmployee() {
		assertEquals(ReturnCode.EMPLOYEE_ALREADY_EXISTS, employees.addEmployee(empl1));
		assertEquals(ReturnCode.OK,
				employees.addEmployee(new Employee(ID1 + 10000, NAME, BIRTHDATE1, SALARY1, DEPARTMENT1)));
	}

	@Test
	void testRemoveEmployee() {
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.removeEmployee(999));
		assertEquals(ReturnCode.OK, employees.removeEmployee(ID1));
		List<Employee> exp = Arrays.asList(empl2, empl3, empl4, empl5, empl6);
		for (Employee e : employees.getAllEmployees()) {
			assertTrue(exp.contains(e));
		}
		List<Employee> list = (List<Employee>) employees.getEmployeesByAge(22, 23);
		assertFalse(list.contains(empl1));
		List<Employee> list1 = (List<Employee>) employees.getEmployeesByDepartment(DEPARTMENT1);
		assertFalse(list1.contains(empl1));
		List<Employee> list2 = (List<Employee>) employees.getEmployeesBySalary(SALARY1, SALARY1 + 3);
		assertFalse(list2.contains(empl1));
		assertEquals(ReturnCode.OK, employees.removeEmployee(ID2));
		List<Employee> list3 = (List<Employee>) employees.getEmployeesBySalary(SALARY1, SALARY1 + 3);
		assertFalse(list3.contains(empl2));
		EmployeesMethods employeesTmp = new EmployeesMethodsMapsImpl();
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employeesTmp.removeEmployee(ID1));

	}

	@Test
	void testGetAllEmployees() {
		Iterable<Employee> col = employees.getAllEmployees();
		for (Employee e : col) {
			assertTrue(employeesList.contains(e));
		}
		EmployeesMethods employeesTmp = new EmployeesMethodsMapsImpl();
		List<Employee> act = (List<Employee>) (employeesTmp.getAllEmployees());
		assertTrue(act.isEmpty());
	}

	@Test
	void testGetEmployee() {
		assertEquals(empl1, employees.getEmployee(ID1));
		assertNull(employees.getEmployee(99));

		EmployeesMethods employeesTmp = new EmployeesMethodsMapsImpl();
		assertNull(employeesTmp.getEmployee(99));
	}

	@Test
	void testGetEmployeesByAge() {
		List<Employee> exp = Arrays.asList(empl1, empl2, empl3);
		for (Employee e : employees.getEmployeesByAge(22, 30)) {
			assertTrue(exp.contains(e));
		}
		List<Employee> act = (List<Employee>) employees.getEmployeesByAge(1, 5);
		assertTrue(act.isEmpty());
		act = (List<Employee>) employees.getEmployeesByAge(50, 20);
		assertTrue(act.isEmpty());

	}

	@Test
	void testGetEmployeesBySalary() {
		List<Employee> exp = Arrays.asList(empl6, empl5, empl1, empl2);
		List<Employee> act = (List<Employee>) employees.getEmployeesBySalary(1000, SALARY2);
		for (Employee e : act) {
			assertTrue(exp.contains(e));
		}
		assertEquals(4, act.size());
		assertIterableEquals(Collections.emptyList(), employees.getEmployeesBySalary(8000, 10000));
		act = (List<Employee>) employees.getEmployeesBySalary(9000, 1000);
		assertTrue(act.isEmpty());
		act = (List<Employee>) employees.getEmployeesBySalary(9, 1000);
		assertTrue(act.isEmpty());

	}

	@Test
	void testGetEmployeesByDepartment() {
		List<Employee> expected = Arrays.asList(empl1, empl2, empl3);
		assertIterableEquals(expected, employees.getEmployeesByDepartment(DEPARTMENT1));
		List<Employee> act = (List<Employee>) employees.getEmployeesByDepartment("ddd");
		assertTrue(act.isEmpty());
	}

	@Test
	void testGetEmployeesByDepartmentAndSalary() {
		List<Employee> expected = Arrays.asList(empl5, empl6);
		assertIterableEquals(expected, employees.getEmployeesByDepartmentAndSalary(DEPARTMENT2, 200, 2000));
	}

	@Test
	void testUpdateSalary() {
		assertEquals(ReturnCode.OK, employees.updateSalary(ID3, 60000));
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.updateSalary(999, 60000));
		assertEquals(60000, employees.getEmployee(ID3).salary);
		for (Employee e : employees.getEmployeesBySalary(60000, 70000)) {
			System.out.printf("\n%d id %d sallary\n", e.id, e.salary);
		assertEquals(ReturnCode.OK, employees.updateSalary(ID4, 50000));
		List<Employee> tmp = (List<Employee>) employees.getEmployeesBySalary(SALARY2, SALARY2 + 1);
		assertTrue(tmp.isEmpty());
		assertEquals(ReturnCode.OK, employees.updateSalary(ID4, SALARY2));
		assertNotEquals(1, tmp.size());
		EmployeesMethods employeesTmp = new EmployeesMethodsMapsImpl();
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employeesTmp.updateSalary(999, 60000));

		}
	}

	@Test
	void testUpdateDepartment() {
		assertEquals(ReturnCode.OK, employees.updateDepartment(ID6, "department updated"));
		assertEquals(ReturnCode.EMPLOYEE_NOT_FOUND, employees.updateDepartment(888, "department updated"));
		assertEquals("department updated", employees.getEmployee(ID6).department);
		assertIterableEquals(Arrays.asList(employees.getEmployee(ID6)),
				employees.getEmployeesByDepartment("department updated"));

	}

}
