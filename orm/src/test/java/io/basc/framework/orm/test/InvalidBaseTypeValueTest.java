package io.basc.framework.orm.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Test;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.ObjectMapping;
import io.basc.framework.orm.annotation.AutoIncrement;
import io.basc.framework.orm.annotation.InvalidBaseTypeValue;
import io.basc.framework.orm.support.OrmUtils;

public class InvalidBaseTypeValueTest {

	@Test
	public void test() {
		for (Field field : A.class.getDeclaredFields()) {
			InvalidBaseTypeValue invalidBaseTypeValue = AnnotatedElementUtils.getMergedAnnotation(field,
					InvalidBaseTypeValue.class);
			System.out.println(invalidBaseTypeValue);
			if (field.getName().equals("a")) {
				assertTrue(invalidBaseTypeValue.value()[0] == 0);
			}

			if (field.getName().equals("b")) {
				assertTrue(2 == invalidBaseTypeValue.value()[0]);
			}

			if (field.getName().equals("c")) {
				assertTrue(1 == invalidBaseTypeValue.value()[0]);
			}

			if (field.getName().equals("d")) {
				assertTrue(2 == invalidBaseTypeValue.value()[0]);
			}

			if (field.getName().equals("e")) {
				assertTrue(invalidBaseTypeValue.value().length == 0);
			}
		}

		A entity = new A();
		boolean b = OrmUtils.getMapper().hasEffectiveValue(entity, ObjectMapping.getFields(A.class).getByName("a"));
		assertTrue(b);
		b = OrmUtils.getMapper().hasEffectiveValue(entity, ObjectMapping.getFields(A.class).getByName("b"));
		assertFalse(b);
		b = OrmUtils.getMapper().hasEffectiveValue(entity, ObjectMapping.getFields(A.class).getByName("c"));
		assertFalse(b);
		b = OrmUtils.getMapper().hasEffectiveValue(entity, ObjectMapping.getFields(A.class).getByName("e"));
		assertTrue(b);
	}

	private static class A {
		@AutoIncrement
		private Integer a = 1;
		@AutoIncrement
		@InvalidBaseTypeValue(2)
		private int b = 2;

		@AutoIncrement(1)
		private Integer c = 1;

		@AutoIncrement(1)
		@InvalidBaseTypeValue(2)
		private Integer d;

		@AutoIncrement
		@InvalidBaseTypeValue
		private int e;

	}
}
