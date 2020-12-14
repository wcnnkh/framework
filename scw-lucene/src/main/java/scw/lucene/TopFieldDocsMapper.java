package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldDocs;

public interface TopFieldDocsMapper<T> {
	T mapper(IndexReader indexReader, IndexSearcher indexSearcher, TopFieldDocs topFieldDocs) throws IOException;
}
