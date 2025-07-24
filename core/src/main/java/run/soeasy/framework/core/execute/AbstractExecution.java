package run.soeasy.framework.core.execute;

import lombok.Getter;
import lombok.NonNull;

/**
 * 执行上下文抽象基类，实现{@link Execution}接口，为可执行元素的执行提供基础实现。
 * 该类封装了执行所需的元数据和参数信息，并提供了参数操作的能力。
 * <p>
 * 子类需实现{@link #execute()}方法，定义具体的执行逻辑。该基类通过Lombok自动生成
 * 元数据和参数的getter方法，并确保参数数组的初始状态。
 * </p>
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>元数据管理：通过构造函数注入可执行元素的元数据</li>
 *   <li>参数操作：提供参数数组的直接访问，支持参数值的动态修改</li>
 *   <li>不可变元数据：元数据在初始化后不可更改，保证执行过程中的一致性</li>
 *   <li>可变参数：参数数组可在执行前修改，支持动态参数调整</li>
 * </ul>
 * </p>
 *
 * <p><b>使用说明：</b>
 * <ul>
 *   <li>子类需通过构造函数传入元数据和初始参数</li>
 *   <li>可通过{@link #getArguments()}获取和修改参数数组</li>
 *   <li>执行逻辑由子类的{@link #execute()}方法实现</li>
 * </ul>
 * </p>
 *
 * @param <W> 可执行元素的元数据类型，需实现{@link ExecutableMetadata}
 * @author soeasy.run
 * @see Execution
 * @see ExecutableMetadata
 */
@Getter
public abstract class AbstractExecution<W extends ExecutableMetadata> implements Execution {
    /**
     * 可执行元素的元数据，包含参数类型、返回类型等信息
     */
    private final W metadata;
    
    /**
     * 执行参数数组，可通过getter方法直接访问和修改
     */
    private final Object[] arguments;

    /**
     * 构造函数，初始化执行上下文
     * 
     * @param metadata 可执行元素的元数据，不可为null
     * @param arguments 初始参数数组，不可为null
     */
    public AbstractExecution(@NonNull W metadata, @NonNull Object... arguments) {
        this.metadata = metadata;
        this.arguments = arguments;
    }
}