package telran.numbers.controller;

import java.util.ArrayList;

import telran.numbers.servises.Calculator;
import telran.view.InputOutput;
import telran.view.Item;

public class CalculatorActions {
private static Calculator calculator;
private CalculatorActions() {
	
}
static public ArrayList<Item> getCalculator(Calculator calculator){
	CalculatorActions.calculator = calculator;
	ArrayList<Item> items = new ArrayList<>();
	items.add(Item.of("Add two numbers", CalculatorActions::add));
	items.add(Item.of("Subtract two numbers", CalculatorActions::subtract));
	items.add(Item.of("Multiply two numbers", CalculatorActions::multiply));
	items.add(Item.of("Divide two numbers", CalculatorActions::divide));

	return items;
}
static private double[] enterTwoNumber(InputOutput io) {
	return new double[] {
			io.readDouble("enter first number"),
			io.readDouble("enter second number")
	};
}
static private void add(InputOutput io) {
	double [] numbers = enterTwoNumber(io);
	io.writeObjectLine(calculator.compute("+", numbers[0],  numbers[1]));
}
static private void subtract(InputOutput io) {
	double [] numbers = enterTwoNumber(io);
	io.writeObjectLine(calculator.compute("-", numbers[0],  numbers[1]));
}
static private void multiply(InputOutput io) {
	double [] numbers = enterTwoNumber(io);
	io.writeObjectLine(calculator.compute("*", numbers[0],  numbers[1]));
}
static private void divide(InputOutput io) {
	double [] numbers = enterTwoNumber(io);
	io.writeObjectLine(calculator.compute("/", numbers[0],  numbers[1]));
}
}