package io.basc.framework.orm.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.orm.ObjectRelationalFactory;
import io.basc.framework.orm.annotation.Entity;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;

@Entity
public class EntityTest {
	private String a;
	private int b;

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

	@Test
	public void test() {
		ObjectRelationalFactory orm = new DefaultObjectRelationalMapper();
		assertTrue(orm.isEntity(EntityTest.class));
		orm.getStructure(EntityTest.class).forEach((e) -> {
			System.out.println(e.isNullable());
		});
	}
}
