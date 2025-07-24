package run.soeasy.framework.messaging.convert.support;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

import lombok.NonNull;
import run.soeasy.framework.beans.BeanFormat;
import run.soeasy.framework.codec.format.URLCodec;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 查询字符串格式化工具类，继承自{@link BeanFormat}，专注于URL查询字符串（如key1=value1&key2=value2）的格式化与解析，
 * 支持对象与查询字符串之间的双向转换，并集成URL编码/解码（通过{@link URLCodec}）处理特殊字符。
 * 
 * <p>核心特性：
 * - 使用"&"作为键值对分隔符，"="作为键值分隔符，符合URL查询字符串规范；
 * - 基于{@link ConcurrentHashMap}实现线程安全的实例缓存，避免重复创建；
 * - 支持自定义字符集（{@link Charset}），适配不同编码场景（如UTF-8、ISO-8859-1）。
 * 
 * @author soeasy.run
 * @see BeanFormat
 * @see URLCodec
 * @see Charset
 */
public class QueryStringFormat extends BeanFormat {

    /**
     * 线程安全的查询字符串格式化实例缓存，键为字符集名称，值为对应字符集的{@link QueryStringFormat}实例，
     * 用于复用实例，提高性能（避免频繁创建编码解码器）。
     */
    private static ConcurrentHashMap<String, QueryStringFormat> formatCacheMap = new ConcurrentHashMap<>();

    /**
     * 基于指定字符集创建查询字符串格式化实例，内部使用{@link URLCodec}处理编码/解码
     * 
     * @param charset 字符集（非空，用于URL编码/解码，如StandardCharsets.UTF_8）
     */
    public QueryStringFormat(Charset charset) {
        this(new URLCodec(charset));
    }

    /**
     * 基于指定的{@link URLCodec}创建查询字符串格式化实例，显式指定编码/解码器
     * 
     * <p>父类构造参数说明：
     * - 键值对分隔符："&"（如key1=val1&key2=val2）；
     * - 键值分隔符："="（如key=value）；
     * - 编码器与解码器：均使用传入的{@link URLCodec}，确保键和值的URL安全编码。
     * 
     * @param urlCodec URL编码/解码器（非空，处理特殊字符如空格、&、=等）
     */
    public QueryStringFormat(URLCodec urlCodec) {
        super("&", "=", urlCodec, urlCodec);
    }

    /**
     * 从缓存中获取或创建指定名称和字符集的查询字符串格式化实例
     * 
     * <p>缓存机制：若缓存中存在对应名称的实例则直接返回，否则创建新实例并放入缓存，
     * 适用于需要区分多个查询字符串格式场景（如不同业务模块使用不同配置）。
     * 
     * @param name 格式化实例的名称（非空，作为缓存键）
     * @param charset 字符集（非空，用于URL编码/解码）
     * @return 缓存的或新创建的{@link QueryStringFormat}实例（非空）
     */
    public static QueryStringFormat getFormat(@NonNull String name, @NonNull Charset charset) {
        QueryStringFormat format = formatCacheMap.get(name);
        if (format == null) {
            format = formatCacheMap.computeIfAbsent(name, (key) -> new QueryStringFormat(charset));
        }
        return format;
    }

    /**
     * 从缓存中获取或创建指定字符集的查询字符串格式化实例（使用字符集名称作为缓存键）
     * 
     * <p>简化版getFormat，适用于仅需按字符集区分的场景（大多数情况）。
     * 
     * @param charset 字符集（非空，用于URL编码/解码）
     * @return 缓存的或新创建的{@link QueryStringFormat}实例（非空）
     */
    public static QueryStringFormat getFormat(@NonNull Charset charset) {
        return getFormat(charset.name(), charset);
    }

    /**
     * 将对象格式化为查询字符串（使用指定字符集编码）
     * 
     * <p>示例：将User对象{id=1, name="test"}转换为"id=1&name=test"（特殊字符会URL编码）。
     * 
     * @param charset 字符集（非空，用于编码）
     * @param source 待格式化的对象（非空，如JavaBean、Map等）
     * @return 格式化后的查询字符串（已URL编码，非空）
     */
    public static String format(@NonNull Charset charset, @NonNull Object source) {
        return getFormat(charset).convert(source, String.class);
    }

    /**
     * 将查询字符串解析为目标类型的对象（使用指定字符集解码）
     * 
     * <p>示例：将"id=1&name=test"解析为User对象{id=1, name="test"}（自动解码URL编码的字符）。
     * 
     * @param charset 字符集（非空，用于解码）
     * @param source 待解析的查询字符串（可为null，null时返回null）
     * @param targetTypeDescriptor 目标类型描述符（非空，指定解析后的对象类型）
     * @return 解析后的目标类型对象（符合targetTypeDescriptor指定的类型）
     */
    public static Object parse(@NonNull Charset charset, String source, @NonNull TypeDescriptor targetTypeDescriptor) {
        return getFormat(charset).convert(source, targetTypeDescriptor);
    }
}