package run.soeasy.framework.core.domain;

import lombok.NonNull;

/**
 * 版本包装器接口，用于包装{@link Version}实例并委托所有操作，
 * 实现装饰器模式以支持对版本操作的透明增强。该接口继承自{@link Version}和{@link ValueWrapper}，
 * 允许在不修改原始版本对象的前提下添加额外功能。
 *
 * <p>核心特性：
 * <ul>
 *   <li>透明委托：所有版本操作均转发给被包装的{@link Version}实例</li>
 *   <li>装饰扩展：支持通过包装器添加日志记录、缓存、验证等额外功能</li>
 *   <li>类型安全：通过泛型确保包装器与被包装版本的类型一致性</li>
 *   <li>函数式支持：作为函数式接口，可通过lambda表达式创建轻量级包装器</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>版本比较日志记录：记录版本比较操作的详细日志</li>
 *   <li>版本组合缓存：缓存频繁使用的版本组合结果</li>
 *   <li>版本合法性验证：在执行版本操作前进行合法性验证</li>
 *   <li>版本操作性能监控：统计版本操作的执行时间</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 原始版本实现
 * Version original = new SemanticVersion("1.0.0");
 * 
 * // 包装版本并添加日志记录
 * VersionWrapper&lt;Version&gt; logged = version -> {
 *     System.out.println("Compare version: " + version.getAsString());
 *     return original;
 * };
 * 
 * // 使用包装后的版本进行比较
 * int result = logged.compareTo(new SemanticVersion("1.0.1")); // 输出日志并执行比较
 * </pre>
 *
 * @param <W> 被包装的版本类型，必须是{@link Version}的子类型
 * @see Version
 * @see ValueWrapper
 */
public interface VersionWrapper<W extends Version> extends Version, ValueWrapper<W> {
    
    /**
     * 比较当前版本与另一个值的大小，转发给被包装的Version实例。
     *
     * @param other 待比较的值，不可为null
     * @return 比较结果：负整数表示当前版本更小，零表示相等，正整数表示更大
     * @see Version#compareTo(Value)
     */
    @Override
    default int compareTo(@NonNull Value other) {
        return getSource().compareTo(other);
    }

    /**
     * 将当前版本与另一个版本连接，转发给被包装的Version实例。
     *
     * @param version 待连接的版本，不可为null
     * @return 连接后的新版本
     * @see Version#join(Version)
     */
    @Override
    default Version join(@NonNull Version version) {
        return getSource().join(version);
    }

    /**
     * 获取当前对象作为版本实例，转发给被包装的Version实例。
     *
     * @return 被包装的版本实例
     * @see Version#getAsVersion()
     */
    @Override
    default Version getAsVersion() {
        return getSource().getAsVersion();
    }
}