package run.soeasy.framework.core.invoke.reflect;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ResolvableType;

@RequiredArgsConstructor
@ToString(of = "executable")
@EqualsAndHashCode(of = "executable")
public class ExecutableExceptionTypeDescriptors implements Provider<TypeDescriptor> {
	@NonNull
	@Getter
	private final Executable executable;
	private volatile TypeDescriptor[] typeDescriptors;

	@Override
	public Iterator<TypeDescriptor> iterator() {
		reload(false);
		return Arrays.asList(typeDescriptors).iterator();
	}

	@Override
	public void reload() {
		reload(true);
	}

	public boolean reload(boolean force) {
		if (force || typeDescriptors == null) {
			synchronized (this) {
				if (force || typeDescriptors == null) {
					AnnotatedType[] annotatedExceptionTypes = executable.getAnnotatedExceptionTypes();
					Class<?>[] exceptionTypes = executable.getExceptionTypes();
					Type[] genericExceptionTypes = executable.getGenericExceptionTypes();
					TypeDescriptor[] typeDescriptors = new TypeDescriptor[exceptionTypes.length];
					for (int i = 0; i < typeDescriptors.length; i++) {
						typeDescriptors[i] = new TypeDescriptor(ResolvableType.forType(genericExceptionTypes[i]),
								exceptionTypes[i], annotatedExceptionTypes[i]);
					}
					this.typeDescriptors = typeDescriptors;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public final boolean isEmpty() {
		return count() == 0;
	}

	@Override
	public long count() {
		reload(false);
		return typeDescriptors.length;
	}

}
