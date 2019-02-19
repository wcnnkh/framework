package scw.sql.orm.result;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public final class MetaDataColumn implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String labelName;
	private String tableName;

	protected MetaDataColumn() {
	};

	public MetaDataColumn(ResultSetMetaData resultSetMetaData, int column) throws SQLException {
		this.tableName = resultSetMetaData.getTableName(column);
		this.name = resultSetMetaData.getColumnName(column);
		this.labelName = resultSetMetaData.getColumnLabel(column);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	public String getLabelName() {
		return labelName;
	}
}
