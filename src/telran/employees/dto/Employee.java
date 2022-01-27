package telran.employees.dto;

import java.time.LocalDate;
import java.util.Objects;

public class Employee {
public long id;
public String name;
public LocalDate birthDate;
public int salary;
public String department;
public Employee(long id, String name, LocalDate birthDate, int salary, String department) {
	this.id = id;
	this.name = name;
	this.birthDate = birthDate;
	this.salary = salary;
	this.department = department;
}

public Employee() {
	
}
public Employee(Employee employee) {
	id = employee.id;
	name = employee.name;
	birthDate = employee.birthDate;
	salary = employee.salary;
	department =employee.department;
}


@Override
public int hashCode() {
	return Objects.hash(id);
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	Employee other = (Employee) obj;
	return id == other.id;
}

}