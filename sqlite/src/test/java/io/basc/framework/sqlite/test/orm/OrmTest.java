package io.basc.framework.sqlite.test.orm;

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
			TestTable1 table1 = db.getById(TestTable1.class, i);
			if (table1 == null) {
				table1 = new TestTable1();
				table1.setId(i);
				table1.setKey(XUtils.getUUID());
				table1.setValue(i);
				boolean b = db.save(table1);
				System.out.println("添加数据" + (b ? "成功" : "失败"));
			}
		}
	}

	private static void saveOrUpdate() {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put("io.basc.framework.sql", Levels.DEBUG.getValue());
		for (int i = 0; i < 5; i++) {
			TestTable1 table1 = new TestTable1();
			table1.setId(i);
			table1.setKey(XUtils.getUUID());
			table1.setValue(i);
			boolean b = db.saveOrUpdate(table1);
			System.out.println("saveOrUpdate数据" + (b ? "成功" : "失败"));
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
		System.out.println(JSONUtils.getJsonSupport().toJSONString(db.getByIdList(TestTable1.class, 1)));
		saveOrUpdate();
		System.out.println(JSONUtils.getJsonSupport().toJSONString(db.getByIdList(TestTable1.class, 1)));
	}
}
