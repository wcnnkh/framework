package run.soeasy.framework.core.type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lombok.NonNull;

/**
 * 使用sun.misc.Unsafe的allocateInstance方法创建实例的工厂。
 * 该工厂通过反射调用Unsafe类的allocateInstance方法，能够在不调用构造函数的情况下创建对象实例，
 * 适用于需要绕过构造函数进行实例化的场景，如序列化、框架内部对象创建等。
 *
 * <p>核心特性：
 * <ul>
 *   <li>无构造函数调用：通过Unsafe.allocateInstance创建实例，不执行任何构造函数</li>
 *   <li>延迟初始化：懒加载Unsafe类和相关方法，避免不必要的反射操作</li>
 *   <li>线程安全：使用双重检查锁确保Unsafe实例和方法的单例创建</li>
 *   <li>类型检查：在实例化前验证类型是否支持无构造实例化</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>序列化框架：在反序列化时创建对象而不调用构造函数</li>
 *   <li>代理生成：创建代理对象实例而不触发初始化逻辑</li>
 *   <li>框架内部工具：需要绕过常规实例化流程的场景</li>
 *   <li>测试工具：创建部分初始化的对象用于测试</li>
 * </ul>
 *
 * <p>注意事项：
 * <ul>
 *   <li>兼容性：Unsafe是Sun的内部API，可能在不同JVM实现中存在差异</li>
 *   <li>安全性：绕过构造函数可能导致对象处于不一致状态，需谨慎使用</li>
 *   <li>限制：无法实例化原始类型、数组、接口、注解和抽象类</li>
 * </ul>
 *
 * @see InstanceFactory
 */
public class AllocateInstanceFactory implements InstanceFactory {
    /** Unsafe类的Class对象，使用volatile确保可见性 */
    private volatile Class<?> unsafeClass;

    /**
     * 获取Unsafe类的Class对象。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return Unsafe类的Class对象，如果不可用则返回null
     */
    public Class<?> getUnsafeClass() {
        if (unsafeClass == null) {
            synchronized (this) {
                if (unsafeClass == null) {
                    unsafeClass = ClassUtils.getClass("sun.misc.Unsafe", null);
                }
            }
        }
        return unsafeClass;
    }

    /** Unsafe类的单例实例，使用volatile确保可见性 */
    private volatile Object unsafe;

    /**
     * 获取Unsafe类的实例。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return Unsafe类的实例，如果不可用则返回null
     */
    public Object getUnsafe() {
        if (unsafe == null && getUnsafeClass() != null) {
            synchronized (this) {
                if (unsafe == null && getUnsafeClass() != null) {
                    try {
                        Field field = getUnsafeClass().getDeclaredField("theUnsafe");
                        this.unsafe = ReflectionUtils.get(field, null);
                    } catch (NoSuchFieldException | SecurityException e) {
                        // 记录日志或忽略，保持null状态
                    }
                }
            }
        }
        return unsafe;
    }

    /** Unsafe类的allocateInstance方法，使用volatile确保可见性 */
    private volatile Method allocateInstanceMethod;

    /**
     * 获取Unsafe类的allocateInstance方法。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return allocateInstance方法对象，如果不可用则返回null
     */
    public Method getAllocateInstanceMethod() {
        if (allocateInstanceMethod == null && getUnsafeClass() != null) {
            synchronized (this) {
                if (allocateInstanceMethod == null && getUnsafeClass() != null) {
                    try {
                        this.allocateInstanceMethod = getUnsafeClass().getMethod("allocateInstance", Class.class);
                    } catch (NoSuchMethodException | SecurityException e) {
                        // 记录日志或忽略，保持null状态
                    }
                }
            }
        }
        return allocateInstanceMethod;
    }

    /**
     * 判断是否可以为指定类型创建实例。
     * 检查条件包括：类型是否为null、是否为基本类型、数组、注解、接口、抽象类，
     * 以及Unsafe类和allocateInstance方法是否可用。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 如果可以创建实例返回true，否则返回false
     */
    @Override
    public boolean canInstantiated(@NonNull ResolvableType requiredType) {
        Class<?> type = requiredType.getRawType();
        if (type == null || type.isPrimitive() || type.isArray() || type.isAnnotation() || type.isInterface()
                || Modifier.isAbstract(type.getModifiers()) || getAllocateInstanceMethod() == null
                || getUnsafe() == null) {
            return false;
        }
        return true;
    }

    /**
     * 使用Unsafe的allocateInstance方法创建指定类型的实例。
     * 该方法不会调用任何构造函数，直接分配内存并创建对象。
     *
     * @param requiredType 需要创建实例的类型描述符，不可为null
     * @return 创建的实例对象
     * @throws RuntimeException 如果实例化失败或Unsafe不可用
     */
    @Override
    public Object newInstance(@NonNull ResolvableType requiredType) {
        Method method = getAllocateInstanceMethod();
        Object unsafeInstance = getUnsafe();
        
        if (method == null || unsafeInstance == null) {
            throw new UnsupportedOperationException("Unsafe.allocateInstance不可用，无法创建实例: " + requiredType);
        }
        
        return ReflectionUtils.invoke(method, unsafeInstance, requiredType.getRawType());
    }
}