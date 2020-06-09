package scw.math;

public abstract class AbstractNumberHolder implements NumberHolder {

	public int compareTo(NumberHolder o) {
		return toBigDecimal().compareTo(o.toBigDecimal());
	}
}
