package run.soeasy.framework.sequences;

import java.util.UUID;

/**
 * UUID序列生成器接口，扩展自{@link StringSequence}并标记为函数式接口，
 * 专门用于生成标准UUID格式的字符串序列，提供固定长度（32位）的无连字符UUID生成功能。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>固定长度：强制生成32位无连字符的UUID字符串（如{@code a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6}）</li>
 *   <li>全局唯一：基于{@link UUID}标准生成，理论上无重复可能</li>
 *   <li>函数式设计：可作为lambda表达式或方法引用的目标类型</li>
 *   <li>便捷工厂：通过{@link #random()}获取默认的随机UUID生成器</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>分布式ID生成：生成全局唯一的资源标识</li>
 *   <li>安全令牌：生成不可预测的安全认证令牌</li>
 *   <li>数据分片键：作为分布式存储的分片标识</li>
 *   <li>日志追踪ID：生成跨系统的请求追踪标识</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see StringSequence
 * @see UUID
 */
@FunctionalInterface
public interface UUIDSequence extends StringSequence {
    /**
     * 获取UUID字符串长度（固定返回32）。
     * <p>
     * 重写父接口以强制返回32，确保生成的UUID字符串为标准无连字符格式。
     * 
     * @return 固定值32
     */
    @Override
    default int getLength() {
        return 32;
    }

    /**
     * 生成指定长度的UUID字符串（强制要求长度为32）。
     * <p>
     * 1. 检查长度是否为32，非32时抛出异常
     * 2. 调用{@link #nextUUID()}生成UUID实例
     * 3. 去除连字符后返回32位字符串
     * 
     * @param length 目标长度（必须为32）
     * @return 无连字符的UUID字符串（如a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6）
     * @throws UnsupportedOperationException 当length≠32时抛出
     */
    @Override
    default String next(int length) throws UnsupportedOperationException {
        if (length != getLength()) {
            throw new UnsupportedOperationException("The length of uuid can only be 32");
        }
        return nextUUID().toString().replace("-", "");
    }

    /**
     * 生成原始UUID实例（带连字符）。
     * <p>
     * 实现类应返回符合{@link UUID}标准的实例，
     * 典型实现为生成随机UUID（如{@code UUID.randomUUID()}）。
     * 
     * @return UUID实例（如a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6）
     */
    UUID nextUUID();

    /**
     * 获取默认的随机UUID序列生成器。
     * <p>
     * 返回一个基于{@link UUID#randomUUID()}的生成器，
     * 适用于大多数需要随机UUID的场景。
     * 
     * @return 随机UUID生成器实例，不可为null
     */
    public static UUIDSequence random() {
        return RandomUUIDSequence.DEFAULT;
    }
}