package telran.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InputOutput {
	String readString(String prompt);

	void writeObject(Object obj);

	default void writeObjectLine(Object obj) {
		writeObject(obj.toString() + "\n");
	}

	default <R> R readObject(String prompt, String errorPrompt, Function<String, R> mapper) {
		while (true) {
			String string = readString(prompt);
			try {
				R result = mapper.apply(string);
				return result;
			} catch (Exception e) {
				writeObjectLine(errorPrompt);
			}
		}
	}

	default String readStringPredicate(String prompt, String errorMessage, Predicate<String> predicate) {
		return readObject(prompt, errorMessage, str -> {
			if (predicate.test(str)) {
				return str;
			}
			throw new IllegalArgumentException();
		});
	}

	default Integer readInt(String prompt) {
		String errorMessage = "This is not number";
		return readObject(prompt, errorMessage, n -> {
			try {
				return Integer.parseInt(n);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		});

	}

	default Integer readInt(String prompt, int min, int max) {
		String errorMessage = String.format("Input must  be integer in range[%d,%d]", min, max);
		return readObject(String.format(prompt + " from %d to %d", min, max), errorMessage, n -> {
			try {
				Integer num = Integer.parseInt(n);
				if (num > min && num < max) {
					return num;
				}
				throw new IllegalArgumentException();
			} catch (NumberFormatException e) {
				writeObjectLine("it's not number");
				throw new IllegalArgumentException();
			}
		});
	}

	default Long readLong(String prompt) {
		String errorMessage = "This is not number";
		return readObject(prompt, errorMessage, n -> {
			try {
				return Long.parseLong(n);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException();
			}
		});
	}

	default String readStringOptions(String prompt, Set<String> options) {
		String errorMessage = "Not in list of options";
		
		return readObject(prompt + "\n" + setToString(options) + "\n", errorMessage, o -> {
			if (options.contains(o)) {
				return o;
			}
			throw new IllegalArgumentException();
		});
	}

	default String setToString(Set<String> options) {
		StringBuffer setStr = new StringBuffer();
		for (String str : options) {
			setStr.append(str + "\n");
		}
		return setStr.toString();
	}
//version with menu options
	default String readStringOptions1(String prompt, Set<String> options) {
		Map<Integer, String> menu = new TreeMap<>();
		int i = 1;
		StringBuffer setStr = new StringBuffer();
		for (String str : options) {
			setStr.append(String.format("%d ", i) + str + "\n");
			menu.put(i++, str);
		}
		String errorMessage = "Not in list of options";
		return readObject(prompt + "\n" + setStr.toString(), errorMessage, o -> {
			
			try {
				Integer num = Integer.parseInt(o);
				String res = menu.get(num);
				if(res != null) {
				return menu.get(num);
				}
				throw new IllegalArgumentException();
			} catch (NumberFormatException e) {
				writeObjectLine("input is not number");
				throw new IllegalArgumentException();
			}
		});
	}

	default LocalDate readDate(String prompt) {
		String errorMessage = "wrong date format";
		return readObject(prompt + " in ISO format yyyy-mm-dd", errorMessage, d -> {
			try {
				return LocalDate.parse(d);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException();
			}
		});
	}

	default LocalDate readDate(String prompt, DateTimeFormatter formatter) {
		String errorMessage = "wrong date format";
		return readObject(prompt, errorMessage, d -> {
			try {
				return LocalDate.parse(d, formatter);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException();
			}
		});

	}
	// TODO write all default methods from UML schema
}