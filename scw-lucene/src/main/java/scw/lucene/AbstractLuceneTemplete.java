package scw.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;

import scw.convert.ConversionService;
import scw.core.utils.ClassUtils;
import scw.env.Sys;
import scw.json.JSONUtils;
import scw.lucene.annotation.LuceneField;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.MapperUtils;
import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;
import scw.util.stream.Processor;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public abstract class AbstractLuceneTemplete implements LuceneTemplate {
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	private Fields getFields(Class<?> clazz) {
		return MapperUtils.getFields(clazz).entity().accept(FieldFeature.EXISTING_GETTER_FIELD)
				.accept(FieldFeature.EXISTING_SETTER_FIELD);
	}

	@Override
	public <T> T mapping(Document document, T instance) {
		return mapping(document, instance, getFields(instance.getClass()));
	}

	@Override
	public <T> T mapping(Document document, T instance, Fields fields) {
		for (IndexableField field : document) {
			if (!field.fieldType().stored()) {
				// 忽略不保存的字段
				continue;
			}

			for (scw.mapper.Field javaField : fields.acceptSetter(field.name(), null)) {
				MapperUtils.setValue(getConversionService(), instance, javaField, field.stringValue());
			}
		}
		return instance;
	}

	protected boolean isLuceneField(scw.mapper.Field field) {
		return Value.isBaseType(field.getGetter().getType());
	}

	@Override
	public Document wrap(Document document, Object instance) {
		return wrap(document, instance, getFields(instance.getClass()).accept((field) -> {
			return field.isAnnotationPresent(LuceneField.class) || Value.isBaseType(field.getGetter().getType());
		}));
	}

	@Override
	public Document wrap(Document document, Object instance, Fields fields) {
		for (scw.mapper.Field field : fields) {
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

	private boolean isStored(FieldDescriptor fieldDescriptor) {
		scw.lucene.annotation.LuceneField annotation = fieldDescriptor
				.getAnnotation(scw.lucene.annotation.LuceneField.class);
		if (annotation != null) {
			return annotation.stored();
		}
		return true;
	}

	public void addField(Document document, FieldDescriptor fieldDescriptor, Value value) {
		if (ClassUtils.isLong(fieldDescriptor.getType()) || ClassUtils.isInt(fieldDescriptor.getType())
				|| ClassUtils.isShort(fieldDescriptor.getType())) {
			document.add(new NumericDocValuesField(fieldDescriptor.getName(), value.getAsLong()));
			if (isStored(fieldDescriptor)) {
				document.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
			return;
		}

		if (ClassUtils.isDouble(fieldDescriptor.getType())) {
			document.add(new DoubleDocValuesField(fieldDescriptor.getName(), value.getAsDoubleValue()));
			if (isStored(fieldDescriptor)) {
				document.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
			return;
		}

		if (ClassUtils.isFloat(fieldDescriptor.getType())) {
			document.add(new FloatDocValuesField(fieldDescriptor.getName(), value.getAsFloatValue()));
			if (isStored(fieldDescriptor)) {
				document.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
			}
			return;
		}

		scw.lucene.annotation.LuceneField annotation = fieldDescriptor
				.getAnnotation(scw.lucene.annotation.LuceneField.class);
		if (annotation == null) {
			document.add(new StringField(fieldDescriptor.getName(), value.getAsString(), Store.YES));
			return;
		}

		if (annotation.indexed()) {
			if (annotation.tokenized()) {
				document.add(new TextField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			} else {
				document.add(new StringField(fieldDescriptor.getName(), value.getAsString(),
						annotation.stored() ? Store.YES : Store.NO));
			}
		} else {
			document.add(new StoredField(fieldDescriptor.getName(), value.getAsString()));
		}
	}

	protected Field toField(FieldDescriptor fieldDescriptor, Value value) {
		scw.lucene.annotation.LuceneField annotation = fieldDescriptor
				.getAnnotation(scw.lucene.annotation.LuceneField.class);
		if (annotation == null) {
			if (ClassUtils.isLong(fieldDescriptor.getType()) || ClassUtils.isInt(fieldDescriptor.getType())
					|| ClassUtils.isShort(fieldDescriptor.getType())) {
				return new NumericDocValuesField(fieldDescriptor.getName(), value.getAsLong());
			}

			if (ClassUtils.isDouble(fieldDescriptor.getType())) {
				return new DoubleDocValuesField(fieldDescriptor.getName(), value.getAsDoubleValue());
			}

			if (ClassUtils.isFloat(fieldDescriptor.getType())) {
				return new FloatDocValuesField(fieldDescriptor.getName(), value.getAsFloatValue());
			}
			return new StringField(fieldDescriptor.getName(), value.getAsString(), Store.YES);
		} else {
			if (annotation.indexed()) {
				if (annotation.tokenized()) {
					return new TextField(fieldDescriptor.getName(), value.getAsString(),
							annotation.stored() ? Store.YES : Store.NO);
				} else {
					return new StringField(fieldDescriptor.getName(), value.getAsString(),
							annotation.stored() ? Store.YES : Store.NO);
				}
			} else if (annotation.stored()) {
				return new StoredField(fieldDescriptor.getName(), value.getAsString());
			} else {
				return new StringField(fieldDescriptor.getName(), value.getAsString(), Store.NO);
			}
		}
	}

	protected abstract IndexWriter getIndexWrite() throws IOException;

	protected abstract IndexReader getIndexReader() throws IOException;

	@Override
	public <T, E extends Throwable> T write(Processor<IndexWriter, T, E> processor) throws LuceneWriteException {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		IndexWriter indexWriter = null;
		try {
			indexWriter = getTransactionIndexWrite();
			T v = processor.process(indexWriter);
			if (transaction == null) {
				indexWriter.commit();
			}
			return v;
		} catch (Throwable e) {
			if (indexWriter != null && transaction == null) {
				try {
					indexWriter.rollback();
				} catch (IOException e1) {
					throw new LuceneException(e);
				}
			}
			if (e instanceof LuceneException) {
				throw (LuceneException) e;
			}
			throw new LuceneWriteException(e);
		} finally {
			if (indexWriter != null && transaction == null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					throw new LuceneException(e);
				}
			}
		}
	}

	@Override
	public <T, E extends Throwable> T read(Processor<IndexReader, T, E> processor) throws LuceneException {
		IndexReader indexReader = null;
		try {
			indexReader = getIndexReader();
			return processor.process(indexReader);
		} catch (Throwable e) {
			if (e instanceof LuceneException) {
				throw (LuceneException) e;
			}
			throw new LuceneReadException(e);
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					throw new LuceneException(e);
				}
			}
		}
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
}
