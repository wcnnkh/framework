package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.collection.Listable;
import run.soeasy.framework.core.page.Cursor;
import run.soeasy.framework.core.page.CursorPaging;
import run.soeasy.framework.core.page.Paging;

/**
 * 类工具类，提供一系列处理Java类和类型的静态方法。
 * 该类封装了类加载、类型检查、类型转换、类信息获取等功能，
 * 适用于反射操作、类型检查、框架开发等需要处理类元数据的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>类加载增强：支持加载基本类型、数组类型和泛型类型</li>
 *   <li>类型检查：提供丰富的类型判断方法（基本类型、包装类型、数组等）</li>
 *   <li>类型转换：支持基本类型与包装类型的相互转换</li>
 *   <li>类信息获取：获取类的全限定名、接口、父类等信息</li>
 *   <li>加载优化：使用缓存机制加速常用类的加载</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>反射操作：动态加载类、获取类信息</li>
 *   <li>框架开发：实现ORM、依赖注入等框架的基础类型处理</li>
 *   <li>类型检查：在参数验证、数据转换中进行类型检查</li>
 *   <li>类路径操作：解析类名、加载类文件</li>
 *   <li>泛型处理：处理泛型类型的解析和转换</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 加载类
 * Class&lt;?&gt; clazz = ClassUtils.forName("java.lang.String");
 * 
 * // 检查类型
 * boolean isString = ClassUtils.isString(clazz);
 * 
 * // 获取接口
 * Paging&lt;Class&lt;?&gt;, Class&lt;?&gt;&gt; interfaces = ClassUtils.getInterfaces(clazz);
 * 
 * // 类型兼容性检查  
 * boolean assignable = ClassUtils.isAssignable(Number.class, Integer.class);
 * </pre>
 *
 * @author soeasy.run
 */
@UtilityClass
public class ClassUtils {
    /** 数组类名后缀："[]" */
    public static final String ARRAY_SUFFIX = "[]";

    /** ".class"文件后缀 */
    public static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 常用类缓存，键为类名，值为对应的Class对象。主要用于高效反序列化远程调用。
     * 包含基本类型、包装类型、常用类及其数组类型。
     */
    private static final Map<String, Class<?>> commonClassCache = new HashMap<String, Class<?>>(32);

    private static final Class<?>[] EMPTY_ARRAY = new Class<?>[0];

    /** 泛型前缀："&gt;" */
    public static final String GENERIC_PREFIX = "<";

    /** 内部数组类名前缀："[" */
    private static final String INTERNAL_ARRAY_PREFIX = "[";

    /** 内部非基本类型数组类名前缀："[L" */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /**
     * 基本类型名称映射，键为基本类型名称，值为对应的Class对象。
     * 例如："int" -> int.class。
     */
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);

    /**
     * 基本类型到包装类型的映射，键为基本类型，值为对应的包装类型。
     * 例如：int.class -> Integer.class。
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    /**
     * 包装类型到基本类型的映射，键为包装类型，值为对应的基本类型。
     * 例如：Integer.class -> int.class。
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<Class<?>, Class<?>>(8);

    static {
        // 初始化包装类型到基本类型的映射
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);

        // 初始化基本类型到包装类型的映射，并注册常用类
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
            registerCommonClasses(entry.getKey());
        }

        // 注册基本类型和基本类型数组到名称映射
        Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(64);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        primitiveTypes.addAll(Arrays.asList(new Class<?>[] { boolean[].class, byte[].class, char[].class,
                double[].class, float[].class, int[].class, long[].class, short[].class }));
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        // 注册常用类及其数组类型到缓存
        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class,
                Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class,
                Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class,
                StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Cloneable.class, Comparable.class);
    }

    /**
     * 返回空的Class数组。
     *
     * @param <T> 数组元素类型
     * @return 空的Class数组
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T>[] emptyArray() {
        return (Class<T>[]) EMPTY_ARRAY;
    }

    /**
     * 替换{@code Class.forName()}的增强方法，支持加载基本类型、数组类名，
     * 并能解析Java源代码风格的内部类名（如"java.lang.Thread.State"而非"java.lang.Thread$State"）。
     *
     * @param name        类名
     * @param classLoader 类加载器（可为null，表示默认类加载器）
     * @return 对应的Class实例
     * @throws ClassNotFoundException 如果类未找到
     * @throws LinkageError           如果类文件无法加载
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public static Class<?> forName(@NonNull String name, ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = commonClassCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        // 处理"java.lang.String[]"风格的数组
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // 处理"[Ljava.lang.String;"风格的数组
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // 处理"[[I"或"[[Ljava.lang.String;"风格的数组
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // 处理泛型类名
        int end = name.indexOf(GENERIC_PREFIX);
        if (end != -1) {
            int begin = name.lastIndexOf(" ", end);
            return forName(name.substring(begin == -1 ? 0 : begin + 1, end), classLoader);
        }

        ClassLoader classLoaderToUse = classLoader;
        if (classLoaderToUse == null) {
            classLoaderToUse = getDefaultClassLoader();
        }
        try {
            return forName0(name, classLoaderToUse);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex != -1) {
                String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
                return forName0(innerClassName, classLoaderToUse);
            }
            throw ex;
        }
    }

    private static Class<?> forName0(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(name, false, classLoader);
    }

    /**
     * 尝试获取指定类名的Class对象，类不存在时返回null。
     *
     * @param className 类名
     * @param classLoader 类加载器
     * @return 对应的Class对象，类不存在时返回null
     */
    public static Class<?> getClass(@NonNull String className, ClassLoader classLoader) {
        Class<?> clazz = null;
        try {
            clazz = forName(className, classLoader);
        } catch (IllegalAccessError err) {
            throw new IllegalStateException(
                    "Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
                    err);
        } catch (Throwable ex) {
            // 通常是ClassNotFoundException或NoClassDefFoundError
        }
        return clazz;
    }

    /**
     * 查找指定类名的Class对象，返回Optional包装结果。
     *
     * @param className 类名
     * @param classLoader 类加载器
     * @return 包含Class对象的Optional，类不存在时返回空Optional
     */
    public static Optional<Class<?>> findClass(@NonNull String className, ClassLoader classLoader) {
        try {
            return Optional.ofNullable(forName(className, classLoader));
        } catch (IllegalAccessError err) {
            throw new IllegalStateException(
                    "Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
                    err);
        } catch (Throwable ex) {
            // 通常是ClassNotFoundException或NoClassDefFoundError
        }
        return Optional.empty();
    }

    /**
     * 返回默认的类加载器：通常是线程上下文类加载器（如有），
     * 否则使用加载ClassUtils类的类加载器作为回退。
     *
     * @return 默认类加载器（从不为null）
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法访问线程上下文类加载器 - 回退到系统类加载器...
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * 获取指定类的所有接口，返回分页结果。
     *
     * @param sourceClass 源类
     * @return 包含接口的分页结果
     */
    public static Paging<Class<?>, Class<?>> getInterfaces(@NonNull Class<?> sourceClass) {
        return new CursorPaging<Class<?>, Class<?>>(sourceClass, (clazz, size) -> {
            Class<?>[] interfaces = clazz.getInterfaces();
            List<Class<?>> list = interfaces == null ? Collections.emptyList() : Arrays.asList(interfaces);
            return new Cursor<>(clazz, Listable.forCollection(list), clazz.getSuperclass());
        });
    }

    /**
     * 返回给定类的全限定名：对于数组类，返回组件类型名 + "[]"；
     * 对于普通类，返回类名。
     *
     * @param clazz 类
     * @return 类的全限定名
     */
    public static String getQualifiedName(@NonNull Class<?> clazz) {
        if (clazz.isArray()) {
            return getQualifiedNameForArray(clazz);
        } else {
            return clazz.getName();
        }
    }

    /**
     * 为数组类构建全限定名：组件类型名 + "[]"。
     *
     * @param clazz 数组类
     * @return 数组类的全限定名
     */
    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append(ClassUtils.ARRAY_SUFFIX);
        }
        result.insert(0, clazz.getName());
        return result.toString();
    }

    /**
     * 检查右侧类型是否可以分配给左侧类型，考虑基本类型包装类可分配给对应基本类型。
     *
     * @param lhsType 目标类型
     * @param rhsType 值类型
     * @return 目标类型是否可从值类型分配
     */
    @SuppressWarnings("rawtypes")
    public static boolean isAssignable(@NonNull Class<?> lhsType, @NonNull Class<?> rhsType) {
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
                return true;
            }
        } else {
            Class resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查左侧类型数组是否可以分配给右侧类型数组。
     *
     * @param lhsTypes 目标类型数组
     * @param rhsTypes 值类型数组
     * @return 目标类型数组是否可从值类型数组分配
     */
    public static boolean isAssignable(Class<?>[] lhsTypes, Class<?>[] rhsTypes) {
        if (ArrayUtils.isEmpty(lhsTypes)) {
            return ArrayUtils.isEmpty(rhsTypes);
        }

        if (lhsTypes.length != (rhsTypes == null ? 0 : rhsTypes.length)) {
            return false;
        }

        for (int i = 0; i < lhsTypes.length; i++) {
            if (!isAssignable(lhsTypes[i], rhsTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 确定给定类型是否可以从给定值分配，考虑基本类型包装类可分配给对应基本类型。
     *
     * @param type 目标类型
     * @param value 值
     * @return 类型是否可从值分配
     */
    public static boolean isAssignableValue(@NonNull Class<?> type, Object value) {
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    /**
     * 检查类型是否为boolean或Boolean。
     *
     * @param type 类型
     * @return 是否为boolean类型
     */
    public static boolean isBoolean(Type type) {
        return type == boolean.class || type == Boolean.class;
    }

    /**
     * 检查类型是否为byte或Byte。
     *
     * @param type 类型
     * @return 是否为byte类型
     */
    public static boolean isByte(Type type) {
        return type == byte.class || type == Byte.class;
    }

    /**
     * 检查类型是否为char或Character。
     *
     * @param type 类型
     * @return 是否为char类型
     */
    public static boolean isChar(Type type) {
        return type == char.class || type == Character.class;
    }

    /**
     * 检查类型是否为double或Double。
     *
     * @param type 类型
     * @return 是否为double类型
     */
    public static boolean isDouble(Type type) {
        return type == double.class || type == Double.class;
    }

    /**
     * 检查类型是否为float或Float。
     *
     * @param type 类型
     * @return 是否为float类型
     */
    public static boolean isFloat(Type type) {
        return type == float.class || type == Float.class;
    }

    /**
     * 检查类型是否为int或Integer。
     *
     * @param type 类型
     * @return 是否为int类型
     */
    public static boolean isInt(Type type) {
        return type == int.class || type == Integer.class;
    }

    /**
     * 检查类型是否为long或Long。
     *
     * @param type 类型
     * @return 是否为long类型
     */
    public static boolean isLong(Type type) {
        return type == long.class || type == Long.class;
    }

    /**
     * 检查类型是否为包含多个值的类型（数组或集合）。
     *
     * @param type 类型
     * @return 是否为包含多个值的类型
     * @see Collection
     * @see Array
     */
    public static boolean isMultipleValues(Class<?> type) {
        return type != null && (type.isArray() || Collection.class.isAssignableFrom(type));
    }

    /**
     * 检查类型是否为基本类型。
     *
     * @param type 类型
     * @return 是否为基本类型
     */
    public static boolean isPrimitive(Type type) {
        return type instanceof Class && ((Class<?>) type).isPrimitive();
    }

    /**
     * 检查给定类是否表示基本类型数组（boolean[], byte[]等）。
     *
     * @param clazz 类
     * @return 是否为基本类型数组类
     */
    public static boolean isPrimitiveArray(@NonNull Class<?> clazz) {
        return (clazz.isArray() && clazz.getComponentType().isPrimitive());
    }

    /**
     * 检查给定类型是否为基本类型或基本类型包装类。
     *
     * @param type 类型
     * @return 是否为基本类型或包装类
     */
    public static boolean isPrimitiveOrWrapper(@NonNull Type type) {
        return isPrimitive(type) || isPrimitiveWrapper(type);
    }

    /**
     * 检查给定类型是否为基本类型包装类（Boolean, Byte等）。
     *
     * @param type 类型
     * @return 是否为基本类型包装类
     */
    public static boolean isPrimitiveWrapper(@NonNull Type type) {
        return primitiveWrapperTypeMap.containsKey(type);
    }

    /**
     * 检查给定类是否表示基本类型包装类数组（Boolean[], Byte[]等）。
     *
     * @param clazz 类
     * @return 是否为基本类型包装类数组类
     */
    public static boolean isPrimitiveWrapperArray(@NonNull Class<?> clazz) {
        return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
    }

    /**
     * 检查类型是否为short或Short。
     *
     * @param type 类型
     * @return 是否为short类型
     */
    public static boolean isShort(Type type) {
        return type == short.class || type == Short.class;
    }

    /**
     * 检查类型是否为String。
     *
     * @param type 类型
     * @return 是否为String类型
     */
    public static boolean isString(Type type) {
        return type == String.class;
    }

    /**
     * 注册常用类到缓存。
     *
     * @param commonClasses 要注册的常用类
     */
    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    /**
     * 解析给定的类名到Class实例。支持基本类型和数组类名。
     *
     * @param className 类名
     * @param classLoader 类加载器（可为null，表示默认类加载器）
     * @return 对应的Class实例
     * @throws IllegalArgumentException 如果类名无法解析
     * @throws IllegalStateException 如果类可解析但继承层次存在可读性不匹配
     * @see #forName(String, ClassLoader)
     */
    public static Class<?> resolveClassName(@NonNull String className, ClassLoader classLoader)
            throws IllegalArgumentException {

        try {
            return forName(className, classLoader);
        } catch (IllegalAccessError err) {
            throw new IllegalStateException(
                    "Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(),
                    err);
        } catch (LinkageError err) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
        }
    }

    /**
     * 根据JVM的基本类型命名规则解析给定的类名是否为基本类型。
     * 也支持JVM内部的基本类型数组类名。
     *
     * @param name 可能的基本类型类名
     * @return 基本类型Class，或null（如果不是基本类型或基本类型数组类名）
     */
    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // 大多数类名会很长，因此长度检查是有价值的
        if (name != null && name.length() <= 8) {
            // 可能是基本类型 - 很可能
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }

    /**
     * 如果给定类是基本类型，返回对应的包装类型，否则返回原类。
     *
     * @param clazz 类
     * @return 原类或基本类型对应的包装类型
     */
    public static Class<?> resolvePrimitiveIfNecessary(@NonNull Class<?> clazz) {
        return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
    }

    /**
     * 判断提供的Class是否是JVM生成的lambda表达式或方法引用的实现类。
     * 基于现代主流JVM的检查规则做出最佳努力判断。
     *
     * @param clazz 要检查的类
     * @return 如果是lambda实现类返回true
     */
    public static boolean isLambdaClass(Class<?> clazz) {
        return (clazz.isSynthetic() && (clazz.getSuperclass() == Object.class) && (clazz.getInterfaces().length > 0)
                && clazz.getName().contains("$$Lambda"));
    }
    
    /**
	 * 判断指定的类是否为数字类型（包含原生数字类型和{@link Number}子类）
	 * <p>
	 * 支持的数字类型清单：
	 * <ul>
	 * <li>原生基本类型：long.class、int.class、byte.class、short.class、float.class、double.class；</li>
	 * <li>包装类及子类：Integer.class、Long.class、BigInteger.class、BigDecimal.class、Float.class、Double.class等（需是Number的直接/间接子类）。</li>
	 * </ul>
	 * <p>
	 * 注意：CharSequence、String等非Number子类即使能表示数字，也不视为数字类型。
	 *
	 * @param type 待判断的类类型（可为null，null时返回false）
	 * @return true：是数字类型；false：非数字类型或type为null
	 */
	public static boolean isNumber(Class<?> type) {
		if (type == null) {
			return false;
		}
		// 匹配原生数字类型
		if (type == long.class || type == int.class || type == byte.class || type == short.class || type == float.class
				|| type == double.class) {
			return true;
		}
		// 匹配Number的子类（包装类、高精度类等）
		return Number.class.isAssignableFrom(type);
	}
}