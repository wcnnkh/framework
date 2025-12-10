package run.soeasy.framework.logging;

import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.exchange.Operation;

/**
 * 日志管理工具类，提供日志系统的统一入口和管理功能，
 * 封装日志器的获取、配置加载和状态检查等核心操作，确保日志功能的便捷使用和统一管理。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>单例访问：通过静态方法提供日志器获取，避免实例化</li>
 *   <li>配置管理：维护日志工厂的配置状态和重新加载功能</li>
 *   <li>线程安全：关键操作使用同步机制保证并发环境下的一致性</li>
 *   <li>便捷获取：支持通过类名或自定义名称获取对应日志器</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>应用启动：初始化日志配置并获取根日志器</li>
 *   <li>日志获取：在各组件中通过{@link #getLogger(Class)}获取专属日志器</li>
 *   <li>配置更新：运行时通过{@link #reloadReceipt()}重新加载日志配置</li>
 * </ul>
 * 
 * @author soeasy.run
 */
@UtilityClass
public class LogManager {
    /** 可配置的日志工厂实例，用于创建日志器 */
    private static final ConfigurableLoggerFactory CONFIGURABLE = new ConfigurableLoggerFactory();
    /** 配置状态收据，记录日志工厂配置是否成功 */
    private static volatile Operation receipt;

    /**
     * 获取日志工厂配置状态。
     * <p>
     * 双重检查锁定确保线程安全，首次调用时触发配置加载，
     * 后续调用直接返回缓存的配置状态。
     * 
     * @return 配置状态收据（成功/失败）
     */
    public static Operation getReceipt() {
        if (receipt == null) {
            synchronized (CONFIGURABLE) {
                if (receipt == null) {
                    receipt = reloadReceipt();
                }
            }
        }
        return receipt;
    }

    /**
     * 重新加载日志工厂配置并更新状态。
     * <p>
     * 同步操作确保配置加载的原子性，
     * 返回最新的配置状态并更新全局收据。
     * 
     * @return 重新加载后的配置状态收据
     */
    public static Operation reloadReceipt() {
        synchronized (CONFIGURABLE) {
        	Operation receipt = CONFIGURABLE.configure();
            LogManager.receipt = receipt;
            return receipt;
        }
    }

    /**
     * 获取可配置的日志工厂实例。
     * 
     * @return 配置日志工厂，不可为null
     */
    public static ConfigurableLoggerFactory getConfigurable() {
        return CONFIGURABLE;
    }

    /**
     * 根据类名获取对应的日志器。
     * <p>
     * 自动使用类的全限定名作为日志器名称，
     * 适用于按类划分日志的场景。
     * 
     * @param clazz 类对象
     * @return 对应的Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * 根据名称获取对应的日志器。
     * <p>
     * 先检查配置状态，失败时尝试重新加载，
     * 确保获取到有效的日志器实例。
     * 
     * @param name 日志器名称
     * @return 对应的Logger实例，不可为null
     */
    public static Logger getLogger(String name) {
    	Operation receipt = getReceipt();
        if (!receipt.isSuccess()) {
            reloadReceipt();
        }
        return CONFIGURABLE.getLogger(name);
    }
}