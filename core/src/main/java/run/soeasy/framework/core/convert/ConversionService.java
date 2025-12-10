package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.exchange.Operation;
import run.soeasy.framework.core.spi.ServiceInjectors;

/**
 * 转换服务核心类，整合多种类型转换机制，提供统一的类型转换入口。
 * <p>
 * 该类集成了{@link Converters}可配置转换器集合和{@link ConverterRegistry}注册表，
 * 支持动态注册转换器，并通过{@link ServiceInjectors}实现转换器的依赖注入，
 * 是框架类型转换体系的核心枢纽。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>双重转换机制：优先使用注册表{@link ConverterRegistry}，再使用可配置转换器{@link Converters}</li>
 *   <li>依赖注入支持：通过{@link ServiceInjectors}自动为转换器注入当前转换服务</li>
 *   <li>多场景转换：支持不同参数组合的类型转换方法，覆盖常见转换场景</li>
 *   <li>动态扩展：支持运行时注册新的转换器，无需重启应用</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Converter
 * @see Converters
 * @see ConverterRegistry
 */
@Getter
public class ConversionService implements Converter {
    
    /** 可配置转换器集合，支持按优先级排序的链式转换 */
    private final Converters converters = new Converters();
    
    /** 服务注入器，用于为转换器注入依赖 */
    private final ServiceInjectors<Object> injectors = new ServiceInjectors<>();
    
    /** 转换器注册表，支持按类型映射快速查找转换器 */
    private final ConverterRegistry registry = new ConverterRegistry();

    /**
     * 构造函数，初始化转换服务并注册依赖注入逻辑
     * <p>
     * 注册一个服务注入器，当注入的服务实现{@link ConverterAware}接口时，
     * 自动为其设置当前转换服务实例，实现转换器与转换服务的关联。
     */
    public ConversionService() {
        injectors.register((service) -> {
            if (service instanceof ConverterAware) {
                ConverterAware converterAware = (ConverterAware) service;
                converterAware.setConverter(this);
            }
            return Operation.SUCCESS;
        });
    }

    /**
     * 判断是否支持指定类型的转换（整合双重转换机制）
     * <p>
     * 先检查{@link ConverterRegistry}是否有匹配的类型映射，
     * 若没有则检查{@link Converters}可配置转换器集合是否有支持的转换器。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return true表示可转换，false表示不可转换
     * @throws NullPointerException 若类型描述符为null
     */
    @Override
    public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
                             @NonNull TypeDescriptor targetTypeDescriptor) {
        return registry.canConvert(sourceTypeDescriptor, targetTypeDescriptor)
                || converters.canConvert(sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 执行类型转换操作（整合双重转换机制）
     * <p>
     * 转换流程：
     * <ol>
     *   <li>先尝试通过{@link ConverterRegistry}进行转换</li>
     *   <li>若失败则尝试通过{@link Converters}可配置转换器集合进行转换</li>
     * </ol>
     * 
     * @param source                  待转换对象
     * @param sourceTypeDescriptor    源类型描述符，不可为null
     * @param targetTypeDescriptor    目标类型描述符，不可为null
     * @return 转换后的目标对象
     * @throws ConversionException    转换失败时抛出
     * @throws NullPointerException   若类型描述符为null
     */
    @Override
    public Object convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
                          @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        if (registry.canConvert(sourceTypeDescriptor, targetTypeDescriptor)) {
            return registry.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
        }
        return converters.convert(source, sourceTypeDescriptor, targetTypeDescriptor);
    }

    /**
     * 注册转换器（根据类型选择注册方式）
     * <p>
     * 若转换器是{@link ConditionalConverter}，则注册到{@link ConverterRegistry}，
     * 否则注册到{@link Converters}可配置转换器集合，并在注册后注入依赖。
     * 
     * @param converter 待注册的转换器，不可为null
     * @return 注册句柄，用于取消注册
     * @throws NullPointerException 若converter为null
     */
    public Operation register(@NonNull Converter converter) {
    	Operation registration = converter instanceof ConditionalConverter
                ? registry.register((ConditionalConverter) converter)
                : converters.register(converter);
        if (!registration.isCancelled()) {
            injectors.inject(converter);
        }
        return registration;
    }

    // --------------------- 以下为Converter接口的默认方法实现 ---------------------

    /**
     * 判断是否支持指定类到类的转换（调用父接口默认实现）
     */
    @Override
    public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
        return Converter.super.canConvert(sourceClass, targetClass);
    }

    /**
     * 判断是否支持指定类到类型描述符的转换（调用父接口默认实现）
     */
    @Override
    public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
        return Converter.super.canConvert(sourceClass, targetTypeDescriptor);
    }

    /**
     * 判断是否支持指定类型描述符到类的转换（调用父接口默认实现）
     */
    @Override
    public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
        return Converter.super.canConvert(sourceTypeDescriptor, targetClass);
    }

    /**
     * 执行对象到指定类的转换（调用父接口默认实现）
     */
    @Override
    public final <T> T convert(@NonNull Object source, @NonNull Class<? extends T> targetClass)
            throws ConversionException {
        return Converter.super.convert(source, targetClass);
    }

    /**
     * 执行指定类到类型描述符的转换（调用父接口默认实现）
     */
    @Override
    public final Object convert(Object source, @NonNull Class<?> sourceClass,
                                @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
        return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
    }

    /**
     * 执行类型描述符到指定类的转换（调用父接口默认实现）
     */
    @Override
    public final <T> T convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
                               @NonNull Class<? extends T> targetClass) throws ConversionException {
        return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
    }

    /**
     * 执行对象到类型描述符的转换（调用父接口默认实现）
     */
    @Override
    public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
            throws ConversionException {
        return Converter.super.convert(source, targetTypeDescriptor);
    }
}