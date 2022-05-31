package io.basc.framework.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.mapper.Structure;
import io.basc.framework.orm.OrmException;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.orm.repository.Repository;
import io.basc.framework.orm.repository.RepositoryColumn;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.Processor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public interface LuceneTemplate extends Repository {
	LuceneMapper getMapper();

	default <T> void save(T entity) throws LuceneWriteException {
		Assert.requiredArgument(entity != null, "entity");
		if (entity instanceof Document) {
			// 同步
			try {
				write((indexWriter) -> indexWriter.addDocument((Document) entity)).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new LuceneException(e);
			}
		} else {
			Repository.super.save(entity);
		}
	}

	@Override
	default <T> boolean delete(T entity) {
		Assert.requiredArgument(entity != null, "entity");
		if (entity instanceof Query) {
			// TODO 应该支持这个吗，有点违背本意, 应该是删除单个
			try {
				write((indexWriter) -> indexWriter.deleteDocuments((Query) entity)).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new LuceneException(e);
			}
		} else if (entity instanceof Term) {
			// 同步
			try {
				write((indexWriter) -> indexWriter.deleteDocuments((Term) entity)).get();
			} catch (InterruptedException | ExecutionException e) {
				throw new LuceneException(e);
			}
		}
		return Repository.super.delete(entity);
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
		return search(new SearchParameters(new TermQuery(term), 1), (search, d) -> d.doc).streamAll().findFirst()
				.isPresent();
	}

	default Future<Long> saveOrUpdate(Term term, Object doc) throws LuceneWriteException {
		return write((writer) -> {
			if (doc == null) {
				return 0L;
			}
			Document document;
			if (doc instanceof Document) {
				document = (Document) doc;
			} else {
				document = getMapper().createDocument(doc);
			}

			if (document == null) {
				return 0L;
			}

			if (isPresent(term)) {
				return writer.updateDocument(term, document);
			} else {
				return writer.addDocument(document);
			}
		});
	}

	default Future<Long> update(Term term, Object doc) throws LuceneWriteException {
		return write((writer) -> {
			if (doc == null) {
				return 0L;
			}
			Document document;
			if (doc instanceof Document) {
				document = (Document) doc;
			} else {
				document = getMapper().createDocument(doc);
			}

			if (document == null) {
				return 0L;
			}
			return writer.updateDocument(term, document);
		});
	}

	default <T, E extends Exception> T search(Processor<IndexSearcher, T, ? extends E> processor)
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
			Processor<Document, T, LuceneException> mapProcessor) throws LuceneSearchException {
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
		return search(parameters, (e) -> getMapper().convert(e, resultType));
	}

	default <T> SearchResults<T> search(SearchParameters parameters, Class<? extends T> resultType)
			throws LuceneSearchException {
		return search(parameters, TypeDescriptor.valueOf(resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, TypeDescriptor resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters, (e) -> getMapper().convert(e, resultType));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, Class<? extends T> resultType)
			throws LuceneSearchException {
		return searchAfter(after, parameters, TypeDescriptor.valueOf(resultType));
	}

	default <T> SearchResults<T> search(SearchParameters parameters, Structure<? extends Property> structure)
			throws LuceneSearchException {
		return search(parameters, (e) -> getMapper().convert(e, structure));
	}

	default <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters,
			Structure<? extends Property> structure) throws LuceneSearchException {
		return searchAfter(after, parameters, (e) -> getMapper().convert(e, structure));
	}

	<T, E extends Exception> Future<T> write(Processor<IndexWriter, T, E> processor) throws LuceneWriteException;

	<T, E extends Exception> T read(Processor<IndexReader, T, E> processor) throws LuceneReadException;

	default Future<Long> deleteAll() {
		return write((e) -> e.deleteAll());
	}

	@Override
	default <E> long save(Class<? extends E> entityClass, Collection<? extends RepositoryColumn> columns)
			throws OrmException {
		List<RepositoryColumn> list = getMapper().open(entityClass, columns, null);
		if (CollectionUtils.isEmpty(list)) {
			return 0L;
		}

		try {
			return write((indexWriter) -> {
				Document document = new Document();
				getMapper().reverseTransform(list, document);
				return indexWriter.addDocument(document);
			}).get();
		} catch (LuceneWriteException | InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	@Override
	default <E> long update(Class<? extends E> entityClass, Collection<? extends RepositoryColumn> columns,
			Conditions conditions) throws OrmException {
		List<RepositoryColumn> columnsToUse = getMapper().open(entityClass, columns, null);
		if (CollectionUtils.isEmpty(columnsToUse)) {
			return 0L;
		}

		Query query = getMapper().parseQuery(getMapper().open(entityClass, conditions, null));
		try {
			return write((writer) -> {
				SearchResults<Document> searchResults = search(new SearchParameters(query, 100), (e) -> e);
				Stream<Document> stream = searchResults.streamAll();
				try {
					Iterator<Document> iterator = stream.iterator();
					while (iterator.hasNext()) {
						// 使用先删除或添加的方式完成更新
						Document document = iterator.next();
						Query documentQuery = getMapper().parseQuery(document);
						writer.deleteDocuments(documentQuery);
						getMapper().reverseTransform(columnsToUse, document);
						writer.addDocument(document);
						writer.commit();
					}
				} finally {
					stream.close();
				}
				return searchResults.getTotal();
			}).get();
		} catch (LuceneSearchException | LuceneWriteException | InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	@Override
	default <E> long delete(Class<? extends E> entityClass, Conditions conditions) throws OrmException {
		Query query = getMapper().parseQuery(getMapper().open(entityClass, conditions, null));
		try {
			return delete(query).get();
		} catch (LuceneWriteException | InterruptedException | ExecutionException e) {
			throw new LuceneException(e);
		}
	}

	default <T> SearchResults<T> search(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			Conditions conditions, List<? extends OrderColumn> orders, int top) {
		List<OrderColumn> orderColumns = new ArrayList<OrderColumn>();
		if (orders != null) {
			orderColumns.addAll(orders);
		}
		Query query = getMapper().parseQuery(getMapper().open(entityClass, conditions, orderColumns));
		Sort sort = getMapper().parseSort(getMapper().getStructure(entityClass), orderColumns);
		SearchParameters parameters = new SearchParameters(query, top);
		parameters.setSort(sort);
		return search(parameters, resultsTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass, Conditions conditions,
			List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		PageRequest request = pageRequest;
		if (request == null) {
			request = PageRequest.getPageRequest();
		}

		if (request == null) {
			return queryAll(resultsTypeDescriptor, entityClass, conditions, orders);
		}

		Cursor<T> cursor = (Cursor<T>) StreamProcessorSupport
				.cursor(search(resultsTypeDescriptor, entityClass, conditions, orders, (int) pageRequest.getPageSize())
						.streamAll());
		return cursor.limit(request.getStart(), request.getPageSize());
	}

	@Override
	default <T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Conditions conditions, List<? extends OrderColumn> orders) throws OrmException {
		SearchResults<T> results = search(resultsTypeDescriptor, entityClass, conditions, orders, 100);
		return StreamProcessorSupport.cursor(results.streamAll());
	}

	@Override
	default <T, E> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass,
			Conditions conditions, List<? extends OrderColumn> orders, PageRequest pageRequest) throws OrmException {
		PageRequest request = pageRequest;
		if (request == null) {
			request = PageRequest.getPageRequest();
		}

		if (request == null) {
			request = new PageRequest();
		}

		SearchResults<T> results = search(resultsTypeDescriptor, entityClass, conditions, orders,
				(int) request.getPageSize());
		return results.toPaginations(request.getStart(), request.getPageSize());
	}
}