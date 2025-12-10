package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;

import lombok.NonNull;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;

/**
 * 可配置参数名称发现器，继承自{@link ConfigurableServices}并实现{@link ParameterNameDiscoverer}接口，
 * 支持注册多个参数名称发现器实现，并按顺序调用以获取可执行元素的参数名称。
 * <p>
 * 该类采用责任链模式，依次尝试每个注册的发现器，返回第一个成功获取参数名称的结果， 实现了参数名称解析的可插拔性和策略 fallback
 * 机制，适用于需要兼容多种参数解析方式的场景。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多策略支持：可注册多个{@link ParameterNameDiscoverer}实现</li>
 * <li>责任链模式：按注册顺序依次尝试，返回首个有效结果</li>
 * <li>可配置扩展：通过SPI机制动态加载发现器实现</li>
 * <li>策略 fallback：当前策略失败时自动尝试下一个策略</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>多JDK版本兼容：同时支持Java 8+原生参数名和旧版调试信息解析</li>
 * <li>可扩展参数绑定：允许插件自定义参数名解析策略</li>
 * <li>复杂项目适配：混合使用不同字节码增强工具的项目</li>
 * <li>动态策略切换：根据运行时环境动态选择最优解析策略</li>
 * </ul>
 *
 * <p>
 * <b>解析顺序：</b>
 * <ol>
 * <li>优先使用显式注册的发现器（通过{@link #register(Object)}）</li>
 * <li>其次使用SPI机制加载的发现器（META-INF/services下的配置）</li>
 * <li>最终返回首个成功解析参数名的结果</li>
 * </ol>
 *
 * @author soeasy.run
 * @see ParameterNameDiscoverer
 * @see ConfigurableServices
 * @see NativeParameterNameDiscoverer
 */
public class ConfigurableParameterNameDiscoverer extends ConfigurableServices<ParameterNameDiscoverer>
		implements ParameterNameDiscoverer {

	/**
	 * 构造函数，初始化可配置参数名称发现器
	 * <p>
	 * 自动设置服务类为{@link ParameterNameDiscoverer}，以便通过SPI机制加载实现类
	 */
	public ConfigurableParameterNameDiscoverer() {
		super(ServiceComparator.defaultServiceComparator());
	}

	/**
	 * 按顺序调用注册的发现器获取参数名称
	 * <p>
	 * 该方法遍历所有注册的{@link ParameterNameDiscoverer}，返回第一个非null的结果：
	 * <ul>
	 * <li>若所有发现器都返回null，则返回null</li>
	 * <li>发现器调用顺序与注册顺序一致</li>
	 * <li>支持通过{@link #register(Object)}动态添加发现器</li>
	 * </ul>
	 * 
	 * @param executable 可执行元素（Method/Constructor），不可为null
	 * @return 参数字符串数组，所有发现器都失败时返回null
	 */
	@Override
	public String[] getParameterNames(@NonNull Executable executable) {
		return map((e) -> e.getParameterNames(executable)).filter((e) -> e != null).first();
	}
}