package run.soeasy.framework.aop;

import lombok.NonNull;

/**
 * 代理工厂接口，定义了创建和管理代理对象的核心契约，
 * 提供代理类生成、代理对象创建、代理类型判断等功能，
 * 是AOP框架中生成代理对象的核心组件，支持动态代理或静态代理的实现。
 * 
 * <p>该接口封装了代理创建的底层细节，为上层提供统一的代理操作入口，
 * 可根据不同的实现（如JDK动态代理、CGLIB代理等）提供多样化的代理方式，
 * 同时支持判断类是否可被代理、区分代理类与原始类等辅助功能。
 * 
 * @author soeasy.run
 * @see Proxy
 * @see ExecutionInterceptor
 */
public interface ProxyFactory {

    /**
     * 判断指定类是否可被代理
     * 
     * <p>实现类可根据类的特性（如是否为final类、是否实现接口等）决定是否支持代理，
     * 通常final类、基本类型等无法被代理。
     * 
     * @param sourceClass 待判断的原始类（非空）
     * @return 可被代理返回true，否则返回false
     */
    boolean canProxy(@NonNull Class<?> sourceClass);

    /**
     * 创建代理对象，将拦截器与原始类绑定
     * 
     * <p>基于原始类、额外实现的接口和执行拦截器，创建代理对象，
     * 代理对象会实现指定的接口，并在方法调用时触发拦截器的逻辑。
     * 
     * @param sourceClass 原始类（被代理的类，非空）
     * @param interfaces 代理对象需要额外实现的接口数组（可为空）
     * @param executionInterceptor 执行拦截器（用于增强代理对象的方法调用，非空）
     * @return 生成的代理对象（实现{@link Proxy}接口）
     */
    Proxy getProxy(@NonNull Class<?> sourceClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor);

    /**
     * 获取代理类的Class对象
     * 
     * <p>生成代理类的Class对象，该类是原始类的代理，通常继承原始类或实现其接口，
     * 可用于反射操作或类型判断。
     * 
     * @param sourceClass 原始类（非空）
     * @param interfaces 代理类需要实现的接口数组（可为空）
     * @return 代理类的Class对象
     */
    Class<?> getProxyClass(@NonNull Class<?> sourceClass, Class<?>[] interfaces);

    /**
     * 从代理类中获取未被代理的原始类
     * 
     * <p>对于给定的代理类，返回其代理的原始类（被代理的目标类），
     * 用于穿透代理获取真实的业务类类型。
     * 
     * @param proxyClass 代理类（非空）
     * @return 原始业务类（非代理类）
     */
    Class<?> getUserClass(@NonNull Class<?> proxyClass);

    /**
     * 通过代理类名和类加载器获取原始类
     * 
     * <p>根据代理类的全限定名和类加载器，加载代理类并返回其代理的原始类，
     * 适用于仅知道类名的场景。
     * 
     * @param proxyClassName 代理类的全限定名（非空）
     * @param classLoader 用于加载类的类加载器（非空）
     * @return 原始业务类
     * @throws ClassNotFoundException 当代理类或原始类无法被加载时抛出
     */
    Class<?> getUserClass(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException;

    /**
     * 判断指定类是否为代理类
     * 
     * <p>检查给定的Class对象是否是由当前工厂生成的代理类。
     * 
     * @param proxyClass 待判断的类（可为空，为空时返回false）
     * @return 是代理类返回true，否则返回false
     */
    boolean isProxy(Class<?> proxyClass);

    /**
     * 通过类名和类加载器判断是否为代理类
     * 
     * <p>根据类名加载类，并判断该类是否为代理类。
     * 
     * @param proxyClassName 类的全限定名（非空）
     * @param classLoader 类加载器（非空）
     * @return 是代理类返回true，否则返回false
     * @throws ClassNotFoundException 当类无法被加载时抛出
     */
    boolean isProxy(@NonNull String proxyClassName, ClassLoader classLoader) throws ClassNotFoundException;
}