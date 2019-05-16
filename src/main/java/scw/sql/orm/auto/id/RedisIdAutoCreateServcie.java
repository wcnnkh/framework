package scw.sql.orm.auto.id;

import scw.beans.annotation.Bean;
import scw.data.redis.Redis;
import scw.db.sql.SimpleSql;
import scw.locks.Lock;
import scw.locks.RedisLock;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.TableInfo;
import scw.sql.orm.auto.AutoCreateService;

@Bean(proxy = false)
public class RedisIdAutoCreateServcie implements AutoCreateService {
	private final Redis redis;

	public RedisIdAutoCreateServcie(Redis redis) {
		this.redis = redis;
	}

	private String getCacheKey(TableInfo tableInfo, String fieldName) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("id:");
		sb.append(this.getClass().getName());
		sb.append("&");
		sb.append(tableInfo.getSource().getName());
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
			String tableName, String[] args) throws Throwable {
		String key = getCacheKey(tableInfo, columnInfo.getName());
		long next;
		if (!redis.getStringOperations().exists(key)) {
			// 不存在
			Lock lock = new RedisLock(redis, key + "&lock");
			try {
				lock.lockWait();

				if (!redis.getStringOperations().exists(key)) {
					long maxId = maxId(ormOperations, tableInfo, tableName, columnInfo);
					next = redis.getStringOperations().incr(key, 1, maxId);
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		next = redis.getStringOperations().incr(key);
		columnInfo.setValueToField(bean, next);
	}
}
