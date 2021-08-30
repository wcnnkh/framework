package io.basc.framework.data.geo;

public interface Metric {
	/**
	 * Returns the multiplier to calculate metrics values from a base scale.
	 *
	 * @return
	 */
	double getMultiplier();

	/**
	 * Returns the scientific abbreviation of the unit the {@link Metric} is in.
	 *
	 * @return
	 */
	String getAbbreviation();
}
