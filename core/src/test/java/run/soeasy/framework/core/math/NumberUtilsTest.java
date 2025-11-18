package run.soeasy.framework.core.math;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.core.NumberUtils;

public class NumberUtilsTest {
	@Test
	public void test() {
		Assert.assertTrue(NumberUtils.stripTrailingZeros(new BigDecimal("10.00")).toString().equals("10"));
	}
}
