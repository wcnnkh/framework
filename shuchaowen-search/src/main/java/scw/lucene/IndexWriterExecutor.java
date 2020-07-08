package scw.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;

public interface IndexWriterExecutor<T> {
	T execute(IndexWriter indexWriter) throws IOException;
}
