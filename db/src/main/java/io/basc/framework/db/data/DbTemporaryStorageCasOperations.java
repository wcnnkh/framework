package io.basc.framework.db.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.support.Base64;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.DataException;
import io.basc.framework.data.DataStorage;
import io.basc.framework.data.TemporaryDataCasOperations;
import io.basc.framework.db.Database;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerException;
import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.TableStructure;
import io.basc.framework.util.Assert;

/**
 * 使用数据库实现cas和临时数据相关的操作，默认使用java序列化
 * 
 * @author wcnnkh
 *
 */
public class DbTemporaryStorageCasOperations implements TemporaryDataCasOperations, DataStorage {
	private final Database db;
	private Serializer serializer = JavaSerializer.INSTANCE;
	private final String tableName;
	private final TableStructure tableStructure;
	private final String casColumnName;
	private final String keyColumnName;
	private final String touchTimeColumnName;
	private final String expColumName;
	private final String whereSql;

	public DbTemporaryStorageCasOperations(Database db, String tableName) {
		this.db = db;
		this.tableName = tableName;
		db.createTable(TemporaryData.class, tableName);
		this.tableStructure = db.getMapper().getStructure(TemporaryData.class);
		this.casColumnName = "`" + tableStructure.getByName("cas").getName() + "`";
		this.keyColumnName = "`" + tableStructure.getByName("key").getName() + "`";
		this.touchTimeColumnName = "`" + tableStructure.getByName("touchTime").getName() + "`";
		this.expColumName = "`" + tableStructure.getByName("exp").getName() + "`";
		this.whereSql = keyColumnName + "=? and (" + expColumName + "<=0 or (" + touchTimeColumnName + "+"
				+ expColumName + ")<?)";
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
	public Long getRemainingSurvivalTime(String key) {
		TemporaryData temporaryData = db.getById(tableName, TemporaryData.class, key);
		if (temporaryData == null) {
			return null;
		}

		long exp = temporaryData.getExp();
		if (exp <= 0) {
			return -1L;
		}

		return exp - (System.currentTimeMillis() - temporaryData.getTouchTime());
	}

	@Override
	public boolean exists(String key) {
		Sql sql = new SimpleSql("select count(*) from `" + tableName + "` where " + whereSql, key,
				System.currentTimeMillis());
		return db.query(long.class, sql).getElements().first() > 0;
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
		return db.saveIfAbsent(TemporaryData.class, temporaryData, tableName);
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] binaryValue = serializer.serialize(value, valueType);
		String base64Value = Base64.DEFAULT.encode(binaryValue);
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql(
				"update `" + tableName + "` set " + expColumName + "=?, " + tableStructure.getByName("value").getName()
						+ "=?," + casColumnName + "=" + casColumnName + " + 1 where " + whereSql,
				Math.max(0, expUnit.toMillis(exp)), base64Value, key, currentTime);
		return db.update(sql) > 0;
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		byte[] binaryValue = serializer.serialize(value, valueType);
		String base64Value = Base64.DEFAULT.encode(binaryValue);
		long currentTime = System.currentTimeMillis();
		Sql sql = new SimpleSql("update `" + tableName + "` set " + expColumName + "=?, `"
				+ tableStructure.getByName("value").getName() + "`=?," + casColumnName + "'='" + casColumnName
				+ " + 1 where " + whereSql + " and " + casColumnName + "=?", Math.max(0, expUnit.toMillis(exp)),
				base64Value, key, currentTime, cas);
		return db.update(sql) > 0;
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType) {
		return setIfPresent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType) {
		set(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		return setIfAbsent(key, value, valueType, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas) {
		return cas(key, value, valueType, cas, 0, TimeUnit.MILLISECONDS);
	}
}
