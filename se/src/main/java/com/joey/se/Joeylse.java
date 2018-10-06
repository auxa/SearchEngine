package com.joey.se;
import java.io.Reader;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;


public class Joeylse extends StopwordAnalyzerBase {

	private int maxTokenLength = 255;

	public Joeylse(CharArraySet stopwords) {
		super(stopwords);
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {
		final Tokenizer src;
	    StandardTokenizer t = new StandardTokenizer();
	    t.setMaxTokenLength(maxTokenLength);
	    src = t;

	    TokenStream tok = new StandardFilter(src);
	    TokenStream token = new LowerCaseFilter(tok);
	    TokenStream tokener = new StopFilter(token, StopAnalyzer.ENGLISH_STOP_WORDS_SET);
      tokener = new PorterStemFilter(tokener);

	    return new TokenStreamComponents(src, tokener) {
	      @Override
	      protected void setReader(final Reader reader) {
	        ((StandardTokenizer)src).setMaxTokenLength(256);
	        super.setReader(reader);
	      }
	    };
	}

}
