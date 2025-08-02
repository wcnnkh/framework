package run.soeasy.framework.core.execute.reflect;

/**
 * 系统参数名称发现器，继承自{@link ConfigurableParameterNameDiscoverer}，
 * 提供全局唯一的参数名称解析服务，采用单例模式确保在整个应用中使用统一的参数解析策略。
 * <p>
 * 该类在初始化时自动注册{@link NativeParameterNameDiscoverer}作为默认参数解析策略，
 * 支持通过父类的可配置服务机制动态添加其他解析策略，形成"原生优先"的参数名称发现链。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>单例模式：全局唯一实例，确保参数解析策略的一致性</li>
 *   <li>原生优先：默认注册{@link NativeParameterNameDiscoverer}作为基础策略</li>
 *   <li>可扩展配置：继承自{@link ConfigurableParameterNameDiscoverer}，支持添加其他解析策略</li>
 *   <li>线程安全：使用双重检查锁实现单例初始化，确保多线程环境下的安全</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>框架核心组件：作为框架内部参数解析的统一入口</li>
 *   <li>全局配置：需要在整个应用中使用一致参数解析策略的场景</li>
 *   <li>多策略兼容：在原生参数解析基础上，可动态添加其他解析策略</li>
 *   <li>性能敏感：单例模式避免重复创建解析器实例，提升性能</li>
 * </ul>
 *
 * <p><b>实现细节：</b>
 * <ul>
 *   <li>懒汉式单例：首次调用时初始化，避免资源浪费</li>
 *   <li>双重检查锁：在单例初始化时使用同步块，兼顾性能与线程安全</li>
 *   <li>默认策略：优先使用Java 8+原生参数解析，保证现代JDK环境的兼容性</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ConfigurableParameterNameDiscoverer
 * @see NativeParameterNameDiscoverer
 */
public final class SystemParameterNameDiscoverer extends ConfigurableParameterNameDiscoverer {
    /** 私有构造函数，防止外部实例化 */
    private SystemParameterNameDiscoverer() {
        // 注册原生参数名称发现器作为默认策略
        register(new NativeParameterNameDiscoverer());
    }

    /** 单例实例，使用volatile保证可见性 */
    private static volatile SystemParameterNameDiscoverer instance;

    /**
     * 获取系统参数名称发现器的单例实例
     * <p>
     * 实现双重检查锁机制，确保：
     * <ol>
     *   <li>首次调用时才初始化实例（懒加载）</li>
     *   <li>多线程环境下仅创建一个实例</li>
     *   <li>初始化完成后无需同步开销</li>
     * </ol>
     * 
     * @return 系统参数名称发现器实例
     */
    public static SystemParameterNameDiscoverer getInstance() {
        if (instance == null) {
            synchronized (SystemParameterNameDiscoverer.class) {
                if (instance == null) {
                    instance = new SystemParameterNameDiscoverer();
                    // 触发父类的配置服务初始化（加载SPI注册的发现器）
                    instance.configure();
                }
            }
        }
        return instance;
    }
}