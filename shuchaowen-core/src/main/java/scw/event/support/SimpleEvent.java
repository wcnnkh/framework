package scw.event.support;

import scw.event.Event;

public class SimpleEvent implements Event{
	private final long createTime;
	
	public SimpleEvent(){
		this.createTime = System.currentTimeMillis();
	}
	
	public final long getCreateTime() {
		return createTime;
	}
	
}
