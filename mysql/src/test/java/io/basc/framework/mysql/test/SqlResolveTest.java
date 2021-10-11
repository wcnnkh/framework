package io.basc.framework.mysql.test;

import org.junit.Test;

import io.basc.framework.mysql.MysqlDialect;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.XUtils;

public class SqlResolveTest {
	private String taskId = XUtils.getUUID();
	private long executeTime = System.currentTimeMillis();
	private Sql saveSql = new SimpleSql("insert into task_lock_table (taskId, lastTime) values (?, ?)", taskId, executeTime);
	private Sql updateSql = new SimpleSql("update task_lock_table set lastTime=? where taskId=? and lastTime<?", executeTime, taskId, executeTime);
	
	@Test
	public void saveOrUpdate() {
		MysqlDialect mysqlDialect = new MysqlDialect();
		Sql sql = mysqlDialect.saveOrUpdate(saveSql, updateSql);
		System.out.println(sql);
	}
}
