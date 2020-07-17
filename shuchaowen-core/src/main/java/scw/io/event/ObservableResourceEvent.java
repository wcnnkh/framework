package scw.io.event;

public class ObservableResourceEvent<T> extends ResourceEvent {
	private final T source;

	public ObservableResourceEvent(ResourceEvent resourceEvent, T source) {
		super(resourceEvent);
		this.source = source;
	}

	public T getSource() {
		return source;
	}

	@Override
	public String toString() {
		return super.toString() + ", source=[" + source + "]";
	}
}
