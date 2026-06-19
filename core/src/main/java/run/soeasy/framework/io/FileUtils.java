package run.soeasy.framework.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 文件操作工具类，提供基于Java IO和NIO的文件系统操作工具方法。
 * 该类封装了文件读写、复制、压缩解压、目录遍历、批量删除等常用操作，简化文件系统编程，兼顾安全性和易用性。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>文件复制：支持缓冲区消费者模式，可自定义数据处理逻辑，支持指定缓冲区大小</li>
 * <li>流操作：安全打开文件输入/输出流，包含存在性、权限、目录类型校验</li>
 * <li>目录遍历：支持递归/非递归、深度控制的目录文件迭代，返回可迭代的元素集合</li>
 * <li>压缩解压：ZIP文件压缩（支持文件/目录过滤、跨平台路径兼容）、解压（防目录穿越、自动创建多级目录）</li>
 * <li>批量删除：提供强制删除（抛异常）和静默删除（不抛异常）两种模式，支持文件/空目录删除</li>
 * </ul>
 *
 * <p>
 * <b>使用场景：</b>
 * <ul>
 * <li>文件内容复制/迁移：如备份文件、流数据写入本地文件</li>
 * <li>流资源管理：安全打开/关闭文件流，避免资源泄漏</li>
 * <li>目录扫描：递归遍历目录获取指定类型文件（如配置文件、日志文件）</li>
 * <li>压缩解压：创建自定义过滤规则的ZIP包、安全解压ZIP文件（防止目录穿越攻击）</li>
 * <li>批量清理：静默删除临时文件、强制删除关键文件（需处理异常）</li>
 * </ul>
 *
 * <p>
 * <b>注意事项：</b>
 * <ul>
 * <li>所有方法均对<code>null</code>参数做严格校验（标注{@link NonNull}），避免空指针异常</li>
 * <li>IO操作均会抛出{@link IOException}，需手动处理或向上抛出</li>
 * <li>压缩/解压方法不会自动关闭传入的流/文件实例，需调用者手动关闭（建议使用try-with-resources）</li>
 * <li>删除方法仅支持文件或空目录，非空目录需先递归删除子内容</li>
 * </ul>
 *
 * @author soeasy.run
 * @see IOUtils 流操作工具类
 * @see ListFileIterator 目录文件迭代器
 */
@UtilityClass
public class FileUtils {
    /**
     * 空文件数组，用于表示无文件的场景
     */
    public static final File[] EMPTY_FILE_ARRAY = new File[0];

    /**
     * 批量强制删除文件/空目录（删除失败抛异常）。
     * <p>
     * 适用于需要确保删除成功的场景（如清理关键临时文件），会明确抛出删除失败的异常。
     *
     * @param files 待删除的文件/空目录集合，支持null元素（自动过滤）
     * @throws IOException 当文件/目录不存在、无删除权限、为非空目录或IO操作失败时抛出
     */
    public static void delete(Collection<? extends File> files) throws IOException {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }

        CollectionUtils.acceptAll(files, (e) -> {
            if (e != null) {
                Files.delete(e.toPath());
            }
        });
    }

    /**
     * 批量静默删除文件/空目录（删除失败不抛异常，返回成功数量）。
     * <p>
     * 适用于无需确保删除成功的场景（如清理临时文件），失败时仅静默跳过，不影响后续操作。
     *
     * @param files 待删除的文件/空目录集合，支持null元素（自动过滤）
     * @return 成功删除的文件/空目录数量（仅文件存在且删除成功时计数）
     */
    public static int deleteQuietly(Collection<? extends File> files) {
        if (CollectionUtils.isEmpty(files)) {
            return 0;
        }

        int count = 0;
        for (File file : files) {
            if (file == null) {
                continue;
            }

            if (file.exists() && file.delete()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 递归迭代目录下的所有文件（不限制深度）。
     * <p>
     * 会遍历目标目录及其所有子目录下的文件，返回的{@link Streamable}支持迭代、流式处理。
     *
     * @param directory 目标目录，不可为null且必须是目录
     * @return 包含所有文件的元素集合（仅文件，不包含目录）
     * @throws IllegalArgumentException 当directory不是目录时抛出
     * @see #listFiles(File, int) 支持深度控制的重载方法
     */
    public static Streamable<File> listAllFiles(@NonNull File directory) {
        return listFiles(directory, -1);
    }

    /**
     * 迭代目录下的文件（不进行递归）。
     * <p>
     * 仅遍历目标目录下的一级文件，不包含子目录及其内容。
     *
     * @param directory 目标目录，不可为null且必须是目录
     * @return 包含当前目录下一级文件的元素集合（仅文件，不包含目录）
     * @throws IllegalArgumentException 当directory不是目录时抛出
     * @see #listFiles(File, int) 支持深度控制的重载方法
     */
    public static Streamable<File> listFiles(@NonNull File directory) {
        return listFiles(directory, 0);
    }

    /**
     * 迭代目录下的文件（支持深度控制）。
     * <p>
     * 灵活控制目录遍历深度，适用于需要限制遍历层级的场景（如仅遍历前两级子目录）。
     *
     * @param directory 目标目录，不可为null且必须是目录
     * @param maxDepth  最大迭代深度：-1表示不限制（递归所有子目录），0表示不递归（仅当前目录），≥1表示指定深度
     * @return 包含迭代范围内所有文件的元素集合（仅文件，不包含目录）
     * @throws IllegalArgumentException 当directory不是目录或maxDepth &lt; -1时抛出
     */
    public static Streamable<File> listFiles(@NonNull File directory, int maxDepth) {
        Assert.isTrue(directory.isDirectory(), () -> directory + " is not a directory");
        Assert.isTrue(maxDepth >= -1, () -> "maxDepth must be ≥ -1, but got " + maxDepth);
        return Streamable.of(() -> new ListFileIterator(directory, maxDepth));
    }
}