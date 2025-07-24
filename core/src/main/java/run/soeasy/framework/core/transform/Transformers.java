package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.spi.ConfigurableServices;

/**
 * 转换器集合管理器，继承自{@link ConfigurableServices}并实现{@link Transformer}接口，
 * 用于管理多个转换器实例并按优先级顺序尝试转换操作。
 * <p>
 * 该类维护一个转换器列表，在执行转换时会按优先级顺序遍历所有转换器，
 * 直到找到一个支持当前类型转换的转换器并执行转换，适用于需要组合多个转换器
 * 处理复杂转换场景的情况，如类型转换链、多转换器优先级处理等。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>转换器集合管理：基于{@link ConfigurableServices}实现转换器的注册、排序和查询</li>
 *   <li>优先级转换：使用{@link TransformerComparator}对转换器进行排序，按优先级尝试转换</li>
 *   <li>自动适配：遍历所有转换器，自动寻找支持当前类型转换的最佳实现</li>
 * </ul>
 * </p>
 *
 * <p><b>潜在问题：</b>
 * <ul>
 *   <li>线程安全：未对转换器集合添加同步机制，多线程环境下可能出现并发修改问题</li>
 *   <li>性能损耗：遍历所有转换器的O(n)操作，在转换器数量较多时可能影响性能</li>
 *   <li>转换顺序依赖：转换结果依赖转换器的注册顺序和比较器逻辑，可能导致不可预知的行为</li>
 *   <li>异常传播：转换器抛出的异常会直接向上传播，未做统一封装处理</li>
 * </ul>
 * </p>
 *
 * @author soeasy.run
 * @see Transformer
 * @see ConfigurableServices
 * @see TransformerComparator
 */
public class Transformers extends ConfigurableServices<Transformer> implements Transformer {

    /**
     * 构造转换器集合管理器
     * <p>
     * 初始化时设置默认的{@link TransformerComparator}，
     * 确保转换器按类型映射和优先级进行排序。
     */
    public Transformers() {
        setComparator(TransformerComparator.DEFAULT);
    }

    /**
     * 判断是否存在支持当前类型转换的转换器
     * <p>
     * 遍历所有转换器，检查是否有任意转换器支持从源类型到目标类型的转换。
     * 
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 存在支持的转换器返回true，否则false
     */
    @Override
    public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull TypeDescriptor targetTypeDescriptor) {
        return anyMatch((e) -> e.canTransform(sourceTypeDescriptor, targetTypeDescriptor));
    }

    /**
     * 按优先级顺序尝试执行类型转换
     * <p>
     * 按转换器优先级顺序依次检查每个转换器，找到第一个支持当前类型转换的转换器并执行转换。
     * 
     * @param source 源对象，不可为null
     * @param sourceTypeDescriptor 源类型描述符，不可为null
     * @param target 目标对象，不可为null
     * @param targetTypeDescriptor 目标类型描述符，不可为null
     * @return 转换成功返回true，否则false
     */
    @Override
    public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
            @NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
        for (Transformer transformer : this) {
            if (transformer.canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
                return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
            }
        }
        return false;
    }
}