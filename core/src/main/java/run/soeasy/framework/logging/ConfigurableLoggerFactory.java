package run.soeasy.framework.logging;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.spi.Configurable;
import run.soeasy.framework.core.spi.ServiceDiscoverer;

/**
 * 可配置的日志工厂实现类，继承自{@link LoggerRegistry}并实现{@link Configurable}接口，
 * 提供动态配置日志工厂和管理日志器实例的功能，支持运行时切换日志实现。
 * 
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>动态配置：支持通过{@link #setLoggerFactory(LoggerFactory)}动态切换日志工厂</li>
 * <li>日志器注册：继承{@link LoggerRegistry}实现日志器缓存和管理</li>
 * <li>自动重加载：调用{@link #reload()}时更新所有日志器引用</li>
 * <li>SPI支持：通过{@link #configure(ServiceDiscoverer)}自动发现日志工厂实现</li>
 * </ul>
 * 
 * <p>
 * <b>线程安全说明：</b>
 * <ul>
 * <li>日志工厂引用使用volatile保证可见性</li>
 * <li>日志器获取和注册使用synchronized确保线程安全</li>
 * <li>重加载操作通过同步块保证一致性</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see LoggerRegistry
 * @see Configurable
 * @see LoggerFactory
 */
@Getter
public class ConfigurableLoggerFactory extends LoggerRegistry implements Configurable {
	/** 日志工厂实例（volatile保证可见性），默认使用JdkLoggerFactory */
	@NonNull
	private volatile LoggerFactory loggerFactory = new JdkLoggerFactory();

	/**
	 * 获取指定名称的日志器（线程安全实现）。
	 * <p>
	 * 先从缓存中获取，不存在时通过日志工厂创建并注册， 使用双重检查锁定避免并发问题。
	 * 
	 * @param name 日志器名称（如类全限定名）
	 * @return 日志器实例，不可为null
	 */
	@Override
	public Logger getLogger(String name) {
		Logger logger = super.getLogger(name);
		if (logger == null) {
			synchronized (this) {
				logger = super.getLogger(name);
				if (logger == null) {
					logger = loggerFactory.getLogger(name);
					if (logger != null) {
						logger = setLogger(name, logger);
					}
				}
			}
		}
		return logger;
	}

	@Override
	protected synchronized void updateRegistry() {
		getLoggers().forEach((facadeLogger) -> {
			Logger logger = loggerFactory.getLogger(facadeLogger.getName());
			if (logger != null) {
				facadeLogger.setSource(logger);
			}
		});
		super.updateRegistry();
	}

	/**
	 * 设置日志工厂并触发重加载。
	 * <p>
	 * 同步更新日志工厂引用，并调用{@link #reload()}更新所有日志器， 确保配置变更立即生效。
	 * 
	 * @param loggerFactory 新日志工厂，不可为null
	 */
	public void setLoggerFactory(@NonNull LoggerFactory loggerFactory) {
		synchronized (this) {
			if (this.loggerFactory == loggerFactory) {
				return;
			}
			this.loggerFactory = loggerFactory;
			updateRegistry();
		}
	}

	/**
	 * 通过提供者工厂配置日志工厂（实现Configurable接口）。
	 * <p>
	 * 从提供者工厂获取LoggerFactory实例，存在时设置为当前工厂， 支持通过SPI机制自动发现日志工厂实现。
	 * 
	 * @param discovery 提供者工厂
	 * @return 配置结果（成功/失败）
	 */
	@Override
	public Operation configure(@NonNull ServiceDiscoverer discovery) {
		try {
			LoggerFactory loggerFactory = discovery.getServices(LoggerFactory.class).first();
			if (loggerFactory != null) {
				setLoggerFactory(loggerFactory);
			}
			return Operation.SUCCESS;
		} catch (Throwable e) {
			return Operation.failure(e);
		}
	}
}