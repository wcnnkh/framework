package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.db.DB;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.ConditionsBuilder;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.sqlite.SQLiteDialect;
import io.basc.framework.util.XUtils;

public class OrmTest {
	private static DB db = new SQLiteDB(Sys.getEnv().getWorkPath() + "/orm_test.db");

	static {
		db.createTable(TestTable1.class);
	}

	private static void initData() {
		db.deleteAll(TestTable1.class);
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
		LoggerFactory.getLevelManager().getSourceMap().put("io.basc.framework.sql", Levels.DEBUG.getValue());
		for (int i = 0; i < 5; i++) {
			TestTable1 table1 = new TestTable1();
			table1.setId(i);
			table1.setKey(XUtils.getUUID());
			table1.setValue(i);
			db.saveOrUpdate(table1);
			// 使用queryAll的原因是为了测试全部
			TestTable1 query = db.getById(TestTable1.class, i);
			if (query == null) {
				// TODO 这样做是为了解决在未知的情况下maven install时出现的空指针，还不知道原因
				query = db.queryAll(TestTable1.class).filter((e) -> e.getId() == table1.getId()).findAny().get();
			}
			assertTrue(query.getKey().equals(table1.getKey()));
		}
	}

	public static void query() {
		for (int i = 0; i < 100; i++) {
			int v = i;
			new Thread(() -> {
				System.out.println(JsonUtils.getJsonSupport().toJsonString(db.getByIdList(TestTable1.class, v)));
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
