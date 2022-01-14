package io.basc.framework.redis;

public class GeoradiusStroage {
	private final String option;
	private final byte[] key;

	public GeoradiusStroage(String option, byte[] key) {
		this.option = option;
		this.key = key;
	}

	public String getOption() {
		return option;
	}

	public byte[] getKey() {
		return key;
	}
}