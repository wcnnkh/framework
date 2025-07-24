package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceInjectors;

/**
 * 转换服务核心类，整合转换器注册、管理与执行功能，实现{@link Transformer}接口。
 * <p>
 * 该服务集成了{@link Transformers}转换器集合和{@link TransformerRegistry}类型映射注册表，
 * 提供统一的转换入口，支持按优先级查找转换器并执行转换操作。同时通过{@link ServiceInjectors}
 * 实现转换器的依赖注入，确保组件间的转换器引用一致性。
 * </p>
 *
 * <p><b>核心组件：</b>
 * <ul>
 *   <li>{@link #injectors}：服务注入器，处理转换器的依赖注入</li>
 *   <li>{@link #transformers}：转换器集合，按优先级管理多个转换器</li>
 *   <li>{@link #registry}：类型映射注册表，基于类型映射管理条件转换器</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>线程安全：内部组件未明确线程安全保障，多线程环境需外部同步</li>
 *   <li>转换顺序：先查注册表后查转换器集合，可能导致优先级倒置</li>
 *   <li>依赖注入：注入逻辑可能引发循环依赖（如服务注入自身）</li>
 *   <li>异常处理：转换方法未封装异常，直接抛出转换器异常</li>
 *   <li>类型安全：条件转换器强制类型转换可能引发{@link ClassCastException}</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Transformer
 * @see Transformers
 * @see TransformerRegistry
 */
@Getter
public class TransformationService implements Transformer {
    
    /** 服务注入器，用于处理转换器的依赖注入 */
    private final ServiceInjectors<Transformer> injectors = new ServiceInjectors<>();
    
    /** 转换器集合，按优先级管理多个转换器 */
    private final Transformers transformers = new Transformers();
    
    /** 类型映射注册表，基于类型映射管理条件转换器 */
    private final TransformerRegistry registry = new TransformerRegistry();

    /**
     * 构造转换服务实例
     * <p>
     * 初始化时注册服务注入逻辑：当注入的服务是{@link TransformationService}自身时，
     * 将其转换为{@link TransformerAware}并设置当前服务为转换器，可能存在循环依赖风险。
     */
    public TransformationService() {
        injectors.register((service) -> {
            if (service instanceof TransformationService) {
                TransformerAware transformerAware = (TransformerAware) service;
                transformerAware.setTransformer(this);
            }
            return Registration.SUCCESS;
        });
    }

    /**
     * 注册转换器到服务中
     * <p>
     * 注册逻辑：
     * <ol>
     *   <li>若转换器是{@link ConditionalTransformer}，注册到类型映射注册表</li>
     *   <li>否则注册到转换器集合</li>
     *   <li>注册成功后通过注入器处理依赖注入</li>
     * </ol>
     * 
     * @param transformer 待注册的转换器，不可为null
     * @return 注册句柄，可用于取消注册
     */
    public Registration register(Transformer transformer) {
        Registration registration = transformers instanceof ConditionalTransformer
                ? registry.register((ConditionalTransformer) transformer)
                : transformers.register(transformer);
        if (!registration.isCancelled()) {
            injectors.inject(transformer);
        }
        return registration;
    }

    /**
     * 判断是否支持从源类型到目标类型的转换
     * <p>
     * 检查逻辑：
     * <ol>
     *   <li>先检查类型映射注册表是否支持</li>
     *   <li>再检查转换器集合是否支持</li>
     * </ol>
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若支持转换返回true，否则false
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return registry.canTransform(sourceTypeDescriptor, targetTypeDescriptor)
                || transformers.canTransform(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行对象属性转换操作
     * <p>
     * 转换逻辑：
     * <ol>
     *   <li>若类型映射注册表支持，使用注册表执行转换</li>
     *   <li>否则使用转换器集合按优先级查找并执行转换</li>
     * </ol>
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换成功返回true，否则false
     * @throws ConverterNotFoundException 当注册表和转换器集合均不支持转换时抛出
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        if (registry.canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
            return registry.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
        }
        return transformers.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
    }
}