package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.collections.CloseableIterator;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.Streams;
import io.basc.framework.util.function.Consumer;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.function.Runnable;
import io.basc.framework.util.function.Supplier;
import io.basc.framework.util.function.StaticSupplier;
import io.basc.framework.util.function.StreamOperations;

public class ResultSetOperations extends Operations<ResultSet, ResultSetOperations> {

	public ResultSetOperations(Supplier<? extends ResultSet, ? extends SQLException> source) {
		super(source);
	}

	public ResultSetOperations(Supplier<? extends ResultSet, ? extends SQLException> source,
			@Nullable Consumer<? super ResultSet, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(source, closeProcessor, closeHandler);
	}

	public ResultSetOperations(
			Function<? super ResultSetOperations, ? extends ResultSet, ? extends SQLException> sourceProcesor) {
		super(sourceProcesor);
	}

	public ResultSetOperations(
			Function<? super ResultSetOperations, ? extends ResultSet, ? extends SQLException> sourceProcesor,
			@Nullable Consumer<? super ResultSet, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceProcesor, closeProcessor, closeHandler);
	}

	public <S> ResultSetOperations(StreamOperations<S, ? extends SQLException> sourceStreamOperations,
			Function<? super S, ? extends ResultSet, ? extends SQLException> processor,
			@Nullable Consumer<? super ResultSet, ? extends SQLException> closeProcessor,
			@Nullable Runnable<? extends SQLException> closeHandler) {
		super(sourceStreamOperations, processor, closeProcessor, closeHandler);
	}

	public <E> Elements<E> rows(Function<? super ResultSet, ? extends E, ? extends Throwable> rowMapper) {
		return Elements.of(() -> {
			ResultSetIterator resultSetIterator = new ResultSetIterator();
			return Streams.stream(resultSetIterator).map((e) -> {
				try {
					return rowMapper.process(e);
				} catch (Throwable err) {
					throw SqlUtils.throwableSqlException(err, () -> ResultSetOperations.this.toString());
				}
			});
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
				if (resultSet == null) {
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
