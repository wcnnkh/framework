package run.soeasy.framework.core.math;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilsTest {
	@Test
	public void test() {
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("123")));
		Assert.assertFalse(NumberUtils.isInteger(new BigDecimal("123.2")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("123.00")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("0.00")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal(12.00f)));
		Assert.assertTrue(NumberUtils.stripTrailingZeros(new BigDecimal("10.00")).toString().equals("10"));
	}
}
