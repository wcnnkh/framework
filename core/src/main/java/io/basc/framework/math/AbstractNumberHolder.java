package io.basc.framework.math;

public abstract class AbstractNumberHolder extends Number implements NumberHolder {
	private static final long serialVersionUID = 1L;

	public int compareTo(NumberHolder o) {
		return toBigDecimal().compareTo(o.toBigDecimal());
	}

	// 将当前值变为同分母分数
	private Fraction toFractionNumberHolder(Fraction numberHolder) {
		return new Fraction(multiply(numberHolder.getDenominator()), numberHolder.getDenominator());
	}

	public final NumberHolder add(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return add((Fraction) numberHolder);
		}
		return addInternal(numberHolder);
	}

	protected abstract NumberHolder addInternal(NumberHolder numberHolder);

	public Fraction add(Fraction numberHolder) {
		return toFractionNumberHolder(numberHolder).add(numberHolder);
	}

	public final NumberHolder subtract(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return subtract((Fraction) numberHolder);
		}
		return subtractInternal(numberHolder);
	}

	protected abstract NumberHolder subtractInternal(NumberHolder numberHolder);

	public Fraction subtract(Fraction numberHolder) {
		Fraction fraction = toFractionNumberHolder(numberHolder);
		return fraction.subtract(numberHolder);
	}

	public final NumberHolder multiply(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return multiply((Fraction) numberHolder);
		}
		return multiplyInternal(numberHolder);
	}

	protected abstract NumberHolder multiplyInternal(NumberHolder numberHolder);

	public Fraction multiply(Fraction numberHolder) {
		return toFractionNumberHolder(numberHolder).multiply(numberHolder);
	}

	public final NumberHolder divide(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return divide((Fraction) numberHolder);
		}
		return divideInternal(numberHolder);
	}

	protected abstract NumberHolder divideInternal(NumberHolder numberHolder);

	public Fraction divide(Fraction numberHolder) {
		return toFractionNumberHolder(numberHolder).divide(numberHolder);
	}

	public final NumberHolder remainder(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return remainder((Fraction) numberHolder);
		}
		return remainderInternal(numberHolder);
	}

	protected abstract NumberHolder remainderInternal(NumberHolder numberHolder);

	public NumberHolder remainder(Fraction numberHolder) {
		return toFractionNumberHolder(numberHolder).remainder(numberHolder);
	}

	public final NumberHolder pow(NumberHolder numberHolder) {
		if (numberHolder instanceof Fraction) {
			return pow((Fraction) numberHolder);
		}
		return powInternal(numberHolder);
	}

	protected abstract NumberHolder powInternal(NumberHolder numberHolder);

	public NumberHolder pow(Fraction numberHolder) {
		return toFractionNumberHolder(numberHolder).pow(numberHolder);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(obj instanceof NumberHolder){
			return toBigDecimal().equals(((NumberHolder) obj).toBigDecimal());
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return toBigDecimal().toString();
	}
	
	@Override
	public int intValue() {
		return toBigDecimal().intValue();
	}
	
	@Override
	public double doubleValue() {
		return toBigDecimal().doubleValue();
	}
	
	@Override
	public float floatValue() {
		return toBigDecimal().floatValue();
	}
	
	@Override
	public long longValue() {
		return toBigDecimal().longValue();
	}
}
