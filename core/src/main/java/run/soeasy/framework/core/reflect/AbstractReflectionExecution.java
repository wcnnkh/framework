package run.soeasy.framework.core.reflect;

import java.lang.reflect.Executable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.exe.Execution;
import run.soeasy.framework.core.param.Parameters;

@Getter
@Setter
public abstract class AbstractReflectionExecution<T extends Executable> extends ReflectionExecutable<T>
		implements Execution {
	@NonNull
	private Parameters defaultParameters = Parameters.EMPTY_PARAMETERS;

	public AbstractReflectionExecution(@NonNull T member) {
		super(member);
	}
}
