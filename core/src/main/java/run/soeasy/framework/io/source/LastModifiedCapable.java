package run.soeasy.framework.io.source;

import java.io.IOException;

/**
 * 具备最后修改时间能力的接口。
 *
 * <p>用于抽象那些内部状态会随时间变化的资源，使其能够通过统一的方式
 * 暴露最后修改时间。
 *
 * <p><b>设计意图：</b>
 * 将“资源是否发生变化”的检测逻辑标准化，常用于缓存一致性校验、
 * 文件同步及热加载场景。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * public class FileResource implements LastModifiedCapable {
 *     private final Path path;
 *
 *     @Override
 *     public long lastModified() throws IOException {
 *         return Files.getLastModifiedTime(path).toMillis();
 *     }
 * }
 *
 * LastModifiedCapable resource = new FileResource(path);
 * long timestamp = resource.lastModified();
 * }</pre>
 *
 * @author soeasy.run
 */
@FunctionalInterface
public interface LastModifiedCapable {

    /**
     * 获取资源的最后修改时间。
     *
     * <p><b>契约：</b>
     * <ul>
     *   <li>当资源内容发生变化时，返回值必须随之变化</li>
     *   <li>返回值应为毫秒级时间戳（自 1970-01-01 起）</li>
     *   <li>若资源不存在或无权限访问，应抛出 {@link IOException}</li>
     * </ul>
     *
     * @return 最后修改时间（毫秒时间戳）
     * @throws IOException 当资源无法访问或获取失败时
     */
    long lastModified() throws IOException;
}