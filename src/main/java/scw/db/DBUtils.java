package scw.db;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.database.TableInfo;
import scw.db.sql.SQLFormat;
import scw.jdbc.Sql;

public final class DBUtils {
	private DBUtils() {
	};

	public static Collection<Sql> getSqlList(SQLFormat sqlFormat, Collection<OperationBean> operationBeans) {
		if (operationBeans == null || operationBeans.isEmpty()) {
			return null;
		}

		List<Sql> list = new LinkedList<Sql>();
		for (OperationBean operationBean : operationBeans) {
			if (operationBean == null) {
				continue;
			}

			list.add(operationBean.getSql(sqlFormat));
		}
		return list;
	}

	public static String getTableName(String tableName, TableInfo tableInfo, Object obj) {
		if (tableName == null || tableName.length() == 0) {
			if (obj instanceof TableName) {
				return ((TableName) obj).tableName();
			} else {
				return tableInfo.getName();
			}
		} else {
			return tableName;
		}
	}
}
