package io.basc.framework.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.stream.IntStream;

import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResultSetProperties implements Properties {
	@NonNull
	private final ResultSet resultSet;

	@Override
	public Elements<Property> getElements() {
		ResultSetMetaData metaData;
		int columnCount;
		try {
			metaData = resultSet.getMetaData();
			columnCount = metaData.getColumnCount();
		} catch (SQLException e) {
			throw new SqlException(e);
		}

		return Elements.of(() -> IntStream.range(0, columnCount).mapToObj((index) -> {
			ResultSetProperty property = new ResultSetProperty(resultSet, index);
			try {
				setName(property, metaData, index + 1);
			} catch (SQLException e) {
				throw new SqlException(e);
			}
			return property;
		}));
	}

	private void setName(ResultSetProperty property, ResultSetMetaData metaData, int resultIndex) throws SQLException {
		String labelName = metaData.getColumnLabel(resultIndex);
		String columnName = metaData.getColumnName(resultIndex);
		String name = StringUtils.isEmpty(labelName) ? columnName : labelName;
		if (StringUtils.isEmpty(name)) {
			return;
		}

		property.setName(name);
		if (StringUtils.isNotEmpty(labelName)) {
			// 如果lableName和columnName相同说明不存在as别名，那么不将columnName做为名称
			Elements<String> aliasNames = StringUtils.equals(labelName, columnName) ? Elements.empty()
					: Elements.singleton(columnName);
			String tableName = metaData.getTableName(resultIndex);
			if (StringUtils.isNotEmpty(tableName)) {
				aliasNames = aliasNames.concat(Elements.singleton(tableName + "." + columnName));
			}
		}
	}
}
