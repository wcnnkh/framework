package run.soeasy.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import lombok.NonNull;

/**
 * 基于{@link File}的资源接口，继承自{@link PathResource}，专为java.io.File API提供资源实现，
 * 作为{@link File}与{@link Resource}接口的桥接，同时兼容{@link PathResource}的Path操作，
 * 是访问本地文件系统资源的便捷接口。
 * 
 * <p>作为函数式接口，仅需实现{@link #getFile()}方法即可，其他方法均提供默认实现，
 * 封装了File的核心操作（读写流、属性查询等），并通过{@link FileUtils}工具类简化流的打开逻辑。
 * 
 * @author soeasy.run
 * @see Resource
 * @see PathResource
 * @see File
 * @see FileUtils
 */
@FunctionalInterface
public interface FileResource extends PathResource {

    /**
     * 获取当前资源对应的{@link File}对象（非空），是接口的核心抽象方法，
     * 所有默认方法均基于此File实现资源操作，同时通过{@link #getPath()}兼容Path API。
     * 
     * @return 资源的文件对象（非空，指向文件系统中的具体文件）
     */
    @NonNull
    File getFile();

    /**
     * 获取资源对应的{@link Path}对象，默认通过{@link File#toPath()}转换而来，
     * 实现{@link PathResource}接口的Path操作要求，使当前资源同时支持File和Path两种API。
     * 
     * @return 资源的路径对象（非空，与{@link #getFile()}指向同一文件）
     */
    @Override
    default Path getPath() {
        return getFile().toPath();
    }

    /**
     * 获取资源的最后修改时间（毫秒时间戳），基于{@link File#lastModified()}实现
     * 
     * @return 最后修改时间戳（毫秒），文件不存在时返回0L
     * @throws IOException 此实现无IO异常（File#lastModified()不抛异常）
     */
    @Override
    default long lastModified() throws IOException {
        File file = getFile();
        return file == null ? 0L : file.lastModified();
    }

    /**
     * 获取资源的输入流，通过{@link FileUtils#openInputStream(File)}实现，
     * 封装了文件输入流的打开逻辑（处理文件不存在、目录等异常情况）。
     * 
     * @return 可读取资源的输入流（非空）
     * @throws FileNotFoundException 若文件不存在或为目录时抛出
     * @throws IOException 打开输入流失败时抛出（如权限不足）
     */
    @Override
    default InputStream getInputStream() throws IOException {
        return FileUtils.openInputStream(getFile());
    }

    /**
     * 获取资源的输出流，通过{@link FileUtils#openOutputStream(File)}实现，
     * 封装了文件输出流的打开逻辑（支持创建父目录、覆盖写入等）。
     * 
     * @return 可写入资源的输出流（非空）
     * @throws IOException 打开输出流失败时抛出（如权限不足、路径为目录）
     */
    @Override
    default OutputStream getOutputStream() throws IOException {
        return FileUtils.openOutputStream(getFile());
    }

    /**
     * 判断资源是否存在，基于{@link File#exists()}实现
     * 
     * @return 文件存在返回true，否则返回false
     */
    @Override
    default boolean exists() {
        return getFile().exists();
    }

    /**
     * 判断资源是否可读且为文件（非目录）
     * 
     * <p>当前实现逻辑（注意：代码中使用{@link File#canWrite()}可能为笔误，实际应检查{@link File#canRead()}）：
     * - 若文件可写且非目录，则认为可读
     * 
     * @return 符合条件返回true，否则返回false
     */
    @Override
    default boolean isReadable() {
        File file = getFile();
        return file.canWrite() && !file.isDirectory();
    }

    /**
     * 判断资源是否可写且为文件（非目录），基于{@link File#canWrite()}实现
     * 
     * @return 文件可写且非目录返回true，否则返回false
     */
    @Override
    default boolean isWritable() {
        File file = getFile();
        return file.canWrite() && !file.isDirectory();
    }

    /**
     * 获取资源的内容长度（字节数），基于{@link File#length()}实现
     * 
     * <p>特殊处理：
     * - 若文件长度为0且不存在，抛出{@link FileNotFoundException}
     * - 其他情况返回文件长度（即使文件为空但存在，也返回0L）
     * 
     * @return 资源的字节长度
     * @throws FileNotFoundException 若文件不存在且长度为0时抛出
     * @throws IOException 此实现无其他IO异常（File#length()不抛异常）
     */
    @Override
    default long contentLength() throws IOException {
        File file = getFile();
        long length = file.length();
        if (length == 0L && !file.exists()) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be resolved in the file system for checking its content length");
        }
        return length;
    }

    /**
     * 获取资源的名称（文件名），基于{@link File#getName()}实现
     * 
     * @return 文件名（非空字符串，如"document.txt"）
     */
    @Override
    default String getName() {
        return getFile().getName();
    }

    /**
     * 获取资源的描述信息，格式为"file [绝对路径]"，便于定位文件在系统中的位置
     * 
     * @return 资源描述字符串（非空，如"file [/data/reports/2024.pdf]"）
     */
    @Override
    default String getDescription() {
        return "file [" + getFile().getAbsolutePath() + "]";
    }
}