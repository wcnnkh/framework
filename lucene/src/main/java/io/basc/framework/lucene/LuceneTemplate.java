package io.basc.framework.lucene;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RepositoryException;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Range;

@SuppressWarnings("unchecked")
public interface LuceneTemplate extends EntityOperations {
	LuceneMapper getMapper();

	default Future<Long> asyncAdd(Document document) {
		return write((indexWriter) -> indexWriter.addDocument(document));
	}

	default long add(Document document) {
		try {
			return asyncAdd(document).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	@Override
	default <T> long delete(Class<? extends T> entityClass, T entity) {
		if (entity instanceof Query) {
			try {
				return delete((Query) entity).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new LuceneException(e);
			}
		} else if (entity instanceof Term) {
			try {
				return delete((Term) entity).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new LuceneException(e);
			}
		}
		return EntityOperations.super.delete(entityClass, entity);
	}

	default Future<Long> delete(Query query) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(query));
	}

	default Future<Long> delete(Query... queries) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(queries));
	}

	default Future<Long> delete(Term term) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(term));
	}

	default Future<Long> delete(Term... terms) throws LuceneWriteException {
		return write((writer) -> writer.deleteDocuments(terms));
	}

	/**
	 * 是否存在
	 * 
	 * @param term
	 * @return
	 */
	default boolean isPresent(Term term) {
		return search(new SearchParameters(new TermQuery(term), 1), (search, d) -> d.doc).all().getElements().findAny()
				.isPresent();
	}

	default Future<Long> asyncSaveOrUpdate(Term term, Document document) {
		Assert.requiredArgument(document != null, "document");
		return write((writer) -> {
			if (isPresent(term)) {
				return writer.updateDocument(term, document);
			} else {
				return writer.addDocument(document);
			}
		});
	}

	default long saveOrUpdate(Term term, Document document) {
		try {
			return asyncSaveOrUpdate(term, document).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	default Future<Long> asyncUpdate(Term term, Document document) {
		Assert.requiredArgument(document != null, "document");
		return write((writer) -> {
			return writer.updateDocument(term, document);
		});
	}

	default long update(Term term, Document document) throws LuceneException {
		try {
			return asyncUpdate(term, document).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	default <T, E extends Exception> T search(Processor<? super IndexSearcher, ? extends T, ? extends E> processor)
			throws LuceneSearchException {
		try {
			return read((reader) -> {
				IndexSearcher indexSearcher = new IndexSearcher(reader);
				return processor.process(indexSearcher);
			});
		} catch (LuceneException e) {
			throw e;
		} catch (Throwable e) {
			throw new LuceneSearchException(e);
		}
	}

	default <T> SearchResults<T> search(SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		return search(new SearchProcessor<>(this, null, parameters, rowMapper));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		return search(new SearchProcessor<>(this, after, parameters, rowMapper));
	}

	default <T> SearchResults<T> search(SearchParameters parameters,
			Processor<? super Document, ? extends T, ? extends LuceneException> mapProcessor)
			throws LuceneSearchException {
		return search(parameters, new ScoreDocMapper<T>() {

			@Override
			public T map(IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException {
				Document document = indexSearcher.doc(scoreDoc.doc);
				return mapProcessor.process(document);
			}
		});
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters,
			Processor<Document, T, LuceneException> mapProcessor) throws LuceneSearchException {
		return searchAfter(after, parameters, new ScoreDocMapper<T>() {

			@Override
			public T map(IndexSearcher indexSearcher, ScoreDoc scoreDoc) throws IOException {
				Document document = indexSearcher.doc(scoreDoc.doc);
				return mapProcessor.process(document);
			}
		});
	}

	default <T> SearchResults<T> search(SearchParameters parameters, TypeDescriptor resultType)
			throws LuceneSearchException {
		return search(parameters, (e) -> (T) getMapper().convert(e, resultType));
	}

	default <T> SearchResults<T> search(SearchParameters parameters, Class<? extends T> resultType)
			throws LuceneSearchException {
		return search(parameters, TypeDescriptor.valueOf(resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, TypeDescriptor resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters, (e) -> (T) getMapper().convert(e, resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, Class<? extends T> resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters, TypeDescriptor.valueOf(resultType));
	}

	<T> Future<T> write(Processor<? super IndexWriter, ? extends T, ? extends Exception> processor);

	<T, E extends Throwable> T read(Processor<? super IndexReader, ? extends T, ? extends E> processor)
			throws LuceneReadException, E;

	default Future<Long> deleteAll() {
		return write((e) -> e.deleteAll());
	}

	@Override
	default long insert(InsertOperation operation) throws RepositoryException {
		if (operation.getColumns().isEmpty()) {
			return 0;
		}

		try {
			return write((indexWriter) -> {
				Document document = getMapper().createDocument(operation, operation.getColumns());
				return indexWriter.addDocument(document);
			}).get();
		} catch (LuceneWriteException | InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	@Override
	default long delete(DeleteOperation operation) throws LuceneException {
		Query query = getMapper().createQuery(operation, operation.getConditions());
		try {
			return write((writer) -> writer.deleteDocuments(query)).get();
		} catch (LuceneSearchException | LuceneWriteException | InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	default long update(Query query, Document document) throws LuceneException {
		SearchParameters searchParameters = new SearchParameters(query, 1000);
		Field[] fields = document.getFields().toArray(new Field[0]);
		SearchResults<Long> result = search(searchParameters, (reader, doc) -> {
			try {
				return write((write) -> {
					return write.tryUpdateDocValue(reader.getIndexReader(), doc.doc, fields);
				}).get();
			} catch (LuceneWriteException | InterruptedException | ExecutionException e) {
				return 0L;
			}
		});
		return result.all().getElements().stream().mapToLong((e) -> e.longValue()).sum();
	}

	@Override
	default long update(UpdateOperation operation) throws RepositoryException {
		Query query = getMapper().createQuery(operation, operation.getConditions());
		Document document = getMapper().createDocument(operation, operation.getColumns());
		return update(query, document);
	}

	/**
	 * lucene不支持使用索引位置进行分页，这里使用skip实现此方案
	 */
	@Override
	default <T> io.basc.framework.data.domain.Query<T> query(TypeDescriptor resultTypeDescriptor,
			QueryOperation operation) throws RepositoryException {
		Query query = getMapper().createQuery(operation, operation.getConditions());
		Sort sort = getMapper().createSort(operation, operation.getOrders());
		int top = 1000;
		Range<Long> limit = operation.getLimit();
		if (limit != null && limit.getUpperBound().isPresent()) {
			top = (int) Math.min(1000L, limit.getUpperBound().get().longValue());
		}
		SearchParameters searchParameters = new SearchParameters(query, top);
		searchParameters.setSort(sort);
		SearchResults<T> searchResults = search(searchParameters, resultTypeDescriptor);
		Elements<T> elements = searchResults.all().getElements();
		long total = searchResults.getTotal();
		if (limit != null) {
			if (limit.getUpperBound().isPresent()) {
				if (limit.getUpperBound().isInclusive()) {
					total = limit.getUpperBound().get() + 1;
				} else {
					total = limit.getUpperBound().get();
				}
				elements = elements.limit(total);
			}

			if (limit.getLowerBound().isPresent()) {
				if (limit.getLowerBound().isInclusive()) {
					total = total - limit.getLowerBound().get();
					elements = elements.skip(limit.getLowerBound().get());
				} else {
					total = total - limit.getLowerBound().get() - 1;
					elements = elements.skip(limit.getLowerBound().get() + 1);
				}
			}

		}

		io.basc.framework.data.domain.Query<T> results = new io.basc.framework.data.domain.Query<>(elements);
		results.setTotal(total);
		return results;
	}
}