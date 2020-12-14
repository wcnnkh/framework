package scw.event;

public interface Event {
	EventType getEventType();
	
	long getCreateTime();
}