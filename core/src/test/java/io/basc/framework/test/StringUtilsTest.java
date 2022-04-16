package io.basc.framework.test;

import io.basc.framework.util.StringUtils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test() {
		Assert.assertTrue(-111 == StringUtils.parseInt("-111,,,"));
		Assert.assertTrue(111 == StringUtils.parseInt("+,   1 11 abc "));
	}
}
