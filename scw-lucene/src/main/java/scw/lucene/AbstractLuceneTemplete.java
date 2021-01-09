package scw.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;

import scw.configure.support.ConfigureUtils;
import scw.core.instance.InstanceUtils;
import scw.json.JSONUtils;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFilter;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;
import scw.transaction.TransactionManager;
import scw.util.Pagination;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.ValueUtils;

public abstract class AbstractLuceneTemplete implements LuceneTemplete {
	protected abstract IndexWriter getIndexWrite() throws IOException;

	protected abstract IndexReader getIndexReader() throws IOException;

	public <T> T indexWriter(IndexWriterExecutor<T> indexWriterExecutor) throws IOException {
		IndexWriter indexWriter = null;
		try {
			indexWriter = getTransactionIndexWrite();
			T v = indexWriterExecutor.execute(indexWriter);
			if (!TransactionManager.hasTransaction()) {
				indexWriter.commit();
			}
			return v;
		} catch (IOException e) {
			if (indexWriter != null && !TransactionManager.hasTransaction()) {
				indexWriter.rollback();
			}
			throw e;
		} finally {
			if (indexWriter != null && !TransactionManager.hasTransaction()) {
				indexWriter.close();
			}
		}
	}

	protected abstract Field toField(FieldDescriptor fieldDescriptor, Value value);

	private Collection<scw.mapper.Field> getFields(Class<?> clazz) {
		return MapperUtils.getMapper().getFields(clazz, FilterFeature.GETTER).toSet(new FieldFilter() {

			public boolean accept(scw.mapper.Field field) {
				return field.getGetter().getField() != null;
			}
		});
	}

	public Document createDocument(Object instance) {
		Document document = new Document();
		for (scw.mapper.Field field : getFields(instance.getClass())) {
			Object value = field.getGetter().get(instance);
			if (value == null) {
				continue;
			}

			Value v;
			if (ValueUtils.isBaseType(field.getGetter().getType())) {
				v = new AnyValue(value);
			} else {
				v = new StringValue(JSONUtils.toJSONString(value));
			}

			Field luceneField = toField(field.getGetter(), v);
			if (luceneField == null) {
				continue;
			}

			document.add(luceneField);
		}
		return document;
	}

	private final IndexWriter getTransactionIndexWrite() throws IOException {
		if (TransactionManager.hasTransaction()) {
			IndexWriterResource resource = TransactionManager.getCurrentTransaction().getResource(IndexWriter.class);
			if (resource == null) {
				resource = new IndexWriterResource(getIndexWrite());
				TransactionManager.getCurrentTransaction().bindResource(IndexWriter.class, resource);
			}
			return resource.getIndexWriter();
		}
		return getIndexWrite();
	}

	public final long createIndex(final Iterable<?> indexs) throws IOException {
		return indexWriter(new IndexWriterExecutor<Long>() {

			public Long execute(IndexWriter indexWriter) throws IOException {
				long count = 0;
				for (Object index : indexs) {
					Document document = createDocument(index);
					if (document == null) {
						continue;
					}
					count += indexWriter.addDocument(document);
				}
				return count;
			}
		});
	}

	public final long createIndex(Object index) throws IOException {
		return createIndex(Arrays.asList(index));
	}

	protected <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.INSTANCE_FACTORY.getInstance(type);
	}

	@SuppressWarnings("unchecked")
	public <T> T parse(Class<? extends T> type, Document document) {
		if (Document.class.isAssignableFrom(type)) {
			return (T) document;
		}

		T instance = newInstance(type);
		for (scw.mapper.Field field : getFields(type)) {
			String value = document.get(field.getGetter().getName());
			if (value == null) {
				continue;
			}

			ConfigureUtils.setValue(instance, field, value);
		}
		return instance;
	}

	public final long deleteIndex(final Query... queries) throws IOException {
		return indexWriter(new IndexWriterExecutor<Long>() {

			public Long execute(IndexWriter indexWriter) throws IOException {
				return indexWriter.deleteDocuments(queries);
			}
		});
	}

	public final long deleteIndex(final Term... terms) throws IOException {
		return indexWriter(new IndexWriterExecutor<Long>() {

			public Long execute(IndexWriter indexWriter) throws IOException {
				return indexWriter.deleteDocuments(terms);
			}
		});
	}

	public final long updateIndex(final Term term, final Iterable<?> indexs) throws IOException {
		return indexWriter(new IndexWriterExecutor<Long>() {

			public Long execute(IndexWriter indexWriter) throws IOException {
				long count = 0;
				for (Object index : indexs) {
					Document document = createDocument(index);
					if (document == null) {
						continue;
					}
					count += indexWriter.updateDocument(term, document);
				}
				return count;
			}
		});
	}

	public long updateIndex(Term term, Object index) throws IOException {
		return updateIndex(term, Arrays.asList(index));
	}

	public <T> T indexReader(IndexReaderExecutor<T> indexReaderExecutor) throws IOException {
		IndexReader indexReader = null;
		try {
			indexReader = getIndexReader();
			return indexReaderExecutor.execute(indexReader);
		} finally {
			if (indexReader != null) {
				indexReader.close();
			}
		}
	}

	public <T> T indexSearcher(final IndexSearchExecutor<T> indexSearchExecutor) throws IOException {
		return indexReader(new IndexReaderExecutor<T>() {

			public T execute(IndexReader indexReader) throws IOException {
				return indexSearchExecutor.execute(indexReader, new IndexSearcher(indexReader));
			}
		});
	}

	public <T> T search(final Query query, final int top, final TopDocsMapper<T> topDocsMapper) throws IOException {
		return indexSearcher(new IndexSearchExecutor<T>() {

			public T execute(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException {
				TopDocs topDocs = indexSearcher.search(query, top);
				return topDocsMapper.mapper(indexReader, indexSearcher, topDocs);
			}
		});
	}

	public <T> T search(final Query query, final int top, final Sort sort, final boolean doDocScores,
			final TopFieldDocsMapper<T> topFieldDocsMapper) throws IOException {
		return indexSearcher(new IndexSearchExecutor<T>() {

			public T execute(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException {
				TopFieldDocs topFieldDocs = indexSearcher.search(query, top, sort, doDocScores);
				return topFieldDocsMapper.mapper(indexReader, indexSearcher, topFieldDocs);
			}
		});
	}

	public <T> Pagination<T> search(Query query, final RowMapper<T> rowMapper, long page, final int limit)
			throws IOException {
		final int begin = Pagination.getBegin(page, limit);
		return search(query, begin + limit, new PaginationTopDocsMapper<T>(rowMapper, begin, limit));
	}

	public <T> Pagination<T> search(Query query, Sort sort, boolean doDocScores, final RowMapper<T> rowMapper,
			long page, final int limit) throws IOException {
		final int begin = Pagination.getBegin(page, limit);
		return search(query, begin + limit, sort, doDocScores, new TopFieldDocsMapper<Pagination<T>>() {

			public Pagination<T> mapper(IndexReader indexReader, IndexSearcher indexSearcher, TopFieldDocs topFieldDocs)
					throws IOException {
				return new PaginationTopDocsMapper<T>(rowMapper, begin, limit).mapper(indexReader, indexSearcher,
						topFieldDocs);
			}
		});
	}

	public <T> Pagination<T> search(Query query, final Class<? extends T> resultType, long page, int limit)
			throws IOException {
		return search(query, new DefaultRowMapper<T>(resultType), page, limit);
	}

	public <T> Pagination<T> search(Query query, Sort sort, boolean doDocScores, final Class<? extends T> resultType,
			long page, int limit) throws IOException {
		return search(query, sort, doDocScores, new DefaultRowMapper<T>(resultType), page, limit);
	}

	public <T> T searchAfter(final ScoreDoc after, final Query query, final int numHits,
			final TopDocsMapper<T> topDocsMapper) throws IOException {
		return indexSearcher(new IndexSearchExecutor<T>() {

			public T execute(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException {
				TopDocs topDocs = indexSearcher.searchAfter(after, query, numHits);
				return topDocsMapper.mapper(indexReader, indexSearcher, topDocs);
			}
		});
	}

	public <T> T searchAfter(final ScoreDoc after, final Query query, final int numHits, final Sort sort,
			final boolean doDocScores, final TopFieldDocsMapper<T> topFieldDocsMapper) throws IOException {
		return indexSearcher(new IndexSearchExecutor<T>() {

			public T execute(IndexReader indexReader, IndexSearcher indexSearcher) throws IOException {
				TopFieldDocs topFieldDocs = indexSearcher.searchAfter(after, query, numHits, sort, doDocScores);
				return topFieldDocsMapper.mapper(indexReader, indexSearcher, topFieldDocs);
			}
		});
	}

	public <T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, RowMapper<T> rowMapper)
			throws IOException {
		return searchAfter(after, query, numHits, new PaginationTopDocsMapper<T>(rowMapper, 0, numHits));
	}

	public <T> Pagination<T> searchAfter(ScoreDoc after, Query query, final int numHits, Sort sort, boolean doDocScores,
			final RowMapper<T> rowMapper) throws IOException {
		return searchAfter(after, query, numHits, sort, doDocScores, new TopFieldDocsMapper<Pagination<T>>() {

			public Pagination<T> mapper(IndexReader indexReader, IndexSearcher indexSearcher, TopFieldDocs topFieldDocs)
					throws IOException {
				return new PaginationTopDocsMapper<T>(rowMapper, 0, numHits).mapper(indexReader, indexSearcher,
						topFieldDocs);
			}
		});
	}

	public <T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, Class<? extends T> resultType)
			throws IOException {
		return searchAfter(after, query, numHits, new DefaultRowMapper<T>(resultType));
	}

	public <T> Pagination<T> searchAfter(ScoreDoc after, Query query, int numHits, Sort sort, boolean doDocScores,
			Class<? extends T> resultType) throws IOException {
		return searchAfter(after, query, numHits, sort, doDocScores, new DefaultRowMapper<T>(resultType));
	}

	private final class DefaultRowMapper<T> implements RowMapper<T> {
		private final Class<? extends T> resultType;

		public DefaultRowMapper(Class<? extends T> resultType) {
			this.resultType = resultType;
		}

		public T mapper(int index, IndexReader indexReader, IndexSearcher indexSearcher, ScoreDoc scoreDoc)
				throws IOException {
			Document document = indexSearcher.doc(scoreDoc.doc);

			T instance = newInstance(resultType);
			for (scw.mapper.Field field : getFields(resultType)) {
				String value = document.get(field.getGetter().getName());
				if (value == null) {
					continue;
				}

				ConfigureUtils.setValue(instance, field, value);
			}
			return instance;
		}
	}

	private final class PaginationTopDocsMapper<T> implements TopDocsMapper<Pagination<T>> {
		private final RowMapper<T> rowMapper;
		private final int begin;
		private final int limit;

		public PaginationTopDocsMapper(RowMapper<T> rowMapper, int begin, int limit) {
			this.rowMapper = rowMapper;
			this.begin = begin;
			this.limit = limit;
		}

		public Pagination<T> mapper(IndexReader indexReader, IndexSearcher indexSearcher, TopDocs topDocs)
				throws IOException {
			List<T> list = new ArrayList<T>();
			int index = 0;
			for (int i = begin; i < topDocs.scoreDocs.length; i++) {
				list.add(rowMapper.mapper(index++, indexReader, indexSearcher, topDocs.scoreDocs[i]));
			}
			return new Pagination<T>(topDocs.totalHits.value, limit, list);
		}

	}
}
