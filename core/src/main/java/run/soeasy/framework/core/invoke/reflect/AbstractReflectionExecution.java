package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.Executable;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.invoke.Execution;

@Getter
@Setter
public abstract class AbstractReflectionExecution<T extends Executable> extends ReflectionExecutable<T>
		implements Execution {

	public AbstractReflectionExecution(@NonNull T member) {
		super(member);
	}
}
