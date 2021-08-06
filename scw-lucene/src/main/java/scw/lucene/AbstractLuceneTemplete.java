package scw.lucene;

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;

import scw.convert.ConversionService;
import scw.env.Sys;
import scw.json.JSONUtils;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;
import scw.util.Accept;
import scw.util.stream.Processor;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public abstract class AbstractLuceneTemplete implements LuceneTemplete {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	protected abstract IndexWriter getIndexWrite() throws IOException;

	protected abstract IndexReader getIndexReader() throws IOException;

	public <T> T indexWriter(IndexWriterExecutor<T> indexWriterExecutor) throws IOException {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		IndexWriter indexWriter = null;
		try {
			indexWriter = getTransactionIndexWrite();
			T v = indexWriterExecutor.execute(indexWriter);
			if (transaction == null) {
				indexWriter.commit();
			}
			return v;
		} catch (IOException e) {
			if (indexWriter != null && transaction == null) {
				indexWriter.rollback();
			}
			throw e;
		} finally {
			if (indexWriter != null && transaction == null) {
				indexWriter.close();
			}
		}
	}

	protected abstract Field toField(FieldDescriptor fieldDescriptor, Value value);

	private Fields getFields(Class<?> clazz) {
		return MapperUtils.getMapper().getFields(clazz).accept(FieldFeature.GETTER)
				.accept(new Accept<scw.mapper.Field>() {

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
			if (Value.isBaseType(field.getGetter().getType())) {
				v = new AnyValue(value, getConversionService());
			} else {
				v = new StringValue(JSONUtils.getJsonSupport().toJSONString(value));
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
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction != null) {
			IndexWriterResource resource = transaction.getResource(IndexWriter.class);
			if (resource == null) {
				IndexWriterResource indexWriterResource = new IndexWriterResource(getIndexWrite());
				resource = transaction.bindResource(IndexWriter.class, indexWriterResource);
				if (resource == null) {
					resource = indexWriterResource;
				}
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
		return Sys.env.getInstance(type);
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

			MapperUtils.setValue(getConversionService(), instance, field, value);
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

	@Override
	public <T, E extends Throwable> T indexReader(Processor<IndexReader, T, E> processor) throws E, IOException {
		IndexReader indexReader = null;
		try {
			indexReader = getIndexReader();
			return processor.process(indexReader);
		} finally {
			if (indexReader != null) {
				indexReader.close();
			}
		}
	}
}
