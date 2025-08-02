package run.soeasy.framework.core.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * 抽象转换器基类，实现{@link Transformer}和{@link TransformerAware}接口，
 * 为转换器提供基础实现和默认行为。
 * <p>
 * 该类定义了一个可配置的内部转换器，并提供了转换可行性判断的抽象方法，
 * 子类需实现{@link #canTransform}方法来定义自身的转换条件。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>转换器注入：支持通过{@link #setTransformer}方法设置内部转换器</li>
 *   <li>默认行为：默认使用{@link Transformer#ignore()}作为内部转换器</li>
 *   <li>条件转换：子类需实现具体的转换可行性判断逻辑</li>
 * </ul>
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
}