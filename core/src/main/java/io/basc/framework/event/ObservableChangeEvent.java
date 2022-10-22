package io.basc.framework.event;

import java.util.function.Function;

public class ObservableChangeEvent<T> extends ChangeEvent<T> {
	private static final long serialVersionUID = 1L;
	private final T oldSource;

	public ObservableChangeEvent(EventType eventType, T oldSource, T source) {
		super(eventType, source);
		this.oldSource = oldSource;
	}

	public ObservableChangeEvent(ChangeEvent<?> sourceEvent, T oldSource, T source) {
		super(sourceEvent, source);
		this.oldSource = oldSource;
	}

	public <S> ObservableChangeEvent(ObservableChangeEvent<S> source, Function<? super S, ? extends T> mapper) {
		super(source, source.getSource() == null ? null : mapper.apply(source.getSource()));
		this.oldSource = source.getOldSource() == null ? null : mapper.apply(source.getOldSource());
	}

	public T getOldSource() {
		return oldSource;
	}
}
