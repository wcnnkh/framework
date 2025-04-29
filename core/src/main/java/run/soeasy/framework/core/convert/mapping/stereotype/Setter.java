package run.soeasy.framework.core.convert.mapping.stereotype;

import run.soeasy.framework.core.convert.TargetDescriptor;

public interface Setter<T> extends TargetDescriptor {
	void set(T target, Object value);
}
