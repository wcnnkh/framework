package run.soeasy.framework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.annotation.MergedAnnotatedElement;
import run.soeasy.framework.core.collection.LRULinkedHashMap;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ResolvableType;

public class TypeDescriptor extends MergedAnnotatedElement {
	private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new LRULinkedHashMap<Class<?>, TypeDescriptor>(
			256);

	private static final Class<?>[] CACHED_COMMON_TYPES = { boolean.class, Boolean.class, byte.class, Byte.class,
			char.class, Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class,
			long.class, Long.class, short.class, Short.class, String.class, Object.class };

	static {
		for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
			commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
		}
	}

	private final Class<?> type;

	private final ResolvableType resolvableType;

	/**
	 * Create a new type descriptor from a {@link ResolvableType}.
	 * <p>
	 * This constructor is used internally and may also be used by subclasses that
	 * support non-Java languages with extended type systems.
	 * 
	 * @param resolvableType    the resolvable type
	 * @param type              the backing type (or {@code null} if it should get
	 *                          resolved)
	 * @param annotatedElements the type annotatedElements
	 */
	public TypeDescriptor(@NonNull ResolvableType resolvableType, Class<?> type,
			@NonNull AnnotatedElement... annotatedElements) {
		super(annotatedElements.length == 0 ? Collections.emptyList() : Arrays.asList(annotatedElements));
		this.resolvableType = resolvableType;
		this.type = (type != null ? type : resolvableType.getRawType());
	}

	/**
	 * Variation of {@link #getType()} that accounts for a primitive type by
	 * returning its object wrapper type.
	 * <p>
	 * This is useful for conversion service implementations that wish to normalize
	 * to object-based types and not work with primitive types directly.
	 */
	public Class<?> getObjectType() {
		return ClassUtils.resolvePrimitiveIfNecessary(getType());
	}

	/**
	 * The type of the backing class, method parameter, field, or property described
	 * by this TypeDescriptor.
	 * <p>
	 * Returns primitive types as-is. See {@link #getObjectType()} for a variation
	 * of this operation that resolves primitive types to their corresponding Object
	 * types if necessary.
	 * 
	 * @see #getObjectType()
	 */
	public Class<?> getType() {
		return this.type;
	}

	/**
	 * Return the underlying {@link ResolvableType}.
	 */
	public ResolvableType getResolvableType() {
		return this.resolvableType;
	}

	public TypeDescriptor getNested(int nestingLevel) {
		return new TypeDescriptor(resolvableType.getNested(nestingLevel), null, this);
	}

	public TypeDescriptor getGeneric(int... indexes) {
		return new TypeDescriptor(resolvableType.getActualTypeArgument(indexes), null, this);
	}

	/**
	 * Narrows this {@link TypeDescriptor} by setting its type to the class of the
	 * provided value.
	 * <p>
	 * If the value is {@code null}, no narrowing is performed and this
	 * TypeDescriptor is returned unchanged.
	 * <p>
	 * Designed to be called by binding frameworks when they read property, field,
	 * or method return values. Allows such frameworks to narrow a TypeDescriptor
	 * built from a declared property, field, or method return value type. For
	 * example, a field declared as {@code java.lang.Object} would be narrowed to
	 * {@code java.util.HashMap} if it was set to a {@code java.util.HashMap} value.
	 * The narrowed TypeDescriptor can then be used to convert the HashMap to some
	 * other type. Annotation and nested type context is preserved by the narrowed
	 * copy.
	 * 
	 * @param value the value to use for narrowing this type descriptor
	 * @return this TypeDescriptor narrowed (returns a copy with its type updated to
	 *         the class of the provided value)
	 */
	public TypeDescriptor narrow(Object value) {
		if (value == null) {
			return this;
		}
		ResolvableType narrowed = ResolvableType.forType(value.getClass(), getResolvableType());
		return new TypeDescriptor(narrowed, value.getClass(), this);
	}

	public TypeDescriptor convert(ResolvableType type) {
		return new TypeDescriptor(type, type.getRawType(), this);
	}

	/**
	 * Cast this {@link TypeDescriptor} to a superclass or implemented interface
	 * preserving annotations and nested type context.
	 * 
	 * @param superType the super type to cast to (can be {@code null})
	 * @return a new TypeDescriptor for the up-cast type
	 * @throws IllegalArgumentException if this type is not assignable to the
	 *                                  super-type
	 */
	public TypeDescriptor upcast(Class<?> superType) {
		if (superType == null) {
			return null;
		}
		Assert.isAssignable(superType, getType());
		return new TypeDescriptor(getResolvableType().as(superType), superType, this);
	}

	/**
	 * Return the name of this type: the fully qualified class name.
	 */
	public String getName() {
		return ClassUtils.getQualifiedName(getType());
	}

	/**
	 * Is this type a primitive type?
	 */
	public boolean isPrimitive() {
		return getType().isPrimitive();
	}

	public boolean isEnum() {
		return getType().isEnum();
	}

	/**
	 * 是否是泛型
	 * 
	 * @return
	 */
	public boolean isGeneric() {
		return getResolvableType().hasActualTypeArguments();
	}

	/**
	 * Returns true if an object of this type descriptor can be assigned to the
	 * location described by the given type descriptor.
	 * <p>
	 * For example,
	 * {@code valueOf(String.class).isAssignableTo(valueOf(CharSequence.class))}
	 * returns {@code true} because a String value can be assigned to a CharSequence
	 * variable. On the other hand,
	 * {@code valueOf(Number.class).isAssignableTo(valueOf(Integer.class))} returns
	 * {@code false} because, while all Integers are Numbers, not all Numbers are
	 * Integers.
	 * <p>
	 * For arrays, collections, and maps, element and key/value types are checked if
	 * declared. For example, a List&lt;String&gt; field value is assignable to a
	 * Collection&lt;CharSequence&gt; field, but List&lt;Number&gt; is not
	 * assignable to List&lt;Integer&gt;.
	 * 
	 * @return {@code true} if this type is assignable to the type represented by
	 *         the provided type descriptor
	 * @see #getObjectType()
	 */
	public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
		boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
		if (!typesAssignable) {
			return false;
		}
		if (isArray() && typeDescriptor.isArray()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isCollection() && typeDescriptor.isCollection()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isMap() && typeDescriptor.isMap()) {
			return isNestedAssignable(getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor())
					&& isNestedAssignable(getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor());
		} else {
			return true;
		}
	}

	private boolean isNestedAssignable(TypeDescriptor nestedTypeDescriptor, TypeDescriptor otherNestedTypeDescriptor) {

		return (nestedTypeDescriptor == null || otherNestedTypeDescriptor == null
				|| nestedTypeDescriptor.isAssignableTo(otherNestedTypeDescriptor));
	}

	/**
	 * Is this type a {@link Collection} type?
	 */
	public boolean isCollection() {
		return Collection.class.isAssignableFrom(getType());
	}

	/**
	 * Is this type an array type?
	 */
	public boolean isArray() {
		return getType().isArray();
	}

	/**
	 * If this type is an array, returns the array's component type. If this type is
	 * a {@code Stream}, returns the stream's component type. If this type is a
	 * {@link Collection} and it is parameterized, returns the Collection's element
	 * type. If the Collection is not parameterized, returns {@code null} indicating
	 * the element type is not declared.
	 * 
	 * @return the array component type or Collection element type, or {@code null}
	 *         if this type is not an array type or a {@code java.util.Collection}
	 *         or if its element type is not parameterized
	 * @see #elementTypeDescriptor(Object)
	 */
	public TypeDescriptor getElementTypeDescriptor() {
		if (getResolvableType().isArray()) {
			return new TypeDescriptor(getResolvableType().getComponentType(), null, this);
		}
		if (Stream.class.isAssignableFrom(getType())) {
			return getRelatedIfResolvable(this, getResolvableType().as(Stream.class).getActualTypeArgument(0));
		}
		return getRelatedIfResolvable(this, getResolvableType().as(Collection.class).getActualTypeArgument(0));
	}

	/**
	 * If this type is a {@link Collection} or an array, creates a element
	 * TypeDescriptor from the provided collection or array element.
	 * <p>
	 * Narrows the {@link #getElementTypeDescriptor() elementType} property to the
	 * class of the provided collection or array element. For example, if this
	 * describes a {@code java.util.List&lt;java.lang.Number&lt;} and the element
	 * argument is an {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer}. If this describes a
	 * {@code java.util.List&lt;?&gt;} and the element argument is an
	 * {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer} as well.
	 * <p>
	 * Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * 
	 * @param element the collection or array element
	 * @return a element type descriptor, narrowed to the type of the provided
	 *         element
	 * @see #getElementTypeDescriptor()
	 * @see #narrow(Object)
	 */
	public TypeDescriptor elementTypeDescriptor(Object element) {
		return narrow(element, getElementTypeDescriptor());
	}

	/**
	 * Is this type a {@link Map} type?
	 */
	public boolean isMap() {
		return Map.class.isAssignableFrom(getType());
	}

	/**
	 * If this type is a {@link Map} and its key type is parameterized, returns the
	 * map's key type. If the Map's key type is not parameterized, returns
	 * {@code null} indicating the key type is not declared.
	 * 
	 * @return the Map key type, or {@code null} if this type is a Map but its key
	 *         type is not parameterized
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 */
	public TypeDescriptor getMapKeyTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return getRelatedIfResolvable(this, getResolvableType().as(Map.class).getActualTypeArgument(0));
	}

	/**
	 * If this type is a {@link Map}, creates a mapKey {@link TypeDescriptor} from
	 * the provided map key.
	 * <p>
	 * Narrows the {@link #getMapKeyTypeDescriptor() mapKeyType} property to the
	 * class of the provided map key. For example, if this describes a
	 * {@code java.util.Map&lt;java.lang.Number, java.lang.String&lt;} and the key
	 * argument is a {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer}. If this describes a
	 * {@code java.util.Map&lt;?, ?&gt;} and the key argument is a
	 * {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer} as well.
	 * <p>
	 * Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * 
	 * @param mapKey the map key
	 * @return the map key type descriptor
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 * @see #narrow(Object)
	 */
	public TypeDescriptor getMapKeyTypeDescriptor(Object mapKey) {
		return narrow(mapKey, getMapKeyTypeDescriptor());
	}

	/**
	 * If this type is a {@link Map} and its value type is parameterized, returns
	 * the map's value type.
	 * <p>
	 * If the Map's value type is not parameterized, returns {@code null} indicating
	 * the value type is not declared.
	 * 
	 * @return the Map value type, or {@code null} if this type is a Map but its
	 *         value type is not parameterized
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 */
	public TypeDescriptor getMapValueTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return getRelatedIfResolvable(this, getResolvableType().as(Map.class).getActualTypeArgument(1));
	}

	/**
	 * If this type is a {@link Map}, creates a mapValue {@link TypeDescriptor} from
	 * the provided map value.
	 * <p>
	 * Narrows the {@link #getMapValueTypeDescriptor() mapValueType} property to the
	 * class of the provided map value. For example, if this describes a
	 * {@code java.util.Map&lt;java.lang.String, java.lang.Number&lt;} and the value
	 * argument is a {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer}. If this describes a
	 * {@code java.util.Map&lt;?, ?&gt;} and the value argument is a
	 * {@code java.lang.Integer}, the returned TypeDescriptor will be
	 * {@code java.lang.Integer} as well.
	 * <p>
	 * Annotation and nested type context will be preserved in the narrowed
	 * TypeDescriptor that is returned.
	 * 
	 * @param mapValue the map value
	 * @return the map value type descriptor
	 * @throws IllegalStateException if this type is not a {@code java.util.Map}
	 * @see #narrow(Object)
	 */
	public TypeDescriptor getMapValueTypeDescriptor(Object mapValue) {
		return narrow(mapValue, getMapValueTypeDescriptor());
	}

	private TypeDescriptor narrow(Object value, TypeDescriptor typeDescriptor) {
		if (typeDescriptor != null) {
			return typeDescriptor.narrow(value);
		}
		if (value != null) {
			return narrow(value);
		}
		return null;
	}

	/**
	 * Create a new type descriptor for an object.
	 * <p>
	 * Use this factory method to introspect a source object before asking the
	 * conversion system to convert it to some another type.
	 * <p>
	 * If the provided object is {@code null}, returns {@code null}, else calls
	 * {@link #valueOf(Class)} to build a TypeDescriptor from the object's class.
	 * 
	 * @param source the source object
	 * @return the type descriptor
	 */
	public static TypeDescriptor forObject(Object source) {
		return (source != null ? valueOf(source.getClass()) : null);
	}

	public static TypeDescriptor forMethodReturnType(@NonNull Executable executable) {
		if (executable instanceof Method) {
			return new TypeDescriptor(ResolvableType.forType(((Method) executable).getGenericReturnType()), null,
					executable);
		}
		return new TypeDescriptor(ResolvableType.forType(executable.getDeclaringClass()), null, executable);
	}

	public static TypeDescriptor forFieldType(@NonNull Field field) {
		return new TypeDescriptor(ResolvableType.forType(field.getGenericType()), null, field);
	}

	public static TypeDescriptor forParameter(@NonNull Parameter parameter) {
		return new TypeDescriptor(ResolvableType.forType(parameter.getParameterizedType()), null, parameter);
	}

	public static TypeDescriptor forExecutableParameter(Executable executable, int index) {
		if (index >= executable.getParameterCount()) {
			throw new IndexOutOfBoundsException("index: " + index);
		}
		Parameter parameter = executable.getParameters()[index];
		return forParameter(parameter);
	}

	/**
	 * Create a new type descriptor from the given type.
	 * <p>
	 * Use this to instruct the conversion system to convert an object to a specific
	 * target type, when no type location such as a method parameter or field is
	 * available to provide additional conversion context.
	 * <p>
	 * Generally prefer use of {@link #forObject(Object)} for constructing type
	 * descriptors from source objects, as it handles the {@code null} object case.
	 * 
	 * @param type the class (may be {@code null} to indicate {@code Object.class})
	 * @return the corresponding type descriptor
	 */
	public static TypeDescriptor valueOf(Class<?> type) {
		if (type == null) {
			type = Object.class;
		}
		TypeDescriptor desc = commonTypesCache.get(type);
		return (desc != null ? desc : new TypeDescriptor(ResolvableType.forType(type), null, type));
	}

	public static TypeDescriptor valueOf(Type type) {
		if (type == null) {
			type = Object.class;
		}

		return new TypeDescriptor(ResolvableType.forType(type), null);
	}

	public static TypeDescriptor valueOf(ResolvableType type) {
		if (type == null) {
			type = ResolvableType.forType(Object.class);
		}
		return new TypeDescriptor(type, null);
	}

	/**
	 * Create a new type descriptor from a {@link java.util.Collection} type.
	 * <p>
	 * Useful for converting to typed Collections.
	 * <p>
	 * For example, a {@code List<String>} could be converted to a
	 * {@code List<EmailAddress>} by converting to a targetType built with this
	 * method. The method call to construct such a {@code TypeDescriptor} would look
	 * something like:
	 * {@code collection(List.class, TypeDescriptor.valueOf(EmailAddress.class));}
	 * 
	 * @param collectionType        the collection type, which must implement
	 *                              {@link Collection}.
	 * @param elementTypeDescriptor a descriptor for the collection's element type,
	 *                              used to convert collection elements
	 * @return the collection type descriptor
	 */
	public static TypeDescriptor collection(@NonNull Class<?> collectionType, TypeDescriptor elementTypeDescriptor) {
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Collection type must be a [java.util.Collection]");
		}
		ResolvableType element = (elementTypeDescriptor != null ? elementTypeDescriptor.resolvableType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(collectionType, element), null);
	}

	public static TypeDescriptor collection(@NonNull Class<?> collectionType, ResolvableType elementType) {
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Collection type must be a [java.util.Collection]");
		}
		ResolvableType element = (elementType != null ? elementType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(collectionType, element), null);
	}

	public static TypeDescriptor collection(Class<?> collectionType, Class<?> elementType) {
		return collection(collectionType, ResolvableType.forType(elementType));
	}

	/**
	 * Create a new type descriptor from a {@link java.util.Map} type.
	 * <p>
	 * Useful for converting to typed Maps.
	 * <p>
	 * For example, a Map&lt;String, String&gt; could be converted to a Map&lt;Id,
	 * EmailAddress&gt; by converting to a targetType built with this method: The
	 * method call to construct such a TypeDescriptor would look something like:
	 * 
	 * <pre class="code">
	 * map(Map.class, TypeDescriptor.valueOf(Id.class), TypeDescriptor.valueOf(EmailAddress.class));
	 * </pre>
	 * 
	 * @param mapType             the map type, which must implement {@link Map}
	 * @param keyTypeDescriptor   a descriptor for the map's key type, used to
	 *                            convert map keys
	 * @param valueTypeDescriptor the map's value type, used to convert map values
	 * @return the map type descriptor
	 */
	public static TypeDescriptor map(@NonNull Class<?> mapType, TypeDescriptor keyTypeDescriptor,
			TypeDescriptor valueTypeDescriptor) {
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("Map type must be a [java.util.Map]");
		}
		ResolvableType key = (keyTypeDescriptor != null ? keyTypeDescriptor.resolvableType : null);
		ResolvableType value = (valueTypeDescriptor != null ? valueTypeDescriptor.resolvableType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(mapType, key, value), null);
	}

	public static TypeDescriptor map(@NonNull Class<?> mapType, ResolvableType keyType, ResolvableType valueType) {
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("Map type must be a [java.util.Map]");
		}
		ResolvableType key = (keyType != null ? keyType : null);
		ResolvableType value = (valueType != null ? valueType : null);
		return new TypeDescriptor(ResolvableType.forClassWithGenerics(mapType, key, value), null);
	}

	public static TypeDescriptor map(@NonNull Class<?> mapType, Class<?> keyType, Class<?> valueType) {
		return map(mapType, ResolvableType.forType(keyType), ResolvableType.forType(valueType));
	}

	/**
	 * Create a new type descriptor as an array of the specified type.
	 * <p>
	 * For example to create a {@code Map<String,String>[]} use:
	 * 
	 * <pre class="code">
	 * TypeDescriptor.array(
	 * 		TypeDescriptor.map(Map.class, TypeDescriptor.value(String.class), TypeDescriptor.value(String.class)));
	 * </pre>
	 * 
	 * @param elementTypeDescriptor the {@link TypeDescriptor} of the array element
	 *                              or {@code null}
	 * @return an array {@link TypeDescriptor} or {@code null} if
	 *         {@code elementTypeDescriptor} is {@code null}
	 */
	public static TypeDescriptor array(TypeDescriptor elementTypeDescriptor) {
		if (elementTypeDescriptor == null) {
			return null;
		}
		return new TypeDescriptor(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType), null,
				elementTypeDescriptor);
	}

	public static TypeDescriptor array(ResolvableType elementType) {
		if (elementType == null) {
			return null;
		}
		return new TypeDescriptor(elementType, null);
	}

	public static TypeDescriptor array(Class<?> elementType) {
		if (elementType == null) {
			return null;
		}
		return array(ResolvableType.forType(elementType));
	}

	private static TypeDescriptor getRelatedIfResolvable(TypeDescriptor source, ResolvableType type) {
		if (type == null) {
			return null;
		}
		return new TypeDescriptor(type, null, source);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Annotation ann : getAnnotations()) {
			builder.append("@").append(ann.annotationType().getName()).append(' ');
		}
		builder.append(getResolvableType().toString());
		return builder.toString();
	}
}
