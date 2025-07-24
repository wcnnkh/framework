package run.soeasy.framework.core.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 使用反射工厂创建序列化构造器的工厂类。
 * 该工厂通过sun.reflect.ReflectionFactory创建特殊的构造器，
 * 允许在反序列化时绕过目标类的构造函数，直接创建对象实例，
 * 主要用于需要自定义序列化逻辑的场景。
 *
 * <p>核心特性：
 * <ul>
 *   <li>序列化构造器：创建可绕过目标类构造函数的特殊构造器</li>
 *   <li>延迟初始化：懒加载反射工厂相关类和方法，避免不必要的反射操作</li>
 *   <li>线程安全：使用双重检查锁确保反射工厂实例和方法的单例创建</li>
 *   <li>兼容性：支持Java标准反射工厂机制</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>序列化框架：自定义反序列化逻辑，绕过构造函数创建对象</li>
 *   <li>框架初始化：创建部分初始化的对象，后续通过其他方式完成初始化</li>
 *   <li>测试工具：创建未完全初始化的对象用于测试特定场景</li>
 *   <li>代理生成：生成不执行构造函数的代理对象</li>
 * </ul>
 *
 * <p>注意事项：
 * <ul>
 *   <li>内部API依赖：依赖sun.reflect包下的非公开API，可能存在兼容性问题</li>
 *   <li>安全性：绕过构造函数可能导致对象处于不一致状态，需谨慎使用</li>
 *   <li>性能开销：反射操作存在一定性能开销，建议缓存生成的构造器</li>
 * </ul>
 *
 * @see ClassMemberFactory
 * @see Constructor
 */
public class SerializationConstructorFactory implements ClassMemberFactory<Constructor<?>> {
    /** Object类的无参构造器，作为序列化构造器的模板 */
    private static Constructor<Object> objectConstructor;
    
    static {
        try {
            objectConstructor = Object.class.getConstructor();
        } catch (NoSuchMethodException e) {
            // 不可能出现Object类没有无参构造器的情况
            throw new AssertionError("Object类必须有无参构造器", e);
        }
    }

    /** ReflectionFactory类的Class对象，使用volatile确保可见性 */
    private volatile Class<?> reflectionFactoryDeclaringClass;

    /**
     * 获取ReflectionFactory类的Class对象。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return ReflectionFactory类的Class对象，如果不可用则返回null
     */
    public Class<?> getReflectionFactoryDeclaringClass() {
        if (reflectionFactoryDeclaringClass == null) {
            synchronized (this) {
                if (reflectionFactoryDeclaringClass == null) {
                    try {
                        this.reflectionFactoryDeclaringClass = Class.forName("sun.reflect.ReflectionFactory");
                    } catch (ClassNotFoundException e) {
                        // 记录日志或忽略，保持null状态
                    }
                }
            }
        }
        return reflectionFactoryDeclaringClass;
    }

    /** newConstructorForSerialization方法，使用volatile确保可见性 */
    private volatile Method newConstructorForSerialization;

    /**
     * 获取ReflectionFactory类的newConstructorForSerialization方法。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return newConstructorForSerialization方法对象，如果不可用则返回null
     */
    public Method getNewConstructorForSerialization() {
        if (newConstructorForSerialization == null && getReflectionFactoryDeclaringClass() != null) {
            synchronized (this) {
                if (newConstructorForSerialization == null && getReflectionFactoryDeclaringClass() != null) {
                    try {
                        this.newConstructorForSerialization = getReflectionFactoryDeclaringClass()
                                .getMethod("newConstructorForSerialization", Class.class, Constructor.class);
                    } catch (NoSuchMethodException | SecurityException e) {
                        // 记录日志或忽略，保持null状态
                    }
                }
            }
        }
        return newConstructorForSerialization;
    }

    /** ReflectionFactory类的实例，使用volatile确保可见性 */
    private volatile Object reflectionFactory;

    /**
     * 获取ReflectionFactory类的实例。
     * 使用双重检查锁实现延迟初始化，确保线程安全。
     *
     * @return ReflectionFactory类的实例，如果不可用则返回null
     */
    public Object getReflectionFactory() {
        if (reflectionFactory == null && getReflectionFactoryDeclaringClass() != null) {
            synchronized (this) {
                // 修正条件判断，原代码中的条件存在逻辑问题
                if (reflectionFactory == null && getReflectionFactoryDeclaringClass() != null) {
                    Method getReflectionFactoryMethod;
                    try {
                        getReflectionFactoryMethod = getReflectionFactoryDeclaringClass().getMethod("getReflectionFactory");
                    } catch (NoSuchMethodException | SecurityException e) {
                        return null;
                    }
                    reflectionFactory = ReflectionUtils.invoke(getReflectionFactoryMethod, null);
                }
            }
        }
        return reflectionFactory;
    }

    /**
     * 获取用于序列化的构造器提供者。
     * 该方法使用ReflectionFactory创建一个特殊构造器，
     * 该构造器可以在不执行目标类构造函数的情况下创建对象。
     *
     * @param declaringClass 声明构造器的类，不可为null
     * @return 构造器提供者，如果无法创建则返回空提供者
     */
    @Override
    public Provider<Constructor<?>> getClassMemberProvider(@NonNull Class<?> declaringClass) {
        if (getReflectionFactory() != null && getNewConstructorForSerialization() != null) {
            Constructor<?> constructor = (Constructor<?>) ReflectionUtils.invoke(
                    getNewConstructorForSerialization(),
                    getReflectionFactory(),
                    declaringClass,
                    objectConstructor
            );
            if (constructor != null) {
                // 缓存构造器，避免重复创建
                return Provider.forSupplier(() -> constructor);
            }
        }
        return Provider.empty();
    }
}