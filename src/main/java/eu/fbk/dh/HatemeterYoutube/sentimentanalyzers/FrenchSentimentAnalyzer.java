package eu.fbk.dh.HatemeterYoutube.sentimentanalyzers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.meaningcloud.*;

public class FrenchSentimentAnalyzer extends SentimentAnalyzer {
    // private static final Logger log = LoggerFactory.getLogger(FrenchAnalyzer.class); //create a logger file to be able to write logs
    private List<List<String>> lexicons;
    private ArrayList<String> negativeWordsFromLexicon;
    private String frenchLemmasApikey;

    public FrenchSentimentAnalyzer(String frenchLemmasApikey) {
        negativeWordsFromLexicon = new ArrayList<String>();
        lexicons = new ArrayList<List<String>>();
        this.frenchLemmasApikey = frenchLemmasApikey;
        try {
            loadLexicon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLexicon() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(FrenchSentimentAnalyzer.class.getResourceAsStream("/frenchlexicon.csv")));
        String line;
        int counter = 0; //lexicon counter and flag at the same time

        while ((line = br.readLine()) != null) {
            if (counter != 0) { //if it's the first time skip it because I don't want the titles
                String[] values = line.split(";"); //split each line based on the ;
                lexicons.add(Arrays.asList(values)); //add the array to each line of the list and transform it to an arraylist of values
            }
            counter++;
        }
        for (int i = 0; i < counter - 1; i++) {
            if (lexicons.get(i).get(2).equals("negative")) negativeWordsFromLexicon.add(lexicons.get(i).get(1));
        }
        //the lexicon in french will contain only negative words
        // log.info("Testing: " + "French lexicon loaded");
    }

    public int getSentiment(String comment) {
        Request.Language language = Request.Language.valueOf("fr".toUpperCase());
        int commentSentiment = 0;
        System.out.println("Comment: "+comment);
        try {
                ParserResponse r = null;
                try {
                    r = ParserRequest
                            .build(frenchLemmasApikey, language)
                            .withText(comment.trim())
                            .send();
                } catch (Exception e) {
                    //don't want the "expected an int error to print"
                }

                List<ParserResponse.Lemma> lemmas = r.lemmatize();
                for (ParserResponse.Lemma lemma : lemmas) {
                    //System.out.println("Lemma:  " + lemma.getLemma());
                    for (int z = 0; z < negativeWordsFromLexicon.size(); z++) {
                        if (negativeWordsFromLexicon.get(z).equals(lemma.getLemma())) {
                            commentSentiment--;
                            //System.out.println("Negative: " + lemma.getLemma() + ": " + z);
                            break;
                        }
                    }
                }

                System.out.println("Comment sentiment: "+commentSentiment);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentSentiment;
    }
}
