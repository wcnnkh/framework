package io.basc.framework.data.geo;

/**
 * 圆
 * 
 * @author wcnnkh
 *
 */
public class Circle implements Shape {
	private static final long serialVersionUID = 1L;
	private final Point point;
	private final Distance radius;

	public Circle(Point point, Distance radius) {
		this.point = point;
		this.radius = radius;
	}

	public Point getPoint() {
		return point;
	}

	public Distance getRadius() {
		return radius;
	}
}
