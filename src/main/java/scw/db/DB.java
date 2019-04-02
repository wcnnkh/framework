package scw.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import scw.common.Iterator;
import scw.db.database.DataBase;
import scw.sql.RowCallback;
import scw.sql.Sql;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.Result;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends AbstractORMCacheTemplate implements ConnectionFactory, AutoCloseable {

	public abstract DataBase getDataBase();

	@Override
	public SqlFormat getSqlFormat() {
		return getDataBase().getDataBaseType().getSqlFormat();
	}

	@Override
	protected Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	/**
	 * @param sqlFormat
	 *            可以为空
	 * @param cache
	 *            可以为空
	 */
	public void iterator(Class<?> tableClass, Iterator<Result> iterator) {
		TableInfo tableInfo = ORMUtils.getTableInfo(tableClass);
		iterator(getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName(), null), iterator);
	}

	public void iterator(Sql sql, final Iterator<Result> iterator) {
		query(sql, new RowCallback() {

			public void processRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					iterator.iterator(new DefaultResult(rs));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
