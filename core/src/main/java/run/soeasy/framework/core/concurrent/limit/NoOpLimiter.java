package run.soeasy.framework.core.concurrent.limit;

import java.util.concurrent.locks.Lock;

import run.soeasy.framework.core.concurrent.locks.NoOpLock;

/**
 * 无操作限制器，继承自DisposableLimiter，提供无限制或永久限制的行为。
 * 该限制器根据状态返回不同的锁实例：
 * <ul>
 *   <li>未受限状态：返回NoOpLock.NO，允许所有操作</li>
 *   <li>受限状态：返回NoOpLock.DEAD，拒绝所有操作</li>
 * </ul>
 * 
 * <p>主要用于测试环境或需要禁用限制功能的场景，实现零开销的限制控制。
 * 该类保持了DisposableLimiter的一次性语义，但通过NoOpLock提供特殊的锁行为。
 *
 * <p>使用场景：
 * <ul>
 *   <li>开发测试阶段替代真实限制器</li>
 *   <li>配置为无限制模式以禁用限制功能</li>
 *   <li>占位用途，在不需要限制的地方保持接口一致性</li>
 * </ul>
 *
 * @author soeasy.run
 * @see DisposableLimiter
 * @see NoOpLock
 */
public class NoOpLimiter extends DisposableLimiter {

    /**
     * 获取资源锁。
     * 根据当前限制器状态返回不同的NoOpLock实例：
     * <ul>
     *   <li>未受限状态：返回NoOpLock.NO，tryLock()始终返回true</li>
     *   <li>受限状态：返回NoOpLock.DEAD，tryLock()始终返回false</li>
     * </ul>
     * 
     * @return 根据状态返回NoOpLock.NO或NoOpLock.DEAD
     */
    @Override
    public Lock getResource() {
        return isLimited() ? NoOpLock.DEAD : NoOpLock.NO;
    }
    
    /**
     * 设置限制器为受限状态。
     * 调用此方法后，后续getResource()将返回NoOpLock.DEAD，拒绝所有操作。
     * 
     * @return 设置成功返回true，失败返回false（如已处于受限状态）
     */
    @Override
    public boolean limited() {
        return super.limited();
    }
    
    /**
     * 检查当前是否处于受限状态。
     * 
     * @return 若处于受限状态返回true，否则返回false
     */
    @Override
    public boolean isLimited() {
        return super.isLimited();
    }
}