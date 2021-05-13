package scw.search.location;

import java.io.Serializable;

/**
 * 位置信息
 * @author shuchaowen
 *
 */
public class Location implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 经度
	 */
	private float longitude;
	/**
	 * 纬度
	 */
	private float latitude;

	/**
	 * 经度
	 * 
	 * @return
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * 经度
	 * 
	 * @param longitude
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	/**
	 * 纬度
	 * 
	 * @return
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * 纬度
	 * 
	 * @param latitude
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
}
