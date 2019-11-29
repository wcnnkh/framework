package scw.orm.sql.support;

import java.sql.SQLException;
import java.util.LinkedList;

import scw.orm.sql.AbstractResultSet;
import scw.orm.sql.ResultMapping;
import scw.orm.sql.ValueIndexMapping;

public class DefaultResultSet extends AbstractResultSet {
	private static final long serialVersionUID = 1L;

	public DefaultResultSet(ValueIndexMapping valueIndexMapping, LinkedList<Object[]> dataList) {
		super(valueIndexMapping, dataList);
	}

	public DefaultResultSet(java.sql.ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	protected ResultMapping createResultMapping(Object[] values) {
		return new DefaultResultMapping(valueIndexMapping, values);
	}
}
