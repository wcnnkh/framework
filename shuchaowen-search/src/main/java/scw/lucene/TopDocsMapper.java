package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public interface TopDocsMapper<T> {
	T mapper(IndexReader indexReader, IndexSearcher indexSearcher, TopDocs topDocs) throws IOException;
}
