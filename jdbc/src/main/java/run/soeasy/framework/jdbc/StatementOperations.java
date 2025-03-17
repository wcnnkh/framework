package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import run.soeasy.framework.lang.Nullable;
import run.soeasy.framework.util.function.Consumer;
import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.function.Runnable;
import run.soeasy.framework.util.function.StreamOperations;
import run.soeasy.framework.util.function.Supplier;

public class StatementOperations<T extends Statement, C extends StatementOperations<T, C>> extends Operations<T, C> {

	public StatementOperations(Supplier<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public StatementOperations(Supplier<? extends T, ? extends SQLException> source,
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
