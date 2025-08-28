package ui;

import java.util.ArrayList;

public class Ui {
	private static final String name = """
                $$$$$$$\\
                $$  __$$\\
                $$ |  $$ | $$$$$$\\   $$$$$$\\   $$$$$$\\  $$\\   $$\\
                $$$$$$$\\ | \\____$$\\ $$  __$$\\ $$  __$$\\ $$ |  $$ |
                $$  __$$\\  $$$$$$$ |$$ |  \\__|$$ |  \\__|$$ |  $$ |
                $$ |  $$ |$$  __$$ |$$ |      $$ |      $$ |  $$ |
                $$$$$$$  |\\$$$$$$$ |$$ |      $$ |      \\$$$$$$$ |
                \\_______/  \\_______|\\__|      \\__|       \\____$$ |
                                                        $$\\   $$ |
                                                        \\$$$$$$  |
                                                         \\______/""";

	private static final String greeting = "Hello from Barry, what can I do for you?";

	public void print(ArrayList<String> strings) {
		System.out.println("\t" + "_".repeat(50));
		for (String s : strings) {
			// Replace all '\n' with '\n\t'
			String formatted = s.replace("\n", "\n\t");

			// Format the first line
			formatted = "\t" + formatted;
			System.out.println(formatted);

		}
		System.out.println("\t" + "_".repeat(50));
	}

	public void printGreetings() {
		ArrayList<String> s = new ArrayList<>();
		s.add(name);
		s.add(greeting);
		this.print(s);
	}
}

