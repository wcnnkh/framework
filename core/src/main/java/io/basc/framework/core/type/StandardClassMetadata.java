package io.basc.framework.core.type;

import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;

/**
 * {@link ClassMetadata} implementation that uses standard reflection
 * to introspect a given {@code Class}.
 */
public class StandardClassMetadata implements ClassMetadata {

	private final Class<?> introspectedClass;


	/**
	 * Create a new StandardClassMetadata wrapper for the given Class.
	 * @param introspectedClass the Class to introspect
	 */
	public StandardClassMetadata(Class<?> introspectedClass) {
		Assert.notNull(introspectedClass, "Class must not be null");
		this.introspectedClass = introspectedClass;
	}

	/**
	 * Return the underlying Class.
	 */
	public final Class<?> getIntrospectedClass() {
		return this.introspectedClass;
	}


	public String getClassName() {
		return this.introspectedClass.getName();
	}

	public boolean isInterface() {
		return this.introspectedClass.isInterface();
	}

	public boolean isAnnotation() {
		return this.introspectedClass.isAnnotation();
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(this.introspectedClass.getModifiers());
	}

	public boolean isConcrete() {
		return !(isInterface() || isAbstract());
	}

	public boolean isFinal() {
		return Modifier.isFinal(this.introspectedClass.getModifiers());
	}

	public boolean isIndependent() {
		return (!hasEnclosingClass() ||
				(this.introspectedClass.getDeclaringClass() != null &&
						Modifier.isStatic(this.introspectedClass.getModifiers())));
	}

	public boolean hasEnclosingClass() {
		return (this.introspectedClass.getEnclosingClass() != null);
	}

	public String getEnclosingClassName() {
		Class<?> enclosingClass = this.introspectedClass.getEnclosingClass();
		return (enclosingClass != null ? enclosingClass.getName() : null);
	}

	public boolean hasSuperClass() {
		return (this.introspectedClass.getSuperclass() != null);
	}

	public String getSuperClassName() {
		Class<?> superClass = this.introspectedClass.getSuperclass();
		return (superClass != null ? superClass.getName() : null);
	}

	public String[] getInterfaceNames() {
		Class<?>[] ifcs = this.introspectedClass.getInterfaces();
		String[] ifcNames = new String[ifcs.length];
		for (int i = 0; i < ifcs.length; i++) {
			ifcNames[i] = ifcs[i].getName();
		}
		return ifcNames;
	}

	public String[] getMemberClassNames() {
		LinkedHashSet<String> memberClassNames = new LinkedHashSet<String>(4);
		for (Class<?> nestedClass : this.introspectedClass.getDeclaredClasses()) {
			memberClassNames.add(nestedClass.getName());
		}
		return StringUtils.toStringArray(memberClassNames);
	}

}
