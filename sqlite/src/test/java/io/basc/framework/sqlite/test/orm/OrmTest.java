package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.db.DB;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.ConditionsBuilder;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.sqlite.SQLiteDialect;
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
			boolean b = db.saveOrUpdate(table1);
			System.out.println(b);
			TestTable1 query = db.getById(TestTable1.class, i);
			System.out.println(query);
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

	@Test
	public void sqlTest() {
		SqlDialect mapper = new SQLiteDialect();
		ConditionsBuilder builder = mapper
				.conditionsBuilder((e) -> e.name("permissionGroupId").greaterThan().value(0).build());
		builder.and((e) -> e.name("permissionGroupId").in().value(1).build());

		String search = "";
		Conditions conditions = builder.newBuilder().and((e) -> e.name("uid").like().value(search).build())
				.and((e) -> e.name("phone").like().value(search).build())
				.and((e) -> e.name("username").like().value(search).build())
				.and((e) -> e.name("nickname").like().value(search).build()).build();
		builder.and(conditions);
		System.out.println(mapper.toSelectSql(mapper.getStructure(OrmTest.class), builder.build(), null));
	}
}
