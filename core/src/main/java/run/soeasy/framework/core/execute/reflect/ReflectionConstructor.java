package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import lombok.NonNull;
import run.soeasy.framework.core.execute.ExecutableElement;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 反射构造函数实现类，继承自{@link ReflectionExecutable}并实现{@link ExecutableElement}接口，
 * 用于封装Java反射中的Constructor对象，提供构造函数的元数据描述和实例化能力。
 * <p>
 * 该类支持构造函数的执行（实例化对象），并实现了序列化接口以支持远程调用或持久化场景。
 * 通过缓存构造函数的声明类和参数类型信息，确保在序列化后仍能恢复构造函数的引用。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Constructor对象封装为框架可执行元素</li>
 *   <li>实例化支持：通过{@link #execute(Object...)}方法创建类的新实例</li>
 *   <li>序列化支持：实现Serializable接口，支持构造函数的序列化和反序列化</li>
 *   <li>状态恢复：反序列化后能通过缓存的类信息恢复构造函数引用</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>依赖注入：动态实例化对象并注入依赖</li>
 *   <li>工厂模式：作为工厂方法的反射实现</li>
 *   <li>RPC框架：远程对象的实例化</li>
 *   <li>对象池：通过反射构造函数创建池化对象</li>
 *   <li>测试框架：动态创建测试对象</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ReflectionExecutable
 * @see ExecutableElement
 * @see java.lang.reflect.Constructor
 */
public class ReflectionConstructor extends ReflectionExecutable<Constructor<?>>
        implements ExecutableElement, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造函数的声明类，用于序列化和恢复构造函数引用
     */
    private Class<?> declaringClass;
    
    /**
     * 构造函数的参数类型数组，用于序列化和恢复构造函数引用
     */
    private Class<?>[] parameterTypes;

    /**
     * 构造函数，初始化反射构造函数
     * 
     * @param member 反射Constructor对象，不可为null
     */
    public ReflectionConstructor(@NonNull Constructor<?> member) {
        super(member);
    }

    /**
     * 设置反射Constructor对象，并缓存声明类和参数类型信息
     * 
     * @param source 反射Constructor对象，不可为null
     */
    @Override
    public synchronized void setSource(@NonNull Constructor<?> source) {
        this.declaringClass = source.getDeclaringClass();
        this.parameterTypes = source.getParameterTypes();
        super.setSource(source);
    }

    /**
     * 获取反射Constructor对象，支持在序列化后恢复构造函数引用
     * <p>
     * 如果构造函数在序列化后丢失引用，会通过缓存的声明类和参数类型信息重新查找构造函数。
     * 
     * @return 反射Constructor对象
     */
    @Override
    public @NonNull Constructor<?> getSource() {
        Constructor<?> constructor = super.getSource();
        if (constructor == null) {
            synchronized (this) {
                if (constructor == null) {
                    constructor = ReflectionUtils.findConstructor(declaringClass, parameterTypes).first();
                    super.setSource(constructor);
                }
            }
        }
        return constructor;
    }

    /**
     * 执行构造函数创建新实例
     * <p>
     * 该方法通过反射调用构造函数，使用指定的参数创建类的新实例。
     * 
     * @param args 构造函数参数
     * @return 新创建的对象实例
     * @throws Throwable 构造函数执行过程中抛出的异常
     */
    @Override
    public Object execute(@NonNull Object... args) throws Throwable {
        return ReflectionUtils.newInstance(getSource(), args);
    }
}