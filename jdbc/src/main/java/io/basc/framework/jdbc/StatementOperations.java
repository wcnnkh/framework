package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Consumer;
import io.basc.framework.util.Function;
import io.basc.framework.util.Runnable;
import io.basc.framework.util.Source;
import io.basc.framework.util.function.StreamOperations;

public class StatementOperations<T extends Statement, C extends StatementOperations<T, C>> extends Operations<T, C> {

	public StatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public StatementOperations(Source<? extends T, ? extends SQLException> source,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public StatementOperations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public StatementOperations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}
	
	public <S> StatementOperations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Function<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public ResultSetOperations query(Function<? super T, ? extends ResultSet, ? extends SQLException> queryProcessor) {
		return new ResultSetOperations(this, queryProcessor, (e) -> e.close(), null);
	}
}
