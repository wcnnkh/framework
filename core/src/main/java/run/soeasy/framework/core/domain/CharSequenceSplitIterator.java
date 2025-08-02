package run.soeasy.framework.core.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import run.soeasy.framework.core.StringUtils;

/**
 * 字符序列分割迭代器，用于按指定过滤器集合分割字符序列，
 * 生成{@link CharSequenceTemplate}实例的迭代器。该迭代器支持在指定索引范围内
 * 按多个过滤器进行分割，适用于复杂分隔符场景的字符序列解析。
 *
 * <p>核心特性：
 * <ul>
 *   <li>多过滤器分割：支持使用多个分隔符过滤器进行字符序列分割</li>
 *   <li>索引范围控制：可指定分割的起始和结束索引，实现部分内容解析</li>
 *   <li>懒加载分割：仅在调用{@link #next()}时进行实际分割操作，提高性能</li>
 *   <li>模板生成：每个分割结果转换为{@link CharSequenceTemplate}实例</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>复杂格式解析：如同时支持","和";"作为分隔符的配置项解析</li>
 *   <li>部分内容提取：需要在字符序列的指定范围内进行分割</li>
 *   <li>多分隔符处理：需要多种分隔符进行分割的文本处理场景</li>
 *   <li>流式解析：需要逐个获取分割结果的流式处理场景</li>
 * </ul>
 *
 * <p>示例用法：
 * <pre class="code">
 * CharSequence text = "apple,banana;orange,pear";
 * Collection&lt;CharSequence&gt; delimiters = Arrays.asList(",", ";");
 * 
 * CharSequenceSplitIterator iterator = new CharSequenceSplitIterator(text, delimiters, 0, text.length());
 * while (iterator.hasNext()) {
 *     CharSequenceTemplate part = iterator.next();
 *     System.out.println(part.getAsString()); // 依次输出: apple, banana, orange, pear
 * }
 * </pre>
 *
 * @see CharSequenceTemplate
 * @see Iterator
 */
public class CharSequenceSplitIterator implements Iterator<CharSequenceTemplate> {
    /** 待分割的字符序列，不可为null */
    private final CharSequence charSequence;
    /** 用于分割的过滤器集合，不可为null */
    private final Collection<? extends CharSequence> filters;
    /** 分割的结束索引（不包含） */
    private final int endIndex;
    /** 当前分割索引（当前处理位置） */
    private int index;
    /** 当前分割结果的供应商，延迟加载分割位置和过滤器 */
    private Supplier<KeyValue<Integer, CharSequence>> current;

    /**
     * 构造字符序列分割迭代器。
     *
     * @param charSequence 待分割的字符序列，不可为null
     * @param filters      分割过滤器集合，不可为null
     * @param beginIndex   分割起始索引（包含）
     * @param endIndex     分割结束索引（不包含）
     * @throws NullPointerException 如果charSequence或filters为null
     */
    public CharSequenceSplitIterator(CharSequence charSequence, Collection<? extends CharSequence> filters,
                                     int beginIndex, int endIndex) {
        this.charSequence = charSequence;
        this.filters = filters;
        this.index = beginIndex;
        this.endIndex = endIndex;
    }

    /**
     * 判断是否存在下一个分割结果。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>若当前索引超出结束索引，返回false</li>
     *   <li>若未找到当前分割位置，遍历所有过滤器寻找下一个分割位置</li>
     *   <li>若找到分割位置，缓存分割信息；否则判断剩余内容是否非空</li>
     * </ol>
     *
     * @return true如果存在下一个分割结果，false否则
     */
    @Override
    public boolean hasNext() {
        if (index >= endIndex) {
            return false;
        }

        if (current == null) {
            for (CharSequence filter : filters) {
                if (filter == null) {
                    continue;
                }

                int foundIndex = StringUtils.indexOf(charSequence, filter, this.index, endIndex);
                if (foundIndex != -1) {
                    current = () -> KeyValue.of(foundIndex, filter);
                    break;
                }
            }
        }

        if (current == null) {
            return index < endIndex;
        }
        return true;
    }

    /**
     * 获取下一个分割结果。
     * <p>
     * 实现逻辑：
     * <ol>
     *   <li>若没有下一个元素，抛出NoSuchElementException</li>
     *   <li>若没有找到分割位置，返回从当前索引到结束索引的剩余内容</li>
     *   <li>否则，返回从当前索引到分割位置的内容，并更新当前索引</li>
     * </ol>
     *
     * @return 下一个分割结果
     * @throws NoSuchElementException 如果没有下一个元素
     */
    @Override
    public CharSequenceTemplate next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (current == null) {
            // 处理最后一段内容
            CharSequence value = index == 0 ? charSequence : charSequence.subSequence(index, endIndex);
            index = endIndex;
            return new CharSequenceTemplate(value);
        }

        // 处理中间段内容
        CharSequence value = charSequence.subSequence(index, current.get().getKey());
        index = current.get().getKey() + current.get().getValue().length();
        CharSequenceTemplate pair = new CharSequenceTemplate(value, current.get().getValue());
        current = null;
        return pair;
    }
}