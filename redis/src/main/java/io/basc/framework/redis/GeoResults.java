package io.basc.framework.redis;

import io.basc.framework.data.geo.Circle;
import io.basc.framework.data.geo.Distance;
import io.basc.framework.data.geo.Point;

public class GeoResults extends Circle {
	private static final long serialVersionUID = 1L;
	private final byte[] key;
	private final byte[] hash;

	public GeoResults(byte[] key, Distance distance, Point point, byte[] hash) {
		super(point, distance);
		this.key = key;
		this.hash = hash;
	}

	public byte[] getKey() {
		return key;
	}

	public byte[] getHash() {
		return hash;
	}
}