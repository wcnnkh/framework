package scw.tencent.wx.offiaccount.message.ordinary;


public class LocationOrdinaryMessage extends OrdinaryMessage{
	private static final long serialVersionUID = 1L;
	private double latitude;//地理位置纬度
	private double longitude;//	地理位置经度
	private int scale;//地图缩放大小
	private String label;//	地理位置信息
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
