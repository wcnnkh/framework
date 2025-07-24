package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import lombok.NonNull;
import run.soeasy.framework.core.spi.ConfigurableServices;

/**
 * 可配置的Bean信息工厂，继承自{@link ConfigurableServices}并实现{@link BeanInfoFactory}，
 * 支持通过SPI机制整合多个{@link BeanInfoFactory}实现，优先使用已注册的工厂获取{@link BeanInfo}，
 * 若所有注册工厂均无法提供，则默认使用{@link Introspector}的标准内省机制获取。
 * 
 * <p>该类作为BeanInfo获取的统一入口，通过配置化方式扩展Bean信息解析策略，适配不同场景下的Bean元数据解析需求（如自定义注解解析、特殊属性处理等）。
 * 
 * @author soeasy.run
 * @see ConfigurableServices
 * @see BeanInfoFactory
 * @see Introspector
 */
class ConfigurableBeanInfoFactory extends ConfigurableServices<BeanInfoFactory> implements BeanInfoFactory {

    /**
     * 构造可配置的Bean信息工厂，初始化服务类为{@link BeanInfoFactory}，
     * 用于后续通过SPI机制加载该接口的实现类。
     */
    ConfigurableBeanInfoFactory() {
        setServiceClass(BeanInfoFactory.class);
    }

    /**
     * 获取指定Bean类的{@link BeanInfo}（优先使用注册的工厂，否则使用默认内省）
     * 
     * <p>处理流程：
     * 1. 遍历所有已注册的{@link BeanInfoFactory}实例，调用其{@link BeanInfoFactory#getBeanInfo(Class)}方法；
     * 2. 若任一工厂返回非null的{@link BeanInfo}，则直接使用该结果；
     * 3. 若所有工厂均未提供有效结果，则调用{@link #loadBeanInfo(Class)}使用默认内省机制获取。
     * 
     * @param beanClass 目标Bean类（非空）
     * @return 包含Bean元信息的{@link BeanInfo}对象
     * @throws IntrospectionException 当所有工厂及默认机制均无法获取BeanInfo时抛出（如类无法被内省解析）
     */
    @Override
    public BeanInfo getBeanInfo(@NonNull Class<?> beanClass) throws IntrospectionException {
        // 遍历注册的BeanInfoFactory，尝试获取BeanInfo
        for (BeanInfoFactory factory : this) {
            BeanInfo beanInfo = factory.getBeanInfo(beanClass);
            if (beanInfo != null) {
                return beanInfo;
            }
        }
        // 所有工厂均未命中，使用默认加载方式
        return loadBeanInfo(beanClass);
    }

    /**
     * 默认的BeanInfo加载方法，基于Java标准内省机制{@link Introspector}
     * 
     * @param beanClass 目标Bean类
     * @return 标准内省机制解析的{@link BeanInfo}
     * @throws IntrospectionException 当内省过程失败时抛出（如类不符合JavaBean规范）
     */
    protected BeanInfo loadBeanInfo(Class<?> beanClass) throws IntrospectionException {
        return Introspector.getBeanInfo(beanClass);
    }
}