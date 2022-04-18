package io.basc.framework.test;

import io.basc.framework.util.StringUtils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void test() {
		Assert.assertTrue(-111 == StringUtils.parseByte(" - 111,,,"));
		Assert.assertTrue(-111 == StringUtils.parseShort(" - 111,,,"));
		Assert.assertTrue(-111 == StringUtils.parseInt(" - 111,,,"));
		Assert.assertTrue(111 == StringUtils.parseLong("+,   1 11 abc "));
		Assert.assertTrue(-111 == StringUtils.parseFloat(" - 111,,,"));
		Assert.assertTrue(111 == StringUtils.parseDouble("+,   1 11 abc "));
		
		Assert.assertTrue(0 == StringUtils.parseUnsignedInt(" - 111,,,"));
		Assert.assertTrue(111 == StringUtils.parseUnsignedLong("+,   1 11 abc "));
	}
}
