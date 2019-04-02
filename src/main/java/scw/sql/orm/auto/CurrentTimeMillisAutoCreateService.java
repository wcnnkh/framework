package scw.sql.orm.auto;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.TableInfo;

public final class CurrentTimeMillisAutoCreateService implements AutoCreateService {
	public static final CurrentTimeMillisAutoCreateService CURRENT_TIME_MILLIS = new CurrentTimeMillisAutoCreateService();

	public void wrapper(ORMOperations ormOperations, Object bean, TableInfo tableInfo, ColumnInfo columnInfo,
			String tableName) throws Throwable {
		columnInfo.setValueToField(bean, System.currentTimeMillis());
	}

}
