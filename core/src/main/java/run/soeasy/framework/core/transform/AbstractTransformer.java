package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 抽象转换器基类，实现{@link Transformer}和{@link TransformerAware}接口，
 * 为转换器提供基础实现和默认行为。
 * <p>
 * 该类定义了一个可配置的内部转换器，并提供了转换可行性判断的抽象方法，
 * 子类需实现{@link #canTransform}方法来定义自身的转换条件。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>转换器注入：支持通过{@link #setTransformer}方法设置内部转换器</li>
 *   <li>默认行为：默认使用{@link Transformer#ignore()}作为内部转换器</li>
 *   <li>条件转换：子类需实现具体的转换可行性判断逻辑</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>未实现transform方法：未提供{@link Transformer#transform}的默认实现，子类必须实现</li>
 *   <li>线程安全：未对transformer字段添加同步机制，多线程环境下可能出现竞态条件</li>
 *   <li>空值处理：虽然使用@NonNull注解，但未防止运行时通过反射将transformer设为null</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Transformer
 * @see TransformerAware
 */
@Getter
@Setter
public abstract class AbstractTransformer implements Transformer, TransformerAware {
    
    /** 内部转换器，用于实际的转换操作，默认使用忽略转换器 */
    @NonNull
    private Transformer transformer = Transformer.ignore();

    /**
     * 判断是否支持从源类型到目标类型的转换
     * <p>
     * 此方法为抽象方法，子类必须提供具体实现，
     * 定义该转换器支持的源类型和目标类型组合。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 若支持转换返回true，否则false
     */
    @Override
    public abstract boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor);

    /**
     * 执行对象属性转换的方法
     * <p>
     * 注意：该方法未在基类中实现，子类必须提供具体实现，
     * 负责将源对象的属性值转换到目标对象。
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换成功返回true，否则false
     * @throws UnsupportedOperationException 若子类未实现此方法
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        // 注意：此方法未在基类中实现，子类必须提供具体实现
        throw new UnsupportedOperationException("Subclasses must implement transform method");
    }
}