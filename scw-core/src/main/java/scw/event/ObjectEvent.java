package scw.event;

import java.util.EventObject;

import scw.core.utils.XTime;
import scw.mapper.MapperUtils;

public class ObjectEvent<T> extends EventObject implements Event {
	private static final long serialVersionUID = 1L;
	private final long createTime;

	public ObjectEvent(ObjectEvent<T> event) {
		this(event.getSource(), event.createTime);
	}

	public ObjectEvent(T source) {
		this(source, System.currentTimeMillis());
	}

	public ObjectEvent(T source, long createTime) {
		super(source);
		this.createTime = createTime;
	}

	public long getCreateTime() {
		return createTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSource() {
		return (T) super.getSource();
	}

	@Override
	public String toString() {
		return XTime.format(createTime, "yyyy-MM-dd HH:mm:ss") + " <"
				+ MapperUtils.getMapper().toString(this) + ">";
	}
}
