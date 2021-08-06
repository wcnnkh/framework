package scw.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import scw.util.stream.Processor;

public interface LuceneTemplete {
	Document createDocument(Object instance);

	<T> T parse(Class<? extends T> type, Document document);

	<T> T indexWriter(IndexWriterExecutor<T> indexWriterExecutor) throws IOException;

	long createIndex(Object instance) throws IOException;

	long createIndex(Iterable<?> indexs) throws IOException;

	long deleteIndex(Query... queries) throws IOException;

	long deleteIndex(Term... terms) throws IOException;

	long updateIndex(Term term, Object index) throws IOException;

	long updateIndex(Term term, Iterable<?> indexs) throws IOException;

	<T, E extends Throwable> T indexReader(Processor<IndexReader, T, E> processor) throws E, IOException;

	default <T> SearchResults<T> search(SearchParameters parameters, ScoreDocMapper<T> rowMapper) throws LuceneException {
		try {
			return indexReader(new SearchProcessor<>(this, null, parameters, rowMapper));
		} catch (IOException e) {
			throw new LuceneException(e);
		}
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		try {
			return indexReader(new SearchProcessor<>(this, after, parameters, rowMapper));
		} catch (IOException e) {
			throw new LuceneException(e);
		}
	}
}