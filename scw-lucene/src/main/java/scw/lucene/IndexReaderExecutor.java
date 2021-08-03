package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;

@FunctionalInterface
public interface IndexReaderExecutor<T> {
	T execute(IndexReader indexReader) throws IOException;
}
