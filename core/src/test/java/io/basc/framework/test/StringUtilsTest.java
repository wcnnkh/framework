package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.core.convert.strings.StringConverter;

public class StringUtilsTest {

	@Test
	public void test() {
		Number value = StringConverter.getInstance().convert("-111", Number.class);
		System.out.println(value.getClass());
		assertTrue(BigDecimal.class.isAssignableFrom(value.getClass()));
		Assert.assertTrue(-111 == StringConverter.getInstance().convert("-111", int.class));
	}
}
