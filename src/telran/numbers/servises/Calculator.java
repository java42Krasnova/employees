package telran.numbers.servises;
import java.util.*;
import java.util.function.BinaryOperator;
public class Calculator {
private static HashMap<String, BinaryOperator<Double>> mapOperators;
static {
	//constucting static filds
	mapOperators = new HashMap<>();
	mapOperators.put("+", (op1,op2) -> op1+op2);
	mapOperators.put("-", (op1,op2) -> op1-op2);
	mapOperators.put("*", (op1,op2) -> op1*op2);
	mapOperators.put("/", (op1,op2) -> op1/op2);
	//mapOperators.put(null, null)
}

public  double compute(String operator, double op1, double op2) {
	BinaryOperator<Double> metod = mapOperators.getOrDefault(operator,
			(a, b) -> {throw new IllegalArgumentException("unknow operator");});
return 	metod.apply(op1, op2);
}
}
