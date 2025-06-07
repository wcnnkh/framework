package run.soeasy.framework.core.execute;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class CustomizeInvocation<W extends InvodableElement> extends AbstractExecution<W> implements Invocation {
	private Object target;

	public CustomizeInvocation(@NonNull W metadata, @NonNull Object[] arguments) {
		super(metadata, arguments);
	}

	@Override
	public Object execute() throws Throwable {
		return getMetadata().invoke(getTarget(), getArguments());
	}
}