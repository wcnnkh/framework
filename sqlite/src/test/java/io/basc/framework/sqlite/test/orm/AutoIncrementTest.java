package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.db.DB;
import io.basc.framework.env.Sys;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.util.XUtils;

public class AutoIncrementTest {
	private static DB db = new SQLiteDB(Sys.env.getWorkPath() + "/auto_increment.db");

	@Test
	public void test() {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put("io.basc.framework.sql", Levels.DEBUG.getValue());
		db.createTable(AutoIncrementTestBean.class);
		db.deleteAll(AutoIncrementTestBean.class);
		int size = 2;
		for (int x = 0; x < 2; x++) {
			for (int i = 1; i <= size; i++) {
				AutoIncrementTestBean bean = new AutoIncrementTestBean();
				bean.setId(i);
				bean.setValue(XUtils.getUUID());
				boolean b = db.saveIfAbsent(bean);
				System.out.println(i + "-" + b);
			}
		}
		long count = db.count(new SimpleSql("select * from auto_increment_test_bean"));
		assertTrue(size == count);
	}
}
