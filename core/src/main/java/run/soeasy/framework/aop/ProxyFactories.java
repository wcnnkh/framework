package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * 代理工厂集合类，继承自{@link ConfigurableServices}并实现{@link ProxyFactory}接口，
 * 用于管理多个{@link ProxyFactory}实例，提供统一的代理创建与管理入口，
 * 支持根据不同的场景自动选择合适的代理工厂实现，是AOP框架中代理工厂的聚合管理器。
 * 
 * <p>该类通过SPI机制加载并管理所有{@link ProxyFactory}实现，在执行代理操作时，
 * 遍历所有代理工厂，选择第一个支持当前操作的工厂执行具体逻辑，从而实现多代理方式的适配，
 * 简化上层对代理工厂的选择与使用。
 * 
 * @author soeasy.run
 * @see ConfigurableServices
 * @see ProxyFactory
 */
public class ProxyFactories extends ConfigurableServices<ProxyFactory> implements ProxyFactory {

    /**
     * 构造代理工厂集合，指定服务类为{@link ProxyFactory}
     */
    public ProxyFactories() {
    	super(ServiceComparator.defaultServiceComparator());
    }

    /**
     * 判断指定类是否可被代理（任意代理工厂支持即可）
     * 
     * <p>遍历所有注册的代理工厂，若存在任一工厂支持代理该类，则返回true。
     * 
     * @param sourceClass 待判断的原始类（非空）
     * @return 存在支持的代理工厂返回true，否则返回false
     */
    @Override
    public boolean canProxy(Class<?> sourceClass) {
        for (ProxyFactory proxy : this) {
            if (proxy.canProxy(sourceClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定类是否为代理类（任意代理工厂识别即可）
     * 
     * <p>遍历所有注册的代理工厂，若存在任一工厂识别该类为代理类，则返回true。
     * 
     * @param proxyClass 待判断的类（可为空，为空时返回false）
     * @return 被任一代理工厂识别为代理类则返回true，否则返回false
     */
    @Override
    public boolean isProxy(Class<?> proxyClass) {
        for (ProxyFactory proxy : this) {
            if (proxy.isProxy(proxyClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从代理类中获取原始类（通过识别该代理类的工厂获取）
     * 
     * <p>遍历所有注册的代理工厂，找到第一个识别该代理类的工厂，调用其{@link ProxyFactory#getUserClass(Class)}方法。
     * 若未找到对应工厂，则返回该类本身。
     * 
     * @param proxyClass 代理类（非空）
     * @return 原始业务类（非代理类）
     */
    @Override
    public Class<?> getUserClass(Class<?> proxyClass) {
        for (ProxyFactory proxy : this) {
            if (proxy.isProxy(proxyClass)) {
                return proxy.getUserClass(proxyClass);
            }
        }
        return proxyClass;
    }

    /**
     * 创建代理对象（使用第一个支持该类的代理工厂）
     * 
     * <p>遍历所有注册的代理工厂，找到第一个支持代理该类的工厂，调用其{@link ProxyFactory#getProxy(Class, Class[], ExecutionInterceptor)}方法。
     * 若未找到支持的工厂，抛出{@link UnsupportedOperationException}。
     * 
     * @param sourceClass 原始类（非空）
     * @param interfaces 代理对象需要实现的接口数组（可为空）
     * @param executionInterceptor 执行拦截器（非空）
     * @return 生成的代理对象
     * @throws UnsupportedOperationException 当没有工厂支持代理该类时抛出
     */
    @Override
    public Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces,
            ExecutionInterceptor executionInterceptor) {
        for (ProxyFactory proxy : this) {
            if (proxy.canProxy(sourceClass)) {
                return proxy.getProxy(sourceClass, interfaces, executionInterceptor);
            }
        }
        throw new UnsupportedOperationException("No proxy factory supports class: " + sourceClass.getName());
    }

    /**
     * 通过类名和类加载器判断是否为代理类（任意代理工厂识别即可）
     * 
     * <p>遍历所有注册的代理工厂，若存在任一工厂识别该类名为代理类，则返回true。
     * 
     * @param proxyClassName 代理类的全限定名（非空）
     * @param classLoader 类加载器（非空）
     * @return 被任一代理工厂识别为代理类则返回true，否则返回false
     * @throws ClassNotFoundException 当类无法被加载时抛出
     */
    @Override
    public boolean isProxy(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException {
        for (ProxyFactory proxy : this) {
            if (proxy.isProxy(proxyClassName, classLoader)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过代理类名和类加载器获取原始类（通过识别该代理类的工厂获取）
     * 
     * <p>遍历所有注册的代理工厂，找到第一个识别该代理类名的工厂，调用其{@link ProxyFactory#getUserClass(String, ClassLoader)}方法。
     * 若未找到对应工厂，则通过类加载器加载并返回该类。
     * 
     * @param proxyClassName 代理类的全限定名（非空）
     * @param classLoader 类加载器（非空）
     * @return 原始业务类
     * @throws ClassNotFoundException 当类无法被加载时抛出
     */
    @Override
    public Class<?> getUserClass(@NonNull String proxyClassName, ClassLoader classLoader)
            throws ClassNotFoundException {
        for (ProxyFactory proxy : this) {
            if (proxy.isProxy(proxyClassName, classLoader)) {
                return proxy.getUserClass(proxyClassName, classLoader);
            }
        }
        return ClassUtils.forName(proxyClassName, classLoader);
    }

    /**
     * 获取代理类的Class对象（使用第一个支持该类的代理工厂）
     * 
     * <p>遍历所有注册的代理工厂，找到第一个支持代理该类的工厂，调用其{@link ProxyFactory#getProxyClass(Class, Class[])}方法。
     * 若未找到支持的工厂，抛出{@link UnsupportedOperationException}。
     * 
     * @param sourceClass 原始类（非空）
     * @param interfaces 代理类需要实现的接口数组（可为空）
     * @return 代理类的Class对象
     * @throws UnsupportedOperationException 当没有工厂支持代理该类时抛出
     */
    @Override
    public Class<?> getProxyClass(@NonNull Class<?> sourceClass, Class<?>[] interfaces) {
        for (ProxyFactory proxy : this) {
            if (proxy.canProxy(sourceClass)) {
                return proxy.getProxyClass(sourceClass, interfaces);
            }
        }
        throw new UnsupportedOperationException("No proxy factory supports class: " + sourceClass.getName());
    }

}