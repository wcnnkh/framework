package io.basc.framework.sql.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.sql.template.TableMapper;
import io.basc.framework.sql.template.annotation.Table;
import io.basc.framework.sql.template.support.DefaultTableMapper;

@Table
public class SqlTest {
	@Test
	public void test() {
		TableMapper orm = new DefaultTableMapper();
		assertTrue(orm.isEntity(TypeDescriptor.valueOf(SqlTest.class)));
	}
}
