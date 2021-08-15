package scw.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;

import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.mapper.Fields;
import scw.mapper.MapProcessDecorator;
import scw.mapper.Mapper;
import scw.orm.EntityStructure;
import scw.orm.Property;
import scw.util.stream.Processor;
import scw.util.task.support.TaskExecutors;

public interface LuceneTemplate {
	Mapper<Document, LuceneException> getMapper();

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
		if (indexExists()) {
			Document document = search(
					new SearchParameters(new TermQuery(term), 1),
					(reader, source) -> reader.doc(source.doc)).first();
			if (document == null) {
				return save(doc);
			} else {
				return update(term, doc);
			}
		}
		return save(doc);
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
		if (!indexExists()) {
			return new SearchResults<T>(parameters, null, rowMapper, this);
		}
		return search(new SearchProcessor<>(this, null, parameters, rowMapper));
	}

	default <T> SearchResults<T> search(SearchParameters parameters,
			Processor<Document, T, LuceneException> mapProcessor)
			throws LuceneSearchException {
		return search(parameters, new ScoreDocMapper<T>() {

			@Override
			public T map(IndexSearcher indexSearcher, ScoreDoc scoreDoc)
					throws IOException {
				Document document = indexSearcher.doc(scoreDoc.doc);
				return mapProcessor.process(document);
			}
		});
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		if (!indexExists()) {
			return new SearchResults<T>(parameters, after, rowMapper, this);
		}
		return search(new SearchProcessor<>(this, after, parameters, rowMapper));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters,
			Processor<Document, T, LuceneException> mapProcessor)
			throws LuceneSearchException {
		return searchAfter(after, parameters, new ScoreDocMapper<T>() {

			@Override
			public T map(IndexSearcher indexSearcher, ScoreDoc scoreDoc)
					throws IOException {
				Document document = indexSearcher.doc(scoreDoc.doc);
				return mapProcessor.process(document);
			}
		});
	}

	@SuppressWarnings("unchecked")
	default <T> Processor<Document, T, LuceneException> getMapProcessor(
			TypeDescriptor type) {
		return new MapProcessDecorator<>(getMapper(),
				new DefaultMapProcessor<T, LuceneException>(type),
				(Class<T>) type.getType());
	}

	default <T> SearchResults<T> search(SearchParameters parameters,
			TypeDescriptor resultType) throws LuceneSearchException {
		return search(parameters, getMapProcessor(resultType));
	}

	default <T> SearchResults<T> search(SearchParameters parameters,
			Class<? extends T> resultType) throws LuceneSearchException {
		return search(parameters, TypeDescriptor.valueOf(resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters, TypeDescriptor resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters, getMapProcessor(resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters, Class<? extends T> resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters,
				TypeDescriptor.valueOf(resultType));
	}

	@SuppressWarnings("unchecked")
	default <T> SearchResults<T> search(SearchParameters parameters,
			EntityStructure<? extends Property> structure)
			throws LuceneSearchException {
		return search(
				parameters,
				new MapProcessDecorator<>(getMapper(),
						new DefaultStructureMapProcessor<T, LuceneException>(
								structure), (Class<T>) structure
								.getEntityClass()));
	}

	@SuppressWarnings("unchecked")
	default <T> SearchResults<T> searchAfter(ScoreDoc after,
			SearchParameters parameters,
			EntityStructure<? extends Property> structure)
			throws LuceneSearchException {
		return searchAfter(
				after,
				parameters,
				new MapProcessDecorator<>(getMapper(),
						new DefaultStructureMapProcessor<T, LuceneException>(
								structure), (Class<T>) structure
								.getEntityClass()));
	}

	boolean indexExists() throws LuceneException;

	<T> T mapping(Document document, T instance);

	<T> T mapping(Document document, T instance, Fields fields);

	Document wrap(Document document, Object instance);

	Document wrap(Document document, Object instance, Fields fields);

	<T, E extends Throwable> T write(Processor<IndexWriter, T, E> processor)
			throws LuceneWriteException;

	<T, E extends Throwable> T read(Processor<IndexReader, T, E> processor)
			throws LuceneReadException;

	Document wrap(Document document,
			EntityStructure<? extends Property> structure, Object instance);
}