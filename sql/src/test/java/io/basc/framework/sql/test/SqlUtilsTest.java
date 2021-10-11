package io.basc.framework.sql.test;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlExpression;
import io.basc.framework.sql.SqlUtils;

public class SqlUtilsTest {
	@Test
	public void resoveUpdate() {
		Sql sql = new SimpleSql("update table set name=IF(A=B, D, E), pwd=? where x=y and z=?", "1", "2");
		Map<String, SqlExpression> map = SqlUtils.resolveUpdateSetMap(sql);
		for(Entry<String, SqlExpression> entry : map.entrySet()) {
			System.out.println("---------------------------------");
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}
}
