package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import scw.common.Iterator;
import scw.common.exception.NotFoundException;
import scw.database.DataBaseUtils;
import scw.db.annotation.AutoCreate;
import scw.db.auto.AutoCreateService;
import scw.db.database.DataBase;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.cache.AbstractORMCacheTemplate;
import scw.sql.orm.result.DefaultResult;
import scw.sql.orm.result.Result;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends AbstractORMCacheTemplate implements
		ConnectionFactory, AutoCloseable {
	private Map<String, AutoCreateService> autoCreateMap;

	protected synchronized void setAutoCreateService(String groupName,
			AutoCreateService autoCreateService) {
		if (autoCreateMap == null) {
			autoCreateMap = new HashMap<String, AutoCreateService>();
		}

		autoCreateMap.put(groupName, autoCreateService);
	}

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
		iterator(
				getSqlFormat().toSelectByIdSql(tableInfo, tableInfo.getName(),
						null), iterator);
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
	public boolean save(Object bean, String tableName) {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		for (ColumnInfo columnInfo : tableInfo.getAutoCreateColumns()) {
			AutoCreate autoCreate = columnInfo.getAutoCreate();
			AutoCreateService service = autoCreateMap == null ? null
					: autoCreateMap.get(autoCreate.value());
			if (service == null) {
				throw new NotFoundException(tableInfo.getClassInfo().getName()
						+ "中字段[" + columnInfo.getName()
						+ "的注解@AutoCreate找不到指定名称的实现:" + autoCreate.value());
			}

			service.wrapper(bean, tableInfo, columnInfo, tableName);
		}
		return super.save(bean, tableName);
	}
}
