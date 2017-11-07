package learning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class Model {
	
	static TokenNameFinderModel nameFinderModel = null;
	
	private static final int ITERATIONS = 70;
	private static final int CUTOFF = 1;
	
	public static void trainModel(String annotated) throws IOException {
		
		// read training data
		InputStreamFactory inputStream = new MarkableFileInputStreamFactory(new File(annotated));
		ObjectStream objectStream = new NameSampleDataStream(
		        new PlainTextByLineStream(inputStream, StandardCharsets.UTF_8));
		
		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, ITERATIONS);
		params.put(TrainingParameters.CUTOFF_PARAM, CUTOFF);
		
		nameFinderModel = NameFinderME.train("en", null, objectStream,
		        params, TokenNameFinderFactory.create(null, null, Collections.<String, Object>emptyMap(), new BioCodec()));
		    
		System.out.println("Model created and trained successfully.");
		    
		File output = new File("ner-model.bin");
		FileOutputStream outputStream;
		outputStream = new FileOutputStream(output);
		nameFinderModel.serialize(outputStream);
		System.out.println("Model saved.");
				
	}	
	
	private static String[] predictOneSentence (String[] testSentence) {
        
		String[] prediction = new String[testSentence.length];
		TokenNameFinder nameFinder = new NameFinderME(nameFinderModel);
	 
	    Span[] names = nameFinder.find(testSentence);
	    int j = 0;
	    
	    for(Span name:names){   	

	    	// the words before the founded named entity will be categorized as "other"
	    	for (;j<name.getStart(); j++)
	        {
	        	prediction[j] = "O";
	        }
	        
	        prediction[j] = "B-" + name.getType();
	        j++;
	        
	        for (;j<name.getEnd(); j++)
	        {
	        	prediction[j] = "I-" + name.getType();
	        }
	    }
	    
	    // "other" category after the last named entity
	    for (;j<testSentence.length; j++)
	    {
	    	prediction[j] = "O";
	    }
	    
	    return prediction;
	}

	public static void predictSentences(String input, String output) throws FileNotFoundException, IOException
	{
		ArrayList<String> list = new ArrayList<String>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			
			String line = br.readLine();
			String nextLine = null;
			
			String[] lineSplits = null;
			
			// only the words of one sentence
			ArrayList<String> sentence = new ArrayList<String>();
			// full rows of one sentences
			ArrayList<String> sentenceFullRows = new ArrayList<String>();
			// prediction of one sentence
			String[] prediction = null;
			
			while (line != null) {			
				
				lineSplits = line.split(";");
				sentence.add(lineSplits[1]);
				sentenceFullRows.add(line);
				
				nextLine = br.readLine();
				
				// last word of the sentence
				if ((nextLine == null) || (nextLine.startsWith("Sentence:")))
				{
					
					prediction = predictOneSentence(sentence.toArray(new String[0]));
					
					for (int i=0; i<sentence.size(); i++)
					{
						list.add(sentenceFullRows.get(i) + ";" + prediction[i]);
					}
					
					sentence.clear();
					sentenceFullRows.clear();
				}
				
				line = nextLine;
			}
			
			/*
			for (int i=0; i<200; i++)
			{
				System.out.println(list.get(i));
			}
			*/
			
			Path file = Paths.get(output);
			Files.write(file, list, Charset.forName("UTF-8"));
		}

	}
	
}
