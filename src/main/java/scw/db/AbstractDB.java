package scw.db;

import java.sql.SQLException;

import scw.common.Iterator;
import scw.database.DataBaseUtils;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.cache.Cache;
import scw.sql.orm.mysql.MysqlFormat;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.Result;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class AbstractDB extends AbstractORMCacheTemplate implements ConnectionFactory, AutoCloseable {

	public AbstractDB(SqlFormat sqlFormat, Cache cache) {
		super(sqlFormat == null ? new MysqlFormat() : sqlFormat, cache);
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

	@Override
	public boolean execute(Sql sql) throws SqlException {
		boolean b = SqlTransactionUtils.executeSql(this, sql);
		if (b) {
			log(sql);
		} else {
			return super.execute(sql);
		}
		return true;
	}

	@Override
	public int update(Sql sql) throws SqlException {
		boolean b = SqlTransactionUtils.executeSql(this, sql);
		if (b) {
			log(sql);
		} else {
			return super.update(sql);
		}
		return 0;
	}
}
