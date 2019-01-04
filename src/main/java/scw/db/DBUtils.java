package scw.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scw.database.SQL;
import scw.database.TableInfo;
import scw.db.sql.SQLFormat;

public final class DBUtils {
	private DBUtils() {
	};

	public static Collection<SQL> getSqlList(SQLFormat sqlFormat, Collection<OperationBean> operationBeans) {
		if (operationBeans == null || operationBeans.isEmpty()) {
			return null;
		}

		List<SQL> list = new ArrayList<SQL>();
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
