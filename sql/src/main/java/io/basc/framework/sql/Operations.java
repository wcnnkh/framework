package io.basc.framework.sql;

import java.sql.SQLException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StandardStreamOperations;

public class Operations<T, C extends Operations<T, C>> extends StandardStreamOperations<T, SQLException, C>
		implements AutoCloseable {
	private static Logger logger = LoggerFactory.getLogger(Operations.class);

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
