package scw.tencent.wx.offiaccount.message.event;

public class MenuEventMessage extends EventMessage{
	private static final long serialVersionUID = 1L;
	private String eventKey;//事件KEY值，设置的跳转URL
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
}
