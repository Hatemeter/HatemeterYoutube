package eu.fbk.dh.HatemeterYoutube.stanfordcorenlp;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;

public class SentimentAnalyzer {

    private StanfordCoreNLP pipeline;

    public void initializeCoreNLP() {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public int getSentiment(String text){
        int totalSentiment=0, negative=-1, positive=1;
        CoreDocument coreDocument=new CoreDocument(text);
        pipeline.annotate(coreDocument);
        List<CoreSentence> coreSentenceList=coreDocument.sentences();
        for(CoreSentence coreSentence: coreSentenceList){
            //System.out.println(coreSentence.toString()+" : "+coreSentence.sentiment());
            if(coreSentence.sentiment().toLowerCase().equals("negative")) totalSentiment=totalSentiment+negative;
            else if(coreSentence.sentiment().toLowerCase().equals("positive")) totalSentiment=totalSentiment+positive;
        }
        if(totalSentiment<0){
            System.out.println(text);
            System.out.println("The overall comment is NEGATIVE");
            System.out.println();
        }
        else if(totalSentiment>0){
            System.out.println(text);
            System.out.println("The overall comment is POSITIVE");
            System.out.println();
        }
        else {
            System.out.println(text);
            System.out.println("The overall comment is NEUTRAL");
            System.out.println();

        }

        return totalSentiment;
    }


}

