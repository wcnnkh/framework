package run.soeasy.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CloseableIterator;
import run.soeasy.framework.core.function.stream.Source;
import run.soeasy.framework.core.invoke.Function;

public class ResultSetIterator<T> extends JdbcWrapper<ResultSet> implements CloseableIterator<T> {
	private Supplier<ResultSet> next;
	private boolean error = false;
	private ResultSet resultSet;
	private final Function<? super ResultSet, ? extends T, ? extends Throwable> mapper;

	public ResultSetIterator(Source<ResultSet, SQLException> source,
			@NonNull Function<? super ResultSet, ? extends T, ? extends Throwable> mapper) {
		super(source);
		this.mapper = mapper;
	}

	@Override
	public boolean hasNext() {
		synchronized (this) {
			if (error) {
				return false;
			}

			if (next != null) {
				return true;
			}

			try {
				if (resultSet == null) {
					resultSet = get();
				}

				synchronized (resultSet) {
					if (resultSet.next()) {
						this.next = () -> resultSet;
						return true;
					}
				}
			} catch (Throwable e) {
				error = true;
				try {
					super.close();
				} catch (Throwable e1) {
					e.addSuppressed(e1);
				}
				throw newJdbcException(e);
			}
			return false;
		}
	}

	protected JdbcException newJdbcException(Throwable exception) {
		return new JdbcException(exception);
	}

	@Override
	public T next() throws JdbcException {
		synchronized (this) {
			if (!hasNext()) {
				throw new NoSuchElementException("ResultSet");
			}

			synchronized (resultSet) {
				ResultSet rs = next.get();
				next = null;
				try {
					return mapper.apply(rs);
				} catch (Throwable e) {
					error = true;
					try {
						super.close();
					} catch (Throwable e1) {
						e.addSuppressed(e1);
					}
					throw newJdbcException(e);
				}
			}
		}
	}

	@Override
	public void close() throws JdbcException {
		synchronized (this) {
			try {
				super.close();
			} catch (Throwable e) {
				throw newJdbcException(e);
			}
		}
	}

}
