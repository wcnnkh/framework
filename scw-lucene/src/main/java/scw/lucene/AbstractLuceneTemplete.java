package scw.lucene;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import scw.core.Assert;
import scw.json.JSONUtils;
import scw.lang.Nullable;
import scw.lucene.annotation.AnnotationFieldResolver;
import scw.lucene.annotation.LuceneField;
import scw.mapper.Field;
import scw.mapper.Fields;
import scw.mapper.MapperConfigurator;
import scw.orm.EntityStructure;
import scw.orm.Property;
import scw.util.concurrent.AsyncExecutor;
import scw.util.concurrent.TaskQueue;
import scw.util.stream.Processor;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public abstract class AbstractLuceneTemplete extends
		MapperConfigurator<Document, LuceneException> implements LuceneTemplate {
	// 默认的写操作队列, 所有的写都排队处理
	protected static final TaskQueue TASK_QUEUE = new TaskQueue();

	static {
		// 启动写操作队列
		TASK_QUEUE.start();
	}

	private FieldResolver fieldResolver = new AnnotationFieldResolver();
	private final AsyncExecutor writeExecutor;// 写执行器
	private final Executor searchExecutor;// 搜索执行器

	public AbstractLuceneTemplete() {
		this.writeExecutor = TASK_QUEUE;
		ExecutorService executorService = Executors.newCachedThreadPool();
		Thread thread = new Thread(() -> {
			executorService.shutdown();
		});
		thread.setName(getClass().getName() + "-shutdown");
		Runtime.getRuntime().addShutdownHook(thread);
		this.searchExecutor = executorService;
	}

	public AbstractLuceneTemplete(Executor searchExecutor) {
		this(TASK_QUEUE, searchExecutor);
	}

	public AbstractLuceneTemplete(AsyncExecutor writeExecutor,
			@Nullable Executor searchExecutor) {
		this.writeExecutor = writeExecutor;
		this.searchExecutor = searchExecutor;
	}

	public final FieldResolver getFieldResolver() {
		return fieldResolver;
	}

	public void setFieldResolver(FieldResolver fieldResolver) {
		Assert.requiredArgument(fieldResolver != null, "fieldResolver");
		this.fieldResolver = fieldResolver;
	}

	protected abstract IndexWriter getIndexWriter() throws IOException;

	@Override
	public <T, E extends Exception> Future<T> write(
			Processor<IndexWriter, T, E> processor) throws LuceneWriteException {
		return writeExecutor.submit(() -> {
			IndexWriter indexWriter = null;
			try {
				indexWriter = getIndexWriter();
				T value = processor.process(indexWriter);
				indexWriter.commit();
				return value;
			} catch (Exception e) {
				if (indexWriter != null) {
					indexWriter.rollback();
				}
				throw e;
			} finally {
				if (indexWriter != null) {
					indexWriter.close();
				}
			}
		});
	}

	@Nullable
	protected abstract IndexReader getIndexReader() throws IOException;

	protected void closeIndexReader(IndexReader indexReader) throws IOException {
		if (indexReader == null) {
			return;
		}

		indexReader.close();
	}

	@Override
	public <T, E extends Exception> T read(
			Processor<IndexReader, T, E> processor) throws LuceneReadException {

		IndexReader indexReader = null;
		try {
			indexReader = getIndexReader();
			return processor.process(indexReader);
		} catch (Exception e) {
			throw new LuceneReadException(e);
		} finally {
			try {
				closeIndexReader(indexReader);
			} catch (IOException e) {
				throw new LuceneReadException(e);
			}
		}
	}

	@Override
	public <T, E extends Exception> T search(
			Processor<IndexSearcher, T, ? extends E> processor)
			throws LuceneSearchException {
		if(searchExecutor == null){
			return LuceneTemplate.super.search(processor);
		}
		try {
			return read((reader) -> {
				IndexSearcher indexSearcher = new IndexSearcher(reader, searchExecutor);
				return processor.process(indexSearcher);
			});
		} catch (LuceneException e) {
			throw e;
		} catch (Throwable e) {
			throw new LuceneSearchException(e);
		}
	}

	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return super.getFields(entityClass, parentField).entity().all();
	}

	@Override
	public <T> T mapping(Document document, T instance) {
		return mapping(document, instance, getFields(instance.getClass()));
	}

	@Override
	public <T> T mapping(Document document, T instance, Fields fields) {
		for (scw.mapper.Field javaField : fields) {
			String value = document.get(javaField.getSetter().getName());
			if (value == null) {
				continue;
			}

			javaField.set(instance, value, getConversionService());
		}
		return instance;
	}

	@Override
	public Document wrap(Document document, Object instance) {
		return wrap(
				document,
				instance,
				getFields(instance.getClass()).accept(
						(field) -> {
							return field.isAnnotationPresent(LuceneField.class)
									|| Value.isBaseType(field.getGetter()
											.getType());
						}).all());
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
				v = new StringValue(JSONUtils.getJsonSupport().toJSONString(
						value));
			}

			fieldResolver.resolve(field.getGetter(), v).forEach(
					(f) -> document.add(f));
		}
		return document;
	}

	@Override
	public Document wrap(Document document,
			EntityStructure<? extends Property> structure, Object instance) {
		for (Property property : structure) {
			Object value = property.getField().get(instance);
			if (value == null) {
				continue;
			}

			Value v;
			if (Value.isBaseType(property.getField().getGetter().getType())) {
				v = new AnyValue(value, getConversionService());
			} else {
				v = new StringValue(JSONUtils.getJsonSupport().toJSONString(
						value));
			}

			fieldResolver.resolve(property.getField().getGetter(), v).forEach(
					(f) -> document.add(f));
		}
		return document;
	}
}
