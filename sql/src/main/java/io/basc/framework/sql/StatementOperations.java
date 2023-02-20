package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StreamOperations;

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
	
	public <S> StatementOperations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends SQLException> processor,
			@Nullable ConsumeProcessor<? super T, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public ResultSetOperations query(Processor<? super T, ? extends ResultSet, ? extends SQLException> queryProcessor) {
		return new ResultSetOperations(this, queryProcessor, (e) -> e.close(), null);
	}
}
