package shuchaowen.db.storage;

import java.util.Collection;
import java.util.List;

import shuchaowen.db.ConnectionSource;
import shuchaowen.db.DB;
import shuchaowen.db.DBUtils;
import shuchaowen.db.OperationBean;
import shuchaowen.db.TableInfo;
import shuchaowen.db.TransactionContext;
import shuchaowen.db.result.ResultSet;
import shuchaowen.db.sql.SQL;
import shuchaowen.db.sql.SQLFormat;

public class AbstractStorage implements Storage{
	protected final SQLFormat sqlFormat;
	protected final ConnectionSource connectionSource;
	
	public AbstractStorage(SQLFormat sqlFormat, ConnectionSource connectionSource){
		this.sqlFormat = sqlFormat;
		this.connectionSource = connectionSource;
	}
	
	public <T> T getById(Class<T> type, Object... params) {
		TableInfo tableInfo = DB.getTableInfo(type);
		SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tableInfo.getName(), params);
		ResultSet resultSet = TransactionContext.getInstance().select(connectionSource, sql);
		return resultSet.getFirst(type);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		TableInfo tableInfo = DB.getTableInfo(type);
		SQL sql = sqlFormat.toSelectByIdSql(tableInfo, tableInfo.getName(), params);
		ResultSet resultSet = TransactionContext.getInstance().select(connectionSource, sql);
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
