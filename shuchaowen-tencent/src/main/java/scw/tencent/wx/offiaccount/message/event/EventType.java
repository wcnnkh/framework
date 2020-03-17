package scw.tencent.wx.offiaccount.message.event;

public enum EventType {
	SUBSCRIBE("订阅"),
	UNSUBSCRIBE("取消订阅"),
	SCAN("扫码"),
	LOCATION("地址位置"),
	CLICK("菜单点击事件"),
	;
	
	private final String describe;
	
	EventType(String describe){
		this.describe = describe;
	}

	public String getDescribe() {
		return describe;
	}
}
