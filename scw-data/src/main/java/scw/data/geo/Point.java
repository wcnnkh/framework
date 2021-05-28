package scw.data.geo;

import java.io.Serializable;
import java.util.Locale;

import scw.core.Assert;
import scw.lang.Nullable;

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	private final double x;
	private final double y;

	/**
	 * Creates a {@link Point} from the given {@code x}, {@code y} coordinate.
	 *
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a {@link Point} from the given {@link Point} coordinate.
	 *
	 * @param point must not be {@literal null}.
	 */
	public Point(Point point) {

		Assert.notNull(point, "Source point must not be null!");

		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Returns the x-coordinate of the {@link Point}.
	 *
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of the {@link Point}.
	 *
	 * @return
	 */
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
