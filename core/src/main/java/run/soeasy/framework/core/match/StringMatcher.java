package run.soeasy.framework.core.match;

import java.util.Comparator;

/**
 * 字符串匹配器接口，定义字符串模式匹配的标准行为，同时实现{@link Comparator}接口
 * 以支持基于匹配规则的字符串比较。该接口提供多种预定义匹配策略，并允许自定义匹配逻辑。
 *
 * <p>核心特性：
 * <ul>
 *   <li>多策略匹配：支持恒等匹配、前缀匹配、通配符匹配等多种策略</li>
 *   <li>模式判断：提供{@link #isPattern}方法判断字符串是否为匹配模式</li>
 *   <li>动态提取：通过{@link #extractWithinPattern}提取模式匹配的部分</li>
 *   <li>比较支持：实现Comparator接口，支持基于匹配规则的字符串排序</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>路径模式匹配（如文件系统路径、URL路径）</li>
 *   <li>配置项模式匹配（如属性键匹配）</li>
 *   <li>权限规则匹配（如资源访问模式）</li>
 *   <li>日志过滤和模式匹配</li>
 * </ul>
 *
 * @see Comparator
 */
public interface StringMatcher extends Comparator<String> {

    /**
     * 获取恒等匹配器，仅当文本与模式完全相同时返回true。
     * <p>
     * 该匹配器等价于String.equals()方法，不支持任何模式匹配，
     * 常用于精确匹配场景。
     *
     * @return 恒等匹配器实例
     */
    public static StringMatcher identity() {
        return IdentityMatcher.INSTANCE;
    }

    /**
     * 获取默认前缀匹配器，基于{@link String#startsWith}实现，
     * 严格区分大小写。模式本身为完整前缀，不支持通配符。
     *
     * @return 前缀匹配器实例
     */
    public static StringMatcher prefix() {
        return PrefixMatcher.DEFAULT;
    }

    /**
     * 获取指定大小写策略的前缀匹配器。
     *
     * @param ignoreCase 是否忽略大小写：
     * @return 前缀匹配器实例
     */
    public static StringMatcher prefix(boolean ignoreCase) {
        return ignoreCase ? PrefixMatcher.IGNORE_CASE : PrefixMatcher.DEFAULT;
    }

    /**
     * 获取标准通配符匹配器，支持以下通配符：
     * <ul>
     *   <li>{@code ?}：匹配任意单个字符</li>
     *   <li>{@code *}：匹配任意数量（包括0个）的字符序列</li>
     * </ul>
     * 该匹配器不支持正则表达式，仅处理标准通配符模式。
     *
     * @return 通配符匹配器实例
     */
    public static StringMatcher wildcard() {
        return WildcardMatcher.INSTANCE;
    }

    /**
     * 判断给定文本是否为匹配模式。
     * <p>
     * 例如：对于通配符匹配器，包含{@code ?}或{@code *}的文本视为模式；
     * 对于前缀匹配器，任何文本都视为模式（因为前缀匹配支持任意前缀）。
     *
     * @param text 待检测的文本
     * @return true如果文本是匹配模式，否则false
     */
    boolean isPattern(String text);

    /**
     * 判断文本是否匹配给定模式。
     *
     * @param pattern 匹配模式
     * @param text    待匹配的文本
     * @return true如果文本匹配模式，否则false
     */
    boolean match(String pattern, String text);

    /**
     * 从完整文本中提取模式匹配的部分。
     * <p>
     * 该方法用于剥离模式中的静态部分，返回实际动态匹配的文本片段。
     * 例如：模式为"myroot/*.html"，文本为"myroot/myfile.html"，
     * 则返回"myfile.html"（即通配符{@code *}匹配的部分）。
     *
     * @param pattern 匹配模式
     * @param text    完整文本
     * @return 模式匹配的文本片段（非null）
     */
    String extractWithinPattern(String pattern, String text);

    /**
     * 基于匹配规则比较两个字符串。
     * <p>
     * 比较逻辑：
     * <ol>
     *   <li>若两个都是模式：能匹配对方的模式返回1，被对方匹配返回-1，否则-1</li>
     *   <li>若只有一个是模式：模式字符串返回1，非模式返回-1</li>
     *   <li>若都不是模式：按字符串自然顺序比较</li>
     * </ol>
     *
     * @param o1 待比较的第一个字符串
     * @param o2 待比较的第二个字符串
     * @return 比较结果（负/零/正）
     */
    @Override
    default int compare(String o1, String o2) {
        if (isPattern(o1) && isPattern(o2)) {
            if (match(o1, o2)) {
                return 1;
            } else if (match(o2, o1)) {
                return -1;
            } else {
                return -1;
            }
        } else if (isPattern(o1)) {
            return 1;
        } else if (isPattern(o2)) {
            return -1;
        }
        return o1.compareTo(o2);
    }
}