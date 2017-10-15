package learning;

import java.io.IOException;

import dal.TestMaker;
import dal.TrainerTagMaker;

public class Main {

	public static void main(String[] args) {
		try {
			System.out.println("Start...");
			TrainerTagMaker.make(
					"C:\\Users\\Danasyo\\Desktop\\TrainNER.csv",
					"C:\\Users\\Danasyo\\Desktop\\TagedTrainNER.csv");
			TestMaker.make(
					"C:\\Users\\Danasyo\\Desktop\\Test1NER.csv",
					"C:\\Users\\Danasyo\\Desktop\\PreparedTest1NER.csv");
			TestMaker.make(
					"C:\\Users\\Danasyo\\Desktop\\Test2NER.csv",
					"C:\\Users\\Danasyo\\Desktop\\PreparedTest2NER.csv");
			System.out.println("...End");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
