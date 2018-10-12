package com.joey.se;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.similarities.*;

public class SearchEngine {
          private static String INDEX_DIRECTORY = "../index";
          private static int MAX_RESULTS = 30;
          public static void main(String[] args) throws Exception {

            Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
            IndexReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
					 	isearcher.setSimilarity(new BM25Similarity());
            Map<String, Float> boost = createBoostMap();
            CharArraySet stopWords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

            Analyzer analyzer = new Joeylse(stopWords);

            MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"title", "published", "author", "content"}, analyzer, boost);
            ArrayList<String> loadedQueries = loadQueriesFromFile();
            ArrayList<String> vars = new ArrayList<String>();
            for (int j=0; loadedQueries.size()>j; j++){
              String q = loadedQueries.get(j);
              q = q.trim();
              if (q.length() >0){
                Query query = null;
                String stringify = QueryParser.escape(q);

                try{
                  query = qp.parse(stringify);
                } catch (ParseException e){
                  System.out.println("Failed to parse: "+ e);
                }
                ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
                for (int i = 0; i < hits.length; i++) {
                  Document hitDoc = isearcher.doc(hits[i].doc);
                  int rank = i+1;
                  double noms = normScore("Other", hits[i].score);
                  if (noms >0){
                    vars.add(j+1 + " 0 " + hitDoc.get("index") + " "+ rank + " "+ noms  +" EXP \n");
                  }
                }
              }
            }

            writeToFile(vars);
            ireader.close();
            directory.close();
          }
      private static Map<String, Float> createBoostMap(){
        Map<String, Float> boost = new HashMap<>();
        boost.put("title", (float) 0.35);
        boost.put("published",(float) 0.01);
        boost.put("author", (float) 0.02);
        boost.put("content", (float) 0.61);
        return boost;
      }
      private static void writeToFile(ArrayList<String> results){
        BufferedWriter writer = null;
        try {
            File logFile = new File("results.txt");
            writer = new BufferedWriter(new FileWriter(logFile));
            for (String res : results){
              writer.write(res);
            }
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              try {
                  writer.close();
              } catch (Exception e) {
              }
          }
      }
      private static ArrayList<String> loadQueriesFromFile() {
        String docPath = "/home/joey/Documents/InfoRet/searchEngine/cran/cran.qry";
        try {
          ArrayList<String> al = new ArrayList<String>();
          FileReader fileReader = new FileReader(docPath);
          BufferedReader bufferedReader = new BufferedReader(fileReader);
          String line = "";
          int index =0;
          while(line != null) {
              index++;
              String qry ="";
              line = bufferedReader.readLine();
              while (line.contains(".I") || line.contains(".W")){
                line = bufferedReader.readLine();
              }
              while(line != null && !line.contains(".I") ){
                qry += " " + line;
                line = bufferedReader.readLine();
              }
              al.add(qry);

      }
      return al;
    }
      catch(FileNotFoundException ex) {
          System.out.println(
              "Unable to open file '" +
              docPath + "'");
      }
      catch(IOException ex) {
          System.out.println( "Error reading file '" + docPath + "'");

      }
      return null;
    }
    private static double normScore(String eval, double score){
			return score;

    }
}
