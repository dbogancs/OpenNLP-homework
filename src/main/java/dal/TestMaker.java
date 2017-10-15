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

public class TestMaker {

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
		if (linesplits.length < 2) return "";

		String word = linesplits[1];
		String output = null;
		boolean isSepChar = word.equals(".") || word.equals(",") || word.contains("'");

		if(isSepChar) output = word;
		else output = " " + word;
		
		return output;
	}
}
