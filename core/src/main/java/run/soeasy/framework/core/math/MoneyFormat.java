package run.soeasy.framework.core.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.core.collection.ArrayUtils;

public final class MoneyFormat implements Codec<BigDecimal, String> {
	/**
	 * 大写
	 * 
	 * @see NumberReplacer#CAPITALIZE
	 */
	public static final MoneyFormat CAPITALIZE = new MoneyFormat(NumberReplacer.CAPITALIZE);

	/**
	 * 小写
	 * 
	 * @see NumberReplacer#LOWERCASE
	 */
	public static final MoneyFormat LOWERCASE = new MoneyFormat(NumberReplacer.LOWERCASE);

	private final Codec<BigDecimal, String> numberCodec;
	private final NumberUnit[] integerUnits;
	private final NumberUnit[] decimalUnits;

	public MoneyFormat(Codec<BigDecimal, String> numberCodec) {
		this(numberCodec,
				new NumberUnit[] { NumberUnit.MONEY_100000000, NumberUnit.MONEY_10000, NumberUnit.MONEY_1000,
						NumberUnit.MONEY_100, NumberUnit.MONEY_10, NumberUnit.MONEY },
				new NumberUnit[] { NumberUnit.MONEY_01, NumberUnit.MONEY_001 });
	}

	public MoneyFormat(@NonNull Codec<BigDecimal, String> numberCodec, @NonNull NumberUnit[] integerUnits, @NonNull NumberUnit[] decimalUnits) {
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
			NumberUtils.format(sb, decimals[0], numberCodec::encode, 0, integerUnits.length, integerUnits);
		}

		decimals[1] = decimals[1].setScale(2, RoundingMode.HALF_UP);
		if (decimals[1].compareTo(BigDecimal.ZERO) == 0) {
			// 整数
			sb.append("整");
		} else {
			NumberUtils.format(sb, decimals[1], numberCodec::encode, 0, decimalUnits.length, decimalUnits);
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
