package telran.numbers.controller;

import java.util.ArrayList;

import telran.numbers.servises.Calculator;
import telran.view.ConsoleInputOutput;
import telran.view.InputOutput;
import telran.view.Item;
import telran.view.Menu;

public class CalculatorApp {

	public static void main(String[] args) {
		InputOutput io = new ConsoleInputOutput();
		Calculator calculator =  new Calculator();
		ArrayList<Item> items = CalculatorActions.getCalculator(calculator);
		items.add(Item.exit());
		Menu menu = new Menu("Calculator menu", items);
		menu.perform(io);
	}

}
