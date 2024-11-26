package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.transform.Property;
import io.basc.framework.util.alias.SimpleNamed;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResultSetProperty extends SimpleNamed implements Property {
	@NonNull
	private final ResultSet resultSet;
	/**
	 * 从0开始
	 */
	private final int positionIndex;

	@Override
	public Object getValue() {
		try {
			return resultSet.getObject(positionIndex + 1);
		} catch (SQLException e) {
			throw new SqlException(e);
		}
	}

	@Override
	public void setValue(Object value) throws UnsupportedOperationException {
		try {
			resultSet.updateObject(positionIndex + 1, value);
		} catch (SQLException e) {
			throw new SqlException(e);
		}
	}
}
