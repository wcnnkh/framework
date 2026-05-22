package run.soeasy.framework.io.source;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 具备 Path 能力的接口。
 *
 * <p>继承 {@link LastModifiedCapable}，表明任何能提供 {@link Path} 的资源，
 * 天然具备获取最后修改时间的能力。
 *
 * <p><b>设计意图：</b>
 * 将“路径访问”与“时间感知”绑定，为文件系统资源提供统一抽象。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * public class FileResource implements PathCapable {
 *     private final Path path;
 *
 *     @Override
 *     public Path getPath() {
 *         return path;
 *     }
 * }
 *
 * PathCapable resource = new FileResource(path);
 * long time = resource.lastModified(); // 自动获得实现
 * }</pre>
 *
 * @author soeasy.run
 */
public interface PathCapable extends LastModifiedCapable {

    /**
     * 获取关联的 Path 对象。
     *
     * @return 关联的 Path（不可为 null）
     */
    @NonNull
    Path getPath();

    /**
     * 获取 Path 的最后修改时间。
     *
     * <p><b>契约：</b>
     * 如果资源不存在或无法访问，实现类应处理 {@link IOException} 或
     * 依赖默认实现抛出异常。
     *
     * @return 最后修改时间（毫秒时间戳）
     * @throws IOException 如果 Path 不存在或无法访问
     */
    @Override
    default long lastModified() throws IOException {
        return Files.getLastModifiedTime(path).toMillis();
    }
}