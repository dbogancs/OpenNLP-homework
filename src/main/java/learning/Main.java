package learning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import dal.TestMaker;
import dal.TrainerTagMaker;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("Start...");
		
		try {
			TrainerTagMaker.make(
					"src/main/resources/TrainNER.csv",
					"src/main/resources/prepared/TagedTrainNER.txt");
			System.out.println("Prepared trainer data created.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*f = new File("src/main/prepared/PreparedTest1NER.txt");
		if(!f.exists()) { 
			try {
				TestMaker.make(
						"src/main/resources/Test1NER.csv",
						"src/main/resources/prepared/PreparedTest1NER.txt");
				System.out.println("Prepared test1 data created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		f = new File("src/main/resources/prepared/PreparedTest2NER.txt");
		if(!f.exists()) { 
			try {
				TestMaker.make(
						"src/main/resources/Test2NER.csv",
						"src/main/resources/prepared/PreparedTest2NER.txt");
				System.out.println("Prepared test2 data created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
		GenerateModel model = new GenerateModel();
		model.trainModel("src/main/resources/prepared/TagedTrainNER.txt");

		System.out.println("...End");

	}

}
