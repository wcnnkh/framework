package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Processor;
import io.basc.framework.util.Source;
import io.basc.framework.util.function.StreamOperations;

public class StatementOperations<T extends Statement, C extends StatementOperations<T, C>> extends Operations<T, C> {

	public StatementOperations(Source<? extends T, ? extends SQLException> source) {
		super(source);
	}

	public StatementOperations(Source<? extends T, ? extends SQLException> source,
			@Nullable Endpoint<? super T, ? extends SQLException> closeProcessor,
			@Nullable Processor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public StatementOperations(Pipeline<? super C, ? extends T, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public StatementOperations(Pipeline<? super C, ? extends T, ? extends SQLException> sourceProcesor,
			@Nullable Endpoint<? super T, ? extends SQLException> closeProcessor,
			@Nullable Processor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}
	
	public <S> StatementOperations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Pipeline<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable Endpoint<? super T, ? extends SQLException> closeProcessor,
			@Nullable Processor<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public ResultSetOperations query(Pipeline<? super T, ? extends ResultSet, ? extends SQLException> queryProcessor) {
		return new ResultSetOperations(this, queryProcessor, (e) -> e.close(), null);
	}
}
