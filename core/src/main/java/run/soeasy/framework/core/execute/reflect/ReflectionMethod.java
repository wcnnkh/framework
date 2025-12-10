package run.soeasy.framework.core.execute.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execute.InvodableElement;
import run.soeasy.framework.core.mapping.property.Property;
import run.soeasy.framework.core.type.ReflectionUtils;

/**
 * 反射方法实现类，继承自{@link ReflectionExecutable}并实现{@link InvodableElement}和{@link Property}接口，
 * 用于封装Java反射中的Method对象，提供方法的元数据描述、动态调用能力以及属性访问功能。
 * <p>
 * 该类支持方法的动态调用，并通过实现Property接口提供类属性级别的访问能力，
 * 可将无参方法视为getter，将单参数方法视为setter，从而实现方法与属性的统一抽象。
 * 同时实现了序列化接口以支持远程调用或持久化场景。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>反射封装：将Java反射的Method对象封装为框架可调用元素</li>
 *   <li>方法调用：通过{@link #invoke(Object, Object...)}方法动态调用目标方法</li>
 *   <li>属性抽象：实现Property接口，支持将方法视为类属性进行访问</li>
 *   <li>序列化支持：实现Serializable接口，支持方法的序列化和反序列化</li>
 *   <li>状态恢复：反序列化后能通过缓存的类信息恢复方法引用</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>依赖注入：通过setter方法注入依赖</li>
 *   <li>Bean属性访问：统一访问JavaBean的属性</li>
 *   <li>AOP框架：拦截和增强方法调用</li>
 *   <li>RPC框架：远程方法调用</li>
 *   <li>表达式语言：动态调用对象方法</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ReflectionExecutable
 * @see InvodableElement
 * @see Property
 * @see java.lang.reflect.Method
 */
public class ReflectionMethod extends ReflectionExecutable<Method> implements InvodableElement, Property, Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 方法名称，用于序列化和恢复方法引用
     */
    private String name;
    
    /**
     * 方法的声明类，用于序列化和恢复方法引用
     */
    private Class<?> declaringClass;
    
    /**
     * 方法的参数类型数组，用于序列化和恢复方法引用
     */
    private Class<?>[] parameterTypes;

    /**
     * 构造函数，初始化反射方法
     * 
     * @param method 反射Method对象，不可为null
     */
    public ReflectionMethod(@NonNull Method method) {
        super(method);
    }

    /**
     * 设置反射Method对象，并缓存方法名称、声明类和参数类型信息
     * 
     * @param source 反射Method对象
     */
    @Override
    public void setSource(Method source) {
        synchronized (this) {
            this.name = source.getName();
            this.declaringClass = source.getDeclaringClass();
            this.parameterTypes = source.getParameterTypes();
            super.setSource(source);
        }
    }

    /**
     * 获取反射Method对象，支持在序列化后恢复方法引用
     * <p>
     * 如果方法在序列化后丢失引用，会通过缓存的名称、声明类和参数类型信息重新查找方法。
     * 
     * @return 反射Method对象
     */
    @Override
    public @NonNull Method getSource() {
        Method method = super.getSource();
        if (method == null) {
            synchronized (this) {
                if (method == null) {
                    method = ReflectionUtils.findMethod(declaringClass, name, parameterTypes).first();
                    super.setSource(method);
                }
            }
        }
        return method;
    }

    /**
     * 获取方法名称
     * 
     * @return 方法名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 动态调用目标对象的方法
     * <p>
     * 该方法通过反射调用目标对象的方法，使用指定的参数并返回结果。
     * 
     * @param target 目标对象，若为静态方法则可为null
     * @param args 方法参数
     * @return 方法返回值
     */
    @Override
    public Object invoke(Object target, @NonNull Object... args) {
        return ReflectionUtils.invoke(getSource(), target, args);
    }

    /**
     * 获取属性所需的类型描述符
     * <p>
     * 该方法仅在方法可写（即单参数方法）时支持，返回参数的类型描述符。
     * 
     * @return 参数的类型描述符
     * @throws UnsupportedOperationException 如果方法不可写
     */
    @Override
    public TypeDescriptor getRequiredTypeDescriptor() throws UnsupportedOperationException {
        if (!isWriteable()) {
            throw new UnsupportedOperationException(getSource().toString());
        }
        return getParameterMapping().elements().first().getReturnTypeDescriptor();
    }

    /**
     * 判断方法是否可读（即是否为无参方法）
     * 
     * @return 如果方法无参数返回true，否则返回false
     */
    @Override
    public boolean isReadable() {
        return getParameterMapping().isEmpty();
    }

    /**
     * 判断方法是否可写（即是否为单参数方法）
     * 
     * @return 如果方法有且仅有一个参数返回true，否则返回false
     */
    @Override
    public boolean isWriteable() {
        return getParameterMapping().count() == 1;
    }

    /**
     * 作为属性读取方法返回值
     * <p>
     * 该方法仅在方法可读（即无参方法）时支持，等效于调用无参方法获取返回值。
     * 
     * @param target 目标对象，若为静态方法则可为null
     * @return 方法返回值
     * @throws UnsupportedOperationException 如果方法不可读
     */
    @Override
    public Object readFrom(Object target) throws UnsupportedOperationException {
        if (!isReadable()) {
            throw new UnsupportedOperationException(getSource().toString());
        }
        return invoke(target, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    /**
     * 作为属性写入值到方法
     * <p>
     * 该方法仅在方法可写（即单参数方法）时支持，等效于调用单参数方法设置值。
     * 
     * @param target 目标对象，若为静态方法则可为null
     * @param value 要设置的值
     * @throws UnsupportedOperationException 如果方法不可写
     */
    @Override
    public void writeTo(Object target, Object value) throws UnsupportedOperationException {
        if (!isWriteable()) {
            throw new UnsupportedOperationException(getSource().toString());
        }
        invoke(target, value);
    }
}