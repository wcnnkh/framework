package scw.db.storage;

import java.util.Collection;
import java.util.List;

import scw.database.ConnectionSource;
import scw.database.DataBaseUtils;
import scw.database.SQL;
import scw.database.TableInfo;
import scw.database.TransactionContext;
import scw.database.result.ResultSet;
import scw.db.DBUtils;
import scw.db.OperationBean;
import scw.db.sql.SQLFormat;

public class AbstractStorage implements Storage{
	protected final SQLFormat sqlFormat;
	protected final ConnectionSource connectionSource;
	
	public AbstractStorage(SQLFormat sqlFormat, ConnectionSource connectionSource){
		this.sqlFormat = sqlFormat;
		this.connectionSource = connectionSource;
	}
	
	public <T> T getById(Class<T> type, Object... params) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tableInfo.getName(), params);
		ResultSet resultSet = TransactionContext.getInstance().select(connectionSource, sql, false);
		return resultSet.getObject(type, 0);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(type);
		SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tableInfo.getName(), params);
		ResultSet resultSet = TransactionContext.getInstance().select(connectionSource, sql, false);
		return resultSet.getList(type);
	}

	public void op(Collection<OperationBean> operationBeans) {
		Collection<SQL> sqls = DBUtils.getSqlList(sqlFormat, operationBeans);
		if(sqls == null || sqls.isEmpty()){
			return;
		}
		TransactionContext.getInstance().execute(connectionSource, sqls);
	}
	
}
