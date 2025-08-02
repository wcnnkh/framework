package run.soeasy.framework.core.domain;

import java.nio.charset.Charset;

/**
 * 字符集能力接口，定义了对象获取字符集信息的标准方法。
 * 实现该接口的类可以提供自身使用的字符集信息，支持字符集对象和字符集名称两种获取方式。
 *
 * <p>核心特性：
 * <ul>
 *   <li>统一接口：通过{@link #getCharset()}方法获取字符集对象</li>
 *   <li>便捷转换：通过{@link #getCharsetName()}方法获取字符集名称</li>
 *   <li>静态工具：提供静态方法{@link #getCharset(Object)}和{@link #getCharsetName(Object)}
 *               支持从任意对象安全获取字符集信息</li>
 *   <li>空安全：所有方法均支持处理null值，确保调用安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>文本编解码：确保数据在不同组件间传输时使用一致的字符集</li>
 *   <li>配置管理：统一管理系统中的字符集配置</li>
 *   <li>数据导入导出：确保导入导出的数据编码一致性</li>
 *   <li>网络通信：设置请求和响应的字符集编码</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 实现CharsetCapable接口的类
 * public class MyData implements CharsetCapable {
 *     private Charset charset = StandardCharsets.UTF_8;
 *     
 *     public Charset getCharset() {
 *         return charset;
 *     }
 * }
 * 
 * // 使用静态工具方法获取字符集
 * MyData data = new MyData();
 * Charset charset = CharsetCapable.getCharset(data); // 获取UTF-8
 * String charsetName = CharsetCapable.getCharsetName(data); // 获取"UTF-8"
 * </pre>
 *
 * @see Charset
 */
public interface CharsetCapable {
    /**
     * 从对象中提取字符集信息。
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>若对象为null，返回null</li>
     *   <li>若对象是{@link Charset}类型，直接返回</li>
     *   <li>若对象实现了{@link CharsetCapable}接口，调用其{@link #getCharset()}方法</li>
     *   <li>其他情况返回null</li>
     * </ol>
     *
     * @param charset 待提取字符集的对象，可为null
     * @return 字符集对象，可能为null
     */
    public static Charset getCharset(Object charset) {
        if (charset instanceof Charset) {
            return (Charset) charset;
        }

        if (charset instanceof CharsetCapable) {
            return ((CharsetCapable) charset).getCharset();
        }
        return null;
    }

    /**
     * 从对象中提取字符集名称。
     * <p>
     * 处理逻辑：
     * <ol>
     *   <li>若对象为null，返回null</li>
     *   <li>若对象是{@link String}类型，直接返回</li>
     *   <li>若对象实现了{@link CharsetCapable}接口，调用其{@link #getCharsetName()}方法</li>
     *   <li>其他情况返回null</li>
     * </ol>
     *
     * @param source 待提取字符集名称的对象，可为null
     * @return 字符集名称，可能为null
     */
    public static String getCharsetName(Object source) {
        if (source instanceof String) {
            return (String) source;
        }

        if (source instanceof CharsetCapable) {
            return ((CharsetCapable) source).getCharsetName();
        }
        return null;
    }

    /**
     * 获取当前对象使用的字符集。
     * <p>
     * 实现类应确保返回非空的字符集对象，若无法确定字符集，
     * 建议返回平台默认字符集{@link Charset#defaultCharset()}。
     *
     * @return 字符集对象，不可为null
     */
    Charset getCharset();

    /**
     * 获取当前对象使用的字符集名称。
     * <p>
     * 默认实现调用{@link #getCharset()}并返回其规范名称，
     * 实现类可根据需要重写此方法以提供更高效的实现。
     *
     * @return 字符集规范名称，如"UTF-8"、"ISO-8859-1"等
     */
    default String getCharsetName() {
        return getCharset().name();
    }
}