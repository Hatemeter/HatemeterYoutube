package eu.fbk.dh.HatemeterYoutube.sentimentanalyzers;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dh.HatemeterYoutube.models.ExcelFile;
import eu.fbk.dh.tint.runner.TintPipeline;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItalianSentimentAnalyzer extends SentimentAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(ItalianSentimentAnalyzer.class);
    private TintPipeline pipeline;
    private ArrayList<ArrayList<String>> negativeAndPositiveLexicon;

    public ItalianSentimentAnalyzer() throws IOException {
        negativeAndPositiveLexicon = new ArrayList<ArrayList<String>>();
        pipeline = new TintPipeline();
        pipeline.loadDefaultProperties();
        pipeline.setProperty("annotators", "ita_toksent, pos,ita_morpho, ita_lemma,keyphrase");
        pipeline.setProperty("customAnnotatorClass.keyphrase", "eu.fbk.dh.kd.annotator.DigiKDAnnotator");
        pipeline.load();
        try {
            loadLexicon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLexicon() throws IOException, InvalidFormatException {
        ExcelFile excelFile = new ExcelFile(new File(getClass().getClassLoader().getResource("italianlexicon.xlsx").getFile())); //get the eu.fbk.dh.ItalianSentimentAnalyzer.excel file
        XSSFSheet sheet = excelFile.getSheet(); //got the sheet from the eu.fbk.dh.ItalianSentimentAnalyzer.excel file
        int numberOfRows = excelFile.getRows(); //got the number of rows of the file
        int numberOfColumns = sheet.getRow(0).getPhysicalNumberOfCells(); //got the number of columns of the file
        ArrayList<String> negativeWords = new ArrayList<String>(); //initialize the arraylist of negative words
        ArrayList<String> positiveWords = new ArrayList<String>();

        for (int i = 1; i < numberOfRows; i++) {
            for (int j = 3; j < numberOfColumns - 1; j++) {
                int pos = (int) Math.round(sheet.getRow(i).getCell(j).getNumericCellValue());
                int neg = (int) Math.round(sheet.getRow(i).getCell(j + 1).getNumericCellValue());
                String word = sheet.getRow(i).getCell(1).toString();

                //System.out.println(word+": "+pos+", "+neg);
                if (neg == 1 && pos == 0) { //pos==0 is not necessary but just to make sure
                    negativeWords.add(word.toLowerCase());
                } else if (pos == 1 && neg == 0) {
                    positiveWords.add(word.toLowerCase());
                }
            }
        }
        negativeAndPositiveLexicon.add(negativeWords);
        negativeAndPositiveLexicon.add(positiveWords);
        // the lexicon in italian will contain negative and positive words
        log.info("Testing: " + "Italian lexicon loaded");
    }

    public int getSentiment(String comment) {
        int commentSentiment = 0;

        System.out.println("Comment: " + comment);
        try {
                Annotation stanfordAnnotation = pipeline.runRaw(comment.trim());
                System.out.println();
                //System.out.println("The lemmatized words in this comment are: ");
                List<CoreMap> sentences = stanfordAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
                for (CoreMap sentence : sentences) {
                    List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                    for (CoreLabel c : tokens) {
                        if (negativeAndPositiveLexicon.get(0).contains(c.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase())) {
                            //System.out.println("Negative: " + c.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase());
                            commentSentiment--;
                        } else if (negativeAndPositiveLexicon.get(1).contains(c.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase())) {
                            //System.out.println("Positive: " + c.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase());
                            commentSentiment++;
                        }
                    }
                }
                System.out.println("Comment sentiment: " + commentSentiment);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return commentSentiment;
    }
}
