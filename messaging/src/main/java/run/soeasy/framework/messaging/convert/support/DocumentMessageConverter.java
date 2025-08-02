package run.soeasy.framework.messaging.convert.support;

import java.util.Arrays;

import run.soeasy.framework.messaging.MediaType;

/**
 * XML文档消息转换器，继承自{@link TextMessageConverter}，
 * 专门用于处理XML格式的文档消息转换，支持多种标准XML媒体类型，
 * 可将XML文本与Java对象进行相互转换（如XML反序列化为对象、对象序列化为XML）。
 * 
 * <p>该转换器默认支持以下XML相关媒体类型：
 * - {@link MediaType#APPLICATION_XML}（application/xml）
 * - {@link MediaType#APPLICATION_ATOM_XML}（application/atom+xml）
 * - {@link MediaType#APPLICATION_XHTML_XML}（application/xhtml+xml）
 * - {@link MediaType#APPLICATION_RSS_XML}（application/rss+xml）
 * 
 * <p>作为{@link TextMessageConverter}的子类，它继承了文本消息的字符集处理逻辑，
 * 自动处理XML文本的字符编码（如UTF-8），并委托父类完成字符串与字节数组的转换，
 * 子类仅需专注于XML与Java对象的核心转换逻辑。
 * 
 * @author soeasy.run
 * @see TextMessageConverter
 * @see MediaType
 */
public class DocumentMessageConverter extends TextMessageConverter {

    /**
     * 初始化XML文档消息转换器，注册支持的XML媒体类型
     * 
     * <p>构造时向媒体类型注册表添加标准XML相关类型，确保仅处理符合这些类型的消息，
     * 为XML格式的文档转换提供明确的类型标识。
     */
    public DocumentMessageConverter() {
        getMediaTypeRegistry().addAll(Arrays.asList(
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_ATOM_XML,
            MediaType.APPLICATION_XHTML_XML,
            MediaType.APPLICATION_RSS_XML
        ));
    }
}