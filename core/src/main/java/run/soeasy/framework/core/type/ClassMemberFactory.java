package run.soeasy.framework.core.type;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

public interface ClassMemberFactory<T> {
	Provider<T> getClassMemberProvider(@NonNull Class<?> declaringClass);
}
