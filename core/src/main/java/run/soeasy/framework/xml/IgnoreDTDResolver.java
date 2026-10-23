package run.soeasy.framework.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.Getter;
import lombok.Setter;

/**
 * IgnoreDTDResolver 是一个实现了 SAX EntityResolver 接口的解析器，
 * 用于在 XML 解析过程中忽略外部 DTD（文档类型定义）的加载请求。
 * 该解析器会拦截对 DTD 文件的请求，并返回一个空的输入源，
 * 从而避免在解析没有网络连接或不需要验证的 XML 文档时因查找 DTD 而导致的错误或延迟。
 * 
 * <p>该类提供了一个单例实例 DEFAULT，方便在大多数场景下直接使用。
 * 通过设置 ignore 标志，可以动态控制是否忽略 DTD 加载请求，
 * 这在需要选择性验证某些 XML 文档时非常有用。
 * 
 * @see EntityResolver
 */
@Getter
@Setter
public class IgnoreDTDResolver implements EntityResolver {
    /**
     * 默认的 IgnoreDTDResolver 单例实例，可直接使用。
     */
    public static final EntityResolver DEFAULT = new IgnoreDTDResolver();

    /**
     * 控制是否忽略 DTD 加载请求的标志。
     * 设置为 true 时，所有 DTD 请求都将被忽略；
     * 设置为 false 时，DTD 请求将按默认方式处理。
     */
    private volatile boolean ignore = true;

    /**
     * 解析实体时调用的方法，用于处理外部实体（如 DTD）的请求。
     * 当 ignore 标志为 true 且请求的系统 ID 是一个 DTD 文件时，
     * 返回一个空的输入源，从而忽略该 DTD 的加载。
     * 
     * @param publicId 要解析的实体的公共标识符，如果没有则为 null
     * @param systemId 要解析的实体的系统标识符（绝对 URI）
     * @return 包含替代输入源的 InputSource 对象，如果返回 null 则使用默认解析机制
     * @throws SAXException 如果在解析过程中发生错误
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (ignore && systemId != null && systemId.endsWith(".dtd")) {
            return new InputSource(new StringReader(""));
        }
        return null;
    }
}