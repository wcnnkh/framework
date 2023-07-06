package io.basc.framework.mysql.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.mysql.MysqlDialect;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.util.Elements;

public class MysqlTest {
	@Test
	public void sqlTest() {
		SqlDialect mapper = new MysqlDialect();
		QueryOperation queryOperation = new QueryOperation(new Repository("mysql_test_table"));
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new Condition("permissionGroupId", ConditionSymbol.EQU, 0, null));
		String search = "";
		conditions.add(new Condition("phone", ConditionSymbol.LIKE, search, null));
		conditions.add(new Condition("username", ConditionSymbol.LIKE, search, null));
		conditions.add(new Condition("nickname", ConditionSymbol.LIKE, search, null));
		queryOperation.setConditions(Elements.of(conditions));
		Sql sql = mapper.toSql(queryOperation);
		System.out.println(sql);
	}
}
