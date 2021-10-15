package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.db.DB;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.util.XUtils;

public class OrmTest {
	private static DB db = new SQLiteDB(Sys.env.getWorkPath() + "/orm_test.db");

	static {
		db.createTable(TestTable1.class);
	}

	private static void initData() {
		for (int i = 0; i < 5; i++) {
			TestTable1 table1 = new TestTable1();
			table1.setId(i);
			table1.setKey(XUtils.getUUID());
			table1.setValue(i);
			db.save(table1);
			assertFalse(db.saveIfAbsent(table1));
		}
	}

	private static void saveOrUpdate() {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put("io.basc.framework.sql", Levels.DEBUG.getValue());
		for (int i = 0; i < 5; i++) {
			TestTable1 table1 = new TestTable1();
			table1.setId(i);
			table1.setKey(XUtils.getUUID());
			table1.setValue(i);
			db.saveOrUpdate(table1);
			TestTable1 query = db.getById(TestTable1.class, i);
			assertTrue(query.getKey().equals(table1.getKey()));
		}
	}

	public static void query() {
		for (int i = 0; i < 100; i++) {
			int v = i;
			new Thread(() -> {
				System.out.println(JSONUtils.getJsonSupport().toJSONString(db.getByIdList(TestTable1.class, v)));
			}).start();
		}
	}

	public static void main(String[] args) {
		query();
	}

	@Test
	public void test() {
		initData();
		saveOrUpdate();
	}
}
