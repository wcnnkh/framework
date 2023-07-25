package io.basc.framework.mysql.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.ConditionSymbol;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.dialect.SqlDialect;
import io.basc.framework.mysql.MysqlDialect;
import io.basc.framework.util.element.Elements;

public class MysqlTest {
	@Test
	public void sqlTest() {
		SqlDialect mapper = new MysqlDialect();
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new Condition("permissionGroupId", ConditionSymbol.EQU, 0, null));
		String search = "";
		conditions.add(new Condition("phone", ConditionSymbol.CONTAINS, search, null));
		conditions.add(new Condition("username", ConditionSymbol.CONTAINS, search, null));
		conditions.add(new Condition("nickname", ConditionSymbol.CONTAINS, search, null));
		QueryOperation queryOperation = new QueryOperation(Elements.of(conditions), new Repository("mysql_test_table"));
		Sql sql = mapper.toSql(queryOperation);
		System.out.println(sql);
	}
}
