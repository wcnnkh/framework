package run.soeasy.framework.core.match;

import run.soeasy.framework.core.ObjectUtils;

/**
 * 恒等匹配器实现，提供严格的字符串相等匹配功能。
 * 该匹配器不支持任何模式匹配，仅当目标字符串与模式字符串完全相同时返回匹配成功。
 *
 * <p>核心特性：
 * <ul>
 *   <li>单例模式：通过{@link #INSTANCE}提供全局唯一实例</li>
 *   <li>精确匹配：使用{@link ObjectUtils#equals}进行严格相等判断</li>
 *   <li>无模式支持：{@link #isPattern}始终返回false，不识别任何模式</li>
 *   <li>线程安全：无状态设计，可安全用于多线程环境</li>
 * </ul>
 *
 * @see StringMatcher
 * @see ObjectUtils
 */
class IdentityMatcher implements StringMatcher {
    static final IdentityMatcher INSTANCE = new IdentityMatcher();

    /**
     * 判断字符串是否为匹配模式。
     * <p>
     * 恒等匹配器不支持任何模式匹配，因此始终返回false。
     *
     * @param source 待检测的字符串
     * @return 始终返回false
     */
    @Override
    public boolean isPattern(String source) {
        return false;
    }

    /**
     * 判断目标字符串是否匹配模式字符串。
     * <p>
     * 仅当目标字符串与模式字符串使用{@link ObjectUtils#equals}比较为true时返回匹配成功。
     *
     * @param pattern 匹配模式（在此实现中必须与目标字符串完全相等）
     * @param source  待匹配的目标字符串
     * @return 匹配成功返回true，否则返回false
     */
    @Override
    public boolean match(String pattern, String source) {
        return ObjectUtils.equals(pattern, source);
    }

    /**
     * 从完整文本中提取模式匹配的部分。
     * <p>
     * 由于恒等匹配不支持模式，直接返回原始文本（无动态匹配部分需要提取）。
     *
     * @param pattern 匹配模式（在此实现中被忽略）
     * @param text    完整文本
     * @return 直接返回输入的完整文本
     */
    @Override
    public String extractWithinPattern(String pattern, String text) {
        return text;
    }
}