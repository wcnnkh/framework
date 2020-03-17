package scw.tencent.wx.offiaccount;

public class LocationMessage extends Message{
	private static final long serialVersionUID = 1L;
	private String locationX;//地理位置维度
	private String locationY;//地理位置经度
	private String scale;//地图缩放大小
	private String label;//	地理位置信息
	public String getLocationX() {
		return locationX;
	}
	public void setLocationX(String locationX) {
		this.locationX = locationX;
	}
	public String getLocationY() {
		return locationY;
	}
	public void setLocationY(String locationY) {
		this.locationY = locationY;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
