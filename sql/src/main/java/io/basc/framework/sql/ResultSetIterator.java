package io.basc.framework.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StaticSupplier;

public class ResultSetIterator implements Iterator<ResultSet> {
	private static Logger logger = LoggerFactory.getLogger(ResultSetIterator.class);
	private final ResultSet resultSet;
	private Supplier<ResultSet> next;
	private boolean error = false;

	public ResultSetIterator(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	@Override
	public boolean hasNext() {
		if (error) {
			return false;
		}

		if (next != null) {
			return true;
		}

		try {
			if (resultSet.next()) {
				this.next = new StaticSupplier<ResultSet>(resultSet);
				return true;
			}
		} catch (SQLException e) {
			error = true;
			logger.error(e, "iterator error");
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
}
