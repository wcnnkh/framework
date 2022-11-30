package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.CloseableIterator;
import io.basc.framework.util.ConsumeProcessor;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Processor;
import io.basc.framework.util.RunnableProcessor;
import io.basc.framework.util.Source;
import io.basc.framework.util.StaticSupplier;

public class ResultSetOperations extends Operations<ResultSet, ResultSetOperations> {

	public ResultSetOperations(Source<? extends ResultSet, ? extends SQLException> source) {
		super(source);
	}

	public ResultSetOperations(Source<? extends ResultSet, ? extends SQLException> source,
			@Nullable ConsumeProcessor<? super ResultSet, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public ResultSetOperations(
			Processor<? super ResultSetOperations, ? extends ResultSet, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public ResultSetOperations(
			Processor<? super ResultSetOperations, ? extends ResultSet, ? extends SQLException> sourceProcesor,
			@Nullable ConsumeProcessor<? super ResultSet, ? extends SQLException> closeProcessor,
			@Nullable RunnableProcessor<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public <E> Cursor<E> rows(Processor<? super ResultSet, ? extends E, ? extends Throwable> rowMapper) {
		ResultSetIterator resultSetIterator = new ResultSetIterator();
		return Cursor.create(resultSetIterator).map((e) -> {
			try {
				return rowMapper.process(e);
			} catch (Throwable err) {
				throw SqlUtils.throwableSqlException(err, () -> ResultSetOperations.this.toString());
			}
		});
	}

	private class ResultSetIterator implements CloseableIterator<ResultSet> {
		private Supplier<ResultSet> next;
		private boolean error = false;
		private ResultSet resultSet;

		@Override
		public boolean hasNext() {
			if (error) {
				return false;
			}

			if (next != null) {
				return true;
			}

			try {
				if(resultSet == null) {
					resultSet = ResultSetOperations.this.get();
				}
				
				if (resultSet.next()) {
					this.next = new StaticSupplier<ResultSet>(resultSet);
					return true;
				}
			} catch (SQLException e) {
				error = true;
				try {
					ResultSetOperations.this.close(resultSet);
				} catch (SQLException e1) {
					e.addSuppressed(e1);
				}
				throw SqlUtils.throwableSqlException(e, () -> ResultSetOperations.this.toString());
			}
			return false;
		}

		@Override
		public ResultSet next() {
			if (!hasNext()) {
				throw new NoSuchElementException("ResultSet");
			}

			ResultSet rs = next.get();
			next = null;
			return rs;
		}

		@Override
		public void close() {
			try {
				if (resultSet != null) {
					try {
						ResultSetOperations.this.close(resultSet);
					} catch (SQLException e) {
						throw SqlUtils.throwableSqlException(e, () -> ResultSetOperations.this.toString());
					}
				}
			} finally {
				try {
					ResultSetOperations.this.close();
				} catch (SQLException e) {
					throw SqlUtils.throwableSqlException(e, () -> ResultSetOperations.this.toString());
				}
			}
		}
	}
}
