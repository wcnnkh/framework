package run.soeasy.framework.core;

import run.soeasy.framework.core.type.ClassUtils;

/**
 * 类加载器访问器接口，定义类加载器的获取和配置标准，
 * 继承自{@link ClassLoaderProvider}以提供类加载器获取功能，并扩展了配置能力。
 * 该接口允许动态设置类加载器获取策略，支持通过类、类加载器实例或自定义提供者进行配置，
 * 并在未设置时提供默认回退机制。
 *
 * <p>核心特性：
 * <ul>
 *   <li>策略配置：支持通过{@link #setClassLoaderProvider}动态设置类加载器获取策略</li>
 *   <li>多源支持：可通过类{@link #setClassLoader(Class)}、类加载器实例{@link #setClassLoader(ClassLoader)}
 *                或自定义提供者{@link #setClassLoaderProvider}配置获取策略</li>
 *   <li>默认回退：当未设置提供者时，自动使用{@link ClassUtils#getDefaultClassLoader()}作为默认</li>
 *   <li>接口继承：直接继承{@link ClassLoaderProvider}，保持函数式接口特性</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>框架组件需要根据运行时上下文动态切换类加载器</li>
 *   <li>插件系统中不同插件需要使用独立类加载器的场景</li>
 *   <li>测试环境中需要模拟或替换类加载器进行隔离测试</li>
 *   <li>动态加载资源时需要灵活指定类加载器的场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 通过接口配置系统类加载器
 * ClassLoaderAccessor accessor = () -&gt; ClassLoader.getSystemClassLoader();
 * accessor.setClassLoader(MyClass.class); // 切换为MyClass的类加载器
 * </pre>
 *
 * @see ClassLoaderProvider
 * @see ClassUtils
 * @see DefaultClassLoaderAccessor
 */
public interface ClassLoaderAccessor extends ClassLoaderProvider {

    /**
     * 获取当前配置的类加载器提供者。
     * <p>
     * 该方法返回用于获取类加载器的提供者实例，
     * 若未设置则返回null，此时{@link #getClassLoader()}将使用默认回退机制。
     *
     * @return 类加载器提供者，可能为null
     */
    ClassLoaderProvider getClassLoaderProvider();

    /**
     * 设置类加载器提供者，用于动态配置类加载器获取策略。
     * <p>
     * 若传入null，{@link #getClassLoader()}将使用默认回退机制。
     *
     * @param classLoaderProvider 新的类加载器提供者，可为null
     */
    void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider);

    /**
     * 通过类加载器实例设置类加载器获取策略。
     * <p>
     * 该方法将类加载器实例包装为提供者，等价于：
     * <pre class="code">
     * setClassLoaderProvider(classLoader == null ? null : () -&gt; classLoader);
     * </pre>
     *
     * @param classLoader 要使用的类加载器实例，可为null
     */
    default void setClassLoader(ClassLoader classLoader) {
        setClassLoaderProvider(classLoader == null ? null : (() -> classLoader));
    }

    /**
     * 通过类设置类加载器获取策略，使用该类的类加载器。
     * <p>
     * 该方法将类的类加载器包装为提供者，等价于：
     * <pre class="code">
     * setClassLoaderProvider(clazz == null ? null : () -&gt; clazz.getClassLoader());
     * </pre>
     *
     * @param clazz 用于获取类加载器的类，可为null
     */
    default void setClassLoader(Class<?> clazz) {
        setClassLoaderProvider(clazz == null ? null : (() -> clazz.getClassLoader()));
    }

    /**
     * 获取类加载器实例，遵循以下优先级：
     * <ol>
     *   <li>若配置了{@link #getClassLoaderProvider()}，使用其获取类加载器</li>
     *   <li>否则，使用{@link ClassUtils#getDefaultClassLoader()}作为默认</li>
     * </ol>
     *
     * @return 类加载器实例，不会为null
     */
    @Override
    default ClassLoader getClassLoader() {
        ClassLoaderProvider classLoaderProvider = getClassLoaderProvider();
        return classLoaderProvider == null ? ClassUtils.getDefaultClassLoader() : classLoaderProvider.getClassLoader();
    }
}