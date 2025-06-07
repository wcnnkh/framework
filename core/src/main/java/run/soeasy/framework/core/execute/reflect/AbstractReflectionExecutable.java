package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Member;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.execute.ExecutableMetadata;

@Data
public abstract class AbstractReflectionExecutable<T extends Member> implements ExecutableMetadata, Wrapper<T> {
	@NonNull
	protected transient T source;

	public AbstractReflectionExecutable(T source) {
		setSource(source);
	}

	@Override
	public String getName() {
		return getSource().getName();
	}

	public Class<?> getDeclaringClass() {
		return getSource().getDeclaringClass();
	}

	@Override
	public TypeDescriptor getDeclaringTypeDescriptor() {
		return TypeDescriptor.valueOf(getDeclaringClass());
	}

	public void setSource(@NonNull T source) {
		this.source = source;
	}
}
