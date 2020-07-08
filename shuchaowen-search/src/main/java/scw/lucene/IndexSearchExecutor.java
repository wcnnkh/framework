package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

public interface IndexSearchExecutor<T> {
	T execute(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException;
}
