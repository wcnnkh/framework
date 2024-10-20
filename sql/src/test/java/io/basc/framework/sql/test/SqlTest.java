package io.basc.framework.sql.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;
import io.basc.framework.sql.orm.annotation.Table;

@Table
public class SqlTest {
	@Test
	public void test() {
		ObjectRelationalFactory orm = new DefaultObjectRelationalMapper();
		assertTrue(orm.isEntity(SqlTest.class));
	}
}
