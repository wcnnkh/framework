package io.basc.framework.jdbc;

import java.sql.SQLException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.ConsumeProcessor;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.RunnableProcessor;
import io.basc.framework.util.function.Source;
import io.basc.framework.util.function.StandardStreamOperations;
import io.basc.framework.util.function.StreamOperations;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class Operations<T, C extends Operations<T, C>> extends StandardStreamOperations<T, SQLException, C>
		implements AutoCloseable {
	private static Logger logger = LogManager.getLogger(Operations.class);

	// 测试用代码
//	static {
//		LoggerFactory.getLevelManager().getSourceMap().put(logger.getName(), Levels.TRACE.getValue());
//	}

	public Operations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public Operations(Source<? extends T, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public Operations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public Operations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public <S> Operations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	@Override
	public T get() throws SQLException {
		T source = super.get();
		if (logger.isTraceEnabled()) {
			logger.trace("Get database operation resources: {}", source);
		}
		return source;
	}

	@Override
	public void close() throws SQLException {
		if (logger.isTraceEnabled()) {
			logger.trace("Closing operation: {}", this);
		}
		super.close();
	}

	@Override
	public void close(T source) throws SQLException {
		if (logger.isTraceEnabled()) {
			logger.trace("Shutting down database operation resources: {}", source);
		}
		super.close(source);
	}
}
