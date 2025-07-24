package run.soeasy.framework.sequences;

import java.util.UUID;

/**
 * 随机UUID序列生成器实现类，实现{@link UUIDSequence}接口，
 * 基于{@link UUID#randomUUID()}生成随机UUID，提供全局唯一的字符串序列。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>随机生成：调用{@link UUID#randomUUID()}生成真正的随机UUID</li>
 *   <li>全局唯一：利用UUID的标准算法，理论上无重复可能</li>
 *   <li>线程安全：底层实现为JDK原生UUID生成，保证多线程安全</li>
 *   <li>单例设计：通过{@link #DEFAULT}提供全局共享实例</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>分布式ID生成：生成跨系统唯一的资源标识</li>
 *   <li>安全令牌：生成不可预测的认证令牌（如API密钥）</li>
 *   <li>临时文件命名：生成唯一的临时文件名称</li>
 *   <li>分布式锁：作为分布式锁的唯一标识</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see UUIDSequence
 * @see UUID
 */
class RandomUUIDSequence implements UUIDSequence {
    /** 全局共享的随机UUID生成器实例，推荐直接使用此实例 */
    public static final RandomUUIDSequence DEFAULT = new RandomUUIDSequence();

    /**
     * 生成随机UUID实例（带连字符）。
     * <p>
     * 直接调用{@link UUID#randomUUID()}，返回符合RFC 4122标准的随机UUID，
     * 格式为8-4-4-4-12（如a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6）。
     * 
     * @return 随机生成的UUID实例，不可为null
     */
    @Override
    public UUID nextUUID() {
        return UUID.randomUUID();
    }
}