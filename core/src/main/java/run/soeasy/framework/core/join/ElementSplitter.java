package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.io.IOUtils;

/**
 * 元素分割器实现，支持按指定分隔符分割文本，并通过编解码器处理元素的编码和解码。
 * 该类继承自{@link ElementJoining}并实现{@link Splitter}接口，
 * 兼具元素连接和分割的双向操作能力。
 *
 * <p>核心特性：
 * <ul>
 *   <li>双向处理：同时支持元素连接（继承自父类）和文本分割</li>
 *   <li>编解码支持：通过{@link Codec}实现元素的编码（连接时）和解码（分割时）</li>
 *   <li>空值处理：自动跳过null元素，并支持null值的编解码转换</li>
 *   <li>流式处理：使用{@link Stream}处理分割结果，支持函数式操作</li>
 *   <li>高效IO：基于{@link IOUtils}实现高性能的文本分割</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>CSV格式数据的解析与生成</li>
 *   <li>自定义协议消息的拆包与打包</li>
 *   <li>配置文件中列表参数的解析</li>
 *   <li>需要编解码处理的文本分割场景（如URL参数、Base64编码等）</li>
 * </ul>
 *
 * @see ElementJoining
 * @see Splitter
 * @see Codec
 */
@Getter
@Setter
public class ElementSplitter extends ElementJoining<Object> implements Splitter<String> {
    
    /**
     * 元素解码器，用于将分割后的字符串转换为目标类型。
     * 该解码器在分割操作时应用，不可为null。
     */
    @NonNull
    private final Function<String, String> decoder;

    /**
     * 构造元素分割器实例，指定分隔符和编解码器。
     * <p>
     * 该构造函数会：
     * <ol>
     *   <li>调用父类构造函数，使用codec的encode方法作为元素编码器</li>
     *   <li>初始化decoder为codec的decode方法</li>
     * </ol>
     *
     * @param delimiter 元素分隔符，不可为null
     * @param codec 编解码器，用于元素的编码和解码，不可为null
     */
    public ElementSplitter(@NonNull CharSequence delimiter, @NonNull Codec<String, String> codec) {
        super(delimiter, (value) -> value == null ? null : codec.encode(String.valueOf(value)));
        this.decoder = (value) -> value == null ? null : codec.decode(value);
    }

    /**
     * 按指定分隔符分割可读输入流，并对每个元素应用解码器。
     * <p>
     * 处理流程：
     * <ol>
     *   <li>使用{@link IOUtils#split}按分隔符分割输入</li>
     *   <li>将每个分割结果转换为字符串（处理null）</li>
     *   <li>对每个字符串应用{@link #decoder}进行解码</li>
     *   <li>返回包含解码结果的Stream</li>
     * </ol>
     *
     * @param readable 可读输入流，不可为null
     * @return 包含解码后元素的Stream
     * @throws IOException 当读取或分割操作失败时抛出
     */
    @Override
    public Stream<String> split(@NonNull Readable readable) throws IOException {
        return IOUtils.split(readable, getDelimiter())
                .map((e) -> e == null ? null : e.toString())
                .map(decoder);
    }
}