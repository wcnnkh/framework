package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.db.DB;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.support.Copy;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.transaction.TransactionUtils;
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
			db.insert(table1);
			assertFalse(db.saveIfAbsent(table1));
		}
	}

	private static void saveOrUpdate() {
		System.out.println("------------------saveOrUpdate");
		LoggerFactory.getSource().getLevelManager().getMaster().put("io.basc.framework.sql", Levels.DEBUG.getValue());
		for (int i = 0; i < 5; i++) {
			TestTable1 table1 = new TestTable1();
			table1.setId(i);
			table1.setKey(XUtils.getUUID());
			table1.setValue(i);
			db.saveOrUpdate(table1);
			TestTable1 query = db.queryByPrimaryKeys(TestTable1.class, i).getElements().first();
			assertTrue(query.getKey().equals(table1.getKey()));
			// 使用queryAll的原因是为了测试全部
			query = db.queryAll(TestTable1.class).getElements().filter((e) -> e.getId() == table1.getId()).findAny()
					.get();
			assertTrue(query.getKey().equals(table1.getKey()));
		}
	}

	public static void query() {
		for (int i = 0; i < 100; i++) {
			int v = i;
			new Thread(() -> {
				System.out.println(JsonUtils.getSupport()
						.toJsonString(db.queryByPrimaryKeys(TestTable1.class, v).getElements().toList()));
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
	public void transactionTest() {
		db.deleteAll(TestTable1.class);
		TestTable1 table1 = new TestTable1();
		table1.setId(1);
		table1.setKey(XUtils.getUUID());
		table1.setValue(1);
		db.saveOrUpdate(table1);

		TestTable1 bean = db.getById(TestTable1.class, 1);
		Transaction transaction = TransactionUtils.getTransaction(TransactionDefinition.DEFAULT);
		TestTable1 updateBean = Copy.clone(bean);
		updateBean.setValue(2);
		db.updateById(updateBean);
		// 直接回滚
		transaction.rollback();
		TestTable1 oldBean = db.getById(TestTable1.class, 1);
		assertEquals(bean, oldBean);
	}
}
