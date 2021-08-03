package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;

@FunctionalInterface
public interface IndexWriterExecutor<T> {
	T execute(IndexWriter indexWriter) throws IOException;
}
