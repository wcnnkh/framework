package scw.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;

import scw.env.Sys;
import scw.mapper.Fields;
import scw.util.stream.Processor;
import scw.util.task.support.TaskExecutors;

public interface LuceneTemplate {
	default Document createDocument(Object instance) {
		return wrap(new Document(), instance);
	}

	default <T> T parse(Document document, Class<? extends T> type) {
		return mapping(document, Sys.env.getInstance(type));
	}

	default long save(Object doc) throws LuceneWriteException {
		return write((indexWriter) -> {
			if (doc == null) {
				return 0L;
			}

			Document document;
			if (doc instanceof Document) {
				document = (Document) doc;
			} else {
				document = createDocument(doc);
			}

			if (document == null) {
				return 0L;
			}
			return indexWriter.addDocument(document);
		});
	}

	default long delete(Query... queries) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(queries));
	}

	default long delete(Term... terms) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(terms));
	}

	default long saveOrUpdate(Term term, Object doc)
			throws LuceneWriteException {
		Document document = search(new SearchParameters(new TermQuery(term), 1), (reader, source) -> reader.doc(source.doc)).first();
		if(document == null){
			wrap(document, doc);
			return update(term, document);
		}
		return update(term, doc);
	}

	default long update(Term term, Object doc) throws LuceneWriteException {
		return write((writer) -> {
			if (doc == null) {
				return 0L;
			}
			Document document;
			if (doc instanceof Document) {
				document = (Document) doc;
			} else {
				document = createDocument(doc);
			}

			if (document == null) {
				return 0L;
			}
			return writer.updateDocument(term, document);
		});
	}

	default <T, E extends Throwable> T search(
			Processor<IndexSearcher, T, ? extends E> processor)
			throws LuceneSearchException {
		try {
			return read((reader) -> {
				IndexSearcher indexSearcher = new IndexSearcher(reader,
						TaskExecutors.getGlobalExecutor());
				return processor.process(indexSearcher);
			});
		} catch (LuceneException e) {
			throw e;
		} catch (Throwable e) {
			throw new LuceneSearchException(e);
		}
	}

	default <T> SearchResults<T> search(SearchParameters parameters,
			ScoreDocMapper<T> rowMapper) throws LuceneSearchException {
		return search(new SearchProcessor<>(this, null, parameters, rowMapper));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		return search(new SearchProcessor<>(this, after, parameters, rowMapper));
	}

	<T> T mapping(Document document, T instance);

	<T> T mapping(Document document, T instance, Fields fields);

	Document wrap(Document document, Object instance);

	Document wrap(Document document, Object instance, Fields fields);

	<T, E extends Throwable> T write(Processor<IndexWriter, T, E> processor)
			throws LuceneWriteException;

	<T, E extends Throwable> T read(Processor<IndexReader, T, E> processor)
			throws LuceneReadException;
}