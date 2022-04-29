package io.basc.framework.util;

import io.basc.framework.env.BascObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class NumberUnit extends BascObject implements Serializable,
		Comparable<NumberUnit> {
	private static final long serialVersionUID = 1L;

	public static final NumberUnit MONEY_001 = new NumberUnit("分", "0.01");
	public static final NumberUnit MONEY_01 = new NumberUnit("角", "0.1");
	public static final NumberUnit MONEY = new NumberUnit("元", 1L);
	public static final NumberUnit MONEY_10 = new NumberUnit("拾", 10L);
	public static final NumberUnit MONEY_100 = new NumberUnit("佰", 100L);
	public static final NumberUnit MONEY_1000 = new NumberUnit("仟", 1000L);
	public static final NumberUnit MONEY_10000 = new NumberUnit("万", 10000L);
	public static final NumberUnit MONEY_100000000 = new NumberUnit("亿",
			100000000L);

	public static final NumberUnit B = new NumberUnit("B", 1l);
	public static final NumberUnit KB = new NumberUnit("KB", 1024L);
	public static final NumberUnit MB = new NumberUnit("MB", 1024L * 1024L);
	public static final NumberUnit GB = new NumberUnit("GB",
			1024L * 1024L * 1024L);
	public static final NumberUnit TB = new NumberUnit("TB",
			1024L * 1024L * 1024L * 1024L);

	private final String name;
	private final BigDecimal radix;

	public NumberUnit(String name, long radix) {
		this(name, new BigDecimal(radix));
	}

	public NumberUnit(String name, String radix) {
		this(name, new BigDecimal(radix));
	}

	public NumberUnit(String name, BigDecimal radix) {
		Assert.requiredArgument(name != null, "name");
		Assert.requiredArgument(
				radix != null && radix.compareTo(BigDecimal.ZERO) > 0, "radix");
		this.name = name;
		this.radix = radix;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getRadix() {
		return radix;
	}

	@Override
	public int compareTo(NumberUnit o) {
		return radix.compareTo(o.radix);
	}
}
