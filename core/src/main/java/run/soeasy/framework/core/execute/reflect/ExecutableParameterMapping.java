package run.soeasy.framework.core.execute.reflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 可执行元素参数模板实现类，实现{@link PropertyMapping}接口，
 * 用于封装可执行元素（Method/Constructor）的参数元数据，提供参数描述符的统一访问和参数名称的动态发现能力。
 * <p>
 * 该类通过反射获取可执行元素的参数信息，并结合{@link ParameterNameDiscoverer}解析参数名称，
 * 将参数转换为{@link PropertyDescriptor}体系，支持参数类型、名称等元数据的延迟加载和缓存，
 * 适用于参数绑定、反射调用、属性映射等需要参数元数据的场景。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>参数元数据封装：将反射参数转换为属性描述符，统一参数与属性的元数据模型</li>
 * <li>名称动态发现：通过{@link ParameterNameDiscoverer}策略解析参数名称，支持多种解析方式</li>
 * <li>延迟加载机制：首次访问时才通过反射获取参数信息，避免不必要的性能开销</li>
 * <li>线程安全缓存：使用双重检查锁实现参数描述符的线程安全加载和缓存</li>
 * <li>可配置策略：支持自定义{@link ParameterNameDiscoverer}实现，灵活适配不同环境</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>参数绑定：将请求参数映射到方法参数（如Web框架、RPC框架）</li>
 * <li>反射调用：动态调用方法时的参数类型校验和转换</li>
 * <li>属性映射：在不同对象的参数间进行值映射（如Bean映射工具）</li>
 * <li>参数校验：基于参数类型和名称实现参数校验逻辑</li>
 * <li>动态代理：生成包含参数元数据的代理方法</li>
 * </ul>
 *
 * @author soeasy.run
 * @see PropertyMapping
 * @see PropertyDescriptor
 * @see ParameterNameDiscoverer
 */
@EqualsAndHashCode(of = "executable")
@ToString(of = "executable")
@RequiredArgsConstructor
public class ExecutableParameterMapping implements PropertyMapping<PropertyDescriptor> {
	/**
	 * 封装的可执行元素（Method/Constructor）
	 */
	@NonNull
	@Getter
	private final Executable executable;

	/**
	 * 参数名称发现器，用于解析参数名称，默认使用系统单例
	 */
	@Getter
	@Setter
	private ParameterNameDiscoverer parameterNameDiscoverer = SystemParameterNameDiscoverer.getInstance();

	/**
	 * 缓存的参数名称数组，延迟初始化
	 */
	private volatile String[] names;

	/**
	 * 缓存的参数描述符数组，延迟初始化
	 */
	private volatile PropertyDescriptor[] parameterDescriptors;

	@Override
	public Streamable<PropertyDescriptor> elements() {
		if (parameterDescriptors == null) {
			synchronized (this) {
				if (parameterDescriptors == null) {
					Parameter[] parameters = executable.getParameters();
					if (parameters.length == 0) {
						parameterDescriptors = PropertyDescriptor.EMPTY_ARRAY;
					} else {
						// 解析参数名称
						names = parameterNameDiscoverer.getParameterNames(executable);

						// 转换为参数描述符并应用名称
						PropertyDescriptor[] array = new PropertyDescriptor[parameters.length];
						for (int i = 0; i < parameters.length; i++) {
							PropertyDescriptor parameterDescriptor = new ExecutableParameterDescriptor(parameters[i]);
							if (names != null && i < names.length && names[i] != null) {
								parameterDescriptor = parameterDescriptor.rename(names[i]);
							}
							array[i] = parameterDescriptor;
						}
						parameterDescriptors = array;
					}
				}
			}
		}
		return Streamable.array(parameterDescriptors);
	}

	@Override
	public PropertyMapping<PropertyDescriptor> reload() {
		ExecutableParameterMapping mapping = new ExecutableParameterMapping(executable);
		mapping.parameterNameDiscoverer = this.parameterNameDiscoverer;
		return mapping;
	}
}