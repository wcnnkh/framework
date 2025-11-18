package run.soeasy.framework.beans;

import java.util.Arrays;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.property.PropertyMappingFilter;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.transform.property.TypedProperties;

/**
 * Bean 操作工具类：基于 {@link BeanMapper} 封装 Bean 属性核心操作，提供「属性复制、属性提取、属性模板获取」三大核心能力，
 * 设计目标是「简化 Bean 操作、统一属性映射规则、支持灵活扩展」，适用于日常开发中 Bean 之间的属性转换、属性元数据提取场景。
 * <p>
 * 核心特性：
 * 1. 线程安全：通过双重检查锁单例模式初始化 {@link BeanMapper}，保证多线程环境下实例唯一性；
 * 2. 灵活扩展：支持自定义 {@link PropertyMappingFilter} 过滤属性或自定义映射规则；
 * 3. 类型适配：支持显式指定源/目标类（应对多态场景）和自动推断类型（简化常规调用）；
 * 4. 元数据支持：提供属性模板（{@link PropertyTemplate}）和带类型信息的属性集合（{@link TypedProperties}）提取能力。
 * <p>
 * 依赖说明：
 * - 核心依赖：{@link BeanMapper}（属性映射核心实现，所有操作最终委托其执行）；
 * - 扩展依赖：{@link PropertyMappingFilter}（属性映射过滤/自定义）、{@link TypeDescriptor}（类型描述）；
 * - 元数据依赖：{@link TypedProperties}（带类型的属性集合）、{@link PropertyTemplate}（属性模板）。
 *
 * @author soeasy.run
 * @see BeanMapper 属性映射核心实现类（具体执行策略由其定义）
 * @see TypedProperties 带类型信息的属性集合
 * @see PropertyTemplate Bean 属性元数据模板
 */
@UtilityClass
public class BeanUtils {
    /**
     * {@link BeanMapper} 单例实例：采用 volatile 修饰保证多线程环境下的可见性，
     * 首次调用 {@link #getBeanMapper()} 时通过双重检查锁初始化，全局唯一。
     */
    public static volatile BeanMapper beanMapper;

    /**
     * 属性复制（显式指定源/目标类）：适用于需精确控制源/目标类型解析的场景（如多态、接口适配）
     * <p>
     * 核心逻辑：将属性复制请求委托给 {@link BeanMapper}，
     * 传入指定的源类/目标类、属性过滤器，具体复制策略（如空值处理、属性匹配规则）由 {@link BeanMapper} 实现类定义。
     *
     * @param <S>         源对象泛型类型
     * @param <T>         目标对象泛型类型
     * @param source      源对象（属性数据来源，具体支持的空值处理逻辑由 {@link BeanMapper} 定义）
     * @param sourceClass 源对象的类对象（非空，用于 {@link BeanMapper} 解析源属性元数据）
     * @param target      目标对象（属性数据接收方，必须为已初始化实例，具体赋值规则由 {@link BeanMapper} 定义）
     * @param targetClass 目标对象的类对象（非空，用于 {@link BeanMapper} 解析目标属性元数据）
     * @param filters     属性映射过滤器（可变参数，用于对属性映射过程进行过滤或自定义处理，支持多过滤器组合）
     * @return 属性复制操作的执行结果标识，具体结果含义由 {@link BeanMapper#transform} 定义
     * @throws NullPointerException 若 sourceClass、targetClass、target 或 filters 为 null
     */
    public static <S, T> boolean copyProperties(S source, @NonNull Class<? extends S> sourceClass, T target,
            @NonNull Class<? extends T> targetClass, @NonNull PropertyMappingFilter... filters) {
        return getBeanMapper().transform(source, TypeDescriptor.valueOf(sourceClass), target,
                TypeDescriptor.valueOf(targetClass), Arrays.asList(filters));
    }

    /**
     * 属性复制（自动推断类型）：适用于源/目标类型明确、无需手动指定类对象的常规场景
     * <p>
     * 简化特性：自动通过源对象、目标对象的实际类型生成 {@link TypeDescriptor}，内部委托 {@link #copyProperties(Object, Class, Object, Class, PropertyMappingFilter...)} 执行，
     * 具体复制策略由 {@link BeanMapper} 实现类定义，减少手动指定类对象的冗余代码。
     *
     * @param <S>     源对象泛型类型
     * @param <T>     目标对象泛型类型
     * @param source  源对象（属性数据来源，具体支持的空值处理逻辑由 {@link BeanMapper} 定义）
     * @param target  目标对象（属性数据接收方，必须为已初始化实例，具体赋值规则由 {@link BeanMapper} 定义）
     * @param filters 属性映射过滤器（可变参数，用于对属性映射过程进行过滤或自定义处理）
     * @return 属性复制操作的执行结果标识，具体结果含义由 {@link BeanMapper#transform} 定义
     * @throws NullPointerException 若 source、target 或 filters 为 null
     */
    public static <S, T> boolean copyProperties(@NonNull S source, @NonNull T target,
            @NonNull PropertyMappingFilter... filters) {
        return copyProperties(source, source.getClass(), target, target.getClass(), filters);
    }

    /**
     * 获取 {@link BeanMapper} 单例实例：采用双重检查锁（DCL）模式保证线程安全和懒加载
     * <p>
     * 初始化逻辑：首次调用时初始化实例，后续调用直接返回已创建的实例，确保全局唯一，避免重复初始化开销。
     *
     * @return 全局唯一的 {@link BeanMapper} 实例（非空）
     */
    public static BeanMapper getBeanMapper() {
        if (beanMapper == null) {
            synchronized (BeanMapper.class) {
                if (beanMapper == null) {
                    beanMapper = new BeanMapper();
                }
            }
        }
        return beanMapper;
    }

    /**
     * 提取 Bean 的带类型属性集合（自动推断类型）：适用于无需自定义类型描述的常规场景
     * <p>
     * 核心逻辑：将属性提取请求委托给 {@link BeanMapper#getMapping(Object, TypeDescriptor)}，
     * 自动通过 Bean 实例生成 {@link TypeDescriptor}，具体属性提取规则由 {@link BeanMapper} 实现类定义。
     *
     * @param bean 待提取属性的 Bean 对象（非空，具体支持的对象类型由 {@link BeanMapper} 定义）
     * @return 带类型信息的属性集合 {@link TypedProperties}，具体返回格式由 {@link BeanMapper#getMapping} 定义
     * @throws NullPointerException 若 bean 为 null
     * @see TypedProperties 带类型信息的属性集合（支持类型安全的属性取值）
     */
    public static TypedProperties getProperties(@NonNull Object bean) {
        return getProperties(bean, TypeDescriptor.forObject(bean));
    }

    /**
     * 提取 Bean 的带类型属性集合（自定义类型描述）：适用于泛型 Bean、多态 Bean 等需要精确类型描述的场景
     * <p>
     * 核心逻辑：将属性提取请求委托给 {@link BeanMapper#getMapping(Object, TypeDescriptor)}，
     * 支持手动指定 {@link TypeDescriptor} 以确保属性元数据解析的准确性，具体提取规则由 {@link BeanMapper} 实现类定义。
     *
     * @param bean           待提取属性的 Bean 对象（具体支持的空值处理、对象类型由 {@link BeanMapper} 定义）
     * @param typeDescriptor 类型描述器（非空，用于 {@link BeanMapper} 精确解析属性元数据，支持泛型、参数化类型）
     * @return 带类型信息的属性集合 {@link TypedProperties}，具体返回格式由 {@link BeanMapper#getMapping} 定义
     * @throws NullPointerException 若 typeDescriptor 为 null
     * @see TypeDescriptor 类型描述器（封装类类型、泛型信息等）
     */
    public static TypedProperties getProperties(Object bean, @NonNull TypeDescriptor typeDescriptor) {
        return getBeanMapper().getMapping(bean, typeDescriptor);
    }

    /**
     * 获取 Bean 的属性模板：封装 Bean 的属性元数据（如属性名、类型、访问器等），适用于重复属性操作场景
     * <p>
     * 核心逻辑：将属性模板获取请求委托给 {@link BeanMapper#getObjectTemplate(Class)}，
     * 具体元数据解析规则、缓存策略由 {@link BeanMapper} 实现类定义。
     *
     * @param beanClass Bean 的类对象（非空，具体支持的类类型由 {@link BeanMapper} 定义）
     * @return Bean 的属性模板 {@link PropertyTemplate}，具体模板内容由 {@link BeanMapper#getObjectTemplate} 定义
     * @throws NullPointerException 若 beanClass 为 null
     * @see PropertyTemplate 属性模板（缓存 Bean 的属性元数据，提升重复操作性能）
     * @see BeanProperty 单个属性的元数据（包含属性名、类型、getter/setter 等）
     */
    public static PropertyTemplate<BeanProperty> getTemplate(@NonNull Class<?> beanClass) {
        return getBeanMapper().getObjectTemplate(beanClass);
    }
}