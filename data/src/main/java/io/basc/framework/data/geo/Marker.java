package io.basc.framework.data.geo;

public class Marker<K> extends Point {
	private static final long serialVersionUID = 1L;
	/**
	 * 标记的名称
	 */
	private final K name;

	public Marker(K name, Point point) {
		super(point);
		this.name = name;
	}

	public Marker(K name, double x, double y) {
		super(x, y);
		this.name = name;
	}

	public K getName() {
		return name;
	}
}
