package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.function.Pipeline;

/**
 * 可定制的资源实现类，支持动态配置资源属性和行为。
 * 该类实现了{@link Resource}接口，通过委派模式将输入输出操作委托给
 * {@link InputSource}和{@link OutputSource}，并提供属性的getter/setter方法，
 * 允许在运行时动态定制资源特性。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>动态配置：支持运行时设置内容长度、修改时间、描述等属性</li>
 *   <li>灵活扩展：通过设置不同的InputSource/OutputSource实现多种资源类型</li>
 *   <li>默认值支持：未显式设置的属性使用接口默认实现或特殊值</li>
 *   <li>状态检查：提供isReadable/isWritable等方法检查资源可用性</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>测试环境：创建模拟资源用于单元测试</li>
 *   <li>动态资源：运行时根据条件构建不同类型的资源</li>
 *   <li>适配器：将非标准资源适配为统一的Resource接口</li>
 *   <li>组合资源：将多个资源特性组合成一个复合资源</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Resource
 * @see InputSource
 * @see OutputSource
 */
@Getter
@Setter
public class CustomizeResource implements Resource {
    /** 资源内容长度（字节），-1表示未知长度 */
    private long contentLength = -1;
    /** 资源描述信息，null时使用接口默认实现 */
    private String description;
    /** 输入源，提供读取资源内容的能力 */
    private InputSource inputSource;
    /** 资源最后修改时间戳（毫秒），0表示未指定 */
    private long lastModified = 0;
    /** 资源名称，null时使用接口默认实现 */
    private String name;
    /** 输出源，提供写入资源内容的能力 */
    private OutputSource outputSource;

    /**
     * 获取资源内容长度。
     * <p>
     * 返回{@link #contentLength}属性值，-1表示长度未知。
     * 
     * @return 资源内容长度（字节）
     * @throws IOException 通常不会抛出，保持接口一致性
     */
    @Override
    public long contentLength() throws IOException {
        return contentLength;
    }

    /**
     * 获取资源描述信息。
     * <p>
     * 如果{@link #description}已设置则返回该值，
     * 否则调用接口默认方法生成描述。
     * 
     * @return 资源描述字符串
     */
    @Override
    public String getDescription() {
        return description == null ? Resource.super.getDescription() : description;
    }

    /**
     * 获取资源输入流。
     * <p>
     * 将操作委派给{@link #inputSource}，
     * 如果inputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 资源输入流
     * @throws IOException                   读取流时发生异常
     * @throws UnsupportedOperationException inputSource未设置时抛出
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (inputSource == null) {
            throw new UnsupportedOperationException();
        }
        return inputSource.getInputStream();
    }

    /**
     * 获取资源输入流流水线。
     * <p>
     * 将操作委派给{@link #inputSource}，
     * 如果inputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 输入流流水线
     * @throws UnsupportedOperationException inputSource未设置时抛出
     */
    @Override
    public @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
        if (inputSource == null) {
            throw new UnsupportedOperationException();
        }
        return inputSource.getInputStreamPipeline();
    }

    /**
     * 获取资源名称。
     * <p>
     * 如果{@link #name}已设置则返回该值，
     * 否则调用接口默认方法生成名称。
     * 
     * @return 资源名称
     */
    @Override
    public String getName() {
        return name == null ? Resource.super.getName() : name;
    }

    /**
     * 获取资源输出流。
     * <p>
     * 将操作委派给{@link #outputSource}，
     * 如果outputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 资源输出流
     * @throws IOException                   写入流时发生异常
     * @throws UnsupportedOperationException outputSource未设置时抛出
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (outputSource == null) {
            throw new UnsupportedOperationException();
        }
        return outputSource.getOutputStream();
    }

    /**
     * 获取资源输出流流水线。
     * <p>
     * 将操作委派给{@link #outputSource}，
     * 如果outputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 输出流流水线
     * @throws UnsupportedOperationException outputSource未设置时抛出
     */
    @Override
    public @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
        if (outputSource == null) {
            throw new UnsupportedOperationException();
        }
        return outputSource.getOutputStreamPipeline();
    }

    /**
     * 获取资源读取器。
     * <p>
     * 将操作委派给{@link #inputSource}，
     * 如果inputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 资源读取器
     * @throws IOException                   读取时发生异常
     * @throws UnsupportedOperationException inputSource未设置时抛出
     */
    @Override
    public Reader getReader() throws IOException {
        if (inputSource == null) {
            throw new UnsupportedOperationException();
        }
        return inputSource.getReader();
    }

    /**
     * 获取资源读取器流水线。
     * <p>
     * 将操作委派给{@link #inputSource}，
     * 如果inputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 读取器流水线
     * @throws UnsupportedOperationException inputSource未设置时抛出
     */
    @Override
    public @NonNull Pipeline<Reader, IOException> getReaderPipeline() {
        if (inputSource == null) {
            throw new UnsupportedOperationException();
        }
        return inputSource.getReaderPipeline();
    }

    /**
     * 获取资源写入器。
     * <p>
     * 将操作委派给{@link #outputSource}，
     * 如果outputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 资源写入器
     * @throws IOException                   写入时发生异常
     * @throws UnsupportedOperationException outputSource未设置时抛出
     */
    @Override
    public Writer getWriter() throws IOException {
        if (outputSource == null) {
            throw new UnsupportedOperationException();
        }
        return outputSource.getWriter();
    }

    /**
     * 获取资源写入器流水线。
     * <p>
     * 将操作委派给{@link #outputSource}，
     * 如果outputSource为null则抛出{@link UnsupportedOperationException}。
     * 
     * @return 写入器流水线
     * @throws UnsupportedOperationException outputSource未设置时抛出
     */
    @Override
    public @NonNull Pipeline<Writer, IOException> getWriterPipeline() {
        if (outputSource == null) {
            throw new UnsupportedOperationException();
        }
        return outputSource.getWriterPipeline();
    }

    /**
     * 判断资源是否已解码。
     * <p>
     * 将操作委派给{@link #inputSource}，
     * 如果inputSource为null则调用接口默认方法。
     * 
     * @return true表示资源已解码
     */
    @Override
    public boolean isDecoded() {
        return inputSource != null ? inputSource.isDecoded() : Resource.super.isDecoded();
    }

    /**
     * 判断资源是否已编码。
     * <p>
     * 将操作委派给{@link #outputSource}，
     * 如果outputSource为null则调用接口默认方法。
     * 
     * @return true表示资源已编码
     */
    @Override
    public boolean isEncoded() {
        return outputSource != null ? outputSource.isEncoded() : Resource.super.isEncoded();
    }

    /**
     * 判断资源是否可读。
     * <p>
     * 如果{@link #inputSource}已设置则返回true，否则返回false。
     * 
     * @return true表示资源可读
     */
    @Override
    public boolean isReadable() {
        return inputSource != null;
    }

    /**
     * 判断资源是否可写。
     * <p>
     * 如果{@link #outputSource}已设置则返回true，否则返回false。
     * 
     * @return true表示资源可写
     */
    @Override
    public boolean isWritable() {
        return outputSource != null;
    }

    /**
     * 获取资源最后修改时间。
     * <p>
     * 返回{@link #lastModified}属性值，0表示未指定。
     * 
     * @return 最后修改时间戳（毫秒）
     * @throws IOException 通常不会抛出，保持接口一致性
     */
    @Override
    public long lastModified() throws IOException {
        return lastModified;
    }

    /**
     * 返回资源描述字符串。
     * <p>
     * 直接返回{@link #getDescription()}的结果。
     * 
     * @return 资源描述字符串
     */
    @Override
    public final String toString() {
        return getDescription();
    }
}