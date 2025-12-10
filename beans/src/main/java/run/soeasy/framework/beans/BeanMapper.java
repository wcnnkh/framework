package run.soeasy.framework.beans;

import lombok.Getter;
import run.soeasy.framework.core.mapping.property.PropertyMapper;

/**
 * Bean映射工具类，继承自{@link PropertyMapper}，专门用于处理JavaBean之间的属性复制与转换，
 * 封装了基于{@link BeanInfoFactory}的属性元数据解析逻辑，提供静态方法简化Bean属性复制操作，
 * 支持自定义属性映射过滤器，适配不同的属性复制规则。
 * 
 * <p>该类采用单例模式，通过{@link ConfigurableBeanInfoFactory}获取Bean元信息，实现源对象到目标对象的属性自动映射，
 * 适用于DTO与实体类转换、对象属性拷贝等场景。
 * 
 * @author soeasy.run
 * @see PropertyMapper
 * @see BeanProperty
 * @see ConfigurableBeanInfoFactory
 */
@Getter
public class BeanMapper extends PropertyMapper<BeanProperty> {

    /**
     * 可配置的Bean信息工厂，用于解析Bean类的元数据（属性、方法等）
     */
    private static final ConfigurableBeanInfoFactory BEAN_INFO_FACTORY = new ConfigurableBeanInfoFactory();

    static {
        // 初始化Bean信息工厂
        BEAN_INFO_FACTORY.configure();
    }

    /**
     * 构造Bean映射器（私有构造，确保单例）
     * 
     * <p>设置对象模板工厂为{@link #BEAN_INFO_FACTORY}，用于解析Bean属性元数据。
     */
    public BeanMapper() {
        getObjectTemplateRegistry().setObjectTemplateFactory(BEAN_INFO_FACTORY);
    }
}