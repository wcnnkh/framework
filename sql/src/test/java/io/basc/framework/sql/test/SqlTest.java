package io.basc.framework.sql.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.sql.orm.TableMapper;
import io.basc.framework.sql.orm.annotation.Table;
import io.basc.framework.sql.orm.support.DefaultTableMapper;

@Table
public class SqlTest {
	@Test
	public void test() {
		TableMapper orm = new DefaultTableMapper();
		assertTrue(orm.isEntity(TypeDescriptor.valueOf(SqlTest.class)));
	}
}
