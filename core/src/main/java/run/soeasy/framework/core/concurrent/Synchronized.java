package run.soeasy.framework.core.concurrent;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 同步代理工具，用于为对象创建线程安全的代理。
 * 该类使用Java动态代理机制，在目标对象的所有方法调用上添加同步锁，
 * 确保在多线程环境下方法调用的线程安全性。
 *
 * <p>核心特性：
 * <ul>
 *   <li>基于JDK动态代理，仅支持代理接口，不支持具体类</li>
 *   <li>可指定自定义锁对象，或使用源对象自身作为锁</li>
 *   <li>所有方法调用都会被同步，包括toString()、hashCode()等</li>
 *   <li>实现Serializable接口，代理对象可序列化（需源对象支持）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>为非线程安全的对象提供线程安全包装</li>
 *   <li>在不修改原有代码的情况下增强对象的线程安全性</li>
 *   <li>对第三方库提供的非线程安全对象进行安全封装</li>
 *   <li>需要细粒度控制同步范围的场景</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * List<String> unsafeList = new ArrayList<>();
 * List<String> safeList = Synchronized.proxy(unsafeList, new Object());
 * }</pre>
 *
 * @author soeasy.run
 * @see InvocationHandler
 * @see Proxy
 */
@RequiredArgsConstructor
public final class Synchronized implements InvocationHandler, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 同步锁对象，所有方法调用都会在该对象上同步。
     */
    @NonNull
    private final Object mutex; // Object on which to synchronize
    
    /**
     * 被代理的源对象，所有方法调用都会转发到该对象。
     */
    @NonNull
    private final Object source;

    /**
     * 代理方法调用的核心逻辑。
     * 在调用源对象的方法前，会先获取锁，确保线程安全。
     *
     * @param proxy  代理对象
     * @param method 被调用的方法
     * @param args   方法参数
     * @return 方法返回值
     * @throws Throwable 方法抛出的异常
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (mutex) {
            return method.invoke(source, args);
        }
    }

    /**
     * 创建一个线程安全的代理对象。
     * 该代理对象会在所有方法调用上进行同步，确保线程安全性。
     *
     * @param <T>   源对象类型
     * @param source 源对象，必须实现至少一个接口
     * @param mutex  同步锁对象
     * @return 线程安全的代理对象
     * @throws IllegalArgumentException 如果源对象没有实现任何接口
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxy(T source, Object mutex) {
        Class<?>[] interfaces = source.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Source object must implement at least one interface");
        }
        return (T) Proxy.newProxyInstance(
            source.getClass().getClassLoader(),
            interfaces,
            new Synchronized(source, mutex)
        );
    }

    /**
     * 创建一个使用源对象自身作为锁的线程安全代理对象。
     * 等价于调用 proxy(source, source)。
     *
     * @param <T>   源对象类型
     * @param source 源对象，必须实现至少一个接口
     * @return 线程安全的代理对象
     * @throws IllegalArgumentException 如果源对象没有实现任何接口
     */
    public static <T> T proxy(T source) {
        return proxy(source, source);
    }
}