package io.basc.framework.redis.core;

import io.basc.framework.data.geo.Metric;

public enum DistanceUnit implements Metric {

	METERS(6378137, "m"), KILOMETERS(6378.137, "km"), MILES(3963.191, "mi"), FEET(
			20925646.325, "ft");

	private final double multiplier;
	private final String abbreviation;

	/**
	 * Creates a new {@link DistanceUnit} using the given muliplier.
	 *
	 * @param multiplier
	 *            the earth radius at equator.
	 */
	private DistanceUnit(double multiplier, String abbreviation) {

		this.multiplier = multiplier;
		this.abbreviation = abbreviation;
	}

	public double getMultiplier() {
		return multiplier;
	}

	@Override
	public String getAbbreviation() {
		return abbreviation;
	}
}
