package run.soeasy.framework.core;

import run.soeasy.framework.core.type.ClassUtils;

/**
 * 类加载器访问器的默认实现，提供灵活的类加载器获取机制。
 * 该实现支持通过类、类加载器实例或自定义提供者接口设置类加载器获取策略，
 * 并在获取类加载器时提供默认fallback机制。
 *
 * <p>核心特性：
 * <ul>
 *   <li>多源初始化：支持通过类、类加载器实例或提供者接口初始化</li>
 *   <li>延迟加载：仅在调用{@link #getClassLoader()}时实际获取类加载器</li>
 *   <li>默认回退：当未设置提供者时，自动使用{@link ClassUtils#getDefaultClassLoader()}</li>
 *   <li>动态切换：运行时可通过setter方法动态切换类加载器获取策略</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>框架组件需要根据上下文动态获取类加载器</li>
 *   <li>插件系统中不同插件使用独立类加载器的场景</li>
 *   <li>需要隔离测试环境类加载器的单元测试</li>
 *   <li>动态加载资源时需要指定特定类加载器的场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 通过类初始化（使用该类的类加载器）
 * ClassLoaderAccessor accessor1 = new DefaultClassLoaderAccessor(MyClass.class);
 * 
 * // 通过类加载器实例初始化
 * ClassLoaderAccessor accessor2 = new DefaultClassLoaderAccessor(Thread.currentThread().getContextClassLoader());
 * 
 * // 通过提供者接口初始化
 * ClassLoaderAccessor accessor3 = new DefaultClassLoaderAccessor(() -> ClassLoader.getSystemClassLoader());
 * </pre>
 *
 * @see ClassLoaderAccessor
 * @see ClassLoaderProvider
 * @see ClassUtils
 */
public class DefaultClassLoaderAccessor implements ClassLoaderAccessor {
    
    /**
     * 类加载器提供者，用于动态获取类加载器实例。
     * 当该字段为null时，将使用{@link ClassUtils#getDefaultClassLoader()}作为回退。
     */
    private ClassLoaderProvider classLoaderProvider;

    /**
     * 无参构造函数，初始化时未设置类加载器提供者，
     * 后续需通过setter方法设置或使用默认回退机制。
     */
    public DefaultClassLoaderAccessor() {
    }

    /**
     * 通过指定类初始化，使用该类的类加载器作为获取策略。
     * <p>
     * 若{@code clazz}为null，类加载器提供者将设置为null，
     * 获取类加载器时使用默认回退机制。
     *
     * @param clazz 用于获取类加载器的类
     */
    public DefaultClassLoaderAccessor(Class<?> clazz) {
        this.classLoaderProvider = clazz == null ? null : (() -> clazz.getClassLoader());
    }

    /**
     * 通过指定类加载器实例初始化，直接使用该实例作为获取策略。
     * <p>
     * 若{@code classLoader}为null，类加载器提供者将设置为null，
     * 获取类加载器时使用默认回退机制。
     *
     * @param classLoader 要使用的类加载器实例
     */
    public DefaultClassLoaderAccessor(ClassLoader classLoader) {
        this.classLoaderProvider = classLoader == null ? null : (() -> classLoader);
    }

    /**
     * 通过类加载器提供者接口初始化，使用自定义的获取策略。
     *
     * @param classLoaderProvider 类加载器提供者，可为null
     */
    public DefaultClassLoaderAccessor(ClassLoaderProvider classLoaderProvider) {
        this.classLoaderProvider = classLoaderProvider;
    }

    /**
     * 获取当前配置的类加载器提供者。
     *
     * @return 类加载器提供者，可能为null
     */
    public ClassLoaderProvider getClassLoaderProvider() {
        return classLoaderProvider;
    }

    /**
     * 设置类加载器提供者。
     *
     * @param classLoaderProvider 新的类加载器提供者，可为null
     */
    public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
        this.classLoaderProvider = classLoaderProvider;
    }

    /**
     * 通过指定类设置类加载器提供者，使用该类的类加载器作为获取策略。
     * <p>
     * 若{@code clazz}为null，类加载器提供者将设置为null，
     * 获取类加载器时使用默认回退机制。
     *
     * @param clazz 用于获取类加载器的类
     */
    public void setClassLoaderProvider(Class<?> clazz) {
        this.classLoaderProvider = clazz == null ? null : (() -> clazz.getClassLoader());
    }

    /**
     * 通过指定类加载器实例设置类加载器提供者，直接使用该实例作为获取策略。
     * <p>
     * 若{@code classLoader}为null，类加载器提供者将设置为null，
     * 获取类加载器时使用默认回退机制。
     *
     * @param classLoader 要使用的类加载器实例
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoaderProvider = classLoader == null ? null : (() -> classLoader);
    }

    /**
     * 获取类加载器实例，遵循以下优先级：
     * <ol>
     *   <li>若设置了{@link #classLoaderProvider}，使用其获取类加载器</li>
     *   <li>否则，使用{@link ClassUtils#getDefaultClassLoader()}</li>
     * </ol>
     *
     * @return 类加载器实例，不会为null
     */
    public ClassLoader getClassLoader() {
        return classLoaderProvider == null ? ClassUtils.getDefaultClassLoader() : classLoaderProvider.getClassLoader();
    }
}