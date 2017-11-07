package learning;


import java.io.IOException;
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
		
		try {
			
			Model.trainModel("src/main/resources/prepared/TagedTrainNER.txt");
			Model.predictSentences("src/main/resources/Test1NER.csv", "src/main/resources/predicted/PredictedTest1NER.csv");
			Model.predictSentences("src/main/resources/Test2NER.csv", "src/main/resources/predicted/PredictedTest2NER.csv");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("...End");

	}

}
