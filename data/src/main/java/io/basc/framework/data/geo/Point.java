package io.basc.framework.data.geo;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

import java.io.Serializable;
import java.util.Locale;

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	private final double x;
	private final double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {

		Assert.notNull(point, "Source point must not be null!");

		this.x = point.x;
		this.y = point.y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 1;

		long temp = Double.doubleToLongBits(x);
		result = 31 * result + (int) (temp ^ temp >>> 32);

		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ temp >>> 32);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Point)) {
			return false;
		}

		Point other = (Point) obj;

		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}

		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "Point [x=%f, y=%f]", x, y);
	}
}
