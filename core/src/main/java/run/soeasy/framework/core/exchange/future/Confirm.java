package run.soeasy.framework.core.exchange.future;

import run.soeasy.framework.core.exchange.Registration;

/**
 * 操作确认接口，定义对未来操作结果的确认机制。
 * 该接口继承自{@link Registration}，扩展了操作成功状态的检查和确认能力，
 * 用于异步操作或需要延迟确认的场景。
 *
 * <p>状态转换模型：
 * <ul>
 *   <li>初始状态：未确认（pending）</li>
 *   <li>成功确认：调用trySuccess()返回true后进入成功状态</li>
 *   <li>取消状态：调用cancel()返回true后进入取消状态</li>
 *   <li>状态不可逆：一旦进入成功或取消状态，无法再转换到其他状态</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>异步操作的结果确认</li>
 *   <li>分布式事务的最终确认</li>
 *   <li>消息传递的确认机制</li>
 *   <li>资源获取的锁确认</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Registration
 */
public interface Confirm extends Registration {

    /**
     * 检查操作是否已成功确认
     * 
     * @return 如果操作已成功确认返回true，否则返回false
     */
    boolean isSuccess();

    /**
     * 尝试将操作标记为成功状态
     * <p>
     * 该操作具有幂等性：
     * <ul>
     *   <li>若当前处于未确认状态，标记为成功并返回true</li>
     *   <li>若已处于成功状态，直接返回true</li>
     *   <li>若已处于取消状态，返回false</li>
     * </ul>
     * 
     * @return 若操作成功确认返回true，否则返回false
     */
    boolean trySuccess();
}