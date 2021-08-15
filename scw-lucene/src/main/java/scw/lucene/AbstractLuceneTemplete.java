package scw.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import scw.convert.ConversionService;
import scw.core.utils.ClassUtils;
import scw.env.Sys;
import scw.json.JSONUtils;
import scw.lucene.annotation.LuceneField;
import scw.mapper.FieldDescriptor;
import scw.mapper.FieldFeature;
import scw.mapper.Fields;
import scw.mapper.Mapper;
import scw.mapper.MapperUtils;
import scw.mapper.SimpleMapper;
import scw.orm.EntityStructure;
import scw.orm.Property;
import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;
import scw.util.stream.Processor;
import scw.value.AnyValue;
import scw.value.EmptyValue;
import scw.value.StringValue;
import scw.value.Value;

public abstract class AbstractLuceneTemplete implements LuceneTemplate {
	private ConversionService conversionService;
	private final Mapper<Document, LuceneException> mapper = new SimpleMapper<Document, LuceneException>();

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	@Override
	public Mapper<Document, LuceneException> getMapper() {
		return mapper;
	}

	private Fields getFields(Class<?> clazz) {
		return MapperUtils.getFields(clazz).entity().all().accept(FieldFeature.EXISTING_GETTER_FIELD)
				.accept(FieldFeature.EXISTING_SETTER_FIELD);
	}

	@Override
	public <T> T mapping(Document document, T instance) {
		return mapping(document, instance, getFields(instance.getClass()));
	}

	@Override
	public <T> T mapping(Document document, T instance, Fields fields) {
		for (scw.mapper.Field javaField : fields) {
			String value = document.get(javaField.getSetter().getName());
			if(value == null){
				continue; 
			}
			
			javaField.set(instance, value, getConversionService());
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
		}).all());
	}

	@Override
	public Document wrap(Document document, Object instance, Fields fields) {
		for (scw.mapper.Field field : fields) {
			Object value = field.getGetter().get(instance);
			Value v;
			if(value == null){
				v = new EmptyValue();
			} else if (Value.isBaseType(field.getGetter().getType())) {
				v = new AnyValue(value, getConversionService());
			} else {
				v = new StringValue(JSONUtils.getJsonSupport().toJSONString(value));
			}

			addField(document, field.getGetter(), v);
		}
		return document;
	}
	
	@Override
	public Document wrap(Document document,
			EntityStructure<? extends Property> structure, Object instance) {
		for(Property property : structure){
			Object value = property.getField().get(instance);
			Value v;
			if(value == null){
				v = new EmptyValue();
			} else if (Value.isBaseType(property.getField().getGetter().getType())) {
				v = new AnyValue(value, getConversionService());
			} else {
				v = new StringValue(JSONUtils.getJsonSupport().toJSONString(value));
			}

			addField(document, property.getField().getGetter(), v);
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
