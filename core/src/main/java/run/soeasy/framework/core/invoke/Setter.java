package run.soeasy.framework.core.invoke;

import run.soeasy.framework.core.convert.TargetDescriptor;

public interface Setter<T> extends TargetDescriptor {
	void set(T target, Object value);
}
