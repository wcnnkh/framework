package io.basc.framework.db.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.support.Base64;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.DataException;
import io.basc.framework.data.TemporaryStorageCasOperations;
import io.basc.framework.data.template.TemporaryStorageTemplate;
import io.basc.framework.db.DB;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerException;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.orm.TableStructure;
import io.basc.framework.util.Assert;

/**
 * 使用数据库实现cas和临时数据相关的操作，默认使用java序列化
 * 
 * @author wcnnkh
 *
 */
public class DbTemporaryStorageCasOperations implements TemporaryStorageCasOperations, TemporaryStorageTemplate {
	private final DB db;
	private Serializer serializer = JavaSerializer.INSTANCE;
	private final String tableName;
	private final TableStructure tableStructure;
	private final String casColumnName;
	private final String keyColumnName;
	private final String touchTimeColumnName;
	private final String expColumName;
	private final String whereSql;

	public DbTemporaryStorageCasOperations(DB db, String tableName) {
		this.db = db;
		this.tableName = tableName;
		db.createTable(TemporaryData.class, tableName);
		this.tableStructure = db.getTableStructure(TemporaryData.class);
		this.casColumnName = "`" + tableStructure.getByFieldName("cas").getName() + "`";
		this.keyColumnName = "`" + tableStructure.getByFieldName("key").getName() + "`";
		this.touchTimeColumnName = "`" + tableStructure.getByFieldName("touchTime").getName() + "`";
		this.expColumName = "`" + tableStructure.getByFieldName("exp").getName() + "`";
		this.whereSql = keyColumnName + "=? and  (" + touchTimeColumnName + "+" + expColumName + ")<?";
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		Assert.requiredArgument(serializer != null, "serializer");
		this.serializer = serializer;
	}

	private Object readValue(TemporaryData temporaryData) {
		if (temporaryData == null) {
			return null;
		}

		if (temporaryData.getExp() > 0
				&& (temporaryData.getTouchTime() + temporaryData.getExp()) < System.currentTimeMillis()) {
			return null;
		}

		String value = temporaryData.getValue();
		if (value == null) {
			return null;
		}

		byte[] binaryValue = Base64.DEFAULT.decode(value);
		try {
			return serializer.deserialize(binaryValue);
		} catch (ClassNotFoundException | SerializerException e) {
			throw new DataException(temporaryData.getKey(), e);
		}
	}

	@Override
	public CAS<Object> gets(String key) {
		TemporaryData temporaryData = db.getById(tableName, TemporaryData.class, key);
		if (temporaryData == null) {
			return null;
		}

		Object value = readValue(temporaryData);
		return new CAS<Object>(temporaryData.getCas(), value);
	}

	@Override
	public Object get(String key) {
		TemporaryData temporaryData = db.getById(tableName, TemporaryData.class, key);
		if (temporaryData == null) {
			return null;
		}

		return readValue(temporaryData);
	}

	@Override
	public boolean exists(String key) {
		Sql sql = new SimpleSql("select count(*) from `" + tableName + "` where " + whereSql, key,
				System.currentTimeMillis());
		return db.queryFirst(long.class, sql) > 0;
	}

	@Override
	public boolean delete(String key) {
		return db.deleteById(tableName, TemporaryData.class, key) != 0;
	}

	@Override
	public boolean delete(String key, long cas) {
		Sql sql = new SimpleSql("delete from `" + tableName + "` where " + whereSql + " and " + casColumnName + "=?",
				key, System.currentTimeMillis(), cas);
		return db.update(sql) > 0;
	}

	@Override
	public boolean touch(String key) {
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql("update `" + tableName + "` set " + touchTimeColumnName + "=? where " + whereSql,
				currentTime, key, currentTime);
		return db.update(sql) > 0;
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql("update `" + tableName + "` set " + touchTimeColumnName + "=?, " + expColumName
				+ "=? where " + whereSql, currentTime, Math.max(0, expUnit.toMillis(exp)), key, currentTime);
		return db.update(sql) > 0;
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql("update `" + tableName + "` set " + expColumName + "=? where " + whereSql,
				Math.max(0, expUnit.toMillis(exp)), key, currentTime);
		return db.update(sql) > 0;
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (setIfAbsent(key, value, valueType, exp, expUnit)) {
			setIfPresent(key, value, valueType, exp, expUnit);
		}
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] binaryValue = serializer.serialize(value, valueType);
		String base64Value = Base64.DEFAULT.encode(binaryValue);
		TemporaryData temporaryData = new TemporaryData();
		temporaryData.setExp(expUnit.toMillis(exp));
		temporaryData.setCreateTime(System.currentTimeMillis());
		temporaryData.setKey(key);
		temporaryData.setValue(base64Value);
		temporaryData.setTouchTime(System.currentTimeMillis());
		return db.saveIfAbsent(TemporaryData.class, temporaryData, tableName) > 0;
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] binaryValue = serializer.serialize(value, valueType);
		String base64Value = Base64.DEFAULT.encode(binaryValue);
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql("update `" + tableName + "` set " + expColumName + "=?, "
				+ tableStructure.getByFieldName("value").getName() + "=?," + casColumnName + "=" + casColumnName
				+ " + 1 where " + whereSql, Math.max(0, expUnit.toMillis(exp)), base64Value, key, currentTime);
		return db.update(sql) > 0;
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		byte[] binaryValue = serializer.serialize(value, valueType);
		String base64Value = Base64.DEFAULT.encode(binaryValue);
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql(
				"update `" + tableName + "` set " + expColumName + "=?, `"
						+ tableStructure.getByFieldName("value").getName() + "`=?," + casColumnName + "'='"
						+ casColumnName + " + 1 where " + whereSql + " and " + casColumnName + "=?",
				Math.max(0, expUnit.toMillis(exp)), base64Value, key, currentTime, cas);
		return db.update(sql) > 0;
	}

}
