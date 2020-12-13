package scw.event;

import scw.core.utils.XTime;
import scw.mapper.MapperUtils;
import scw.util.KeyValuePair;

public class KeyValuePairEvent<K, V> extends KeyValuePair<K, V> implements Event{
	private static final long serialVersionUID = 1L;
	private final long createTime;
	private final EventType eventType;
	
	public KeyValuePairEvent(KeyValuePairEvent<K, V> event) {
		super(event);
		this.createTime = event.createTime;
		this.eventType = event.eventType;
	}
	
	public KeyValuePairEvent(EventType eventType, K key, V value) {
		super(key, value);
		this.createTime = System.currentTimeMillis();
		this.eventType = eventType;
	}

	public long getCreateTime() {
		return createTime;
	}

	public EventType getEventType() {
		return eventType;
	}
	
	@Override
	public String toString() {
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <" + MapperUtils.getMapper().toString(this) + ">";
	}
}
