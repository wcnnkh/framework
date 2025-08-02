package run.soeasy.framework.io;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.LinkedCaseInsensitiveMap;
import run.soeasy.framework.core.domain.CharsetCapable;

/**
 * MIME类型核心类，实现{@link CharsetCapable}、{@link Comparable}和{@link Serializable}接口，
 * 用于标准化表示互联网媒体类型（Multipurpose Internet Mail Extensions），遵循RFC 2616规范，
 * 封装了主类型（type）、子类型（subtype）及相关参数（如字符集charset），支持类型解析、包含关系判断、
 * 兼容性校验等核心功能，是处理内容类型（如HTTP请求头、文件类型）的基础组件。
 * 
 * <p>核心特性：
 * - 支持通配符类型（如{@code *&#47;*}表示所有类型，{@code text/*}表示所有文本类型）；
 * - 内置字符集参数处理，实现{@link CharsetCapable}接口提供字符集访问能力；
 * - 提供丰富的比较逻辑，支持包含关系（{@link #includes(MimeType)}）、兼容性（{@link #isCompatibleWith(MimeType)}）
 *   和排序（{@link #compareTo(MimeType)}）；
 * - 参数键名不区分大小写，确保与HTTP头字段处理规则一致。
 * 
 * @author soeasy.run
 * @see CharsetCapable
 * @see MimeTypeUtils
 * @see <a href="https://tools.ietf.org/html/rfc2616#section-3.7">RFC 2616 Section 3.7</a>
 */
public class MimeType implements CharsetCapable, Comparable<MimeType>, Serializable {

    private static final long serialVersionUID = 4085923477777865903L;

    /**
     * 通配符类型常量（"*"），用于表示任意类型或子类型（如{@code *&#47;*}、{@code text/*}）
     */
    protected static final String WILDCARD_TYPE = "*";

    /**
     * 字符集参数名（"charset"），用于标识MIME类型的字符编码（如{@code text/plain;charset=UTF-8}）
     */
    private static final String PARAM_CHARSET = "charset";

    /**
     * 合法令牌（token）字符集，基于RFC 2616 Section 2.2定义，排除控制字符（CTL）和分隔符（separators）
     */
    private static final BitSet TOKEN;

    static {
        // 控制字符（CTL）：ASCII 0-31及127（DEL）
        BitSet ctl = new BitSet(128);
        for (int i = 0; i <= 31; i++) {
            ctl.set(i);
        }
        ctl.set(127);

        // 分隔符（separators）：RFC 2616定义的特殊字符
        BitSet separators = new BitSet(128);
        separators.set('(');
        separators.set(')');
        separators.set('<');
        separators.set('>');
        separators.set('@');
        separators.set(',');
        separators.set(';');
        separators.set(':');
        separators.set('\\');
        separators.set('\"');
        separators.set('/');
        separators.set('[');
        separators.set(']');
        separators.set('?');
        separators.set('=');
        separators.set('{');
        separators.set('}');
        separators.set(' ');
        separators.set('\t');

        // 令牌字符集：所有ASCII字符中排除控制字符和分隔符
        TOKEN = new BitSet(128);
        TOKEN.set(0, 128);
        TOKEN.andNot(ctl);
        TOKEN.andNot(separators);
    }

    /**
     * 主类型（如"text"、"application"、"image"），统一转为小写存储
     */
    private final String type;

    /**
     * 子类型（如"plain"、"json"、"*"），统一转为小写存储
     */
    private final String subtype;

    /**
     * MIME类型参数映射（如{@code charset=UTF-8}、{@code boundary=xxx}），键不区分大小写
     */
    private final Map<String, String> parameters;

    /**
     * 创建指定主类型的MIME类型，子类型默认为通配符"*"，无参数
     * 
     * @param type 主类型（非空，如"text"、"*"）
     * @throws IllegalArgumentException 若主类型包含非法字符（不符合RFC 2616令牌规则）
     */
    public MimeType(String type) {
        this(type, WILDCARD_TYPE);
    }

    /**
     * 创建指定主类型和子类型的MIME类型，无参数
     * 
     * @param type 主类型（非空，如"text"）
     * @param subtype 子类型（非空，如"plain"、"*"、"*+xml"）
     * @throws IllegalArgumentException 若主类型或子类型包含非法字符
     */
    public MimeType(String type, String subtype) {
        this(type, subtype, Collections.<String, String>emptyMap());
    }

    /**
     * 创建指定主类型、子类型和字符集的MIME类型
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空）
     * @param charset 字符集（非空）
     * @throws IllegalArgumentException 若主类型、子类型或字符集参数包含非法字符
     */
    public MimeType(@NonNull String type, @NonNull String subtype, @NonNull Charset charset) {
        this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charset.name()));
    }

    /**
     * 复制构造方法：基于现有MIME类型，修改字符集参数
     * 
     * @param other 源MIME类型（非空）
     * @param charset 新字符集（非空）
     * @throws IllegalArgumentException 若字符集参数包含非法字符
     */
    public MimeType(MimeType other, Charset charset) {
        this(other.getType(), other.getSubtype(), addCharsetParameter(charset.name(), other.getParameters()));
    }

    /**
     * 复制构造方法：基于现有MIME类型，修改字符集名称参数
     * 
     * @param other 源MIME类型（非空）
     * @param charsetName 新字符集名称（非空，如"UTF-8"）
     * @throws IllegalArgumentException 若字符集名称参数包含非法字符
     */
    public MimeType(MimeType other, String charsetName) {
        this(other.getType(), other.getSubtype(), addCharsetParameter(charsetName, other.getParameters()));
    }

    /**
     * 复制构造方法：基于现有MIME类型，修改参数集合
     * 
     * @param other 源MIME类型（非空）
     * @param parameters 新参数集合（可为空）
     * @throws IllegalArgumentException 若参数包含非法字符
     */
    public MimeType(MimeType other, Map<String, String> parameters) {
        this(other.getType(), other.getSubtype(), parameters);
    }

    /**
     * 创建指定主类型、子类型和参数的MIME类型
     * 
     * @param type 主类型（非空）
     * @param subtype 子类型（非空）
     * @param parameters 参数集合（可为空，键不区分大小写）
     * @throws IllegalArgumentException 若主类型、子类型或参数包含非法字符
     */
    public MimeType(String type, String subtype, Map<String, String> parameters) {
        Assert.hasLength(type, "'type' must not be empty");
        Assert.hasLength(subtype, "'subtype' must not be empty");
        checkToken(type);
        checkToken(subtype);
        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subtype = subtype.toLowerCase(Locale.ENGLISH);
        if (!CollectionUtils.isEmpty(parameters)) {
            // 使用不区分大小写的Map存储参数，确保参数名大小写无关
            Map<String, String> map = new LinkedCaseInsensitiveMap<String>(parameters.size(), Locale.ENGLISH);
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String attribute = entry.getKey();
                String value = entry.getValue();
                checkParameters(attribute, value);
                map.put(attribute, value);
            }
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    /**
     * 验证令牌（主类型、子类型或参数名）是否包含非法字符（基于{@link #TOKEN}字符集）
     * 
     * @param token 待验证的令牌
     * @throws IllegalArgumentException 若包含非法字符
     */
    private void checkToken(String token) {
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (!TOKEN.get(ch)) {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
            }
        }
    }

    /**
     * 验证参数名和值的合法性
     * 
     * <p>规则：
     * - 参数名必须符合令牌规则；
     * - 字符集参数值会被解引用（去除引号）并验证是否为有效字符集；
     * - 非字符集参数值若未加引号，需符合令牌规则。
     * 
     * @param attribute 参数名（非空）
     * @param value 参数值（非空）
     * @throws IllegalArgumentException 若参数名或值不合法
     */
    protected void checkParameters(String attribute, String value) {
        Assert.hasLength(attribute, "'attribute' must not be empty");
        Assert.hasLength(value, "'value' must not be empty");
        checkToken(attribute);
        if (PARAM_CHARSET.equals(attribute)) {
            value = unquote(value);
            Charset.forName(value); // 验证字符集有效性
        } else if (!isQuotedString(value)) {
            checkToken(value);
        }
    }

    /**
     * 判断参数值是否为带引号的字符串（单引号或双引号包裹）
     * 
     * @param s 参数值
     * @return 是带引号的字符串返回true（如"UTF-8"、'ISO-8859-1'）
     */
    private boolean isQuotedString(String s) {
        if (s.length() < 2) {
            return false;
        } else {
            return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
        }
    }

    /**
     * 去除参数值的引号（若有）
     * 
     * @param s 参数值（可为null）
     * @return 去除引号后的字符串，null则返回null
     */
    protected String unquote(String s) {
        if (s == null) {
            return null;
        }
        return (isQuotedString(s) ? s.substring(1, s.length() - 1) : s);
    }

    /**
     * 判断当前MIME类型的主类型是否为通配符"*"（如{@code *&#47;*}）
     * 
     * @return 主类型是通配符返回true
     */
    public boolean isWildcardType() {
        return WILDCARD_TYPE.equals(getType());
    }

    /**
     * 判断当前MIME类型的子类型是否为通配符（包括"*"或"*+后缀"，如{@code text/*}、{@code application/*+xml}）
     * 
     * @return 子类型是通配符返回true
     */
    public boolean isWildcardSubtype() {
        return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith("*+");
    }

    /**
     * 判断当前MIME类型与另一个MIME类型的主类型和子类型是否完全一致（忽略大小写）
     * 
     * @param other 另一个MIME类型（可为null）
     * @return 主类型和子类型一致返回true
     */
    public boolean equalsTypeAndSubtype(MimeType other) {
        if (other == null) {
            return false;
        }
        return getType().equalsIgnoreCase(other.getType()) && getSubtype().equalsIgnoreCase(other.getSubtype());
    }

    /**
     * 判断当前MIME类型是否为具体类型（主类型和子类型均非通配符，如{@code text/plain}、{@code application/json}）
     * 
     * @return 是具体类型返回true
     */
    public boolean isConcrete() {
        return !isWildcardType() && !isWildcardSubtype();
    }

    /**
     * 获取主类型（如"text"、"application"）
     * 
     * @return 主类型（非空，已转为小写）
     */
    public String getType() {
        return this.type;
    }

    /**
     * 获取子类型（如"plain"、"json"、"*"）
     * 
     * @return 子类型（非空，已转为小写）
     */
    public String getSubtype() {
        return this.subtype;
    }

    /**
     * 获取字符集（从"charset"参数解析，自动去除引号）
     * 
     * @return 字符集对象，无charset参数则返回null
     * @throws java.nio.charset.UnsupportedCharsetException 若字符集不被支持
     */
    public Charset getCharset() {
        String charset = getCharsetName();
        return (charset != null ? Charset.forName(unquote(charset)) : null);
    }

    /**
     * 获取字符集名称（"charset"参数的原始值，可能包含引号）
     * 
     * @return 字符集名称，无charset参数则返回null
     */
    public String getCharsetName() {
        return getParameter(PARAM_CHARSET);
    }

    /**
     * 获取指定参数的值（参数名不区分大小写）
     * 
     * @param name 参数名（如"boundary"）
     * @return 参数值，无此参数则返回null
     */
    public String getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * 获取所有参数的不可修改映射（参数名不区分大小写）
     * 
     * @return 参数映射（可能为空，非null）
     */
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    /**
     * 判断当前MIME类型是否包含另一个MIME类型（非对称关系）
     * 
     * <p>包含规则：
     * 1. 主类型为通配符{@code *}包含所有类型（如{@code *&#47;*}包含{@code text/plain}）；
     * 2. 主类型相同且子类型为{@code *}包含同主类型的所有子类型（如{@code text/*}包含{@code text/html}）；
     * 3. 子类型为{@code *+后缀}包含同后缀的子类型（如{@code application/*+xml}包含{@code application/soap+xml}）。
     * 
     * @param other 另一个MIME类型（可为null）
     * @return 包含返回true
     */
    public boolean includes(MimeType other) {
        if (other == null) {
            return false;
        }
        if (isWildcardType()) {
            return true; // *&#47;* 包含所有类型
        } else if (getType().equals(other.getType())) {
            if (getSubtype().equals(other.getSubtype())) {
                return true; // 完全匹配
            }
            if (isWildcardSubtype()) {
                // 处理*+后缀的包含关系（如application/*+xml 包含 application/soap+xml）
                int thisPlusIdx = getSubtype().lastIndexOf('+');
                if (thisPlusIdx == -1) {
                    return true; // 子类型为*（如audio/* 包含 audio/basic）
                } else {
                    int otherPlusIdx = other.getSubtype().indexOf('+');
                    if (otherPlusIdx != -1) {
                        String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
                        String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
                        String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
                        return thisSubtypeSuffix.equals(otherSubtypeSuffix) 
                                && WILDCARD_TYPE.equals(thisSubtypeNoSuffix);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断当前MIME类型与另一个MIME类型是否兼容（对称关系）
     * 
     * <p>兼容规则：
     * 1. 任一类型的主类型为{@code *}则兼容；
     * 2. 主类型相同且任一子类型为{@code *}则兼容（如{@code text/*}与{@code text/plain}兼容）；
     * 3. 主类型相同且子类型为{@code *+后缀}与同后缀子类型兼容（如{@code application/*+xml}与{@code application/soap+xml}兼容）。
     * 
     * @param other 另一个MIME类型（可为null）
     * @return 兼容返回true
     */
    public boolean isCompatibleWith(MimeType other) {
        if (other == null) {
            return false;
        }
        if (isWildcardType() || other.isWildcardType()) {
            return true;
        } else if (getType().equals(other.getType())) {
            if (getSubtype().equals(other.getSubtype())) {
                return true; // 完全匹配
            }
            // 处理*+后缀的兼容性
            if (isWildcardSubtype() || other.isWildcardSubtype()) {
                int thisPlusIdx = getSubtype().indexOf('+');
                int otherPlusIdx = other.getSubtype().indexOf('+');
                if (thisPlusIdx == -1 && otherPlusIdx == -1) {
                    return true; // 子类型均为非通配符但不同（如audio/basic与audio/wave）
                } else if (thisPlusIdx != -1 && otherPlusIdx != -1) {
                    String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
                    String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
                    String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
                    String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);
                    return thisSubtypeSuffix.equals(otherSubtypeSuffix) 
                            && (WILDCARD_TYPE.equals(thisSubtypeNoSuffix) 
                                    || WILDCARD_TYPE.equals(otherSubtypeNoSuffix));
                }
            }
        }
        return false;
    }

    /**
     * 比较两个MIME类型是否相等（主类型、子类型及参数均需一致，字符集参数比较实际字符集）
     * 
     * @param other 另一个对象
     * @return 相等返回true
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MimeType)) {
            return false;
        }
        MimeType otherType = (MimeType) other;
        return (this.type.equalsIgnoreCase(otherType.type) 
                && this.subtype.equalsIgnoreCase(otherType.subtype)
                && parametersAreEqual(otherType));
    }

    /**
     * 比较两个MIME类型的参数是否相等（字符集参数比较实际字符集对象，其他参数比较值）
     * 
     * @param other 另一个MIME类型
     * @return 参数相等返回true
     */
    private boolean parametersAreEqual(MimeType other) {
        if (this.parameters.size() != other.parameters.size()) {
            return false;
        }

        for (String key : this.parameters.keySet()) {
            if (!other.parameters.containsKey(key)) {
                return false;
            }
            if (PARAM_CHARSET.equals(key)) {
                if (!ObjectUtils.equals(getCharset(), other.getCharset())) {
                    return false;
                }
            } else if (!ObjectUtils.equals(this.parameters.get(key), other.parameters.get(key))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 计算哈希值（基于主类型、子类型和参数）
     * 
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.subtype.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    /**
     * 转换为MIME类型字符串（如{@code text/plain;charset=UTF-8}）
     * 
     * @return MIME类型字符串
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    /**
     * 将MIME类型追加到字符串构建器（格式：type/subtype;param1=value1;param2=value2）
     * 
     * @param builder 字符串构建器（非空）
     */
    protected void appendTo(StringBuilder builder) {
        builder.append(this.type);
        builder.append('/');
        builder.append(this.subtype);
        appendTo(this.parameters, builder);
    }

    /**
     * 将参数追加到字符串构建器（格式：;key=value）
     * 
     * @param map 参数映射
     * @param builder 字符串构建器
     */
    private void appendTo(Map<String, String> map, StringBuilder builder) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(';');
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
        }
    }

    /**
     * 按字母顺序比较两个MIME类型（主类型 -&gt; 子类型 -&gt; 参数数量 -&gt; 参数名 -&gt; 参数值）
     * 
     * @param other 另一个MIME类型（非空）
     * @return 比较结果（负整数、零、正整数分别表示当前对象小于、等于、大于另一个对象）
     */
    public int compareTo(MimeType other) {
        int comp = getType().compareToIgnoreCase(other.getType());
        if (comp != 0) {
            return comp;
        }
        comp = getSubtype().compareToIgnoreCase(other.getSubtype());
        if (comp != 0) {
            return comp;
        }
        comp = getParameters().size() - other.getParameters().size();
        if (comp != 0) {
            return comp;
        }

        // 按参数名排序后比较参数值
        TreeSet<String> thisAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        thisAttributes.addAll(getParameters().keySet());
        TreeSet<String> otherAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        otherAttributes.addAll(other.getParameters().keySet());
        Iterator<String> thisAttributesIterator = thisAttributes.iterator();
        Iterator<String> otherAttributesIterator = otherAttributes.iterator();

        while (thisAttributesIterator.hasNext()) {
            String thisAttribute = thisAttributesIterator.next();
            String otherAttribute = otherAttributesIterator.next();
            comp = thisAttribute.compareToIgnoreCase(otherAttribute);
            if (comp != 0) {
                return comp;
            }
            if (PARAM_CHARSET.equals(thisAttribute)) {
                Charset thisCharset = getCharset();
                Charset otherCharset = other.getCharset();
                if (thisCharset != otherCharset) {
                    if (thisCharset == null) {
                        return -1;
                    }
                    if (otherCharset == null) {
                        return 1;
                    }
                    comp = thisCharset.compareTo(otherCharset);
                    if (comp != 0) {
                        return comp;
                    }
                }
            } else {
                String thisValue = getParameters().get(thisAttribute);
                String otherValue = other.getParameters().get(otherAttribute);
                if (otherValue == null) {
                    otherValue = "";
                }
                comp = thisValue.compareTo(otherValue);
                if (comp != 0) {
                    return comp;
                }
            }
        }

        return 0;
    }

    /**
     * 解析字符串为MIME类型（等价于{@link MimeTypeUtils#parseMimeType(String)}）
     * 
     * @param value MIME类型字符串（如{@code text/plain;charset=UTF-8}）
     * @return 解析后的MIME类型
     * @throws InvalidMimeTypeException 解析失败时抛出
     */
    public static MimeType valueOf(String value) {
        return MimeTypeUtils.parseMimeType(value);
    }

    /**
     * 为参数映射添加或替换字符集参数
     * 
     * @param charsetName 字符集名称
     * @param parameters 原参数映射
     * @return 包含新字符集参数的映射
     */
    private static Map<String, String> addCharsetParameter(String charsetName, Map<String, String> parameters) {
        Map<String, String> map = new LinkedHashMap<String, String>(parameters);
        map.put(PARAM_CHARSET, charsetName);
        return map;
    }

    /**
     * MIME类型特异性比较器，用于按特异性排序（具体类型优先于通配符类型）
     * 
     * <p>排序优先级：
     * 1. 主类型非通配符 &gt; 主类型通配符；
     * 2. 主类型相同，子类型非通配符 &gt; 子类型通配符；
     * 3. 子类型相同，参数多的 &gt; 参数少的。
     * 
     * @param <T> MimeType子类
     */
    public static class SpecificityComparator<T extends MimeType> implements Comparator<T> {

        /**
         * 比较两个MIME类型的特异性
         * 
         * @param mimeType1 第一个MIME类型
         * @param mimeType2 第二个MIME类型
         * @return 比较结果（负整数、零、正整数分别表示mimeType1小于、等于、大于mimeType2）
         */
        public int compare(T mimeType1, T mimeType2) {
            if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // *&#47;* < audio/*
                return 1;
            } else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/* > *&#47;*
                return -1;
            } else if (!mimeType1.getType().equals(mimeType2.getType())) { // 不同主类型视为相等（如audio/basic与text/html）
                return 0;
            } else { // 主类型相同
                if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/* < audio/basic
                    return 1;
                } else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic > audio/*
                    return -1;
                } else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // 不同子类型视为相等（如audio/basic与audio/wave）
                    return 0;
                } else { // 子类型相同，比较参数数量
                    return compareParameters(mimeType1, mimeType2);
                }
            }
        }

        /**
         * 比较两个MIME类型的参数数量（参数多的更具体）
         * 
         * @param mimeType1 第一个MIME类型
         * @param mimeType2 第二个MIME类型
         * @return 比较结果
         */
        protected int compareParameters(T mimeType1, T mimeType2) {
            int paramsSize1 = mimeType1.getParameters().size();
            int paramsSize2 = mimeType2.getParameters().size();
            return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1)); // 参数多的更优先
        }
    }
}
    