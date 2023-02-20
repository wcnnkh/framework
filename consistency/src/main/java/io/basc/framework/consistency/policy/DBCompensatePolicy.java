package io.basc.framework.consistency.policy;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import io.basc.framework.codec.support.Base64;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.db.DB;
import io.basc.framework.db.locks.TableLockFactory;
import io.basc.framework.io.SerializerException;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.orm.annotation.Table;
import io.basc.framework.util.CollectionUtils;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DBCompensatePolicy extends StorageCompensatePolicy {
	private static final String TABLE_NAME = "_compensat_table";
	private final DB db;
	private final TableLockFactory tableLockFactory;

	public DBCompensatePolicy(DB db) {
		this.db = db;
		this.tableLockFactory = new TableLockFactory(db);
		db.createTable(CompensatTable.class, false);
	}

	@Override
	public Lock getLock(String group, String id) {
		checkParameter(group, id);

		return tableLockFactory.getLock(group + "-" + id);
	}

	@Override
	public Enumeration<String> getUnfinishedGroups() {
		Sql sql = new SimpleSql("select `group` from " + TABLE_NAME + " where cts<? group by `group` order by cts desc",
				(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(getCompenstBeforeMinute())));
		List<String> groups = db.query(String.class, sql).toList();
		if (CollectionUtils.isEmpty(groups)) {
			return Collections.emptyEnumeration();
		}

		return CollectionUtils.toEnumeration(groups.iterator());
	}

	@Override
	public String getLastUnfinishedId(String group) {
		Sql sql = new SimpleSql("select `id` from " + TABLE_NAME + " where group=? order by cts desc limit 0,1");
		return db.query(String.class, sql).first();
	}

	@Override
	public boolean add(String group, String id, Runnable runnable) {
		if (!isDone(group, id)) {
			return false;
		}

		byte[] data = getSerializer().serialize(runnable);
		String content = Base64.DEFAULT.encode(data);
		CompensatTable compensatTable = new CompensatTable();
		compensatTable.setGroup(group);
		compensatTable.setId(id);
		compensatTable.setCts(System.currentTimeMillis());
		compensatTable.setContent(content);
		db.save(compensatTable);
		return true;
	}

	@Override
	protected Runnable getRunnable(String group, String id) {
		CompensatTable table = db.getById(CompensatTable.class, group, id);
		if (table == null) {
			return null;
		}

		byte[] data = Base64.DEFAULT.decode(table.getContent());
		try {
			return getSerializer().deserialize(data);
		} catch (ClassNotFoundException e) {
			logger.error(e, "Get not found class fail group {} id {}", group, id);
		} catch (SerializerException e) {
			logger.error(e, "Get ser fail group {} id {}", group, id);
		}
		return null;
	}

	@Override
	public boolean exists(String group, String id) {
		return db
				.query(String.class,
						new SimpleSql("select id from " + TABLE_NAME + " where group=? and id=?", group, id))
				.findFirst().isPresent();
	}

	@Override
	public boolean remove(String group, String id) {
		return db.update(new SimpleSql("delete from " + TABLE_NAME + " where group=? and id=?", group, id)) > 0;
	}

	@Table(name = TABLE_NAME)
	@SuppressWarnings("unused")
	private static class CompensatTable {
		@PrimaryKey
		private String group;
		@PrimaryKey
		private String id;

		private String content;

		private long cts;

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public long getCts() {
			return cts;
		}

		public void setCts(long cts) {
			this.cts = cts;
		}
	}
}
