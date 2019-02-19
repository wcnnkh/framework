package scw.db;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.sql.Sql;
import scw.sql.orm.SqlFormat;

public abstract class DBUtils {
	
	public static Collection<Sql> getSqlList(SqlFormat sqlFormat, Collection<OperationBean> operationBeans) {
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
}
