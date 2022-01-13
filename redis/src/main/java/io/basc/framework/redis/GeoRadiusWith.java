package io.basc.framework.redis;

import java.io.Serializable;

public class GeoRadiusWith implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean withCoord;
	private boolean withDist;
	private boolean withHash;

	public GeoRadiusWith withCoord(boolean withCoord) {
		this.withCoord = withCoord;
		return this;
	}

	public GeoRadiusWith withDist(boolean withDist) {
		this.withDist = withDist;
		return this;
	}

	public GeoRadiusWith withHash(boolean withHash) {
		this.withHash = withHash;
		return this;
	}

	public final GeoRadiusWith withCoord() {
		return withCoord(true);
	}

	public final GeoRadiusWith withDist() {
		return withDist(true);
	}

	public final GeoRadiusWith withHash() {
		return withHash(true);
	}

	public boolean isWithCoord() {
		return withCoord;
	}

	public boolean isWithDist() {
		return withDist;
	}

	public boolean isWithHash() {
		return withHash;
	}
}
