package run.soeasy.framework.core.exchange;

/**
 * 忽略发布器实现，用于丢弃所有发布的消息。
 * 该类实现了{@link Publisher}接口，但实际上不会处理或传播任何消息，
 * 所有发布操作都会立即返回成功回执，适用于需要静默丢弃消息的场景。
 *
 * <p>设计特点：
 * <ul>
 *   <li>单例模式：通过静态常量INSTANCE提供全局唯一实例</li>
 *   <li>无操作实现：所有发布请求都会被忽略，不执行任何操作</li>
 *   <li>轻量级：不持有任何状态，不依赖外部资源</li>
 * </ul>
 *
 * <p>典型应用场景：
 * <ul>
 *   <li>测试环境中替代实际发布器</li>
 *   <li>开发阶段临时禁用消息发布功能</li>
 *   <li>实现消息的条件性丢弃策略</li>
 *   <li>作为默认发布器避免空指针异常</li>
 * </ul>
 *
 * @param <T> 发布的消息类型
 * 
 * @author soeasy.run
 * @see Publisher
 * @see Receipt
 */
class IgnorePublisher<T> implements Publisher<T> {

    /**
     * 全局单例实例，用于所有类型的消息
     */
    static final Publisher<?> INSTANCE = new IgnorePublisher<>();

    /**
     * 忽略所有发布的消息，直接返回成功回执
     * 
     * @param resource 待发布的消息资源
     * @return 表示操作成功的回执
     */
    @Override
    public Receipt publish(T resource) {
        return Receipt.SUCCESS;
    }
}