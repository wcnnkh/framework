package run.soeasy.framework.beans;

import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyMapper;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;

/**
 * Bean映射工具类，继承自{@link PropertyMapper<BeanProperty>}，专门用于处理JavaBean之间的属性复制与转换，
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

    /**
     * 单例实例，使用volatile保证多线程环境下的可见性
     */
    public static volatile BeanMapper instance;

    static {
        // 初始化Bean信息工厂
        BEAN_INFO_FACTORY.configure();
    }

    /**
     * 复制源对象到目标对象的属性（指定源类和目标类）
     * 
     * <p>通过源对象和目标对象的类信息解析属性元数据，应用指定的过滤器规则，完成属性复制，
     * 适用于源对象与目标对象类型不一致（但存在继承或接口关系）的场景。
     * 
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @param source 源对象（可为null，null时不执行复制）
     * @param sourceClass 源对象的类（非空，用于解析源属性）
     * @param target 目标对象（需初始化，非空）
     * @param targetClass 目标对象的类（非空，用于解析目标属性）
     * @param filters 属性映射过滤器（可多个，用于过滤或自定义属性映射规则）
     * @return 复制成功返回true，否则返回false
     */
    public static <S, T> boolean copyProperties(S source, @NonNull Class<? extends S> sourceClass, T target,
            @NonNull Class<? extends T> targetClass, @NonNull PropertyMappingFilter... filters) {
        return getInstane().transform(source, TypeDescriptor.valueOf(sourceClass), target,
                TypeDescriptor.valueOf(targetClass), Arrays.asList(filters));
    }

    /**
     * 复制源对象到目标对象的属性（自动推断类型）
     * 
     * <p>基于源对象和目标对象的实际类型解析属性元数据，适用于大多数属性复制场景，
     * 当源对象和目标对象类型明确时，推荐使用此方法简化调用。
     * 
     * @param <S> 源对象类型
     * @param <T> 目标对象类型
     * @param source 源对象（非空）
     * @param target 目标对象（非空）
     * @param filters 属性映射过滤器
     * @return 复制成功返回true，否则返回false
     */
    public static <S, T> boolean copyProperties(@NonNull S source, @NonNull T target,
            @NonNull PropertyMappingFilter... filters) {
        return copyProperties(source, source.getClass(), target, target.getClass(), filters);
    }

    /**
     * 获取单例实例（双重检查锁保证线程安全）
     * 
     * @return 唯一的{@link BeanMapper}实例
     */
    public static BeanMapper getInstane() {
        if (instance == null) {
            synchronized (BeanMapper.class) {
                if (instance == null) {
                    instance = new BeanMapper();
                }
            }
        }
        return instance;
    }

    /**
     * 构造Bean映射器（私有构造，确保单例）
     * 
     * <p>设置对象模板工厂为{@link #BEAN_INFO_FACTORY}，用于解析Bean属性元数据。
     */
    private BeanMapper() {
        getObjectTemplateRegistry().setObjectTemplateFactory(BEAN_INFO_FACTORY);
    }
}