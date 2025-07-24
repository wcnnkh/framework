package run.soeasy.framework.core.domain;

import java.util.Iterator;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 组合版本实现，用于将多个{@link Version}对象组合为一个复合版本。
 * 组合后的版本按元素顺序进行比较，字符串表示使用指定分隔符连接各元素的字符串表示。
 *
 * <p>核心特性：
 * <ul>
 *   <li>版本组合：支持将多个版本对象按顺序组合为一个复合版本</li>
 *   <li>自定义分隔符：可指定分隔符用于字符串表示（如"1.0.0+build2023"）</li>
 *   <li>比较策略：按元素顺序逐个比较，若前序元素相同则比较元素数量</li>
 *   <li>不可变性：所有操作返回新的JoinVersion实例，不修改原对象</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>语义化版本扩展：如"主版本.次版本.修订版本+构建号"的组合表示</li>
 *   <li>多维度版本控制：如"API版本+实现版本"的组合</li>
 *   <li>版本号拼接：将多个版本组件组合为完整版本标识</li>
 *   <li>版本比较增强：需要按特定顺序比较多个版本组件的场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * // 创建基础版本
 * Version apiVersion = new SemanticVersion("1.0.0");
 * Version buildVersion = new SimpleVersion("build20230615");
 * 
 * // 组合版本（使用"+"分隔符）
 * JoinVersion combined = new JoinVersion(Elements.of(apiVersion, buildVersion), "+");
 * 
 * // 版本比较
 * int result = combined.compareTo(new SemanticVersion("1.0.1")); // 按元素顺序比较
 * 
 * // 字符串表示
 * String versionStr = combined.getAsString(); // 输出: "1.0.0+build20230615"
 * </pre>
 *
 * @see Version
 * @see Elements
 */
@Data
public class JoinVersion implements Version {
    @NonNull
    private final Elements<Version> elements;
    private final CharSequence delimiter;

    /**
     * 比较当前组合版本与另一个值的大小。
     * <p>
     * 比较逻辑：
     * <ol>
     *   <li>若另一个值是多值类型，按元素顺序逐个比较：
     *     <ul>
     *       <li>若某元素比较结果不为0，返回该结果</li>
     *       <li>若所有比较元素均相同，比较元素数量</li>
     *     </ul>
     *   </li>
     *   <li>若另一个值是单值类型：
     *     <ul>
     *       <li>若当前组合版本为空，返回-1</li>
     *       <li>比较第一个元素，若相同则判断元素数量（大于1返回1，否则返回0）</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param other 待比较的值，不可为null
     * @return 比较结果：负整数表示当前版本更小，零表示相等，正整数表示更大
     */
    @Override
    public int compareTo(Value other) {
        if (other.isMultiple()) {
            Elements<? extends Value> otherElements = other.getAsElements().toList();
            int compare = compareTo(otherElements);
            if (compare != 0) {
                return compare;
            }

            long count = elements.count();
            long otherCount = otherElements.count();
            return Long.compare(count, otherCount);
        } else {
            if (elements.isEmpty()) {
                return -1;
            }

            int value = elements.first().compareTo(other);
            return value == 0 ? (elements.count() > 1 ? 1 : 0) : value;
        }
    }

    /**
     * 按元素顺序逐个比较两个元素集合。
     * <p>
     * 若所有比较元素均相同，返回0。
     *
     * @param otherElements 待比较的元素集合
     * @return 比较结果：负整数表示当前版本更小，零表示相等，正整数表示更大
     */
    private int compareTo(Elements<? extends Value> otherElements) {
        Iterator<? extends Version> iterator = elements.iterator();
        Iterator<? extends Value> otherIterator = otherElements.iterator();
        while (iterator.hasNext() && otherIterator.hasNext()) {
            Version version = iterator.next();
            Value other = otherIterator.next();
            int v = version.compareTo(other);
            if (v == 0) {
                continue;
            }
            return v;
        }
        return 0;
    }

    /**
     * 获取组合版本的字符串表示。
     * <p>
     * 若指定了分隔符，使用分隔符连接各元素的字符串表示；
     * 否则直接拼接各元素的字符串表示。
     *
     * @return 组合版本的字符串表示
     */
    @Override
    public String getAsString() {
        return delimiter == null ? elements.map((e) -> e.getAsString()).collect(Collectors.joining())
                : elements.map((e) -> e.getAsString()).collect(Collectors.joining(delimiter));
    }

    /**
     * 组合版本不支持转换为数字类型，调用此方法将抛出异常。
     *
     * @return 不支持此操作
     * @throws UnsupportedOperationException 始终抛出
     */
    @Override
    public NumberValue getAsNumber() {
        throw new UnsupportedOperationException("Not a Number");
    }

    /**
     * 判断当前对象是否为多值类型。
     * <p>
     * 组合版本始终返回true。
     *
     * @return true
     */
    @Override
    public boolean isMultiple() {
        return true;
    }

    /**
     * 判断当前对象是否为数字类型。
     * <p>
     * 组合版本始终返回false。
     *
     * @return false
     */
    @Override
    public boolean isNumber() {
        return false;
    }

    /**
     * 将当前组合版本与另一个版本连接，返回新的组合版本。
     * <p>
     * 新组合版本包含当前版本的所有元素和指定版本，使用相同的分隔符。
     *
     * @param version 待连接的版本，不可为null
     * @return 新的组合版本
     */
    @Override
    public Version join(@NonNull Version version) {
        Elements<? extends Version> joinElements = Elements.singleton(version);
        return new JoinVersion(this.elements.concat(joinElements), delimiter);
    }
}