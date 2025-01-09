package io.basc.framework.jdbc;

import java.sql.SQLException;

import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Runnable;
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
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public Operations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public Operations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public <S> Operations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Function<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
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
