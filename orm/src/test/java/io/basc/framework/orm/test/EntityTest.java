package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelational;
import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.annotation.Entity;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;

@Entity
public class EntityTest {
	private String a;
	private int b;
	private InternalEntity entity;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	public InternalEntity getEntity() {
		return entity;
	}

	public void setEntity(InternalEntity entity) {
		this.entity = entity;
	}

	@Test
	public void test() {
		ObjectRelationalFactory orm = new DefaultObjectRelationalMapper();
		assertTrue(orm.isEntity(EntityTest.class));
		ObjectRelational<? extends Property> objectRelational = orm.getStructure(EntityTest.class)
				.withEntitysAfter((e) -> e.setNameNestingDepth(1));
		objectRelational.getElements().forEach((e) -> System.out.println(e.getName()));
		assertTrue(
				objectRelational.filter((e) -> e.getName().startsWith("entity")).getElements().findAny().isPresent());
	}

	@Entity
	public static class InternalEntity {
		private String c;

		public String getC() {
			return c;
		}

		public void setC(String c) {
			this.c = c;
		}
	}
}
