package run.soeasy.framework.messaging;

import java.nio.charset.Charset;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.io.MimeType;

/**
 * 消息接口，定义了消息的基本契约，整合消息头部元数据与字符集处理能力，
 * 是消息传递框架中表示消息的核心接口，支持获取消息头部、内容长度、内容类型及字符集等基础信息。
 * 
 * <p>该接口继承自{@link CharsetCapable}，提供字符集相关的标准访问方法，
 * 并通过{@link Headers}管理消息的元数据（如Content-Type、Content-Length等），
 * 适用于各类消息场景（如HTTP请求/响应、队列消息、RPC通信等）。
 * 
 * @author soeasy.run
 * @see Headers
 * @see CharsetCapable
 * @see MimeType
 * @see MediaType
 */
public interface Message extends CharsetCapable {

    /**
     * 获取消息头部信息
     * 
     * <p>消息头部包含了消息的元数据，如内容类型（Content-Type）、内容长度（Content-Length）、
     * 编码方式等，通过{@link Headers}接口统一管理。
     * 
     * @return 消息头部对象（非空，包含消息的元数据信息）
     */
    Headers getHeaders();

    /**
     * 获取消息内容的长度（默认实现）
     * 
     * <p>默认通过{@link Headers#getContentLength()}获取内容长度，单位通常为字节，
     * 表示消息体的字节数，适用于需要计算消息大小的场景（如传输限流、存储优化等）。
     * 
     * @return 消息内容的长度（字节数，若未设置可能返回-1或0）
     */
    default long getContentLength() {
        return getHeaders().getContentLength();
    }

    /**
     * 获取消息的内容类型（默认实现）
     * 
     * <p>默认通过{@link Headers#getContentType()}获取消息的MIME类型（如application/json、text/plain），
     * 用于标识消息内容的格式，指导接收方进行解析处理。
     * 
     * @return 消息的内容类型（{@link MediaType}实例，可能为null表示未指定）
     */
    default MediaType getContentType() {
        return getHeaders().getContentType();
    }

    /**
     * 获取消息内容的字符集（实现{@link CharsetCapable}接口）
     * 
     * <p>逻辑：
     * 1. 通过{@link #getCharsetName()}获取字符集名称；
     * 2. 若名称非空，通过{@link Charset#forName(String)}转换为{@link Charset}对象；
     * 3. 若名称为空，返回null。
     * 
     * @return 字符集对象（可能为null，未指定字符集时返回null）
     */
    @Override
    default Charset getCharset() {
        String name = getCharsetName();
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return Charset.forName(name);
    }

    /**
     * 获取消息内容的字符集名称（重写{@link CharsetCapable}方法）
     * 
     * <p>从消息的内容类型（{@link MimeType}）中提取字符集名称（如UTF-8、ISO-8859-1），
     * 字符集通常作为Content-Type的参数存在（如text/html;charset=UTF-8）。
     * 
     * @return 字符集名称（可能为null，内容类型未指定字符集时返回null）
     */
    @Override
    default String getCharsetName() {
        MimeType mimeType = getContentType();
        return mimeType == null ? null : mimeType.getCharsetName();
    }
}