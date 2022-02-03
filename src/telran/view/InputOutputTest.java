package telran.view;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import telran.employees.dto.Employee;

class InputOutputTest {
InputOutput io = new ConsoleInputOutput();
	@BeforeEach
	void setUp() throws Exception {
	}

//	@Test
//	void employeeInputAsOneString() {
//		Employee empl = io.readObject("Enter employee data as string <id>#<name>#<birthdate ISO>#<salary>#<department>",
//				"Wrong format of employee data", InputOutputTest::toEmployeeFromStr);
//		io.writeObjectLine(empl);
//	}
//	static Employee toEmployeeFromStr(String str) {
//		String emplTokens[] = str.split("#");
//		long id = Long.parseLong ( emplTokens[0] );
//		String name = emplTokens[1];
//		LocalDate birthDate = LocalDate.parse(emplTokens[2]);
//		int salary = Integer.parseInt(emplTokens[3]);
//		String department = emplTokens[4];
//		return new Employee(id, name, birthDate, salary, department);
//	}
//	
//	@Test 
//	void readPredicateTest() {
//		String str = io.readStringPredicate("enter any string containing exactly 3 symbols",
//				"this is no string containing exactly 3 symbols", s -> s.length() == 3);
//		assertEquals(3, str.length());
//	}
	@Test
	void employeeBySeparateFields() {
//enter ID by readLong method
		Long id = io.readLong("Enter Employees ID");
		io.writeObjectLine(id);
//		enter Name by readStringPredicate (only letters with capital first letter)
		String strName =  io.readStringPredicate("Enter name with capital first letter", "this name didnt start with capital first letter", 
				s -> s.matches("[A-Z][a-z]*"));
		io.writeObjectLine(strName);
//enter birthdate by readDate 
		LocalDate birthday = io.readDate("Write date");
		io.writeObjectLine(birthday);
//enter salary by readInt(prompt, min, max)
		Integer intInRange = io.readInt("write int in range", 2000, 10000);
		io.writeObjectLine(intInRange);
//enter department by readStringOption specifying possible departments
		Set<String> setDepartments =  new HashSet<>(Arrays.asList("dep1","dep2","dep3","dep4","dep5"));
		String department = io.readStringOptions("enter department", setDepartments);
		io.writeObjectLine(department);
//enter number of department by readStringOption specifying possible departments
		String departmentMenu = io.readStringOptions1("select number of department", setDepartments);
		io.writeObjectLine(departmentMenu);
//date with formatter		
		String formatter = "dd MM yyyy";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(formatter);
		LocalDate birthdayFormaated = io.readDate("Write date " + formatter, dtf);
		io.writeObjectLine(birthdayFormaated);
	}

}