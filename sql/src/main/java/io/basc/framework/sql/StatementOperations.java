package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;

public class StatementOperations<T extends Statement, C extends StatementOperations<T, C>> extends Operations<T, C> {

	public StatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public StatementOperations(Source<? extends T, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public StatementOperations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public StatementOperations(Processor<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public ResultSetOperations query(Processor<? super T, ? extends ResultSet, ? extends SQLException> queryProcessor) {
		return new ResultSetOperations((operations) -> {
			T statement = StatementOperations.this.get();
			try {
				return queryProcessor.process(statement);
			} catch (Throwable e) {
				statement.close();
				throw e;
			} finally {
				operations.onClose(() -> StatementOperations.this.close(statement))
						.onClose(() -> StatementOperations.this.close());
			}
		}, (e) -> e.close(), null) {
			@Override
			public String toString() {
				return StatementOperations.this.toString();
			}
		};
	}
}
