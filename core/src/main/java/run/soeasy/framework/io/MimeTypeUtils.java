package run.soeasy.framework.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.io.MimeType.SpecificityComparator;

/**
 * MIME类型工具类，提供常用MIME类型常量定义、字符串解析、排序及 multipart 边界生成等功能，
 * 是处理MIME类型（如HTTP内容类型、文件类型标识）的核心工具，遵循RFC 7231规范。
 * 
 * <p>核心功能包括：
 * - 预定义常用MIME类型常量（如{@link #APPLICATION_JSON}、{@link #TEXT_PLAIN}）；
 * - 将字符串解析为{@link MimeType}对象（支持单类型和逗号分隔的多类型字符串）；
 * - 按特异性排序MIME类型列表（{@link #sortBySpecificity(List)}）；
 * - 生成 multipart 表单数据的随机边界（用于分隔不同部分）。
 * 
 * @author soeasy.run
 * @see MimeType
 * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">RFC 7231 Section 5.3.2</a>
 */
public final class MimeTypeUtils {

    /**
     * 生成 multipart 边界时使用的字符集（包含字母、数字、连字符和下划线）
     */
    private static final byte[] BOUNDARY_CHARS = new byte[] { '-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    /**
     * 用于生成随机边界的安全随机数生成器
     */
    private static final Random RND = new SecureRandom();

    /**
     * US-ASCII字符集常量（{@link StandardCharsets#US_ASCII}）
     */
    public static Charset US_ASCII = StandardCharsets.US_ASCII;

    /**
     * 用于按特异性排序MIME类型的比较器（基于{@link MimeType.SpecificityComparator}）
     */
    public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MimeType>();

    /**
     * 表示所有MIME类型的常量（{@code *&#47;*}）
     */
    public static final MimeType ALL;

    /**
     * {@link #ALL}对应的字符串值（"*&#47;*"）
     */
    public static final String ALL_VALUE = "*/*";

    /**
     * {@code application/atom+xml}类型常量
     */
    public static final MimeType APPLICATION_ATOM_XML;

    /**
     * {@link #APPLICATION_ATOM_XML}对应的字符串值（"application/atom+xml"）
     */
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

    /**
     * text/json类型常量（非规范JSON类型，规范类型为{@link #APPLICATION_JSON}）
     */
    public static final MimeType TEXT_JSON = new MimeType("text", "json");

    /**
     * {@link #TEXT_JSON}对应的字符串值（"text/json"）
     */
    public static final String TEXT_JSON_VALUE = "text/json";

    /**
     * {@code application/json}类型常量（JSON规范类型）
     */
    public static final MimeType APPLICATION_JSON;

    /**
     * {@link #APPLICATION_JSON}对应的字符串值（"application/json"）
     */
    public static final String APPLICATION_JSON_VALUE = "application/json";

    /**
     * {@code application/octet-stream}类型常量（二进制流类型）
     */
    public static final MimeType APPLICATION_OCTET_STREAM;

    /**
     * {@link #APPLICATION_OCTET_STREAM}对应的字符串值（"application/octet-stream"）
     */
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    /**
     * {@code application/xhtml+xml}类型常量
     */
    public static final MimeType APPLICATION_XHTML_XML;

    /**
     * {@link #APPLICATION_XHTML_XML}对应的字符串值（"application/xhtml+xml"）
     */
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";

    /**
     * {@code application/xml}类型常量
     */
    public static final MimeType APPLICATION_XML;

    /**
     * {@link #APPLICATION_XML}对应的字符串值（"application/xml"）
     */
    public static final String APPLICATION_XML_VALUE = "application/xml";

    /**
     * {@code image/gif}类型常量（GIF图片）
     */
    public static final MimeType IMAGE_GIF;

    /**
     * {@link #IMAGE_GIF}对应的字符串值（"image/gif"）
     */
    public static final String IMAGE_GIF_VALUE = "image/gif";

    /**
     * {@code image/jpeg}类型常量（JPEG图片）
     */
    public static final MimeType IMAGE_JPEG;

    /**
     * {@link #IMAGE_JPEG}对应的字符串值（"image/jpeg"）
     */
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";

    /**
     * {@code image/png}类型常量（PNG图片）
     */
    public static final MimeType IMAGE_PNG;

    /**
     * {@link #IMAGE_PNG}对应的字符串值（"image/png"）
     */
    public static final String IMAGE_PNG_VALUE = "image/png";

    /**
     * {@code text/html}类型常量（HTML文本）
     */
    public static final MimeType TEXT_HTML;

    /**
     * {@link #TEXT_HTML}对应的字符串值（"text/html"）
     */
    public static final String TEXT_HTML_VALUE = "text/html";

    /**
     * {@code text/plain}类型常量（纯文本）
     */
    public static final MimeType TEXT_PLAIN;

    /**
     * {@link #TEXT_PLAIN}对应的字符串值（"text/plain"）
     */
    public static final String TEXT_PLAIN_VALUE = "text/plain";

    /**
     * {@code text/xml}类型常量
     */
    public static final MimeType TEXT_XML;

    /**
     * {@link #TEXT_XML}对应的字符串值（"text/xml"）
     */
    public static final String TEXT_XML_VALUE = "text/xml";

    /**
     * {@code text/javascript}类型常量（JavaScript脚本类型）
     */
    public static final MimeType TEXT_JAVASCRIPT = new MimeType("text", "javascript");

    /**
     * {@link #TEXT_JAVASCRIPT}对应的字符串值（"text/javascript"）
     */
    public static final String TEXT_JAVASCRIPT_VALUE = "text/javascript";

    static {
        // 修复此处注释中的转义问题 - 原代码可能在此处有未转义的/*
        ALL = MimeType.valueOf(ALL_VALUE);  // ALL_VALUE为"*/*"，已通过实体编码转义
        APPLICATION_ATOM_XML = MimeType.valueOf(APPLICATION_ATOM_XML_VALUE);
        APPLICATION_JSON = MimeType.valueOf(APPLICATION_JSON_VALUE);
        APPLICATION_OCTET_STREAM = MimeType.valueOf(APPLICATION_OCTET_STREAM_VALUE);
        APPLICATION_XHTML_XML = MimeType.valueOf(APPLICATION_XHTML_XML_VALUE);
        APPLICATION_XML = MimeType.valueOf(APPLICATION_XML_VALUE);
        IMAGE_GIF = MimeType.valueOf(IMAGE_GIF_VALUE);
        IMAGE_JPEG = MimeType.valueOf(IMAGE_JPEG_VALUE);
        IMAGE_PNG = MimeType.valueOf(IMAGE_PNG_VALUE);
        TEXT_HTML = MimeType.valueOf(TEXT_HTML_VALUE);
        TEXT_PLAIN = MimeType.valueOf(TEXT_PLAIN_VALUE);
        TEXT_XML = MimeType.valueOf(TEXT_XML_VALUE);
    }

    /**
     * 解析字符串为MimeType对象（支持带参数的类型，如"text/plain;charset=UTF-8"）
     * 
     * <p>解析逻辑：
     * 1. 分割类型与参数（分号";"分隔）；
     * 2. 验证主类型与子类型格式（必须包含"/"）；
     * 3. 解析参数部分（键值对形式，支持引号包裹的值）；
     * 4. 构建并返回{@link MimeType}实例。
     * 
     * @param mimeType 待解析的MIME类型字符串（如"application/json;charset=UTF-8"）
     * @return 解析后的{@link MimeType}对象
     * @throws InvalidMimeTypeException 若字符串格式非法（如无"/"、通配符使用不当、字符集不支持等）
     */
    public static MimeType parseMimeType(String mimeType) {
        if (StringUtils.isEmpty(mimeType)) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }

        int index = mimeType.indexOf(';');
        String fullType = (index >= 0 ? mimeType.substring(0, index) : mimeType).trim();
        if (fullType.isEmpty()) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }

        // 处理java.net.HttpURLConnection返回的"*; q=.2"格式，转为标准"*/*"
        if (MimeType.WILDCARD_TYPE.equals(fullType)) {
            fullType = "*/*";
        }
        int subIndex = fullType.indexOf('/');
        if (subIndex == -1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
        }
        if (subIndex == fullType.length() - 1) {
            throw new InvalidMimeTypeException(mimeType, "does not contain subtype after '/'");
        }
        String type = fullType.substring(0, subIndex);
        String subtype = fullType.substring(subIndex + 1, fullType.length());
        if (MimeType.WILDCARD_TYPE.equals(type) && !MimeType.WILDCARD_TYPE.equals(subtype)) {
            throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
        }

        Map<String, String> parameters = null;
        do {
            int nextIndex = index + 1;
            boolean quoted = false;
            // 处理参数部分，支持引号包裹的值（如charset="UTF-8"）
            while (nextIndex < mimeType.length()) {
                char ch = mimeType.charAt(nextIndex);
                if (ch == ';') {
                    if (!quoted) {
                        break;
                    }
                } else if (ch == '"') {
                    quoted = !quoted;
                }
                nextIndex++;
            }
            String parameter = mimeType.substring(index + 1, nextIndex).trim();
            if (parameter.length() > 0) {
                if (parameters == null) {
                    parameters = new LinkedHashMap<String, String>(4);
                }
                int eqIndex = parameter.indexOf('=');
                if (eqIndex >= 0) {
                    String attribute = parameter.substring(0, eqIndex).trim();
                    String value = parameter.substring(eqIndex + 1, parameter.length()).trim();
                    parameters.put(attribute, value);
                }
            }
            index = nextIndex;
        } while (index < mimeType.length());

        try {
            return new MimeType(type, subtype, parameters);
        } catch (UnsupportedCharsetException ex) {
            throw new InvalidMimeTypeException(mimeType, "unsupported charset '" + ex.getCharsetName() + "'");
        } catch (IllegalArgumentException ex) {
            throw new InvalidMimeTypeException(mimeType, ex.getMessage());
        }
    }

    /**
     * 解析逗号分隔的字符串为MimeType列表（如"text/plain,application/json"）
     * 
     * @param mimeTypes 待解析的多MIME类型字符串
     * @return 解析后的{@link MimeType}列表（非null，可能为空）
     * @throws InvalidMimeTypeException 若任一子字符串格式非法
     */
    public static List<MimeType> parseMimeTypes(String mimeTypes) {
        if (StringUtils.isEmpty(mimeTypes)) {
            return Collections.emptyList();
        }
        String[] tokens = StringUtils.tokenizeToArray(mimeTypes, ",");
        List<MimeType> result = new ArrayList<MimeType>(tokens.length);
        for (String token : tokens) {
            result.add(parseMimeType(token));
        }
        return result;
    }

    /**
     * 将MimeType列表转换为逗号分隔的字符串（如[text/plain, application/json] -> "text/plain, application/json"）
     * 
     * @param mimeTypes 待转换的MimeType列表
     * @return 逗号分隔的MIME类型字符串（非null）
     */
    public static String toString(Iterable<? extends MimeType> mimeTypes) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<? extends MimeType> iterator = mimeTypes.iterator(); iterator.hasNext();) {
            MimeType mimeType = iterator.next();
            mimeType.appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    /**
     * 按特异性排序MimeType列表（具体类型优先于通配符类型）
     * 
     * <p>排序规则（优先级从高到低）：
     * 1. 主类型非通配符 > 主类型通配符（如{@code text/*} > {@code *&#47;*}）；
     * 2. 主类型相同，子类型非通配符 > 子类型通配符（如{@code text/plain} > {@code text/*}）；
     * 3. 子类型相同，参数多的 > 参数少的（如{@code text/plain;charset=UTF-8} > {@code text/plain}）；
     * 4. 主类型或子类型不同时，视为同等优先级，保持原有顺序。
     * 
     * @param mimeTypes 待排序的MimeType列表（非null）
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">RFC 7231 Section 5.3.2</a>
     */
    public static void sortBySpecificity(@NonNull List<MimeType> mimeTypes) {
        if (mimeTypes.size() > 1) {
            Collections.sort(mimeTypes, SPECIFICITY_COMPARATOR);
        }
    }

    /**
     * 生成multipart表单数据的随机边界字节数组（用于分隔不同部分）
     * 
     * <p>边界长度为30-40字节，由{@link #BOUNDARY_CHARS}中的字符随机组成，符合multipart协议要求。
     * 
     * @return 随机边界字节数组（非null）
     */
    public static byte[] generateMultipartBoundary() {
        byte[] boundary = new byte[RND.nextInt(11) + 30]; // 长度30-40字节
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[RND.nextInt(BOUNDARY_CHARS.length)];
        }
        return boundary;
    }

    /**
     * 解析字符串列表为MimeType列表（每个字符串可能是逗号分隔的多类型）
     * 
     * @param mimeTypes 待解析的字符串列表（可为空）
     * @return 解析后的{@link MimeType}列表（非null，可能为空）
     * @throws InvalidMimeTypeException 若任一字符串格式非法
     */
    public static List<MimeType> parseMimeTypes(List<String> mimeTypes) {
        if (CollectionUtils.isEmpty(mimeTypes)) {
            return Collections.<MimeType>emptyList();
        } else if (mimeTypes.size() == 1) {
            return parseMimeTypes(mimeTypes.get(0));
        } else {
            List<MimeType> result = new ArrayList<MimeType>(8);
            for (String mediaType : mimeTypes) {
                result.addAll(parseMimeTypes(mediaType));
            }
            return result;
        }
    }

    /**
     * 生成multipart表单数据的随机边界字符串（US-ASCII编码）
     * 
     * @return 随机边界字符串（非null）
     * @see #generateMultipartBoundary()
     */
    public static String generateMultipartBoundaryString() {
        return new String(generateMultipartBoundary(), StandardCharsets.US_ASCII);
    }

}
    