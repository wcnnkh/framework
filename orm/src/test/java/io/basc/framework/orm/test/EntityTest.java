package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.annotation.Entity;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;

@Entity
public class EntityTest {
	@Test
	public void test() {
		ObjectRelationalMapper orm = new DefaultObjectRelationalMapper();
		assertTrue(orm.isEntity(EntityTest.class));
	}
}
