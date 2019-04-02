package scw.sql.orm.auto.id;

import scw.beans.annotaion.Bean;
import scw.db.sql.SimpleSql;
import scw.locks.Lock;
import scw.locks.MemcachedLock;
import scw.memcached.Memcached;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.TableInfo;
import scw.sql.orm.auto.AutoCreateService;

@Bean(proxy = false)
public class MemcachedIdAutoCreateServcie implements AutoCreateService {
	private final Memcached memcached;

	public MemcachedIdAutoCreateServcie(Memcached memcached) {
		this.memcached = memcached;
	}

	private String getCacheKey(TableInfo tableInfo, String fieldName) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("id:");
		sb.append(this.getClass().getName());
		sb.append("&");
		sb.append(tableInfo.getClassInfo().getName());
		sb.append("&");
		sb.append(fieldName);
		return sb.toString();
	}

	public long maxId(ORMOperations ormOperations, TableInfo tableInfo, String tableName, ColumnInfo columnInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("select max(").append(columnInfo.getSqlColumnName()).append(")");
		sb.append(" from ");
		sb.append("`");
		sb.append(tableName);
		sb.append("`");
		Object id = ormOperations.selectOne(columnInfo.getType(), new SimpleSql(sb.toString()));
		if (id == null) {
			return 0;
		}

		return (Long) id;
	}

	public void wrapper(ORMOperations ormOperations, Object bean, TableInfo tableInfo, ColumnInfo columnInfo,
			String tableName) throws Throwable {
		String key = getCacheKey(tableInfo, columnInfo.getName());
		long next;
		if (memcached.get(key) == null) {
			// 不存在
			Lock lock = new MemcachedLock(memcached, key + "&lock");
			try {
				lock.lockWait();

				if (memcached.get(key) == null) {
					long maxId = maxId(ormOperations, tableInfo, tableName, columnInfo);
					next = memcached.incr(key, 1, maxId + 1);
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		next = memcached.incr(key, 1);
		columnInfo.setValueToField(bean, next);
	}
}
