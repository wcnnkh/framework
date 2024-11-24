package io.basc.framework.core.type.filter;

import io.basc.framework.util.ClassUtils;

/**
 * A simple filter which matches classes that are assignable to a given type.
 *
 * @author Rod Johnson
 * @author Mark Fisher
 * @author Ramnivas Laddad
 */
public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {

	private final Class<?> targetType;

	/**
	 * Create a new AssignableTypeFilter for the given type.
	 * 
	 * @param targetType the type to match
	 */
	public AssignableTypeFilter(Class<?> targetType) {
		super(true, true);
		this.targetType = targetType;
	}

	/**
	 * Return the {@code type} that this instance is using to filter candidates.
	 * 
	 */
	public final Class<?> getTargetType() {
		return this.targetType;
	}

	@Override
	protected boolean matchClassName(String className) {
		return this.targetType.getName().equals(className);
	}

	@Override
	
	protected Boolean matchSuperClass(String superClassName) {
		return matchTargetType(superClassName);
	}

	@Override
	
	protected Boolean matchInterface(String interfaceName) {
		return matchTargetType(interfaceName);
	}

	
	protected Boolean matchTargetType(String typeName) {
		if (this.targetType.getName().equals(typeName)) {
			return true;
		} else if (Object.class.getName().equals(typeName)) {
			return false;
		} else if (typeName.startsWith("java")) {
			try {
				Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
				return this.targetType.isAssignableFrom(clazz);
			} catch (Throwable ex) {
				// Class not regularly loadable - can't determine a match that way.
			}
		}
		return null;
	}

}
