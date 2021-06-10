package scw.orm.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import scw.orm.DefaultObjectRelationalMapping;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.annotation.Table;

@Table
public class SqlTest {
	@Test
	public void test() {
		ObjectRelationalMapping orm = new DefaultObjectRelationalMapping();
		assertTrue(orm.isEntity(SqlTest.class));
	}
}
