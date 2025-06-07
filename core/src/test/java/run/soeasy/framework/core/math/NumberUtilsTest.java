package run.soeasy.framework.core.math;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.codec.currency.RmbFormat;

public class NumberUtilsTest {
	@Test
	public void test() {
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("123")));
		Assert.assertFalse(NumberUtils.isInteger(new BigDecimal("123.2")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("123.00")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal("0.00")));
		Assert.assertTrue(NumberUtils.isInteger(new BigDecimal(12.00f)));

		Assert.assertTrue(NumberUtils.stripTrailingZeros(new BigDecimal("10.00")).toString().equals("10"));

		Assert.assertEquals("零一二三四五六七八九", NumberReplacer.LOWERCASE.encode("0123456789"));
		Assert.assertEquals("零壹贰叁肆伍陆柒捌玖", NumberReplacer.CAPITALIZE.encode("0123456789"));
		Assert.assertTrue(RmbFormat.CAPITALIZE.encode(1112).equals("壹仟壹佰壹拾贰元整"));
		Assert.assertTrue(RmbFormat.CAPITALIZE.encode(new BigDecimal("0.12")).equals("零元壹角贰分"));
		Assert.assertTrue(
				RmbFormat.CAPITALIZE.encode(new BigDecimal("123456789.12")).equals("壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖元壹角贰分"));
		Assert.assertTrue(
				RmbFormat.CAPITALIZE.decode("壹亿贰仟叁佰肆拾伍万陆仟柒佰捌拾玖元壹角贰分").equals(new BigDecimal("123456789.12")));
		Assert.assertEquals(RmbFormat.CAPITALIZE.encode(new BigDecimal("0.125")), "零元壹角叁分");
	}
}
