package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 字符序列模板类，实现{@link CharSequence}和{@link Version}接口，
 * 用于处理可分割的字符序列，支持版本号格式解析、数字转换和版本组合等功能。
 * 该类支持通过分隔符将字符序列拆分为多个子序列，并提供统一的版本号操作接口。
 *
 * <p>核心特性：
 * <ul>
 *   <li>字符序列操作：实现CharSequence接口，支持字符访问、子序列提取等基本操作</li>
 *   <li>版本号处理：实现Version接口，支持版本比较、组合和数字转换</li>
 *   <li>可分割性：通过分隔符支持将字符序列拆分为多个子模板</li>
 *   <li>类型转换：支持将字符序列转换为数字值（通过{@link #getAsNumber()}）</li>
 *   <li>不可变性：所有操作返回新实例，确保线程安全</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>版本号解析：如"1.0.0"或"1.0.0+build2023"等格式的版本号处理</li>
 *   <li>配置项模板：带分隔符的配置值解析（如"key1=value1;key2=value2"）</li>
 *   <li>数据分割：需要按指定分隔符拆分为多个部分的字符序列处理</li>
 *   <li>数字转换：可转换为数字的字符序列（如"123.45"）的类型转换</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建字符序列模板
 * CharSequenceTemplate version = new CharSequenceTemplate("1.0.0", ".");
 * 
 * // 分割为子模板
 * Elements&lt;CharSequenceTemplate&gt; parts = version.getAsElements();
 * 
 * // 转换为数字
 * NumberValue number = version.getAsNumber(); // 转换为1.00的BigDecimalValue
 * 
 * // 组合版本
 * Version combined = version.join(new CharSequenceTemplate("build2023", "+"));
 * </pre>
 *
 * @see CharSequence
 * @see Version
 * @see Serializable
 */
@Data
@EqualsAndHashCode(of = "value")
public class CharSequenceTemplate implements CharSequence, Version, Serializable {
    private static final long serialVersionUID = 1L;
    /** 字符序列的实际内容，不可为null */
    private final CharSequence value;
    /** 用于分割字符序列的分隔符，可为null表示不可分割 */
    private final CharSequence delimiter;

    /**
     * 构造指定字符序列的模板，分隔符默认为null。
     *
     * @param value 字符序列内容，不可为null
     */
    public CharSequenceTemplate(@NonNull CharSequence value) {
        this(value, null);
    }

    /**
     * 构造指定字符序列和分隔符的模板。
     *
     * @param value     字符序列内容，不可为null
     * @param delimiter 分隔符，可为null表示不可分割
     */
    public CharSequenceTemplate(@NonNull CharSequence value, CharSequence delimiter) {
        this.value = value;
        this.delimiter = delimiter;
    }

    /**
     * 返回指定索引处的字符。
     *
     * @param index 字符索引（0-based）
     * @return 索引处的字符
     * @throws IndexOutOfBoundsException 如果索引超出范围
     */
    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    /**
     * 获取字符序列的元素集合，按分隔符分割。
     * <p>
     * 若分隔符为null，返回包含自身的单元素集合；
     * 否则按分隔符分割为多个子模板。
     *
     * @return 分割后的元素集合
     */
    @Override
    public Elements<? extends CharSequenceTemplate> getAsElements() {
        if (delimiter == null) {
            return Elements.singleton(this);
        }
        return getAsElements(this.delimiter);
    }

    /**
     * 按指定分隔符获取字符序列的元素集合。
     *
     * @param delimiter 用于分割的分隔符
     * @return 分割后的元素集合
     */
    public Elements<? extends CharSequenceTemplate> getAsElements(CharSequence delimiter) {
        if (value == null) {
            return Elements.empty();
        }
        return StringUtils.split(value, delimiter).map((e) -> new CharSequenceTemplate(e, delimiter));
    }

    /**
     * 将字符序列转换为数字值。
     * <p>
     * 内部通过{@link BigDecimalValue}实现转换，
     * 若字符序列无法转换为数字，抛出{@link NumberFormatException}。
     *
     * @return 数字值对象
     * @throws NumberFormatException 如果字符序列不是有效的数字格式
     */
    @Override
    public NumberValue getAsNumber() throws NumberFormatException {
        return new BigDecimalValue(getAsString());
    }

    /**
     * 获取字符序列的字符串表示。
     *
     * @return 字符序列的字符串形式
     */
    @Override
    public String getAsString() {
        return value.toString();
    }

    /**
     * 判断是否为多值类型（即是否包含分隔符）。
     *
     * @return true如果分隔符不为null，false否则
     */
    @Override
    public boolean isMultiple() {
        return delimiter != null;
    }

    /**
     * 判断字符序列是否可转换为数字。
     * <p>
     * 通过尝试调用{@link #getAsNumber()}进行验证，
     * 若抛出异常则返回false。
     *
     * @return true如果可转换为数字，false否则
     */
    @Override
    public boolean isNumber() {
        try {
            getAsNumber();
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * 返回字符序列的长度。
     *
     * @return 字符序列长度，value为null时返回0
     */
    @Override
    public int length() {
        return value == null ? 0 : value.length();
    }

    /**
     * 返回从start到end的子序列模板。
     *
     * @param start 起始索引（包含）
     * @param end   结束索引（不包含）
     * @return 子序列模板
     * @throws IndexOutOfBoundsException 如果start或end超出范围
     */
    @Override
    public CharSequenceTemplate subSequence(int start, int end) {
        CharSequence subSequence = value.subSequence(start, end);
        return new CharSequenceTemplate(subSequence, delimiter);
    }

    /**
     * 返回去除首尾空格的新模板。
     *
     * @return 去除首尾空格的新模板
     */
    public CharSequenceTemplate trim() {
        return new CharSequenceTemplate(value.toString().trim(), delimiter);
    }

    /**
     * 将当前版本与另一个版本组合为新的{@link JoinVersion}。
     *
     * @param version 待组合的版本，不可为null
     * @return 组合后的新版本
     */
    @Override
    public Version join(@NonNull Version version) {
        Elements<Version> elements = getAsElements().map(Function.identity());
        elements = elements.concat(Elements.singleton(version));
        return new JoinVersion(elements, delimiter);
    }

    /**
     * 返回字符序列的字符串表示，等价于{@link #getAsString()}。
     *
     * @return 字符序列的字符串形式
     */
    @Override
    public String toString() {
        return getAsString();
    }
}