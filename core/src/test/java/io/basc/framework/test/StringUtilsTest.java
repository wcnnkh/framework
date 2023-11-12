package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.convert.strings.StringConverter;

public class StringUtilsTest {

	@Test
	public void test() {
		Number value = StringConverter.DEFAULT_STRING_CONVERTER.convert("-111", Number.class);
		assertTrue(BigDecimal.class.isAssignableFrom(value.getClass()));
		Assert.assertTrue(-111 == StringConverter.parseByte("-111"));
	}
}
