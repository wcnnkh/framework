package run.soeasy.framework.io.resolver;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.io.Resource;

/**
 * 可配置的属性解析器容器，继承自{@link ConfigurableServices}并实现{@link PropertiesResolver}接口，
 * 提供基于SPI机制的属性解析器服务发现和链式解析功能。
 * 
 * <p>该类维护一个{@link PropertiesResolver}服务集合，当处理资源时会按顺序尝试每个解析器，
 * 直到找到能处理该资源的解析器，实现可扩展的属性文件解析能力。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>SPI服务发现：通过{@link run.soeasy.framework.core.spi.Services}自动加载解析器实现</li>
 *   <li>链式解析机制：按顺序尝试所有注册的解析器，直到找到匹配项</li>
 *   <li>异常封装：当无解析器支持时抛出带资源描述的UnsupportedOperationException</li>
 *   <li>空安全处理：资源为null或不存在时直接返回，避免NPE</li>
 * </ul>
 * 
 * <p><b>扩展方式：</b>
 * <ul>
 *   <li>实现{@link PropertiesResolver}接口并添加SPI服务配置</li>
 *   <li>通过{@link #register(PropertiesResolver)}手动注册解析器</li>
 *   <li>继承此类并重写服务加载逻辑（如指定类路径）</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see PropertiesResolver
 * @see ConfigurableServices
 * @see run.soeasy.framework.core.spi.Services
 */
public class ConfigurablePropertiesResolver extends ConfigurableServices<PropertiesResolver>
        implements PropertiesResolver {

    /**
     * 构造函数，设置服务类为{@link PropertiesResolver}。
     * <p>
     * 自动通过SPI机制发现并加载所有{@link PropertiesResolver}实现类。
     */
    public ConfigurablePropertiesResolver() {
        setServiceClass(PropertiesResolver.class);
    }

    /**
     * 判断资源是否可被任意注册的解析器处理。
     * <p>
     * 按顺序遍历所有解析器，返回第一个能处理该资源的解析器的判断结果。
     * 
     * @param resource 待检测资源
     * @return true表示至少有一个解析器支持该资源
     */
    @Override
    public boolean canResolveProperties(Resource resource) {
        for (PropertiesResolver resolver : this) {
            if (resolver.canResolveProperties(resource)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从资源解析属性到Properties对象。
     * <p>
     * 按顺序遍历所有解析器，找到能处理该资源的解析器并调用其解析方法，
     * 若没有解析器支持则抛出UnsupportedOperationException。
     * 
     * @param properties 目标Properties对象
     * @param resource   源资源
     * @throws IOException                   解析过程中发生IO错误
     * @throws InvalidPropertiesFormatException 资源格式错误
     * @throws UnsupportedOperationException 无解析器支持该资源
     */
    @Override
    public void resolveProperties(Properties properties, Resource resource)
            throws IOException, InvalidPropertiesFormatException {
        if (resource == null || !resource.exists()) {
            return;
        }

        for (PropertiesResolver resolver : this) {
            if (resolver.canResolveProperties(resource)) {
                resolver.resolveProperties(properties, resource);
                return;
            }
        }
        throw new UnsupportedOperationException(resource.getDescription());
    }

    /**
     * 将Properties对象持久化到资源。
     * <p>
     * 按顺序遍历所有解析器，找到能处理该资源的解析器并调用其持久化方法，
     * 若没有解析器支持则抛出UnsupportedOperationException。
     * 
     * @param properties 源Properties对象
     * @param resource   目标资源
     * @throws IOException 持久化过程中发生IO错误
     * @throws UnsupportedOperationException 无解析器支持该资源
     */
    @Override
    public void persistenceProperties(Properties properties, Resource resource) throws IOException {
        if (resource == null || !resource.exists()) {
            return;
        }

        for (PropertiesResolver resolver : this) {
            if (resolver.canResolveProperties(resource)) {
                resolver.persistenceProperties(properties, resource);
                return;
            }
        }
        throw new UnsupportedOperationException(resource.getDescription());
    }
}