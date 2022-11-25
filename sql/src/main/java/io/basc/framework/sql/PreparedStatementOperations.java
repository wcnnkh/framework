package io.basc.framework.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;

public class PreparedStatementOperations<T extends PreparedStatement, C extends PreparedStatementOperations<T, C>>
		extends StatementOperations<T, C> {
	public PreparedStatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public PreparedStatementOperations(Source<? extends T, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public PreparedStatementOperations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public PreparedStatementOperations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	@Override
	public <R, X extends Throwable> R process(Processor<? super T, ? extends R, ? extends X> processor) {
		try {
			return super.process(processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, () -> toString());
		}
	}

	@Override
	public <X extends Throwable> void consume(ConsumeProcessor<? super T, ? extends X> processor) {
		try {
			super.consume(processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, () -> toString());
		}
	}

	/**
	 * @return 返回结果并不代表是否执行成功，意义请参考jdk文档<br/>
	 *         true if the first result is a ResultSet object; false if the first
	 *         result is an update count or there is no result
	 * @throws SQLException
	 */
	public boolean execute() {
		return process((e) -> e.execute());
	}

	public int executeUpdate() {
		return process((e) -> e.executeUpdate());
	}

	public final ResultSetOperations query() {
		return query((e) -> e.executeQuery());
	}

	public PreparedStatementOperations<T, C> batch(Object... args) {
		return after((e) -> SqlUtils.setSqlParams(e, args));
	}
}
