package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public interface ScoreDocMapper<T> {
	T map(IndexReader indexReader, IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException;
}
