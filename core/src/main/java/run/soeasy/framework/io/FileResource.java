package run.soeasy.framework.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.io.watch.FileVariable;

/**
 * 文件资源实现类，基于{@link File}封装文件系统资源操作，
 * 实现{@link Resource}和{@link FileVariable}接口以提供统一资源访问和文件监控能力。
 * 
 * <p>该类将本地文件映射为可读写资源，支持文件内容读取、写入、
 * 元数据获取及文件变更监控等功能，适用于标准文件系统操作场景。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>文件系统操作：封装{@link File} API实现本地文件访问</li>
 *   <li>资源元数据：支持获取文件长度、修改时间等元数据</li>
 *   <li>双向读写：同时支持输入流({@link #getInputStream()})和输出流({@link #getOutputStream()})</li>
 *   <li>文件监控：实现{@link FileVariable}接口支持文件变更通知</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>文件内容读写：读取配置文件、写入日志数据等</li>
 *   <li>文件元数据操作：获取文件大小、修改时间等</li>
 *   <li>文件系统监控：通过{@link FileVariable}监听文件变更</li>
 *   <li>本地文件操作：替代直接使用{@link File}实现统一资源接口</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Resource
 * @see FileVariable
 * @see File
 */
@Data
class FileResource implements Resource, FileVariable {
    /** 被包装的本地文件，不可为null */
    @NonNull
    private final File file;

    /**
     * 判断文件是否存在。
     * <p>
     * 调用{@link File#exists()}检查文件是否存在，
     * 对于不存在的文件返回false。
     * 
     * @return true表示文件存在，false表示不存在
     */
    @Override
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * 判断资源是否可读。
     * <p>
     * 检查文件是否可读且不是目录，
     * 调用{@link File#canRead()}和{@link File#isDirectory()}。
     * 
     * @return true表示可读且非目录，false表示不可读或为目录
     */
    @Override
    public boolean isReadable() {
        return this.file.canRead() && !this.file.isDirectory();
    }

    /**
     * 获取输入流读取文件内容。
     * <p>
     * 直接调用{@link FileInputStream}构造函数，
     * 若文件不存在则抛出{@link FileNotFoundException}。
     * 
     * @return 文件内容的输入流
     * @throws FileNotFoundException 文件不存在时抛出
     * @throws IOException 读取过程中发生IO错误时抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * 判断资源是否可写。
     * <p>
     * 检查文件是否可写且不是目录，
     * 调用{@link File#canWrite()}和{@link File#isDirectory()}。
     * 
     * @return true表示可写且非目录，false表示不可写或为目录
     */
    @Override
    public boolean isWritable() {
        return this.file.canWrite() && !this.file.isDirectory();
    }

    /**
     * 获取输出流写入文件内容。
     * <p>
     * 直接调用{@link FileOutputStream}构造函数，
     * 若文件为目录则抛出{@link FileNotFoundException}。
     * 
     * @return 文件内容的输出流
     * @throws FileNotFoundException 文件为目录时抛出
     * @throws IOException 写入过程中发生IO错误时抛出
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }

    /**
     * 获取资源内容长度（字节数）。
     * <p>
     * 调用{@link File#length()}获取文件大小，
     * 若文件不存在且长度为0则抛出{@link FileNotFoundException}。
     * 
     * @return 内容长度（字节）
     * @throws FileNotFoundException 文件不存在且长度为0时抛出
     * @throws IOException 读取长度时发生IO错误时抛出
     */
    @Override
    public long contentLength() throws IOException {
        long length = this.file.length();
        if (length == 0L && !this.file.exists()) {
            throw new FileNotFoundException(
                    getDescription() + " cannot be resolved in the file system for checking its content length");
        }
        return length;
    }

    /**
     * 获取资源最后修改时间。
     * <p>
     * 调用{@link File#lastModified()}获取文件修改时间，
     * 返回毫秒时间戳。
     * 
     * @return 最后修改时间戳（毫秒）
     * @throws IOException 通常不会抛出，保持接口一致性
     */
    @Override
    public long lastModified() throws IOException {
        return file.lastModified();
    }

    /**
     * 获取资源名称（文件名）。
     * <p>
     * 调用{@link File#getName()}获取文件名，
     * 不包含路径信息。
     * 
     * @return 资源名称（如"file.txt"）
     */
    @Override
    public String getName() {
        return this.file.getName();
    }

    /**
     * 获取资源描述信息（绝对路径）。
     * <p>
     * 返回格式为"file [绝对路径]"，如"file [/usr/local/file.txt]"。
     * 
     * @return 资源描述字符串
     */
    @Override
    public String getDescription() {
        return "file [" + this.file.getAbsolutePath() + "]";
    }
}