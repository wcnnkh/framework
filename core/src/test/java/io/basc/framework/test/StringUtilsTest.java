package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.support.strings.StringConverter;

public class StringUtilsTest {

	@Test
	public void test() {
		Number value = (Number) StringConverter.getInstance().convert("-111", TypeDescriptor.valueOf(String.class),
				TypeDescriptor.valueOf(Number.class));
		assertTrue(BigDecimal.class.isAssignableFrom(value.getClass()));
		Assert.assertTrue(-111 == (int) StringConverter.getInstance().convert("-111",
				TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(int.class)));
	}
}
