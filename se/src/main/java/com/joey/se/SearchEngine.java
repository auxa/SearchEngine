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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;

public class SearchEngine {
          private static String INDEX_DIRECTORY = "../index";
          private static int MAX_RESULTS = 30;
          public static void main(String[] args) throws Exception {
            // Open the folder that contains our search index
            Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
            IndexReader ireader = DirectoryReader.open(directory);
            // create objects to read and search across the index
            IndexSearcher isearcher = new IndexSearcher(ireader);
            Map<String, Float> boost = createBoostMap();
            // builder class for creating our query
            CharArraySet myStopSet = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

            String field = "contents";
            Analyzer analyzer = new Joeylse(myStopSet);

            QueryParser qp = new MultiFieldQueryParser(new String[] {"title", "published", "author", "content"}, analyzer, boost);
            // Some words that we want to find and the field in which we expect
            // to find them
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
                  int noms = normScore(hits[i].score);
                  if (noms >0){
                    vars.add(j+1 + " 0 " + hitDoc.get("index") + " "+ rank + " "+ noms  +" EXP \n");
                  }

                }


              }

            }

            writeToFile(vars);

            // close everything we used
            ireader.close();
            directory.close();
          }
      private static Map<String, Float> createBoostMap(){
        Map<String, Float> boost = new HashMap<>();
        boost.put("title", (float) 0.62);
        boost.put("published",(float) 0.03);
        boost.put("author", (float) 0.1);
        boost.put("content", (float) 0.35);
        return boost;
      }
      private static void writeToFile(ArrayList<String> results){
        BufferedWriter writer = null;
        try {
            //create a temporary file
            // This will output the full path where the file will be written to...
            File logFile = new File("results.txt");
            writer = new BufferedWriter(new FileWriter(logFile));
            for (String res : results){
              writer.write(res);
            }
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              try {
                  // Close the writer regardless of what happens...
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
                qry += line;
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
    private static int normScore(double score){
      if (score > 12){
        return 5;
      }
      else if (score >10) {
        return 4;
      }
      else if(score>8 ){
        return 3;
      }else if(score>6){
        return 2;
      }else if(score >5){
        return 1;
      }
      else{
        return -1;
      }
    }
}
