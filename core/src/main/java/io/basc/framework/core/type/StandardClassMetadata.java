package io.basc.framework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

/**
 * {@link ClassMetadata} implementation that uses standard reflection to
 * introspect a given {@code Class}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/StandardClassMetadata.java
 */
public class StandardClassMetadata implements ClassMetadata {

	private final Class<?> introspectedClass;

	/**
	 * Create a new StandardClassMetadata wrapper for the given Class.
	 * 
	 * @param introspectedClass the Class to introspect
	 */
	public StandardClassMetadata(Class<?> introspectedClass) {
		Assert.notNull(introspectedClass, "Class must not be null");
		this.introspectedClass = introspectedClass;
	}

	public final Class<?> getIntrospectedClass() {
		return this.introspectedClass;
	}

	@Override
	public String getClassName() {
		return this.introspectedClass.getName();
	}

	@Override
	public boolean isInterface() {
		return this.introspectedClass.isInterface();
	}

	@Override
	public boolean isAnnotation() {
		return this.introspectedClass.isAnnotation();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(this.introspectedClass.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(this.introspectedClass.getModifiers());
	}

	@Override
	public boolean isIndependent() {
		return (!hasEnclosingClass() || (this.introspectedClass.getDeclaringClass() != null
				&& Modifier.isStatic(this.introspectedClass.getModifiers())));
	}

	@Override
	@Nullable
	public String getEnclosingClassName() {
		Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
		return (enclosingClass != null ? enclosingClass.getName() : null);
	}

	@Override
	@Nullable
	public String getSuperClassName() {
		Class<?> superClass = this.introspectedClass.getSuperclass();
		return (superClass != null ? superClass.getName() : null);
	}

	@Override
	public String[] getInterfaceNames() {
		Class<?>[] ifcs = this.introspectedClass.getInterfaces();
		String[] ifcNames = new String[ifcs.length];
		for (int i = 0; i < ifcs.length; i++) {
			ifcNames[i] = ifcs[i].getName();
		}
		return ifcNames;
	}

	@Override
	public String[] getMemberClassNames() {
		LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
		for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
			memberClassNames.add(nestedClass.getName());
		}
		return StringUtils.toStringArray(memberClassNames);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return ((this == obj) || ((obj instanceof StandardClassMetadata)
				&& getIntrospectedClass().equals(((StandardClassMetadata) obj).getIntrospectedClass())));
	}

	@Override
	public int hashCode() {
		return getIntrospectedClass().hashCode();
	}

	@Override
	public String toString() {
		return getClassName();
	}

	@Override
	public boolean isEnum() {
		return this.introspectedClass.isEnum();
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(this.introspectedClass.getModifiers());
	}

}
