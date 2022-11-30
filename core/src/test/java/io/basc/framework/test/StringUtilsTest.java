package io.basc.framework.test;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.convert.lang.StringConverter;

public class StringUtilsTest {

	@Test
	public void test() {
		Assert.assertTrue(-111 == StringConverter.parseByte(" -111"));
	}
}
