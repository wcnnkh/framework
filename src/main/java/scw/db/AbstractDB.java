package scw.db;

import java.sql.SQLException;
import java.util.Collection;

import scw.common.Iterator;
import scw.common.Logger;
import scw.database.ConnectionSource;
import scw.database.DataBaseUtils;
import scw.db.sql.MysqlSelect;
import scw.db.sql.Select;
import scw.sql.Sql;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.mysql.MysqlFormat;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.Result;

public abstract class AbstractDB extends JdbcTemplate implements ConnectionSource, AutoCloseable {
	{
		Logger.info("Init DB for className:" + this.getClass().getName());
	}

	public AbstractDB(SqlFormat sqlFormat) {
		super(sqlFormat == null ? new MysqlFormat() : sqlFormat);
	}

	public void iterator(Class<?> tableClass, Iterator<Result> iterator) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		iterator(getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName(), null), iterator);
	}

	public void iterator(Sql sql, final Iterator<Result> iterator) {
		DataBaseUtils.iterator(this, sql, new Iterator<java.sql.ResultSet>() {

			public void iterator(java.sql.ResultSet data) {
				try {
					iterator.iterator(new DefaultResult(data));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Select createSelect() {
		return new MysqlSelect(this);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String tableName, String columnName) {
		Select select = createSelect();
		select.desc(tableClass, columnName);
		return select.getResultSet().getFirst().get(type, tableName);
	}

	public <T> T getMaxValue(Class<T> type, Class<?> tableClass, String columnName) {
		return getMaxValue(type, tableClass, null, columnName);
	}

	public int getMaxIntValue(Class<?> tableClass, String fieldName) {
		Integer maxId = getMaxValue(Integer.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0;
		}
		return maxId;
	}

	public long getMaxLongValue(Class<?> tableClass, String fieldName) {
		Long maxId = getMaxValue(Long.class, tableClass, fieldName);
		if (maxId == null) {
			maxId = 0L;
		}
		return maxId;
	}

	public void execute(Collection<OperationBean> operationBeans) {
		Collection<Sql> sqls = DBUtils.getSqlList(getSqlFormat(), operationBeans);
		if (sqls == null || sqls.isEmpty()) {
			return;
		}

		execute(operationBeans);
	}
}
