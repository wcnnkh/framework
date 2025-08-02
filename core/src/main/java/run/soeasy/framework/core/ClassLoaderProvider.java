package run.soeasy.framework.core;

import java.lang.ClassLoader;

/**
 * 类加载器提供者接口，定义获取类加载器的标准行为。
 * 该函数式接口仅包含一个无参数方法，用于抽象类加载器的获取逻辑，
 * 允许在框架中以统一方式获取不同上下文的类加载器。
 *
 * <p>核心特性：
 * <ul>
 *   <li>函数式设计：可作为lambda表达式或方法引用的目标</li>
 *   <li>上下文抽象：屏蔽不同环境下类加载器的获取差异</li>
 *   <li>延迟加载：按需获取类加载器，避免过早初始化</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>资源加载：获取类路径资源时动态指定类加载器</li>
 *   <li>类加载：需要自定义类加载逻辑的框架组件</li>
 *   <li>插件系统：支持不同插件使用独立类加载器</li>
 *   <li>测试环境：模拟或替换类加载器进行隔离测试</li>
 * </ul>
 *
 * <p>典型实现：
 * <pre class="code">
 * // 使用当前线程类加载器
 * ClassLoaderProvider threadClassLoader = Thread::getContextClassLoader;
 * 
 * // 使用系统类加载器
 * ClassLoaderProvider systemClassLoader = ClassLoader::getSystemClassLoader;
 * </pre>
 *
 * @see ClassLoader
 * @see Thread#getContextClassLoader()
 * @see ClassLoader#getSystemClassLoader()
 */
@FunctionalInterface
public interface ClassLoaderProvider {
    
    /**
     * 获取类加载器实例。
     * <p>
     * 该方法应返回有效的类加载器，常见实现包括：
     * <ul>
     *   <li>当前线程的上下文类加载器</li>
     *   <li>系统类加载器</li>
     *   <li>指定类的类加载器</li>
     * </ul>
     *
     * @return 类加载器实例，不应为null
     * @throws IllegalStateException 若无法获取有效的类加载器（极少发生）
     */
    ClassLoader getClassLoader();
}