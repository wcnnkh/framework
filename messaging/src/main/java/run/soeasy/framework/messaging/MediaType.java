package run.soeasy.framework.messaging;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.io.InvalidMimeTypeException;
import run.soeasy.framework.io.MimeType;
import run.soeasy.framework.io.MimeTypeUtils;

/**
 * MediaType类继承自{@link MimeType}，扩展了对媒体类型质量因子（q参数）的支持，
 * 是HTTP协议中处理内容协商（如Accept头、Content-Type头）的核心类，遵循RFC 7231规范。
 * 
 * <p>该类在{@link MimeType}基础上新增了质量因子相关的功能，包括：
 * - 验证质量因子（q参数）的合法性（必须在0.0-1.0之间）；
 * - 提供质量因子的获取、复制和移除方法；
 * - 增强媒体类型的比较逻辑，支持按质量因子和特异性排序。
 * 
 * <p>预定义了常用媒体类型常量（如{@link #APPLICATION_JSON}、{@link #TEXT_PLAIN}），
 * 并提供解析字符串为MediaType的工具方法，适用于处理HTTP请求/响应中的内容类型标识。
 * 
 * @author soeasy.run
 * @see MimeType
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3">RFC 7231 Section 5.3</a>
 */
public class MediaType extends MimeType implements Serializable {

    private static final long serialVersionUID = 2069937152339670231L;

    /**
     * 表示所有媒体类型的常量（即{@code *&#47;*}）
     */
    public static final MediaType ALL;

    /**
     * {@link #ALL}对应的字符串值（"*&#47;*"）
     */
    public static final String ALL_VALUE = "*/*";

    /**
     * {@code application/atom+xml}媒体类型常量
     */
    public final static MediaType APPLICATION_ATOM_XML;

    /**
     * {@link #APPLICATION_ATOM_XML}对应的字符串值（"application/atom+xml"）
     */
    public final static String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

    /**
     * {@code application/x-www-form-urlencoded}媒体类型常量（表单默认提交类型）
     */
    public final static MediaType APPLICATION_FORM_URLENCODED;

    /**
     * {@link #APPLICATION_FORM_URLENCODED}对应的字符串值（"application/x-www-form-urlencoded"）
     */
    public final static String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    /**
     * {@code application/json}媒体类型常量（JSON数据类型）
     * 
     * @see #APPLICATION_JSON_UTF8
     */
    public final static MediaType APPLICATION_JSON;

    /**
     * {@link #APPLICATION_JSON}对应的字符串值（"application/json"）
     * 
     * @see #APPLICATION_JSON_UTF8_VALUE
     */
    public final static String APPLICATION_JSON_VALUE = "application/json";

    /**
     * {@code application/json;charset=UTF-8}媒体类型常量（带UTF-8字符集的JSON）
     */
    public final static MediaType APPLICATION_JSON_UTF8;

    /**
     * {@link #APPLICATION_JSON_UTF8}对应的字符串值（"application/json;charset=UTF-8"）
     */
    public final static String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    /**
     * {@code application/octet-stream}媒体类型常量（二进制流类型）
     */
    public final static MediaType APPLICATION_OCTET_STREAM;

    /**
     * {@link #APPLICATION_OCTET_STREAM}对应的字符串值（"application/octet-stream"）
     */
    public final static String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    /**
     * {@code application/pdf}媒体类型常量（PDF文件类型）
     */
    public final static MediaType APPLICATION_PDF;

    /**
     * {@link #APPLICATION_PDF}对应的字符串值（"application/pdf"）
     */
    public final static String APPLICATION_PDF_VALUE = "application/pdf";

    /**
     * {@code application/rss+xml}媒体类型常量（RSS订阅类型）
     */
    public final static MediaType APPLICATION_RSS_XML;

    /**
     * {@link #APPLICATION_RSS_XML}对应的字符串值（"application/rss+xml"）
     */
    public final static String APPLICATION_RSS_XML_VALUE = "application/rss+xml";

    /**
     * {@code application/xhtml+xml}媒体类型常量（XHTML文档类型）
     */
    public final static MediaType APPLICATION_XHTML_XML;

    /**
     * {@link #APPLICATION_XHTML_XML}对应的字符串值（"application/xhtml+xml"）
     */
    public final static String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

    /**
     * {@code application/xml}媒体类型常量（XML文档类型）
     */
    public final static MediaType APPLICATION_XML;

    /**
     * {@link #APPLICATION_XML}对应的字符串值（"application/xml"）
     */
    public final static String APPLICATION_XML_VALUE = "application/xml";

    /**
     * {@code image/gif}媒体类型常量（GIF图片类型）
     */
    public final static MediaType IMAGE_GIF;

    /**
     * {@link #IMAGE_GIF}对应的字符串值（"image/gif"）
     */
    public final static String IMAGE_GIF_VALUE = "image/gif";

    /**
     * {@code image/jpeg}媒体类型常量（JPEG图片类型）
     */
    public final static MediaType IMAGE_JPEG;

    /**
     * {@link #IMAGE_JPEG}对应的字符串值（"image/jpeg"）
     */
    public final static String IMAGE_JPEG_VALUE = "image/jpeg";

    /**
     * {@code image/png}媒体类型常量（PNG图片类型）
     */
    public final static MediaType IMAGE_PNG;

    /**
     * {@link #IMAGE_PNG}对应的字符串值（"image/png"）
     */
    public final static String IMAGE_PNG_VALUE = "image/png";

    /**
     * {@code multipart/form-data}媒体类型常量（带文件上传的表单类型）
     */
    public final static MediaType MULTIPART_FORM_DATA;

    /**
     * {@link #MULTIPART_FORM_DATA}对应的字符串值（"multipart/form-data"）
     */
    public final static String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    /**
     * {@code text/event-stream}媒体类型常量（服务器发送事件SSE类型）
     * 
     * @see <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events W3C recommendation</a>
     */
    public final static MediaType TEXT_EVENT_STREAM;

    /**
     * {@link #TEXT_EVENT_STREAM}对应的字符串值（"text/event-stream"）
     */
    public final static String TEXT_EVENT_STREAM_VALUE = "text/event-stream";

    /**
     * {@code text/html}媒体类型常量（HTML文档类型）
     */
    public final static MediaType TEXT_HTML;

    /**
     * {@link #TEXT_HTML}对应的字符串值（"text/html"）
     */
    public final static String TEXT_HTML_VALUE = "text/html";

    /**
     * {@code text/markdown}媒体类型常量（Markdown文档类型）
     */
    public final static MediaType TEXT_MARKDOWN;

    /**
     * {@link #TEXT_MARKDOWN}对应的字符串值（"text/markdown"）
     */
    public final static String TEXT_MARKDOWN_VALUE = "text/markdown";

    /**
     * {@code text/plain}媒体类型常量（纯文本类型）
     */
    public final static MediaType TEXT_PLAIN;

    /**
     * {@link #TEXT_PLAIN}对应的字符串值（"text/plain"）
     */
    public final static String TEXT_PLAIN_VALUE = "text/plain";

    /**
     * {@code text/xml}媒体类型常量（文本格式XML类型）
     */
    public final static MediaType TEXT_XML;

    /**
     * {@link #TEXT_XML}对应的字符串值（"text/xml"）
     */
    public final static String TEXT_XML_VALUE = "text/xml";

    /**
     * {@code text/javascript}媒体类型常量（JavaScript脚本类型）
     */
    public static final MediaType TEXT_JAVASCRIPT;

    /**
     * 质量因子参数名（"q"），用于表示媒体类型的优先级（0.0-1.0）
     */
    private static final String PARAM_QUALITY_FACTOR = "q";

    static {
        ALL = valueOf(ALL_VALUE);
        APPLICATION_ATOM_XML = valueOf(APPLICATION_ATOM_XML_VALUE);
        APPLICATION_FORM_URLENCODED = valueOf(APPLICATION_FORM_URLENCODED_VALUE);
        APPLICATION_JSON = valueOf(APPLICATION_JSON_VALUE);
        APPLICATION_JSON_UTF8 = valueOf(APPLICATION_JSON_UTF8_VALUE);
        APPLICATION_OCTET_STREAM = valueOf(APPLICATION_OCTET_STREAM_VALUE);
        APPLICATION_PDF = valueOf(APPLICATION_PDF_VALUE);
        APPLICATION_RSS_XML = valueOf(APPLICATION_RSS_XML_VALUE);
        APPLICATION_XHTML_XML = valueOf(APPLICATION_XHTML_XML_VALUE);
        APPLICATION_XML = valueOf(APPLICATION_XML_VALUE);
        IMAGE_GIF = valueOf(IMAGE_GIF_VALUE);
        IMAGE_JPEG = valueOf(IMAGE_JPEG_VALUE);
        IMAGE_PNG = valueOf(IMAGE_PNG_VALUE);
        MULTIPART_FORM_DATA = valueOf(MULTIPART_FORM_DATA_VALUE);
        TEXT_EVENT_STREAM = valueOf(TEXT_EVENT_STREAM_VALUE);
        TEXT_HTML = valueOf(TEXT_HTML_VALUE);
        TEXT_MARKDOWN = valueOf(TEXT_MARKDOWN_VALUE);
        TEXT_PLAIN = valueOf(TEXT_PLAIN_VALUE);
        TEXT_XML = valueOf(TEXT_XML_VALUE);
        TEXT_JAVASCRIPT = valueOf(MimeTypeUtils.TEXT_JAVASCRIPT_VALUE);
    }

    /**
     * 创建指定主类型的MediaType，子类型默认为通配符"*"，无参数
     * 
     * @param type 主类型（非空，如"text"）
     * @throws IllegalArgumentException 若主类型包含非法字符
     */
    public MediaType(String type) {
        super(type);
    }

    /**
     * 基于已有MimeType创建MediaType（复制主类型、子类型和参数）
     * 
     * @param mimeType 源MimeType（非空）
     */
    public MediaType(MimeType mimeType) {
        this(mimeType.getType(), mimeType.getSubtype(), mimeType.getParameters());
    }

    /**
     * 创建指定主类型和子类型的MediaType，无参数
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空，如"plain"）
     * @throws IllegalArgumentException 若主类型或子类型包含非法字符
     */
    public MediaType(String type, String subtype) {
        super(type, subtype, Collections.<String, String>emptyMap());
    }

    /**
     * 创建指定主类型、子类型和字符集的MediaType
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空）
     * @param charset 字符集（非空）
     * @throws IllegalArgumentException 若参数包含非法字符
     */
    public MediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    /**
     * 创建指定主类型、子类型和质量因子的MediaType
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空）
     * @param qualityValue 质量因子（0.0-1.0之间）
     * @throws IllegalArgumentException 若质量因子超出范围或参数包含非法字符
     */
    public MediaType(String type, String subtype, double qualityValue) {
        this(type, subtype, Collections.singletonMap(PARAM_QUALITY_FACTOR, Double.toString(qualityValue)));
    }

    /**
     * 复制构造方法：基于已有MimeType，修改字符集参数
     * 
     * @param other 源MimeType（非空）
     * @param charset 新字符集（非空）
     * @throws IllegalArgumentException 若字符集参数包含非法字符
     */
    public MediaType(MimeType other, Charset charset) {
        super(other, charset);
    }

    /**
     * 复制构造方法：基于已有MimeType，修改字符集名称参数
     * 
     * @param other 源MimeType（非空）
     * @param charsetName 新字符集名称（非空，如"UTF-8"）
     * @throws IllegalArgumentException 若字符集名称参数包含非法字符
     */
    public MediaType(MimeType other, String charsetName) {
        super(other, charsetName);
    }

    /**
     * 复制构造方法：基于已有MimeType，修改参数集合
     * 
     * @param other 源MimeType（非空）
     * @param parameters 新参数集合（可为空）
     * @throws IllegalArgumentException 若参数包含非法字符（尤其是质量因子超出范围）
     */
    public MediaType(MimeType other, Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }

    /**
     * 创建指定主类型、子类型和参数的MediaType
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空）
     * @param parameters 参数集合（可为空，若包含"q"参数需在0.0-1.0之间）
     * @throws IllegalArgumentException 若参数包含非法字符或质量因子无效
     */
    public MediaType(String type, String subtype, Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    /**
     * 验证参数合法性，增强父类逻辑以校验质量因子（q参数）
     * 
     * <p>对于质量因子参数（"q"），需确保其值为0.0-1.0之间的数字，否则抛出异常。
     * 
     * @param attribute 参数名
     * @param value 参数值
     * @throws IllegalArgumentException 若参数名或值非法（尤其是质量因子超出范围）
     */
    @Override
    protected void checkParameters(String attribute, String value) {
        super.checkParameters(attribute, value);
        if (PARAM_QUALITY_FACTOR.equals(attribute)) {
            value = unquote(value);
            double d = Double.parseDouble(value);
            Assert.isTrue(d >= 0D && d <= 1D, "Invalid quality value \"" + value + "\": should be between 0.0 and 1.0");
        }
    }

    /**
     * 获取质量因子（q参数），默认为1.0
     * 
     * @return 质量因子（0.0-1.0之间）
     */
    public double getQualityValue() {
        String qualityFactor = getParameter(PARAM_QUALITY_FACTOR);
        return (qualityFactor != null ? Double.parseDouble(unquote(qualityFactor)) : 1D);
    }

    /**
     * 判断当前媒体类型是否包含另一个媒体类型（非对称）
     * 
     * <p>调用父类{@link #includes(MimeType)}方法，仅参数类型为{@link MediaType}，确保二进制兼容性。
     * 
     * @param other 另一个媒体类型（可为null）
     * @return 包含返回true（如{@code text/*}包含{@code text/plain}）
     */
    public boolean includes(MediaType other) {
        return super.includes(other);
    }

    /**
     * 判断当前媒体类型与另一个媒体类型是否兼容（对称）
     * 
     * <p>调用父类{@link #isCompatibleWith(MimeType)}方法，仅参数类型为{@link MediaType}，确保二进制兼容性。
     * 
     * @param other 另一个媒体类型（可为null）
     * @return 兼容返回true（如{@code text/*}与{@code text/plain}兼容）
     */
    public boolean isCompatibleWith(MediaType other) {
        return super.isCompatibleWith(other);
    }

    /**
     * 复制当前媒体类型，并复制指定媒体类型的质量因子（q参数）
     * 
     * @param mediaType 提供质量因子的媒体类型（非空）
     * @return 若指定类型无质量因子则返回当前实例，否则返回包含新质量因子的新实例
     */
    public MediaType copyQualityValue(MediaType mediaType) {
        if (!mediaType.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<String, String>(getParameters());
        params.put(PARAM_QUALITY_FACTOR, mediaType.getParameters().get(PARAM_QUALITY_FACTOR));
        return new MediaType(this, params);
    }

    /**
     * 复制当前媒体类型，并移除质量因子（q参数）
     * 
     * @return 若当前类型无质量因子则返回当前实例，否则返回移除质量因子的新实例
     */
    public MediaType removeQualityValue() {
        if (!getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<String, String>(getParameters());
        params.remove(PARAM_QUALITY_FACTOR);
        return new MediaType(this, params);
    }

    /**
     * 将字符串解析为MediaType（等价于{@link #parseMediaType(String)}）
     * 
     * @param value 待解析的字符串（如"application/json;q=0.8"）
     * @return 解析后的{@link MediaType}对象
     * @throws InvalidMediaTypeException 若字符串格式非法或质量因子无效
     */
    public static MediaType valueOf(String value) {
        return parseMediaType(value);
    }

    /**
     * 将字符串解析为MediaType对象
     * 
     * @param mediaType 待解析的字符串（如"text/plain;charset=UTF-8;q=0.9"）
     * @return 解析后的{@link MediaType}对象
     * @throws InvalidMediaTypeException 若解析失败（如格式错误、质量因子无效）
     */
    public static MediaType parseMediaType(String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(mediaType);
        } catch (InvalidMimeTypeException ex) {
            throw new InvalidMediaTypeException(ex);
        }
        try {
            return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
        } catch (IllegalArgumentException ex) {
            throw new InvalidMediaTypeException(mediaType, ex.getMessage());
        }
    }

    /**
     * 解析逗号分隔的字符串为MediaType列表（如解析Accept头的值）
     * 
     * @param mediaTypes 待解析的字符串（如"text/html,application/json;q=0.8"）
     * @return 解析后的{@link MediaType}列表（非null，可能为空）
     * @throws InvalidMediaTypeException 若任一子字符串解析失败
     */
    public static List<MediaType> parseMediaTypes(String mediaTypes) {
        if (StringUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        }
        String[] tokens = StringUtils.tokenizeToArray(mediaTypes, ",");
        List<MediaType> result = new ArrayList<MediaType>(tokens.length);
        for (String token : tokens) {
            result.add(parseMediaType(token));
        }
        return result;
    }

    /**
     * 解析字符串列表为MediaType列表（每个字符串可能是逗号分隔的多类型）
     * 
     * @param mediaTypes 待解析的字符串列表（可为空）
     * @return 解析后的{@link MediaType}列表（非null，可能为空）
     * @throws InvalidMediaTypeException 若任一字符串解析失败
     */
    public static List<MediaType> parseMediaTypes(List<? extends String> mediaTypes) {
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.<MediaType>emptyList();
        } else if (mediaTypes.size() == 1) {
            return parseMediaTypes(mediaTypes.get(0));
        } else {
            List<MediaType> result = new ArrayList<MediaType>(8);
            for (String mediaType : mediaTypes) {
                result.addAll(parseMediaTypes(mediaType));
            }
            return result;
        }
    }

    /**
     * 将MediaType列表转换为逗号分隔的字符串（如用于构建Accept头）
     * 
     * @param mediaTypes 待转换的媒体类型列表
     * @return 逗号分隔的字符串（非null）
     */
    public static String toString(Collection<MediaType> mediaTypes) {
        return MimeTypeUtils.toString(mediaTypes);
    }

    /**
     * 按特异性排序MediaType列表（特异性高的优先）
     * 
     * <p>排序规则（优先级从高到低）：
     * 1. 主类型非通配符 > 主类型通配符（如{@code text/*} > {@code *&#47;*}）；
     * 2. 主类型相同，子类型非通配符 > 子类型通配符（如{@code text/plain} > {@code text/*}）；
     * 3. 子类型相同，质量因子高的 > 质量因子低的（如{@code text/plain;q=0.9} > {@code text/plain;q=0.5}）；
     * 4. 质量因子相同，参数多的 > 参数少的（如{@code text/plain;charset=UTF-8} > {@code text/plain}）。
     * 
     * @param mediaTypes 待排序的列表（非null）
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">RFC 7231 Section 5.3.2</a>
     */
    public static void sortBySpecificity(@NonNull List<MediaType> mediaTypes) {
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, SPECIFICITY_COMPARATOR);
        }
    }

    /**
     * 按质量因子排序MediaType列表（质量因子高的优先）
     * 
     * <p>排序规则（优先级从高到低）：
     * 1. 质量因子高的 > 质量因子低的；
     * 2. 质量因子相同，主类型非通配符 > 主类型通配符；
     * 3. 主类型相同，子类型非通配符 > 子类型通配符；
     * 4. 子类型相同，参数多的 > 参数少的。
     * 
     * @param mediaTypes 待排序的列表（非null）
     */
    public static void sortByQualityValue(@NonNull List<MediaType> mediaTypes) {
        if (mediaTypes.size() > 1) {
            Collections.sort(mediaTypes, QUALITY_VALUE_COMPARATOR);
        }
    }

    /**
     * 按特异性为主、质量因子为辅排序MediaType列表
     * 
     * @param mediaTypes 待排序的列表（非null）
     * @see #sortBySpecificity(List)
     * @see #sortByQualityValue(List)
     */
    public static void sortBySpecificityAndQuality(@NonNull List<MediaType> mediaTypes) {
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(MediaType.SPECIFICITY_COMPARATOR.thenComparing(MediaType.QUALITY_VALUE_COMPARATOR));
        }
    }

    /**
     * 按质量因子排序的比较器
     */
    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = new Comparator<MediaType>() {

        public int compare(MediaType mediaType1, MediaType mediaType2) {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison; // 质量因子高的优先
            } else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // *&#47;* < audio/*
                return 1;
            } else if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) { // audio/* > *&#47;*
                return -1;
            } else if (!mediaType1.getType().equals(mediaType2.getType())) { // 不同主类型视为相等
                return 0;
            } else { // 主类型相同
                if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) { // audio/* < audio/basic
                    return 1;
                } else if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) { // audio/basic > audio/*
                    return -1;
                } else if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) { // 不同子类型视为相等
                    return 0;
                } else { // 子类型相同，参数多的优先
                    int paramsSize1 = mediaType1.getParameters().size();
                    int paramsSize2 = mediaType2.getParameters().size();
                    return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1));
                }
            }
        }
    };

    /**
     * 按特异性排序的比较器
     */
    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MediaType>() {

        @Override
        protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison; // 质量因子高的优先
            }
            return super.compareParameters(mediaType1, mediaType2); // 父类逻辑：参数多的优先
        }
    };

}