package run.soeasy.framework.core.execute;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class AbstractExecution<W extends ExecutableMetadata> implements Execution {
	private final W metadata;
	private final Object[] arguments;

	public AbstractExecution(@NonNull W metadata, @NonNull Object... arguments) {
		this.metadata = metadata;
		this.arguments = arguments;
	}
}