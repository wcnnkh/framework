package io.basc.framework.mysql.test;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.util.Pair;

public class SqlResolveTest {

	@Test
	public void resoveUpdate() {
		String sql = "update table set name=IF(A=B, D, E), pwd=? where x=y and z=?";
		System.out.println(SqlUtils.split(new SimpleSql("x=y and z=?", "1"), "and").collect(Collectors.toList()));
	}

	public Map<String, Pair<Sql, Sql>> resolveCondition(Sql sql, String separator, Collection<String> keywords) {
		//Stream<Sql> stream = SqlUtils.split(sql, "and");
		return null;
	}
}
