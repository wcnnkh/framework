package io.basc.framework.mysql.test;

import org.junit.Test;

import io.basc.framework.mysql.MysqlDialect;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.ConditionsBuilder;
import io.basc.framework.sql.orm.SqlDialect;

public class MysqlTest {
	@Test
	public void sqlTest() {
		SqlDialect mapper = new MysqlDialect();
		mapper.getStructure(MysqlTest.class);

		ConditionsBuilder builder = mapper
				.conditionsBuilder((e) -> e.name("permissionGroupId").greaterThan().value(0).build());
		builder.and((e) -> e.name("permissionGroupId").in().value(1).build());

		String search = "";
		Conditions conditions = builder.newBuilder().and((e) -> e.name("uid").like().value(search).build())
				.and((e) -> e.name("phone").like().value(search).build())
				.and((e) -> e.name("username").like().value(search).build())
				.and((e) -> e.name("nickname").like().value(search).build()).build();
		builder.and(conditions);
		System.out.println(mapper.toSelectSql(mapper.getStructure(getClass()), builder.build(), null));
	}
}
