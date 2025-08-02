package run.soeasy.framework.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.Data;
import run.soeasy.framework.core.StringUtils;

/**
 * 基于分隔符的可读内容迭代器，用于按指定分隔符将{@link Readable}
 * 内容分割为多个字符序列片段。
 * 
 * <p>该迭代器从底层可读源读取内容，并根据指定的分隔符将其分割为多个部分，
 * 实现流式处理大文本内容而无需一次性加载全部数据。
 * 
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>流式处理：逐块读取数据，避免一次性加载大文件</li>
 *   <li>缓存机制：使用内部缓冲区处理跨块的分隔符匹配</li>
 *   <li>异常处理：将IO异常转换为UncheckedIOException</li>
 * </ul>
 * 
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>处理大型文本文件的行分割</li>
 *   <li>解析基于特定分隔符的数据流</li>
 *   <li>实现非阻塞的文本处理管道</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see Iterator
 * @see Readable
 */
@Data
class SplitReadableIterator implements Iterator<CharSequence> {
    /**
     * 底层可读数据源
     */
    private final Readable readable;
    
    /**
     * 用于读取数据的字符缓冲区
     */
    private final CharBuffer buffer;
    
    /**
     * 用于分割内容的分隔符
     */
    private final CharSequence separator;
    
    /**
     * 存储未完全匹配分隔符的剩余内容
     */
    private final StringBuilder cache = new StringBuilder();
    
    /**
     * 预读取的下一个元素
     */
    private CharSequence next;

    /**
     * 检查缓存中是否存在下一个分隔符分割的元素。
     * 如果存在，则设置next字段并从缓存中移除该元素。
     * 
     * @return 如果缓存中存在下一个元素返回true，否则返回false
     */
    private boolean hasNextCache() {
        int index = StringUtils.indexOf(cache, separator);
        if (index == -1) {
            return false;
        }

        next = cache.subSequence(0, index);
        cache.delete(0, index + separator.length());
        return true;
    }

    /**
     * 判断是否存在下一个元素。
     * 优先检查预读取的元素，然后检查缓存，最后尝试从底层数据源读取更多内容。
     * 
     * @return 如果存在下一个元素返回true，否则返回false
     * @throws UncheckedIOException 如果读取过程中发生IO异常
     */
    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }

        if (hasNextCache()) {
            return true;
        }

        try {
            while (readable.read(buffer) != -1) {
                cache.append(buffer.flip().toString());
                if (hasNextCache()) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        if (cache.length() != 0) {
            next = cache.toString();
            cache.delete(0, cache.length());
            return true;
        }

        return false;
    }

    /**
     * 返回下一个元素，并将指针后移。
     * 如果没有下一个元素，抛出NoSuchElementException。
     * 
     * @return 下一个字符序列
     * @throws NoSuchElementException 如果没有下一个元素
     */
    @Override
    public CharSequence next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        try {
            return next;
        } finally {
            next = null;
        }
    }
}