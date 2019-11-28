package scw.orm.sql.support;

import java.sql.SQLException;
import java.util.LinkedList;

import scw.orm.sql.AbstractResultSet;
import scw.orm.sql.ResultMapping;
import scw.orm.sql.ValueIndexMapping;

public class DefaultResultSetMapping extends AbstractResultSet {
	private static final long serialVersionUID = 1L;

	public DefaultResultSetMapping(ValueIndexMapping valueIndexMapping, LinkedList<Object[]> dataList) {
		super(valueIndexMapping, dataList);
	}

	public DefaultResultSetMapping(java.sql.ResultSet resultSet) throws SQLException {
		super(resultSet);
	}

	protected ResultMapping createResultMapping(Object[] values) {
		return new DefaultResultMapping(valueIndexMapping, values);
	}
}
