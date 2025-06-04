package run.soeasy.framework.core.invoke;

import lombok.NonNull;

public class CustomizeExecution<W extends ExecutableElement> extends AbstractExecution<W> {

	public CustomizeExecution(@NonNull W metadata, @NonNull Object[] arguments) {
		super(metadata, arguments);
	}

	@Override
	public Object execute() throws Throwable {
		return getMetadata().execute(getArguments());
	}
}
