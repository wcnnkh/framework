package run.soeasy.framework.aop.jdk;

import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.aop.ExecutionInterceptor;
import run.soeasy.framework.aop.Proxy;
import run.soeasy.framework.aop.ProxyFactories;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.type.ClassUtils;

/**
 * JDK动态代理工厂类，继承自{@link ProxyFactories}，专注于基于JDK反射机制的动态代理实现，
 * 补充父类在JDK代理场景下的适配逻辑，支持接口类的代理创建、代理类识别及原始类还原等功能，
 * 是AOP框架中JDK代理模式的核心工厂实现。
 * 
 * <p>该类通过重写父类方法，针对JDK动态代理的特性（如基于接口、代理类命名规则等）进行定制：
 * 1. 支持对接口类的代理（JDK代理的核心能力）；
 * 2. 识别JDK原生代理类（通过{@link java.lang.reflect.Proxy#isProxyClass(Class)}）；
 * 3. 合并接口数组以确保代理类实现必要的接口；
 * 4. 基于JDK的{@link java.lang.reflect.Proxy}生成代理类和代理对象。
 * 
 * @author soeasy.run
 * @see ProxyFactories
 * @see java.lang.reflect.Proxy
 * @see JdkProxy
 */
public class JdkProxyFactory extends ProxyFactories {

    /**
     * JDK动态代理类的名称前缀（JDK生成的代理类名以此开头）
     */
    public static final String PROXY_NAME_PREFIX = "java.lang.reflect.Proxy";

    /**
     * 判断类是否可被JDK代理（支持接口类或父类可代理的类）
     * 
     * <p>JDK动态代理的核心限制是通常基于接口实现，因此：
     * - 若类是接口（{@link Class#isInterface()}），则可被代理；
     * - 若父类判断可代理，也支持代理（兼容父类的代理逻辑）。
     * 
     * @param clazz 待判断的类（可为空，为空时返回false）
     * @return 可被JDK代理返回true，否则返回false
     */
    @Override
    public boolean canProxy(Class<?> clazz) {
        return super.canProxy(clazz) || (clazz != null && clazz.isInterface());
    }

    /**
     * 判断类是否为JDK动态代理类（结合父类判断和JDK原生代理类识别）
     * 
     * <p>判断逻辑：
     * - 若父类认为是代理类，则返回true；
     * - 否则通过{@link java.lang.reflect.Proxy#isProxyClass(Class)}判断是否为JDK生成的代理类。
     * 
     * @param clazz 待判断的类（可为空，为空时返回false）
     * @return 是JDK代理类返回true，否则返回false
     */
    @Override
    public boolean isProxy(Class<?> clazz) {
        return super.isProxy(clazz) || (clazz != null && java.lang.reflect.Proxy.isProxyClass(clazz));
    }

    /**
     * 合并接口数组，确保代理类实现必要的接口
     * 
     * <p>处理逻辑：
     * 1. 若输入接口数组为空，且源类是接口，则以源类作为唯一接口；
     * 2. 否则合并源类（若为接口）与输入接口数组，自动去重（避免重复实现同一接口）；
     * 3. 返回合并后的接口数组，确保代理类能覆盖所有必要接口。
     * 
     * @param clazz 源类（可能是接口，用于补充接口列表）
     * @param interfaces 额外需要实现的接口数组（可为空）
     * @return 合并去重后的接口数组（非空，至少包含必要接口）
     */
    private final Class<?>[] mergeInterfaces(Class<?> clazz, Class<?>[] interfaces) {
        if (ArrayUtils.isEmpty(interfaces)) {
            if (clazz.isInterface()) {
                return new Class<?>[] { clazz };
            } else {
                return new Class<?>[0];
            }
        } else {
            // 初始化数组，容量为1（源类）+ 输入接口长度
            Class<?>[] array = new Class<?>[1 + interfaces.length];
            int index = 0;
            // 添加源类（若为接口）
            array[index++] = clazz;
            // 添加入口接口，跳过与源类重复的接口
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i] == clazz) {
                    continue;
                }
                array[index++] = interfaces[i];
            }
            // 截取有效长度（去重后可能缩短）
            return Arrays.copyOfRange(array, 0, index);
        }
    }

    /**
     * 创建JDK动态代理对象（优先使用父类逻辑，否则创建{@link JdkProxy}）
     * 
     * <p>处理逻辑：
     * 1. 若父类可代理当前类，调用父类方法创建代理；
     * 2. 否则合并接口数组，通过{@link JdkProxy}创建基于JDK的代理对象，绑定拦截器。
     * 
     * @param clazz 被代理的原始类（非空）
     * @param interfaces 代理类需实现的额外接口（可为空）
     * @param executionInterceptor 代理拦截器（非空，用于增强方法调用）
     * @return JDK动态代理对象（实现{@link Proxy}接口）
     */
    @Override
    public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
        if (super.canProxy(clazz)) {
            return super.getProxy(clazz, interfaces, executionInterceptor);
        }
        // 合并接口并创建JdkProxy实例
        return new JdkProxy(clazz, mergeInterfaces(clazz, interfaces), executionInterceptor);
    }

    /**
     * 获取JDK动态代理类的Class对象（优先使用父类逻辑，否则通过JDK原生方法生成）
     * 
     * <p>处理逻辑：
     * 1. 若父类可处理，调用父类方法获取代理类；
     * 2. 否则通过{@link java.lang.reflect.Proxy#getProxyClass(ClassLoader, Class[])}生成JDK代理类。
     * 
     * @param sourceClass 被代理的原始类（非空）
     * @param interfaces 代理类需实现的接口（可为空）
     * @return JDK代理类的Class对象
     */
    @Override
    public Class<?> getProxyClass(Class<?> sourceClass, Class<?>[] interfaces) {
        if (super.canProxy(sourceClass)) {
            return super.getProxyClass(sourceClass, interfaces);
        }
        // 合并接口并生成JDK代理类
        return java.lang.reflect.Proxy.getProxyClass(
            sourceClass.getClassLoader(),
            mergeInterfaces(sourceClass, interfaces)
        );
    }

    /**
     * 获取JDK代理类对应的原始类（接口）
     * 
     * <p>处理逻辑：
     * 1. 若为父类的代理类，调用父类方法获取原始类；
     * 2. 否则返回代理类实现的第一个接口（JDK代理基于接口，原始类通常为接口）。
     * 
     * @param clazz 代理类（非空）
     * @return 原始接口类（非代理类）
     */
    @Override
    public Class<?> getUserClass(Class<?> clazz) {
        if (super.isProxy(clazz)) {
            return super.getUserClass(clazz);
        }
        // JDK代理类实现的接口中，第一个通常为原始接口
        Class<?>[] interfaces = clazz.getInterfaces();
        return interfaces.length > 0 ? interfaces[0] : clazz;
    }

    /**
     * 通过类名判断是否为JDK代理类（结合父类判断和类名前缀匹配）
     * 
     * <p>判断逻辑：
     * 1. 若父类认为是代理类，返回true；
     * 2. 否则判断类名是否以{@value #PROXY_NAME_PREFIX}开头（JDK代理类的命名特征）。
     * 
     * @param className 类的全限定名（非空）
     * @param classLoader 类加载器（非空）
     * @return 是JDK代理类返回true，否则返回false
     * @throws ClassNotFoundException 类加载失败时抛出
     */
    @Override
    public boolean isProxy(@NonNull String className, ClassLoader classLoader) throws ClassNotFoundException {
        return super.isProxy(className, classLoader) || className.startsWith(PROXY_NAME_PREFIX);
    }

    /**
     * 通过类名获取JDK代理类对应的原始类
     * 
     * <p>处理逻辑：
     * 1. 若为父类的代理类，调用父类方法获取原始类；
     * 2. 否则加载类后通过{@link #getUserClass(Class)}获取原始类。
     * 
     * @param className 代理类的全限定名（非空）
     * @param classLoader 类加载器（非空）
     * @return 原始接口类
     * @throws ClassNotFoundException 类加载失败时抛出
     */
    @Override
    public Class<?> getUserClass(@NonNull String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (super.isProxy(className, classLoader)) {
            return super.getUserClass(className, classLoader);
        }
        // 加载类后获取原始类
        return getUserClass(ClassUtils.forName(className, classLoader));
    }
}