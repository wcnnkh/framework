package io.basc.framework.core.type.classreading.share;

import java.lang.reflect.Modifier;

import io.basc.framework.core.type.ClassMetadata;
import lombok.Data;
import lombok.NonNull;

@Data
public class SharableClassMetadata implements ClassMetadata {
	@NonNull
	private final Class<?> sourceClass;

	@Override
	public String getClassName() {
		return sourceClass.getName();
	}

	@Override
	public boolean isInterface() {
		return sourceClass.isInterface();
	}

	@Override
	public boolean isAnnotation() {
		return sourceClass.isAnnotation();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(sourceClass.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(sourceClass.getModifiers());
	}

	@Override
	public boolean isIndependent() {
		return Modifier.isStatic(sourceClass.getModifiers()) && getEnclosingClassName() == null;
	}

	@Override
	public String getEnclosingClassName() {
		Class<?> enclosingClass = sourceClass.getEnclosingClass();
		return enclosingClass == null ? null : enclosingClass.getName();
	}

	@Override
	public String getSuperClassName() {
		Class<?> superClass = sourceClass.getSuperclass();
		return superClass == null ? null : superClass.getName();
	}

	@Override
	public String[] getInterfaceNames() {
		Class<?>[] interfaces = sourceClass.getInterfaces();
		if (interfaces == null) {
			return new String[0];
		}

		String[] names = new String[interfaces.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = interfaces[i].getName();
		}
		return names;
	}

	@Override
	public String[] getMemberClassNames() {
		return new String[0];
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(sourceClass.getModifiers());
	}

	@Override
	public boolean isEnum() {
		return sourceClass.isEnum();
	}

}
