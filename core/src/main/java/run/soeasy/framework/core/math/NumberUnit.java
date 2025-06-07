package run.soeasy.framework.core.math;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;

@Data
public class NumberUnit implements Serializable, Comparable<NumberUnit> {
	private static final long serialVersionUID = 1L;

	public static final NumberUnit B = new NumberUnit("B", 1l);
	public static final NumberUnit KB = new NumberUnit("KB", 1024L);
	public static final NumberUnit MB = new NumberUnit("MB", 1024L * 1024L);
	public static final NumberUnit GB = new NumberUnit("GB", 1024L * 1024L * 1024L);
	public static final NumberUnit TB = new NumberUnit("TB", 1024L * 1024L * 1024L * 1024L);

	private final String name;
	private final BigDecimal radix;

	public NumberUnit(String name, long radix) {
		this(name, new BigDecimal(radix));
	}

	public NumberUnit(String name, String radix) {
		this(name, new BigDecimal(radix));
	}

	public NumberUnit(@NonNull String name, @NonNull BigDecimal radix) {
		Assert.isTrue(radix.compareTo(BigDecimal.ZERO) > 0, "radix need to be greater than 0");
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
