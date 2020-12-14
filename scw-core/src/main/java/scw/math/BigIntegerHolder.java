package scw.math;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerHolder extends AbstractNumberHolder {
	private static final long serialVersionUID = 1L;

	public static final BigIntegerHolder ZERO = new BigIntegerHolder(BigInteger.ZERO);
	
	public static final BigIntegerHolder ONE = new BigIntegerHolder(BigInteger.ONE);
	
	private BigInteger bigInteger;

	public BigIntegerHolder(BigInteger bigInteger) {
		this.bigInteger = bigInteger;
	}

	public int compareTo(NumberHolder o) {
		if (o instanceof BigIntegerHolder) {
			return bigInteger.compareTo(((BigIntegerHolder) o).bigInteger);
		}
		return super.compareTo(o);
	}

	protected NumberHolder addInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.add(numberHolder.toBigDecimal().toBigIntegerExact()));
	}

	protected NumberHolder subtractInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.subtract(numberHolder.toBigDecimal().toBigIntegerExact()));
	}

	protected NumberHolder multiplyInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.multiply(numberHolder.toBigDecimal().toBigIntegerExact()));
	}

	protected NumberHolder divideInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.divide(numberHolder.toBigDecimal().toBigIntegerExact()));
	}

	protected NumberHolder remainderInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.remainder(numberHolder.toBigDecimal().toBigIntegerExact()));
	}

	protected NumberHolder powInternal(NumberHolder numberHolder) {
		return new BigIntegerHolder(bigInteger.pow(numberHolder.toBigDecimal().intValueExact()));
	}

	public NumberHolder abs() {
		return new BigIntegerHolder(bigInteger.abs());
	}

	public BigDecimal toBigDecimal() {
		return new BigDecimal(bigInteger);
	}

	@Override
	public int hashCode() {
		return bigInteger.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(obj instanceof BigIntegerHolder){
			return bigInteger.equals(((BigIntegerHolder) obj).bigInteger);
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return bigInteger.toString();
	}
}
