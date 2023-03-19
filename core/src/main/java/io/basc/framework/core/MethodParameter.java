package io.basc.framework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ObjectUtils;

public class MethodParameter {

	private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];

	private static final Class<?> javaUtilOptionalClass;

	static {
		Class<?> clazz;
		try {
			clazz = ClassUtils.forName("java.util.Optional", MethodParameter.class.getClassLoader());
		} catch (ClassNotFoundException ex) {
			// Java 8 not available - Optional references simply not supported then.
			clazz = null;
		}
		javaUtilOptionalClass = clazz;
	}

	private final Method method;

	private final Constructor<?> constructor;

	private final int parameterIndex;

	private int nestingLevel;

	/** Map from Integer level to Integer type index */
	Map<Integer, Integer> typeIndexesPerLevel;

	/**
	 * The containing class. Could also be supplied by overriding
	 * {@link #getContainingClass()}
	 */
	private volatile Class<?> containingClass;

	private volatile Class<?> parameterType;

	private volatile Type genericParameterType;

	private volatile Annotation[] parameterAnnotations;

	private volatile ParameterNameDiscoverer parameterNameDiscoverer;

	private volatile String parameterName;

	private volatile MethodParameter nestedMethodParameter;

	/**
	 * Create a new {@code MethodParameter} for the given method, with nesting level
	 * 1.
	 * 
	 * @param method         the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method return
	 *                       type; 0 for the first method parameter; 1 for the
	 *                       second method parameter, etc.
	 */
	public MethodParameter(Method method, int parameterIndex) {
		this(method, parameterIndex, 1);
	}

	/**
	 * Create a new {@code MethodParameter} for the given method.
	 * 
	 * @param method         the Method to specify a parameter for
	 * @param parameterIndex the index of the parameter: -1 for the method return
	 *                       type; 0 for the first method parameter; 1 for the
	 *                       second method parameter, etc.
	 * @param nestingLevel   the nesting level of the target type (typically 1; e.g.
	 *                       in case of a List of Lists, 1 would indicate the nested
	 *                       List, whereas 2 would indicate the element of the
	 *                       nested List)
	 */
	public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
		Assert.notNull(method, "Method must not be null");
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.constructor = null;
	}

	/**
	 * Create a new MethodParameter for the given constructor, with nesting level 1.
	 * 
	 * @param constructor    the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex) {
		this(constructor, parameterIndex, 1);
	}

	/**
	 * Create a new MethodParameter for the given constructor.
	 * 
	 * @param constructor    the Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @param nestingLevel   the nesting level of the target type (typically 1; e.g.
	 *                       in case of a List of Lists, 1 would indicate the nested
	 *                       List, whereas 2 would indicate the element of the
	 *                       nested List)
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
		Assert.notNull(constructor, "Constructor must not be null");
		this.constructor = constructor;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.method = null;
	}

	/**
	 * Copy constructor, resulting in an independent MethodParameter object based on
	 * the same metadata and cache state that the original object was in.
	 * 
	 * @param original the original MethodParameter object to copy from
	 */
	public MethodParameter(MethodParameter original) {
		Assert.notNull(original, "Original must not be null");
		this.method = original.method;
		this.constructor = original.constructor;
		this.parameterIndex = original.parameterIndex;
		this.nestingLevel = original.nestingLevel;
		this.typeIndexesPerLevel = original.typeIndexesPerLevel;
		this.containingClass = original.containingClass;
		this.parameterType = original.parameterType;
		this.genericParameterType = original.genericParameterType;
		this.parameterAnnotations = original.parameterAnnotations;
		this.parameterNameDiscoverer = original.parameterNameDiscoverer;
		this.parameterName = original.parameterName;
	}

	/**
	 * Return the wrapped Method, if any.
	 * <p>
	 * Note: Either Method or Constructor is available.
	 * 
	 * @return the Method, or {@code null} if none
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Return the wrapped Constructor, if any.
	 * <p>
	 * Note: Either Method or Constructor is available.
	 * 
	 * @return the Constructor, or {@code null} if none
	 */
	public Constructor<?> getConstructor() {
		return this.constructor;
	}

	public Class<?> getDeclaringClass() {
		return getMember().getDeclaringClass();
	}

	/**
	 * Return the wrapped member.
	 * 
	 * @return the Method or Constructor as Member
	 */
	public Member getMember() {
		// NOTE: no ternary expression to retain JDK <8 compatibility even when using
		// the JDK 8 compiler (potentially selecting java.lang.reflect.Executable
		// as common type, with that new base class not available on older JDKs)
		if (this.method != null) {
			return this.method;
		} else {
			return this.constructor;
		}
	}

	/**
	 * Return the wrapped annotated element.
	 * <p>
	 * Note: This method exposes the annotations declared on the method/constructor
	 * itself (i.e. at the method/constructor level, not at the parameter level).
	 * 
	 * @return the Method or Constructor as AnnotatedElement
	 */
	public AnnotatedElement getAnnotatedElement() {
		// NOTE: no ternary expression to retain JDK <8 compatibility even when using
		// the JDK 8 compiler (potentially selecting java.lang.reflect.Executable
		// as common type, with that new base class not available on older JDKs)
		if (this.method != null) {
			return this.method;
		} else {
			return this.constructor;
		}
	}

	/**
	 * Return the index of the method/constructor parameter.
	 * 
	 * @return the parameter index (-1 in case of the return type)
	 */
	public int getParameterIndex() {
		return this.parameterIndex;
	}

	/**
	 * Increase this parameter's nesting level.
	 * 
	 * @see #getNestingLevel()
	 */
	public void increaseNestingLevel() {
		this.nestingLevel++;
	}

	public void decreaseNestingLevel() {
		getTypeIndexesPerLevel().remove(this.nestingLevel);
		this.nestingLevel--;
	}

	public int getNestingLevel() {
		return this.nestingLevel;
	}

	public void setTypeIndexForCurrentLevel(int typeIndex) {
		getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
	}

	public Integer getTypeIndexForCurrentLevel() {
		return getTypeIndexForLevel(this.nestingLevel);
	}

	public Integer getTypeIndexForLevel(int nestingLevel) {
		return getTypeIndexesPerLevel().get(nestingLevel);
	}

	private Map<Integer, Integer> getTypeIndexesPerLevel() {
		if (this.typeIndexesPerLevel == null) {
			this.typeIndexesPerLevel = new HashMap<Integer, Integer>(4);
		}
		return this.typeIndexesPerLevel;
	}

	public MethodParameter nested() {
		if (this.nestedMethodParameter != null) {
			return this.nestedMethodParameter;
		}
		MethodParameter nestedParam = clone();
		nestedParam.nestingLevel = this.nestingLevel + 1;
		this.nestedMethodParameter = nestedParam;
		return nestedParam;
	}

	public boolean isOptional() {
		return (getParameterType() == javaUtilOptionalClass);
	}

	public MethodParameter nestedIfOptional() {
		return (isOptional() ? nested() : this);
	}

	void setContainingClass(Class<?> containingClass) {
		this.containingClass = containingClass;
	}

	/**
	 * Return the containing class for this method parameter.
	 * 
	 * @return a specific containing class (potentially a subclass of the declaring
	 *         class), or otherwise simply the declaring class itself
	 * @see #getDeclaringClass()
	 */
	public Class<?> getContainingClass() {
		return (this.containingClass != null ? this.containingClass : getDeclaringClass());
	}

	void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * Return the type of the method/constructor parameter.
	 * 
	 * @return the parameter type (never {@code null})
	 */
	public Class<?> getParameterType() {
		Class<?> paramType = this.parameterType;
		if (paramType == null) {
			if (this.parameterIndex < 0) {
				Method method = getMethod();
				paramType = (method != null ? method.getReturnType() : void.class);
			} else {
				paramType = (this.method != null ? this.method.getParameterTypes()[this.parameterIndex]
						: this.constructor.getParameterTypes()[this.parameterIndex]);
			}
			this.parameterType = paramType;
		}
		return paramType;
	}

	/**
	 * Return the generic type of the method/constructor parameter.
	 * 
	 * @return the parameter type (never {@code null})
	 */
	public Type getGenericParameterType() {
		Type paramType = this.genericParameterType;
		if (paramType == null) {
			if (this.parameterIndex < 0) {
				Method method = getMethod();
				paramType = (method != null ? method.getGenericReturnType() : void.class);
			} else {
				Type[] genericParameterTypes = (this.method != null ? this.method.getGenericParameterTypes()
						: this.constructor.getGenericParameterTypes());
				int index = this.parameterIndex;
				if (this.constructor != null && this.constructor.getDeclaringClass().isMemberClass()
						&& !Modifier.isStatic(this.constructor.getDeclaringClass().getModifiers())
						&& genericParameterTypes.length == this.constructor.getParameterTypes().length - 1) {
					// Bug in javac: type array excludes enclosing instance parameter
					// for inner classes with at least one generic constructor parameter,
					// so access it with the actual parameter index lowered by 1
					index = this.parameterIndex - 1;
				}
				paramType = (index >= 0 && index < genericParameterTypes.length ? genericParameterTypes[index]
						: getParameterType());
			}
			this.genericParameterType = paramType;
		}
		return paramType;
	}

	/**
	 * Return the nested type of the method/constructor parameter.
	 * 
	 * @return the parameter type (never {@code null})
	 * @see #getNestingLevel()
	 */
	public Class<?> getNestedParameterType() {
		if (this.nestingLevel > 1) {
			Type type = getGenericParameterType();
			for (int i = 2; i <= this.nestingLevel; i++) {
				if (type instanceof ParameterizedType) {
					Type[] args = ((ParameterizedType) type).getActualTypeArguments();
					Integer index = getTypeIndexForLevel(i);
					type = args[index != null ? index : args.length - 1];
				}
				// TODO: Object.class if unresolvable
			}
			if (type instanceof Class) {
				return (Class<?>) type;
			} else if (type instanceof ParameterizedType) {
				Type arg = ((ParameterizedType) type).getRawType();
				if (arg instanceof Class) {
					return (Class<?>) arg;
				}
			}
			return Object.class;
		} else {
			return getParameterType();
		}
	}

	/**
	 * Return the nested generic type of the method/constructor parameter.
	 * 
	 * @return the parameter type (never {@code null})
	 * @see #getNestingLevel()
	 */
	public Type getNestedGenericParameterType() {
		if (this.nestingLevel > 1) {
			Type type = getGenericParameterType();
			for (int i = 2; i <= this.nestingLevel; i++) {
				if (type instanceof ParameterizedType) {
					Type[] args = ((ParameterizedType) type).getActualTypeArguments();
					Integer index = getTypeIndexForLevel(i);
					type = args[index != null ? index : args.length - 1];
				}
			}
			return type;
		} else {
			return getGenericParameterType();
		}
	}

	/**
	 * Return the annotations associated with the target method/constructor itself.
	 */
	public Annotation[] getMethodAnnotations() {
		return adaptAnnotationArray(getAnnotatedElement().getAnnotations());
	}

	public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
		return adaptAnnotation(getAnnotatedElement().getAnnotation(annotationType));
	}

	public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
		return getAnnotatedElement().isAnnotationPresent(annotationType);
	}

	public Annotation[] getParameterAnnotations() {
		Annotation[] paramAnns = this.parameterAnnotations;
		if (paramAnns == null) {
			Annotation[][] annotationArray = (this.method != null ? this.method.getParameterAnnotations()
					: this.constructor.getParameterAnnotations());
			int index = this.parameterIndex;
			if (this.constructor != null && this.constructor.getDeclaringClass().isMemberClass()
					&& !Modifier.isStatic(this.constructor.getDeclaringClass().getModifiers())
					&& annotationArray.length == this.constructor.getParameterTypes().length - 1) {
				// Bug in javac in JDK <9: annotation array excludes enclosing instance
				// parameter
				// for inner classes, so access it with the actual parameter index lowered by 1
				index = this.parameterIndex - 1;
			}
			paramAnns = (index >= 0 && index < annotationArray.length ? adaptAnnotationArray(annotationArray[index])
					: EMPTY_ANNOTATION_ARRAY);
			this.parameterAnnotations = paramAnns;
		}
		return paramAnns;
	}

	public boolean hasParameterAnnotations() {
		return (getParameterAnnotations().length != 0);
	}

	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getParameterAnnotation(Class<A> annotationType) {
		Annotation[] anns = getParameterAnnotations();
		for (Annotation ann : anns) {
			if (annotationType.isInstance(ann)) {
				return (A) ann;
			}
		}
		return null;
	}

	public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
		return (getParameterAnnotation(annotationType) != null);
	}

	public void initParameterNameDiscovery(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	/**
	 * Return the name of the method/constructor parameter.
	 * 
	 * @return the parameter name (may be {@code null} if no parameter name metadata
	 *         is contained in the class file or no
	 *         {@link #initParameterNameDiscovery ParameterNameDiscoverer} has been
	 *         set to begin with)
	 */
	public String getParameterName() {
		ParameterNameDiscoverer discoverer = this.parameterNameDiscoverer;
		if (discoverer != null) {
			String[] parameterNames = (this.method != null ? discoverer.getParameterNames(this.method)
					: discoverer.getParameterNames(this.constructor));
			if (parameterNames != null) {
				this.parameterName = parameterNames[this.parameterIndex];
			}
			this.parameterNameDiscoverer = null;
		}
		return this.parameterName;
	}

	protected <A extends Annotation> A adaptAnnotation(A annotation) {
		return annotation;
	}

	/**
	 * A template method to post-process a given annotation array before returning
	 * it to the caller.
	 * <p>
	 * The default implementation simply returns the given annotation array as-is.
	 * 
	 * @param annotations the annotation array about to be returned
	 * @return the post-processed annotation array (or simply the original one)
	 */
	protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
		return annotations;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodParameter)) {
			return false;
		}
		MethodParameter otherParam = (MethodParameter) other;
		return (getContainingClass() == otherParam.getContainingClass()
				&& ObjectUtils.equals(this.typeIndexesPerLevel, otherParam.typeIndexesPerLevel)
				&& this.nestingLevel == otherParam.nestingLevel && this.parameterIndex == otherParam.parameterIndex
				&& getMember().equals(otherParam.getMember()));
	}

	@Override
	public int hashCode() {
		return (getMember().hashCode() * 31 + this.parameterIndex);
	}

	@Override
	public String toString() {
		return (this.method != null ? "method '" + this.method.getName() + "'" : "constructor") + " parameter "
				+ this.parameterIndex;
	}

	@Override
	public MethodParameter clone() {
		return new MethodParameter(this);
	}

	/**
	 * Create a new MethodParameter for the given method or constructor.
	 * <p>
	 * This is a convenience factory method for scenarios where a Method or
	 * Constructor reference is treated in a generic fashion.
	 * 
	 * @param executable     the Method or Constructor to specify a parameter for
	 * @param parameterIndex the index of the parameter
	 * @return the corresponding MethodParameter instance
	 */
	public static MethodParameter forExecutable(Executable executable, int parameterIndex) {
		if (executable instanceof Method) {
			return new MethodParameter((Method) executable, parameterIndex);
		} else if (executable instanceof Constructor) {
			return new MethodParameter((Constructor<?>) executable, parameterIndex);
		} else {
			throw new IllegalArgumentException("Not a Method/Constructor: " + executable);
		}
	}

	/**
	 * Create a new MethodParameter for the given parameter descriptor.
	 * <p>
	 * This is a convenience factory method for scenarios where a Java 8
	 * {@link Parameter} descriptor is already available.
	 * 
	 * @param parameter the parameter descriptor
	 * @return the corresponding MethodParameter instance
	 */
	public static MethodParameter forParameter(Parameter parameter) {
		return forExecutable(parameter.getDeclaringExecutable(), findParameterIndex(parameter));
	}

	protected static int findParameterIndex(Parameter parameter) {
		Executable executable = parameter.getDeclaringExecutable();
		Parameter[] allParams = executable.getParameters();
		// Try first with identity checks for greater performance.
		for (int i = 0; i < allParams.length; i++) {
			if (parameter == allParams[i]) {
				return i;
			}
		}
		// Potentially try again with object equality checks in order to avoid race
		// conditions while invoking java.lang.reflect.Executable.getParameters().
		for (int i = 0; i < allParams.length; i++) {
			if (parameter.equals(allParams[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException(
				"Given parameter [" + parameter + "] does not match any parameter in the declaring executable");
	}

}
