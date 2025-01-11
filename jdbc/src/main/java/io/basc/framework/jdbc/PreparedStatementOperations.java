package io.basc.framework.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Runnable;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.function.StreamOperations;

public class PreparedStatementOperations<T extends PreparedStatement, C extends PreparedStatementOperations<T, C>>
		extends StatementOperations<T, C> {
	public PreparedStatementOperations(Supplier<? extends T, ? extends SQLException> source) {
		super(source);
	}
	
	public <S> PreparedStatementOperations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Function<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public PreparedStatementOperations(Supplier<? extends T, ? extends SQLException> source,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public PreparedStatementOperations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public PreparedStatementOperations(Function<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable Consumer<? super T, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	@Override
	public <R, X extends Throwable> R process(Function<? super T, ? extends R, ? extends X> processor) {
		try {
			return super.process(processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, () -> toString());
		}
	}

	@Override
	public <X extends Throwable> void consume(Consumer<? super T, ? extends X> processor) {
		try {
			super.consume(processor);
		} catch (Throwable e) {
			throw SqlUtils.throwableSqlException(e, () -> toString());
		}
	}

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
