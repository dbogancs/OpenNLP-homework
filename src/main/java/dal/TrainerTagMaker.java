package dal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TrainerTagMaker {

	private static boolean lastWasTagged = false;

	public static void make(String input, String output)
			throws FileNotFoundException, IOException {

		String tagedContent = null;

		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(processing(line));
				sb.append(System.lineSeparator());
				line = br.readLine();
			}

			tagedContent = sb.toString()
					.replace("\n", "")
					.replace("\r", "")
					.substring(1);
		}

		ArrayList<String> list = new ArrayList<String>();
		list.add(tagedContent);
		Path file = Paths.get(output);
		Files.write(file, list, Charset.forName("UTF-8"));
	}

	private static String processing(String line) {

		if (line == null) return "";
		String[] linesplits = line.split(";");
		if (linesplits.length != 4) return "";

		String sentence = linesplits[0];
		String word = linesplits[1];
		String POS = linesplits[2];
		String category = linesplits[3];
		String position = null;
		String tag = null;
		String output = null;
		boolean existsTag = !category.equals("O") && category.contains("-");
		boolean neutralTag = category.equals("O");
		boolean isSepChar = word.equals(".") || word.equals(",") || word.contains("'");

		if (existsTag) {

			String[] categorysplits = category.split("-");
			position = categorysplits[0];
			tag = categorysplits[1];

			if (position.equals("B") && lastWasTagged) {

				output = "<END>" + " " + "<START:" + tag + ">" + word;
				lastWasTagged = true;

			} else if (position.equals("B") && !lastWasTagged) {

				output = " " + "<START:" + tag + ">" + word;
				lastWasTagged = true;

			} else if (position.equals("I")) {

				output = " " + word;

			}

		} else if (neutralTag) {

			if (isSepChar && lastWasTagged) {

				output = "<END>" + word;
				lastWasTagged = false;

			} else if (isSepChar && !lastWasTagged) {

				output = "" + word;

			} else if (!isSepChar && lastWasTagged) {

				output = "<END>" + " " + word;
				lastWasTagged = false;

			} else if (!isSepChar && !lastWasTagged) {

				output = " " + word;

			}

		} else {
			
			output = "";
			
		}

		return output;
	}
}
