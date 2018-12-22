package scw.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import scw.database.SQL;
import scw.db.sql.SQLFormat;

public final class DBUtils {
	private DBUtils() {
	};

	public static Collection<SQL> getSqlList(SQLFormat sqlFormat,
			Collection<OperationBean> operationBeans) {
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

	public static List<SQL> getSaveSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}

			sqls.add(sqlFormat.toInsertSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getUpdateSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toUpdateSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getDeleteSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toDeleteSql(obj));
		}
		return sqls;
	}

	public static List<SQL> getSaveOrUpdateSqlList(SQLFormat sqlFormat,
			Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return null;
		}

		List<SQL> sqls = new ArrayList<SQL>();
		for (Object obj : beans) {
			if (obj == null) {
				continue;
			}
			sqls.add(sqlFormat.toSaveOrUpdateSql(obj));
		}
		return sqls;
	}
}
