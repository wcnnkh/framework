package scw.event;

public class EventObject extends java.util.EventObject implements Event {
	private static final long serialVersionUID = 1L;
	private final long createTime;

	public EventObject(Object source) {
		super(source);
		this.createTime = System.currentTimeMillis();
	}

	public long getCreateTime() {
		return createTime;
	}
}
