package scw.data.geo;

public enum Metrics implements Metric {

	KILOMETERS(6378.137, "km"), MILES(3963.191, "mi"), NEUTRAL(1, "");

	private final double multiplier;
	private final String abbreviation;

	/**
	 * Creates a new {@link Metrics} using the given multiplier.
	 *
	 * @param multiplier   the earth radius at equator, must not be {@literal null}.
	 * @param abbreviation the abbreviation to use for this {@link Metric}, must not
	 *                     be {@literal null}.
	 */
	private Metrics(double multiplier, String abbreviation) {
		this.multiplier = multiplier;
		this.abbreviation = abbreviation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.mongodb.core.geo.Metric#getMultiplier()
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.geo.Metric#getAbbreviation()
	 */
	@Override
	public String getAbbreviation() {
		return abbreviation;
	}
}