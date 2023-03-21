package io.basc.framework.event;

import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ObservableChangeEvent<T> extends ChangeEvent<T> {
	private static final long serialVersionUID = 1L;
	private final T oldSource;

	public ObservableChangeEvent(ChangeType changeType, T oldSource, T source) {
		super(changeType, source);
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
}
