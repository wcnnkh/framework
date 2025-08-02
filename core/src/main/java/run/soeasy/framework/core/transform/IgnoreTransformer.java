package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 忽略所有转换请求的转换器，实现{@link Transformer}接口的单例实现。
 * <p>
 * 该转换器的所有转换相关方法始终返回false，表示不支持任何转换操作，
 * 适用于需要禁用转换或作为占位符的场景。
 *
 * <p><b>单例模式实现：</b>
 * <ul>
 *   <li>通过{@link #INSTANCE}提供全局唯一实例</li>
 *   <li>注意：构造函数未显式声明为private，理论上可被外部实例化（需自行保证单例约束）</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Transformer
 */
class IgnoreTransformer implements Transformer {
    
    /** 忽略转换器的全局唯一实例 */
    public static final IgnoreTransformer INSTANCE = new IgnoreTransformer();

    /**
     * 判断是否支持源类型到目标类型的转换（始终返回false）
     * 
     * @param sourceTypeDescriptor 源类型描述符（未使用）
     * @param targetTypeDescriptor 目标类型描述符（未使用）
     * @return 始终返回false
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return false;
    }

    /**
     * 执行对象属性转换（始终返回false，不执行任何操作）
     * 
     * @param source 源对象（未使用）
     * @param sourceTypeDescriptor 源类型描述符（未使用）
     * @param target 目标对象（未使用）
     * @param targetTypeDescriptor 目标类型描述符（未使用）
     * @return 始终返回false
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        // 忽略所有转换请求，不执行任何操作
        return false;
    }
}