package run.soeasy.framework.core.transform;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;

/**
 * 消费型条件转换器，实现{@link ConditionalTransformer}接口，
 * 通过指定的{@link BiConsumer}函数执行源对象到目标对象的属性转换。
 * <p>
 * 该转换器支持将源类型{S}的对象属性值消费并应用到目标类型{T}的对象，
 * 适用于需要自定义属性转换逻辑的简单场景，如函数式转换或属性映射。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>类型映射：通过{@link TypeMapping}定义唯一支持的源类型和目标类型</li>
 *   <li>函数式转换：使用{@link BiConsumer}定义具体的属性转换逻辑</li>
 *   <li>条件判断：基于预定义的类型映射实现转换可行性判断</li>
 * </ul>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>类型安全风险：{@link #transform}方法中存在强制类型转换，可能引发{@link ClassCastException}</li>
 *   <li>空值处理缺失：未对源对象和目标对象进行空值校验，可能导致NPE</li>
 *   <li>线程安全：未实现线程安全机制，多线程环境下共享实例可能引发并发问题</li>
 *   <li>异常封装：转换过程中BiConsumer抛出的异常会直接向上传播，未做封装处理</li>
 * </ul>
 *
 * @param <S> 源对象类型
 * @param <T> 目标对象类型
 * 
 * @author soeasy.run
 * @see ConditionalTransformer
 * @see TypeMapping
 * @see BiConsumer
 */
class ConsumeTransformer<S, T> implements ConditionalTransformer {
    
    /** 定义支持的源类型到目标类型的映射 */
    private final TypeMapping typeMapping;
    
    /** 执行属性转换的函数式接口，消费源对象并更新目标对象 */
    private final BiConsumer<? super S, ? super T> consumer;

    /**
     * 构造消费型转换器
     * <p>
     * 初始化时指定源类型、目标类型和属性转换逻辑
     * 
     * @param sourceType 源类型Class，不可为null
     * @param targetType 目标类型Class，不可为null
     * @param consumer 转换逻辑函数，参数为(源对象, 目标对象)，不可为null
     * @throws NullPointerException 若sourceType、targetType或consumer为null
     */
    public ConsumeTransformer(@NonNull Class<S> sourceType, @NonNull Class<T> targetType,
            @NonNull BiConsumer<? super S, ? super T> consumer) {
        this.typeMapping = new TypeMapping(sourceType, targetType);
        this.consumer = consumer;
    }

    /**
     * 获取支持的类型映射集合（仅包含构造时定义的类型映射）
     * <p>
     * 返回不可变的单例集合，确保类型映射规则不可修改
     * 
     * @return 包含唯一类型映射的不可变集合
     */
    @Override
    public Set<TypeMapping> getTransformableTypeMappings() {
        return Collections.singleton(typeMapping);
    }

    /**
     * 执行源对象到目标对象的属性转换
     * <p>
     * <b>类型安全警告：</b>
     * 直接将Object强制转换为泛型类型{S}和{T}，若实际类型不匹配将抛出{@link ClassCastException}
     * 
     * @param source 源对象，需为{S}类型或可强制转换为{S}
     * @param sourceTypeDescriptor 源类型描述符（未使用，仅满足接口要求）
     * @param target 目标对象，需为{T}类型或可强制转换为{T}
     * @param targetTypeDescriptor 目标类型描述符（未使用，仅满足接口要求）
     * @return 始终返回true（假设转换逻辑无异常）
     * @throws ClassCastException 当源对象或目标对象类型不匹配时抛出
     * @throws NullPointerException 若consumer执行过程中出现NPE
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        consumer.accept((S) source, (T) target);
        return true;
    }
}