package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.annotation.Entity;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;

@Entity
public class EntityTest {
	@Test
	public void test() {
		ObjectRelationalMapping orm = new DefaultObjectRelationalMapping();
		assertTrue(orm.isEntity(EntityTest.class));
	}
}
