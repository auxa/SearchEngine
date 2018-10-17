package com.joey.se;

import java.io.IOException;
import java.nio.file.Paths;
import java.io.*;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.search.similarities.*;

public class CreateIndex {

 	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY = "../index";

	public static void main(String[] args) throws IOException {
		CharArraySet stopwords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

		Analyzer analyzer = new Joeylse(stopwords);

		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// Change the "ClassicSimilarity" to change similarity scoring
	  indexWriterConfig = indexWriterConfig.setSimilarity(new ClassicSimilarity());

		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		IndexWriter iwriter = new IndexWriter(directory, indexWriterConfig);

    iwriter = addDocuments(iwriter);

		iwriter.close();
		directory.close();

	}

  private static IndexWriter addDocuments(IndexWriter iw){
    String docPath = "../cran/cran.all.1400";
    try {
      FileReader fileReader = new FileReader(docPath);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line = "";
      int index =0;
      while(line != null) {
          String title ="";
          String author ="";
          String pub = "";
          String words ="";
          index++;
          line = bufferedReader.readLine();
          while(!line.contains(".A")){
            line = bufferedReader.readLine();
            if (line.contains(".A")){
              break;
            }else if ( line.contains(".T")){
              line = bufferedReader.readLine();
            }
            title+=line +" ";
          }
          while( !line.contains(".B")){
            line = bufferedReader.readLine();
            if (line.contains(".B")){
              break;
            }
            author+=line +" ";
          }
          while(!line.contains(".W")){
            line = bufferedReader.readLine();
            if (line.contains(".W")){
              break;
            }
            pub+=line +" ";
          }
          while(!line.contains(".I")){
            line = bufferedReader.readLine();
            if (line == null || line.contains(".I")){
              break;
            }
            words+=line+ " ";
          }
          Document doc = createDoc(index, title, author, pub, words);
          iw.addDocument(doc);

       }

        bufferedReader.close();
       }
       catch(FileNotFoundException ex) {
           System.out.println(
               "Unable to open file '" +
               docPath + "'");
       }
       catch(IOException ex) {
           System.out.println( "Error reading file '" + docPath + "'");

       }
      return iw;
  }
  private static Document createDoc(int index, String title, String author, String pub, String words) throws IOException {
        Document doc = new Document();
				doc.add(new TextField("index", index+"", Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("published", pub, Field.Store.YES));
        doc.add(new TextField("content", words, Field.Store.YES));
        return doc;
  }


}
