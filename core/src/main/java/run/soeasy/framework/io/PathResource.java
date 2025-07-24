package run.soeasy.framework.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.NonNull;

/**
 * 基于{@link Path}的资源接口，继承自{@link Resource}，专为文件系统中的路径资源提供实现，
 * 封装了Java NIO的{@link Path}操作，提供资源的存在性检查、读写流获取、属性查询等功能。
 * 
 * <p>作为函数式接口，仅需实现{@link #getPath()}方法即可，其他方法均提供默认实现，
 * 适用于访问本地文件系统中的文件资源（如普通文件、配置文件等），支持NIO通道操作。
 * 
 * @author soeasy.run
 * @see Resource
 * @see Path
 * @see Files
 */
@FunctionalInterface
public interface PathResource extends Resource {

    /**
     * 获取当前资源对应的{@link Path}对象（非空），是接口的核心抽象方法，
     * 所有默认方法均基于此Path实现资源操作。
     * 
     * @return 资源的路径对象（非空，指向文件系统中的具体位置）
     */
    @NonNull
    Path getPath();

    /**
     * 获取资源的输出流，用于向资源写入内容
     * 
     * <p>实现逻辑：
     * 1. 若路径指向目录，抛出{@link FileNotFoundException}；
     * 2. 否则通过{@link Files#newOutputStream(Path)}创建输出流（默认按"创建或覆盖"模式打开）。
     * 
     * @return 可写入资源的输出流（非空）
     * @throws FileNotFoundException 若路径是目录时抛出
     * @throws IOException 打开输出流失败时抛出（如权限不足、路径不存在且无法创建）
     */
    @Override
    default OutputStream getOutputStream() throws IOException {
        Path path = getPath();
        if (Files.isDirectory(path)) {
            throw new FileNotFoundException(path + " (is a directory)");
        }
        return Files.newOutputStream(path);
    }

    /**
     * 获取资源的可读字节通道，用于高效读取资源内容（NIO通道方式）
     * 
     * <p>通过{@link Files#newByteChannel(Path, StandardOpenOption)}以只读模式打开通道，
     * 适用于大文件或需要非阻塞读取的场景。
     * 
     * @return 可读字节通道（非空）
     * @throws IOException 打开通道失败时抛出（如文件不存在、权限不足）
     */
    @Override
    default ReadableByteChannel readableChannel() throws IOException {
        return Files.newByteChannel(getPath(), StandardOpenOption.READ);
    }

    /**
     * 获取资源的可写字节通道，用于高效写入资源内容（NIO通道方式）
     * 
     * <p>通过{@link Files#newByteChannel(Path, StandardOpenOption)}以可写模式打开通道，
     * 适用于大文件或需要非阻塞写入的场景。
     * 
     * @return 可写字节通道（非空）
     * @throws IOException 打开通道失败时抛出（如权限不足、路径为目录）
     */
    @Override
    default WritableByteChannel writableChannel() throws IOException {
        return Files.newByteChannel(getPath(), StandardOpenOption.WRITE);
    }

    /**
     * 判断资源是否存在于文件系统中
     * 
     * <p>通过{@link Files#exists(Path)}检查路径是否存在，包括文件和目录。
     * 
     * @return 资源存在返回true，否则返回false
     */
    @Override
    default boolean exists() {
        return Files.exists(getPath());
    }

    /**
     * 判断资源是否可读且为文件（非目录）
     * 
     * <p>可读条件：
     * 1. 路径可被当前进程读取（{@link Files#isReadable(Path)}）；
     * 2. 路径指向的是文件而非目录（{@link Files#isDirectory(Path)}）。
     * 
     * @return 资源可读且为文件返回true，否则返回false
     */
    @Override
    default boolean isReadable() {
        Path path = getPath();
        return (Files.isReadable(path) && !Files.isDirectory(path));
    }

    /**
     * 获取资源的输入流，用于读取资源内容
     * 
     * <p>实现逻辑：
     * 1. 若资源不存在，抛出{@link FileNotFoundException}；
     * 2. 若资源是目录，抛出{@link FileNotFoundException}；
     * 3. 否则通过{@link Files#newInputStream(Path)}创建输入流（只读模式）。
     * 
     * @return 可读取资源的输入流（非空）
     * @throws FileNotFoundException 资源不存在或为目录时抛出
     * @throws IOException 打开输入流失败时抛出（如权限不足）
     */
    @Override
    default InputStream getInputStream() throws IOException {
        Path path = getPath();
        if (!exists()) {
            throw new FileNotFoundException(path + " (no such file or directory)");
        }
        if (Files.isDirectory(path)) {
            throw new FileNotFoundException(path + " (is a directory)");
        }

        return Files.newInputStream(path);
    }

    /**
     * 判断资源是否可写且为文件（非目录）
     * 
     * <p>可写条件：
     * 1. 路径可被当前进程写入（{@link Files#isWritable(Path)}）；
     * 2. 路径指向的是文件而非目录（{@link Files#isDirectory(Path)}）。
     * 
     * @return 资源可写且为文件返回true，否则返回false
     */
    @Override
    default boolean isWritable() {
        Path path = getPath();
        return (Files.isWritable(path) && !Files.isDirectory(path));
    }

    /**
     * 获取资源的内容长度（字节数）
     * 
     * <p>通过{@link Files#size(Path)}获取文件大小，仅适用于文件（目录会抛出异常）。
     * 
     * @return 资源的字节长度（大于等于0）
     * @throws IOException 无法获取文件大小时抛出（如资源不存在、为目录、权限不足）
     */
    @Override
    default long contentLength() throws IOException {
        return Files.size(getPath());
    }

    /**
     * 获取资源的最后修改时间（毫秒时间戳）
     * 
     * <p>通过{@link Files#getLastModifiedTime(Path)}获取文件的最后修改时间，
     * 转换为毫秒级时间戳返回（与{@link System#currentTimeMillis()}兼容）。
     * 
     * @return 最后修改时间的毫秒时间戳
     * @throws IOException 无法获取修改时间时抛出（如资源不存在、权限不足）
     */
    @Override
    default long lastModified() throws IOException {
        return Files.getLastModifiedTime(getPath()).toMillis();
    }

    /**
     * 获取资源的名称（路径中的文件名部分）
     * 
     * <p>通过{@link Path#getFileName()}获取路径的最后一个元素（即文件名），
     * 例如路径"/usr/local/file.txt"的名称为"file.txt"。
     * 
     * @return 资源的文件名（非空字符串，可能为空字符串但不会为null）
     */
    @Override
    default String getName() {
        return getPath().getFileName().toString();
    }

    /**
     * 获取资源的描述信息，用于日志或错误提示
     * 
     * <p>返回格式为"path [绝对路径]"，例如"path [/home/user/data.csv]"，
     * 便于定位资源在文件系统中的具体位置。
     * 
     * @return 资源的描述字符串（非空）
     */
    @Override
    default String getDescription() {
        return "path [" + getPath().toAbsolutePath() + "]";
    }
}