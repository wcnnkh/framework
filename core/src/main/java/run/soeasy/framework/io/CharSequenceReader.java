package run.soeasy.framework.io;
import java.io.IOException;
import java.nio.CharBuffer;

/**
 * CharSequence到Readable的适配器
 * 允许将任何CharSequence（如String、StringBuilder）作为Readable使用
 * 
 * 该实现避免了将CharSequence转换为String的额外开销，
 * 直接从CharSequence中读取字符，提高性能
 */
public class CharSequenceReader implements Readable {
    private final CharSequence charSequence;
    private int position = 0;

    /**
     * 构造函数
     * 
     * @param charSequence 要适配的字符序列
     */
    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    /**
     * 从字符序列中读取字符到目标字符缓冲区
     * 
     * @param target 目标字符缓冲区
     * @return 读取的字符数，-1表示已到达末尾
     * @throws IOException 当读取过程中发生错误时（此实现不会抛出）
     */
    @Override
    public int read(CharBuffer target) throws IOException {
        if (position >= charSequence.length()) {
            return -1; // 已到达字符序列末尾
        }

        int remaining = charSequence.length() - position;
        int charsToRead = Math.min(remaining, target.remaining());

        for (int i = 0; i < charsToRead; i++) {
            target.put(charSequence.charAt(position + i));
        }

        position += charsToRead;
        return charsToRead;
    }

    /**
     * 重置读取位置到开头
     */
    public void reset() {
        position = 0;
    }

    /**
     * 获取当前读取位置
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取字符序列的总长度
     */
    public int length() {
        return charSequence.length();
    }
}