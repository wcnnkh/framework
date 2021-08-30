package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalMapping;
import io.basc.framework.orm.sql.annotation.Table;

import org.junit.Test;

@Table
public class SqlTest {
	@Test
	public void test() {
		ObjectRelationalMapping orm = new AnnotationObjectRelationalMapping();
		assertTrue(orm.isEntity(SqlTest.class));
	}
}
