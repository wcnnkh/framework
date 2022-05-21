package io.basc.framework.lucene;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

import io.basc.framework.lang.Nullable;
import io.basc.framework.lucene.support.DefaultLuceneMapper;
import io.basc.framework.util.concurrent.AsyncExecutor;
import io.basc.framework.util.concurrent.TaskQueue;
import io.basc.framework.util.stream.Processor;

public abstract class AbstractLuceneTemplate implements LuceneTemplate {

	// 默认的写操作队列, 所有的写都排队处理
	protected static final TaskQueue TASK_QUEUE = new TaskQueue();

	static {
		// 启动写操作队列
		TASK_QUEUE.setName(AbstractLuceneTemplate.class.getName());
		TASK_QUEUE.start();
	}

	private LuceneMapper mapper = new DefaultLuceneMapper();
	private final AsyncExecutor writeExecutor;// 写执行器
	private final Executor searchExecutor;// 搜索执行器

	public AbstractLuceneTemplate() {
		this(Executors.newWorkStealingPool());
	}

	public AbstractLuceneTemplate(Executor searchExecutor) {
		this(TASK_QUEUE, searchExecutor);
	}

	public AbstractLuceneTemplate(AsyncExecutor writeExecutor, @Nullable Executor searchExecutor) {
		this.writeExecutor = writeExecutor;
		this.searchExecutor = searchExecutor;
	}

	@Override
	public LuceneMapper getMapper() {
		return this.mapper;
	}

	public void setMapper(LuceneMapper mapper) {
		this.mapper = mapper;
	}

	protected abstract IndexWriter getIndexWriter() throws IOException;

	@Override
	public <T, E extends Exception> Future<T> write(Processor<IndexWriter, T, E> processor)
			throws LuceneWriteException {
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
	public <T, E extends Exception> T read(Processor<IndexReader, T, E> processor) throws LuceneReadException {

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
	public <T, E extends Exception> T search(Processor<IndexSearcher, T, ? extends E> processor)
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
}
