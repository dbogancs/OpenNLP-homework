package learning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.SequenceCodec;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.uima.namefind.NameFinder;

public class GenerateModel {
	
	private InputStreamFactory inputStream = null;
	private ObjectStream objectStream = null;
	private static final int ITERATIONS = 70;
	private static final int CUTOFF = 1;
	
	private void readTrainingData(String annotated) {	
		
		try {
			inputStream = new MarkableFileInputStreamFactory(new File(annotated));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
			
		try {
			objectStream = new NameSampleDataStream(
		        new PlainTextByLineStream(inputStream, StandardCharsets.UTF_8));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void trainModel(String annotated) {
		
		this.readTrainingData(annotated);
		
		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, ITERATIONS);
		params.put(TrainingParameters.CUTOFF_PARAM, CUTOFF);
		
		TokenNameFinderModel nameFinderModel = null;
		
		try {
		    nameFinderModel = NameFinderME.train("en", null, objectStream,
		        params, TokenNameFinderFactory.create(null, null, Collections.<String, Object>emptyMap(), new BioCodec()));
		    
		    System.out.println("Model created and trained successfully.");
		    File output = new File("ner-model.bin");
			FileOutputStream outputStream;
			outputStream = new FileOutputStream(output);
			nameFinderModel.serialize(outputStream);
			System.out.println("Model saved.");
			
			this.testModel(nameFinderModel);
			
		} catch (IOException e) {

		    e.printStackTrace();
		}	
	}	
	
	public void testModel (TokenNameFinderModel nameFinderModel) {
        
		TokenNameFinder nameFinder = new NameFinderME(nameFinderModel);
        String[] testSentence ={"In", "2005", "Zambia", "qualified", "for", 
        		"debt", "relief", "under", "the", "Highly", "Indebted", "Poor", 
        		"Country", "Initiative", "consisting", "of", "approximately", 
        		"USD", "6", "billion", "in", "debt", "relief"};
 
        System.out.println("Finding types in the test sentence..");
        Span[] names = nameFinder.find(testSentence);
        for(Span name:names){
            String personName="";
            for(int i=name.getStart();i<name.getEnd();i++){
                personName+=testSentence[i]+" ";
            }
            System.out.println(name.getType()+" : "+personName+"\t [probability="+name.getProb()+"]");
        }
	}
}
