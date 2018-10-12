package com.joey.se;

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
	public Joeylse(CharArraySet stopwords) {
		super(stopwords);
	}
	public CharArraySet getStopwords() {
			return stopwords;
	}
	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {
		final Tokenizer src;
	    StandardTokenizer t = new StandardTokenizer();
	    src = t;
			CharArraySet stopWords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

	    TokenStream tok = new StandardFilter(src);
	    tok = new LowerCaseFilter(tok);
	    tok = new StopFilter(tok, stopWords);
      tok = new PorterStemFilter(tok);

	    return new TokenStreamComponents(src, tok);
	}

}
