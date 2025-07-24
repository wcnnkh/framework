package run.soeasy.framework.io;

import lombok.Data;
import lombok.NonNull;

/**
 * 重命名资源包装器，用于为现有资源提供逻辑上的重命名功能。
 * 该类实现{@link ResourceWrapper}接口，通过装饰模式为资源添加新名称，
 * 而不修改原始资源的物理名称或属性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>逻辑重命名：仅修改资源的逻辑名称，不影响物理存储</li>
 *   <li>透明代理：除名称外，其他属性和操作均委派给原始资源</li>
 *   <li>链式调用：支持连续调用{@link #rename(String)}创建多层包装</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>统一资源命名规范，如添加前缀或后缀</li>
 *   <li>临时重命名资源以满足特定业务需求</li>
 *   <li>在不修改原始资源的情况下提供别名</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <W> 被包装的资源类型，需继承{@link Resource}
 * @see ResourceWrapper
 * @see Resource#rename(String)
 */
@Data
class RenamedResource<W extends Resource> implements ResourceWrapper<W> {
    @NonNull
    private final W source;
    @NonNull
    private final String name;

    /**
     * 创建新的重命名资源实例，覆盖原始资源的名称。
     * <p>
     * 该方法返回一个新的{@link RenamedResource}实例，
     * 保留原始资源的所有属性，仅修改其逻辑名称。
     * 
     * @param name 新名称，不可为null
     * @return 重命名后的资源实例
     */
    @Override
    public Resource rename(String name) {
        return new RenamedResource<>(this.source, name);
    }
}