package io.basc.framework.sql.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;
import io.basc.framework.sql.orm.annotation.Table;

@Table
public class SqlTest {
	@Test
	public void test() {
		ObjectRelationalMapping orm = new DefaultObjectRelationalMapping();
		assertTrue(orm.isEntity(SqlTest.class));
	}
}
