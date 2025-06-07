package run.soeasy.framework.codec.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.math.NumberReplacer;
import run.soeasy.framework.core.math.NumberUnit;
import run.soeasy.framework.core.math.NumberUtils;

public final class RmbFormat implements Codec<BigDecimal, String> {
	public static final NumberUnit MONEY_001 = new NumberUnit("分", "0.01");
	public static final NumberUnit MONEY_01 = new NumberUnit("角", "0.1");
	public static final NumberUnit MONEY = new NumberUnit("元", 1L);
	public static final NumberUnit MONEY_10 = new NumberUnit("拾", 10L);
	public static final NumberUnit MONEY_100 = new NumberUnit("佰", 100L);
	public static final NumberUnit MONEY_1000 = new NumberUnit("仟", 1000L);
	public static final NumberUnit MONEY_10000 = new NumberUnit("万", 10000L);
	public static final NumberUnit MONEY_100000000 = new NumberUnit("亿", 100000000L);

	/**
	 * 大写
	 * 
	 * @see NumberReplacer#CAPITALIZE
	 */
	public static final RmbFormat CAPITALIZE = new RmbFormat(NumberReplacer.CAPITALIZE);

	/**
	 * 小写
	 * 
	 * @see NumberReplacer#LOWERCASE
	 */
	public static final RmbFormat LOWERCASE = new RmbFormat(NumberReplacer.LOWERCASE);

	private final Codec<BigDecimal, String> numberCodec;
	private final NumberUnit[] integerUnits;
	private final NumberUnit[] decimalUnits;

	public RmbFormat(Codec<BigDecimal, String> numberCodec) {
		this(numberCodec, new NumberUnit[] { MONEY_100000000, MONEY_10000, MONEY_1000, MONEY_100, MONEY_10, MONEY },
				new NumberUnit[] { MONEY_01, MONEY_001 });
	}

	public RmbFormat(@NonNull Codec<BigDecimal, String> numberCodec, @NonNull NumberUnit[] integerUnits,
			@NonNull NumberUnit[] decimalUnits) {
		this.numberCodec = numberCodec;
		this.integerUnits = integerUnits;
		this.decimalUnits = decimalUnits;
	}

	public Codec<BigDecimal, String> getNumberCodec() {
		return numberCodec;
	}

	@Override
	public String encode(BigDecimal money) throws EncodeException {
		StringBuilder sb = new StringBuilder();
		BigDecimal number = money.abs();
		BigDecimal[] decimals = number.divideAndRemainder(BigDecimal.ONE);
		if (decimals[0].compareTo(BigDecimal.ZERO) == 0) {
			sb.append(numberCodec.encode(BigDecimal.ZERO));
			sb.append("元");
		} else {
			sb.append(NumberUtils.format(decimals[0], numberCodec::encode, integerUnits));
		}

		decimals[1] = decimals[1].setScale(2, RoundingMode.HALF_UP);
		if (decimals[1].compareTo(BigDecimal.ZERO) == 0) {
			// 整数
			sb.append("整");
		} else {
			sb.append(NumberUtils.format(decimals[1], numberCodec::encode, decimalUnits));
		}
		return sb.toString();
	}

	@Override
	public BigDecimal decode(String money) throws DecodeException {
		int index = money.indexOf("整");
		if (index == -1) {
			// 不是整数
			index = money.indexOf("元");
			if (index == -1) {
				// 内嵌的
				return NumberUtils.parse(money, numberCodec::decode, ArrayUtils.merge(integerUnits, decimalUnits));
			} else {
				return NumberUtils.parse(money.substring(0, index), this::decode, integerUnits)
						.add(NumberUtils.parse(money.substring(index + 1), this::decode, decimalUnits));
			}
		} else {
			// 是整数
			return NumberUtils.parse(money.substring(0, index), this::decode, integerUnits);
		}
	}

	public String encode(long money) {
		return encode(new BigDecimal(money));
	}
}
