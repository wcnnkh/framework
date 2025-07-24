package run.soeasy.framework.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.io.watch.PathVariable;

/**
 * 路径资源实现类，基于{@link Path}封装文件系统资源操作，
 * 实现{@link Resource}和{@link PathVariable}接口以提供统一资源访问和路径监控能力。
 * 
 * <p>该类将文件系统路径映射为可读写资源，支持文件内容读取、写入、
 * 元数据获取及路径变更监控等功能，适用于标准文件系统操作场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>文件系统操作：封装{@link Files} API实现路径资源访问</li>
 *   <li>资源元数据：支持获取文件长度、修改时间等元数据</li>
 *   <li>双向读写：同时支持输入流({@link #getInputStream()})和输出流({@link #getOutputStream()})</li>
 *   <li>路径监控：实现{@link PathVariable}接口支持路径变更通知</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件内容读写：读取配置文件、写入日志数据等</li>
 *   <li>文件元数据操作：获取文件大小、修改时间等</li>
 *   <li>文件系统监控：通过{@link PathVariable}监听路径变更</li>
 *   <li>字节通道操作：通过{@link #readableChannel()}进行高效IO</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Resource
 * @see PathVariable
 * @see Path
 */
@Data
class PathResource implements Resource, PathVariable {
    /** 被包装的文件系统路径，不可为null */
    @NonNull
    private final Path path;

    /**
     * 判断路径是否存在。
     * <p>
     * 调用{@link Files#exists(Path)}检查路径是否存在，
     * 对于不存在的路径返回false。
     * 
     * @return true表示路径存在，false表示不存在
     */
    @Override
    public boolean exists() {
        return Files.exists(this.path);
    }

    /**
     * 判断资源是否可读。
     * <p>
     * 检查路径是否可读且不是目录，
     * 调用{@link Files#isReadable(Path)}和{@link Files#isDirectory(Path)}。
     * 
     * @return true表示可读且非目录，false表示不可读或为目录
     */
    @Override
    public boolean isReadable() {
        return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
    }

    /**
     * 获取输入流读取路径内容。
     * <p>
     * 若路径不存在或为目录则抛出{@link FileNotFoundException}，
     * 否则调用{@link Files#newInputStream(Path)}创建输入流。
     * 
     * @return 路径内容的输入流
     * @throws FileNotFoundException 路径不存在或为目录时抛出
     * @throws IOException 读取过程中发生IO错误时抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(getPath() + " (no such file or directory)");
        }
        if (Files.isDirectory(this.path)) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }

        return Files.newInputStream(this.path);
    }

    /**
     * 判断资源是否可写。
     * <p>
     * 检查路径是否可写且不是目录，
     * 调用{@link Files#isWritable(Path)}和{@link Files#isDirectory(Path)}。
     * 
     * @return true表示可写且非目录，false表示不可写或为目录
     */
    @Override
    public boolean isWritable() {
        return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
    }

    /**
     * 获取输出流写入路径内容。
     * <p>
     * 若路径为目录则抛出{@link FileNotFoundException}，
     * 否则调用{@link Files#newOutputStream(Path)}创建输出流。
     * 
     * @return 路径内容的输出流
     * @throws FileNotFoundException 路径为目录时抛出
     * @throws IOException 写入过程中发生IO错误时抛出
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (Files.isDirectory(this.path)) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newOutputStream(this.path);
    }

    /**
     * 获取可读字节通道。
     * <p>
     * 调用{@link Files#newByteChannel(Path, StandardOpenOption...)}
     * 并设置{@link StandardOpenOption#READ}选项，
     * 若路径不存在则转换为{@link FileNotFoundException}。
     * 
     * @return 可读字节通道
     * @throws FileNotFoundException 路径不存在时抛出
     * @throws IOException 打开通道时发生IO错误时抛出
     */
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return Files.newByteChannel(this.path, StandardOpenOption.READ);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    /**
     * 获取可写字节通道。
     * <p>
     * 调用{@link Files#newByteChannel(Path, StandardOpenOption...)}
     * 并设置{@link StandardOpenOption#WRITE}选项。
     * 
     * @return 可写字节通道
     * @throws IOException 打开通道时发生IO错误时抛出
     */
    public WritableByteChannel writableChannel() throws IOException {
        return Files.newByteChannel(this.path, StandardOpenOption.WRITE);
    }

    /**
     * 获取资源内容长度（字节数）。
     * <p>
     * 调用{@link Files#size(Path)}获取路径对应文件的大小，
     * 若路径为目录或不存在则抛出异常。
     * 
     * @return 内容长度（字节）
     * @throws IOException 读取长度时发生IO错误时抛出
     */
    @Override
    public long contentLength() throws IOException {
        return Files.size(this.path);
    }

    /**
     * 获取资源最后修改时间。
     * <p>
     * 调用{@link Files#getLastModifiedTime(Path)}获取文件修改时间，
     * 转换为毫秒时间戳返回。
     * 
     * @return 最后修改时间戳（毫秒）
     * @throws IOException 获取修改时间时发生IO错误时抛出
     */
    @Override
    public long lastModified() throws IOException {
        return Files.getLastModifiedTime(this.path).toMillis();
    }

    /**
     * 获取资源名称（路径的文件名部分）。
     * <p>
     * 调用{@link Path#getFileName()}获取路径的最后一个元素，
     * 转换为字符串返回。
     * 
     * @return 资源名称（如"file.txt"）
     */
    @Override
    public String getName() {
        return this.path.getFileName().toString();
    }

    /**
     * 获取资源描述信息（绝对路径）。
     * <p>
     * 返回格式为"path [绝对路径]"，如"path [/usr/local/file.txt]"。
     * 
     * @return 资源描述字符串
     */
    @Override
    public String getDescription() {
        return "path [" + this.path.toAbsolutePath() + "]";
    }
}