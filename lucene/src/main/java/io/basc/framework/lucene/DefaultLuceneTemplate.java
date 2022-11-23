package io.basc.framework.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import io.basc.framework.env.Sys;
import io.basc.framework.lucene.support.DefaultLuceneMapper;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.orm.support.DefaultObjectKeyFormat;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Processor;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.concurrent.AsyncExecutor;
import io.basc.framework.util.concurrent.TaskQueue;

public class DefaultLuceneTemplate implements LuceneTemplate {
	public static final String DIRECTORY_PREFIX = "lucene-documents";

	// 默认的写操作队列, 所有的写都排队处理
	protected static final TaskQueue TASK_QUEUE = new TaskQueue();

	static {
		// 启动写操作队列
		TASK_QUEUE.setName(DefaultLuceneTemplate.class.getName());
		TASK_QUEUE.start();
	}

	private LuceneMapper mapper = new DefaultLuceneMapper();
	private final AsyncExecutor writeExecutor;// 写执行器
	private Executor searchExecutor = XUtils.getCommonExecutor();// 搜索执行器
	private final LucenePool<IndexWriter> indexWriterPool;
	private final LucenePool<IndexReader> indexReaderPool;
	private ObjectKeyFormat objectKeyFormat = new DefaultObjectKeyFormat();

	public DefaultLuceneTemplate(String... more) {
		this(new IndexWriterConfig(), more);
	}

	public DefaultLuceneTemplate(IndexWriterConfig config, String... more) {
		this(Paths.get(Paths.get(new File(Sys.getEnv().getWorkPath()).toPath().toString(), DIRECTORY_PREFIX).toString(),
				checkAndReturnMore(more)), config);
	}

	private static String[] checkAndReturnMore(String... more) {
		Assert.requiredArgument(!ArrayUtils.isEmpty(more), "more");
		return more;
	}

	public DefaultLuceneTemplate(Path path, IndexWriterConfig config) {
		this(open(path), config);
	}

	public DefaultLuceneTemplate(Directory directory, IndexWriterConfig config) {
		this(new IndexWriterPool(directory, config), new IndexReaderPool(directory));
	}

	public DefaultLuceneTemplate(AsyncExecutor writerExecutor, Directory directory, IndexWriterConfig config) {
		this(writerExecutor, new IndexWriterPool(directory, config), new IndexReaderPool(directory));
	}

	public DefaultLuceneTemplate(LucenePool<IndexWriter> indexWriterPool, LucenePool<IndexReader> indexReaderPool) {
		this(TASK_QUEUE, indexWriterPool, indexReaderPool);
	}

	public DefaultLuceneTemplate(AsyncExecutor writeExecutor, LucenePool<IndexWriter> indexWriterPool,
			LucenePool<IndexReader> indexReaderPool) {
		this.writeExecutor = writeExecutor;
		this.indexReaderPool = indexReaderPool;
		this.indexWriterPool = indexWriterPool;
	}

	public ObjectKeyFormat getObjectKeyFormat() {
		return objectKeyFormat;
	}

	public void setObjectKeyFormat(ObjectKeyFormat objectKeyFormat) {
		Assert.requiredArgument(objectKeyFormat != null, "objectKeyFormat");
		this.objectKeyFormat = objectKeyFormat;
	}

	@Override
	public LuceneMapper getMapper() {
		return this.mapper;
	}

	public void setMapper(LuceneMapper mapper) {
		this.mapper = mapper;
	}

	public LucenePool<IndexWriter> getIndexWriterPool() {
		return indexWriterPool;
	}

	public LucenePool<IndexReader> getIndexReaderPool() {
		return indexReaderPool;
	}

	public Executor getSearchExecutor() {
		return searchExecutor;
	}

	public void setSearchExecutor(Executor searchExecutor) {
		this.searchExecutor = searchExecutor;
	}

	@Override
	public <T> Future<T> write(Processor<? super IndexWriter, ? extends T, ? extends Exception> processor)
			throws LuceneWriteException {
		return writeExecutor.submit(() -> {
			return getIndexWriterPool().process((indexWriter) -> {
				try {
					T value = processor.process(indexWriter);
					indexWriter.commit();
					return value;
				} catch (Exception e) {
					if (indexWriter != null) {
						indexWriter.rollback();
					}
					throw e;
				}
			});
		});
	}

	@Override
	public <T, E extends Throwable> T read(Processor<? super IndexReader, ? extends T, ? extends E> processor)
			throws LuceneReadException, E {
		return getIndexReaderPool().process(processor);
	}

	@Override
	public <T, E extends Exception> T search(Processor<? super IndexSearcher, ? extends T, ? extends E> processor)
			throws LuceneSearchException {
		if (searchExecutor == null) {
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
	public <T> SearchResults<T> search(SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		if (!getIndexReaderPool().indexExists()) {
			return new SearchResults<>(parameters, null, rowMapper, this);
		}
		return LuceneTemplate.super.search(parameters, rowMapper);
	}

	@Override
	public <T> SearchResults<T> searchAfter(ScoreDoc after, SearchParameters parameters, ScoreDocMapper<T> rowMapper)
			throws LuceneSearchException {
		if (!getIndexReaderPool().indexExists()) {
			return new SearchResults<>(parameters, null, rowMapper, this);
		}

		return LuceneTemplate.super.searchAfter(after, parameters, rowMapper);
	}

	public static Directory open(Path path) throws LuceneException {
		try {
			return MMapDirectory.open(path);
		} catch (IOException e) {
			throw new LuceneException(path.toString(), e);
		}
	}
}
