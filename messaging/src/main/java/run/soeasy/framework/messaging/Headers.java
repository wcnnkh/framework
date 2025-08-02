package run.soeasy.framework.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.AbstractMultiValueMap;
import run.soeasy.framework.core.collection.LinkedCaseInsensitiveMap;

/**
 * 消息头集合类，继承自{@link AbstractMultiValueMap}，实现多值映射功能，
 * 专门用于管理消息头（如HTTP请求头/响应头），支持大小写不敏感的键操作和标准HTTP头字段处理。
 * 
 * <p>核心特性：
 * - 支持单键多值存储（符合HTTP头字段可重复的特性）；
 * - 可配置键是否区分大小写（默认不区分，符合HTTP头字段规范）；
 * - 提供HTTP标准头字段（如Content-Type、Content-Length）的便捷操作方法；
 * - 支持设置为只读模式，防止意外修改。
 * 
 * @author soeasy.run
 * @see AbstractMultiValueMap
 * @see MediaType
 */
public class Headers extends AbstractMultiValueMap<String, String, Map<String, List<String>>> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 表示空的只读消息头实例（不可修改）
     */
    public static final Headers EMPTY = new Headers(Collections.emptyMap(), true);

    /**
     * Content-Length头字段名，用于指定消息体的长度（字节数）
     * 
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">RFC 7230 Section 3.3.2</a>
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * Content-Type头字段名，用于指定消息体的媒体类型
     * 
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.5">RFC 7231 Section 3.1.1.5</a>
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * Content-Disposition头字段名，用于指定消息体的处置方式（如文件下载时的文件名）
     * 
     * @see <a href="https://tools.ietf.org/html/rfc6266">RFC 6266</a>
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * 存储消息头键值对的底层Map，键为头字段名，值为头字段值的列表
     */
    private Map<String, List<String>> source;

    /**
     * 是否为只读模式（true表示不可修改）
     */
    private boolean readyOnly;

    {
        // 设置值列表的创建器，使用LinkedList作为底层存储（适合频繁添加操作）
        setValuesCreator((key) -> new LinkedList<>());
    }

    /**
     * 创建一个新的消息头实例，指定键是否区分大小写
     * 
     * @param caseSensitiveKey true表示键区分大小写，false表示不区分（符合HTTP头规范）
     */
    public Headers(boolean caseSensitiveKey) {
        if (caseSensitiveKey) {
            this.source = new LinkedHashMap<String, List<String>>(8);
        } else {
            // 使用不区分大小写的Map实现，基于英文Locale
            this.source = new LinkedCaseInsensitiveMap<List<String>>(8, Locale.ENGLISH);
        }
    }

    /**
     * 基于已有头字段映射创建消息头实例，并指定是否为只读
     * 
     * @param headers 已有头字段映射（键为头名，值为头值列表）
     * @param readyOnly 是否设置为只读模式
     */
    public Headers(Map<String, List<String>> headers, boolean readyOnly) {
        this.source = readyOnly ? Collections.unmodifiableMap(headers) : headers;
        this.readyOnly = readyOnly;
    }

    /**
     * 克隆一个新的消息头实例，复制源实例的所有头字段
     * 
     * @param headers 源消息头实例（非空）
     */
    public Headers(@NonNull Headers headers) {
        this(headers.isCaseSensitiveKey());
        for (Entry<String, List<String>> entry : headers.source.entrySet()) {
            // 复制值列表，避免外部修改影响
            this.source.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        this.readyOnly = headers.readyOnly;
    }

    /**
     * 获取底层存储的头字段映射
     * 
     * @return 头字段映射（键为头名，值为头值列表）
     */
    @Override
    public Map<String, List<String>> getSource() {
        return source;
    }

    /**
     * 配置键是否区分大小写
     * 
     * <p>若当前模式与目标模式不同，会创建新的Map并复制原有内容，保持原有顺序。
     * 
     * @param caseSensitiveKey true表示键区分大小写，false表示不区分
     */
    public void caseSensitiveKey(boolean caseSensitiveKey) {
        Map<String, List<String>> map = source;
        if (caseSensitiveKey) {
            // 转换为区分大小写的Map（LinkedHashMap）
            if (source instanceof LinkedCaseInsensitiveMap) {
                map = new LinkedHashMap<String, List<String>>();
                map.putAll(source);
            }
        } else {
            // 转换为不区分大小写的Map（LinkedCaseInsensitiveMap）
            if (!(source instanceof LinkedCaseInsensitiveMap)) {
                map = new LinkedCaseInsensitiveMap<List<String>>(source.size(), Locale.ENGLISH);
                map.putAll(source);
            }
        }
        this.source = map;
    }

    /**
     * 判断当前消息头是否为只读模式
     * 
     * @return 只读返回true，否则返回false
     */
    public final boolean isReadyOnly() {
        return readyOnly;
    }

    /**
     * 判断当前消息头的键是否区分大小写
     * 
     * @return 区分大小写返回true，否则返回false
     */
    public final boolean isCaseSensitiveKey() {
        return !(source instanceof LinkedCaseInsensitiveMap);
    }

    /**
     * 将当前消息头设置为只读模式，设置后无法修改
     */
    public void readyOnly() {
        if (isReadyOnly()) {
            return;
        }
        this.readyOnly = true;
        this.source = Collections.unmodifiableMap(this.source);
    }

    /**
     * 配置键是否区分大小写并设置为只读模式
     * 
     * @param caseSensitiveKey true表示键区分大小写，false表示不区分
     */
    public void readyOnly(boolean caseSensitiveKey) {
        caseSensitiveKey(caseSensitiveKey);
        readyOnly();
    }

    /**
     * 获取指定头字段的所有值，并按指定分隔符拆分后返回
     * 
     * <p>适用于处理逗号分隔的头字段值（如Accept、Allow等），拆分后去重并保持顺序。
     * 
     * @param headerName 头字段名
     * @param tokenize 分隔符（如","）
     * @return 拆分后的所有值列表（非null，可能为空）
     */
    public List<String> getValuesAsList(String headerName, String tokenize) {
        List<String> values = get(headerName);
        if (values != null) {
            List<String> result = new ArrayList<String>();
            for (String value : values) {
                if (value != null) {
                    String[] tokens = StringUtils.tokenizeToArray(value, tokenize);
                    for (String token : tokens) {
                        result.add(token);
                    }
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    /**
     * 设置multipart/form-data类型的Content-Disposition头字段
     * 
     * <p>格式为：form-data; name="控件名"; filename="文件名"（文件名可选）
     * 
     * @param name 表单控件名（非空）
     * @param filename 文件名（可为null，不指定时不包含filename参数）
     */
    public void setContentDispositionFormData(@NonNull String name, String filename) {
        StringBuilder builder = new StringBuilder("form-data; name=\"");
        builder.append(name).append('\"');
        if (filename != null) {
            builder.append("; filename=\"");
            builder.append(filename).append('\"');
        }
        set(CONTENT_DISPOSITION, builder.toString());
    }

    /**
     * 设置Content-Length头字段，指定消息体的长度（字节数）
     * 
     * @param contentLength 消息体长度（字节数，非负）
     */
    public void setContentLength(long contentLength) {
        set(CONTENT_LENGTH, Long.toString(contentLength));
    }

    /**
     * 获取Content-Length头字段的值，即消息体的长度（字节数）
     * 
     * @return 消息体长度，未知时返回-1
     */
    public long getContentLength() {
        String value = getFirst(CONTENT_LENGTH);
        return (value != null ? Long.parseLong(value) : -1);
    }

    /**
     * 设置Content-Type头字段，指定消息体的媒体类型
     * 
     * <p>不允许使用通配符类型（如*&#47;*或text/*），确保消息体类型明确。
     * 
     * @param mediaType 媒体类型（可为null，null表示移除该头字段）
     * @throws IllegalArgumentException 若媒体类型包含通配符
     */
    public void setContentType(MediaType mediaType) {
        if (mediaType == null) {
            remove(CONTENT_TYPE);
            return;
        }

        Assert.isTrue(!mediaType.isWildcardType(), "Content-Type cannot contain wildcard type '*'");
        Assert.isTrue(!mediaType.isWildcardSubtype(), "Content-Type cannot contain wildcard subtype '*'");
        set(CONTENT_TYPE, mediaType.toString());
    }

    /**
     * 获取Content-Type头字段对应的MediaType对象
     * 
     * @return 媒体类型对象，未知时返回null
     * @throws InvalidMediaTypeException 若头字段值格式非法
     */
    public MediaType getContentType() {
        String value = getFirst(CONTENT_TYPE);
        return (StringUtils.isEmpty(value) ? null : MediaType.parseMediaType(value));
    }
}
    