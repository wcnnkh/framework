package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 成功注册的回执实现，用于表示注册操作已成功完成。
 * 该类实现了{@link Receipt}和{@link RegistrationWrapper}接口，
 * 既可以作为注册操作的结果回执，也可以作为注册操作的包装器。
 *
 * <p>设计特点：
 * <ul>
 *   <li>不可变对象：创建后状态不可修改</li>
 *   <li>装饰模式：包装原始注册操作，提供统一接口</li>
 *   <li>成功语义：固定表示操作成功，cause()返回null</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>作为注册服务的成功响应</li>
 *   <li>在事件总线中表示监听器注册成功</li>
 *   <li>在资源管理系统中表示资源注册成功</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Receipt
 * @see RegistrationWrapper
 * @see Registration
 */
@RequiredArgsConstructor
public class SuccessfullyRegistered implements Receipt, RegistrationWrapper<Registration> {

    /**
     * 被包装的原始注册操作
     */
    @NonNull
    private final Registration source;

    /**
     * 获取被包装的原始注册操作
     * 
     * @return 原始注册操作实例
     */
    @Override
    public Registration getSource() {
        return source;
    }

    /**
     * 获取操作失败的原因
     * 由于该类表示操作成功，此方法始终返回null
     * 
     * @return null
     */
    @Override
    public Throwable cause() {
        return null;
    }

    /**
     * 判断操作是否已完成
     * 由于该类表示操作成功，此方法始终返回true
     * 
     * @return true
     */
    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * 判断操作是否成功
     * 由于该类表示操作成功，此方法始终返回true
     * 
     * @return true
     */
    @Override
    public boolean isSuccess() {
        return true;
    }
}