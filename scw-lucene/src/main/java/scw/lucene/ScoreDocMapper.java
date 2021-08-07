package scw.lucene;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

@FunctionalInterface
public interface ScoreDocMapper<T> {
	T map(IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException;
}
