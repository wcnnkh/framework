package run.soeasy.framework.core.invoke;

import run.soeasy.framework.core.convert.SourceDescriptor;

public interface Getter<T> extends SourceDescriptor {
	Object get(T target);
}
