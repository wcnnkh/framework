package run.soeasy.framework.io;

import java.io.IOException;
import java.nio.CharBuffer;

/**
 * {@link CharSequence} 到 {@link Readable} 的适配器实现，允许将字符序列（如{@link String}、{@link StringBuilder}）作为可读取数据源使用。
 * 
 * <p>核心设计目标：
 * 避免将{@link CharSequence}转换为{@link String}或{@link java.io.Reader}的额外开销，直接通过字符序列的原生方法实现流式读取，
 * 适用于需要以{@link Readable}接口为输入的场景（如{@link java.util.Scanner}、自定义解析器等）。
 * 
 * <p>功能特性：
 * <ul>
 * <li><strong>零拷贝读取</strong>：直接操作原始{@link CharSequence}，无需中间缓冲区或字符串转换，减少内存占用；</li>
 * <li><strong>位置可重置</strong>：通过{@link #reset()}方法将读取位置重置为开头，支持重复读取同一字符序列；</li>
 * <li><strong>边界安全</strong>：读取过程中自动校验字符序列长度与目标缓冲区容量，避免数组越界；</li>
 * <li><strong>全版本兼容</strong>：适配 Java 8 及以下版本，无依赖新增 API 的兼容性问题。</li>
 * </ul>
 * 
 * <h3>典型使用示例</h3>
 * <pre class="code">
 * // 1. 基础读取：用Scanner解析字符序列
 * CharSequence content = "apple,banana,orange";
 * try (CharSequenceReader reader = new CharSequenceReader(content)) {
 *     Scanner scanner = new Scanner(reader).useDelimiter(",");
 *     while (scanner.hasNext()) {
 *         System.out.println("水果：" + scanner.next());
 *     }
 * }
 * 
 * // 2. 重复读取：重置位置后再次读取
 * CharSequence text = "Hello World";
 * CharSequenceReader reader = new CharSequenceReader(text);
 * CharBuffer buffer = CharBuffer.allocate(5);
 * 
 * // 第一次读取
 * reader.read(buffer);
 * buffer.flip();
 * System.out.println(buffer.toString()); // 输出 "Hello"
 * 
 * // 重置后第二次读取
 * reader.reset();
 * buffer.clear();
 * reader.read(buffer);
 * buffer.flip();
 * System.out.println(buffer.toString()); // 再次输出 "Hello"
 * </pre>
 * 
 * @see Readable
 * @see CharSequence
 * @see CharBuffer
 */
public class CharSequenceReader implements Readable {

    private final CharSequence charSequence;
    private int position = 0;

    /**
     * 构造一个基于指定字符序列的{@link Readable}适配器
     * 
     * @param charSequence 要适配的字符序列，不可为null
     */
    public CharSequenceReader(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    /**
     * 从字符序列中读取字符到目标缓冲区
     * 
     * @param target 接收读取字符的目标缓冲区，不可为null
     * @return 实际读取的字符数（≥0）；若已到达序列末尾，返回-1
     * @throws IOException 本实现中不会抛出此异常
     */
    @Override
    public int read(CharBuffer target) throws IOException {
        if (position >= charSequence.length()) {
            return -1;
        }

        int remainingInSequence = charSequence.length() - position;
        int remainingInBuffer = target.remaining();
        int charsToRead = Math.min(remainingInSequence, remainingInBuffer);

        // 逐个写入字符（兼容所有Java版本）
        for (int i = 0; i < charsToRead; i++) {
            target.put(charSequence.charAt(position + i));
        }

        position += charsToRead;
        return charsToRead;
    }

    /**
     * 重置读取位置到字符序列的开头
     */
    public void reset() {
        position = 0;
    }

    /**
     * 获取当前读取位置
     * 
     * @return 当前读取位置（下一个要读取的字符索引）
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取字符序列的总长度
     * 
     * @return 字符序列的长度
     */
    public int length() {
        return charSequence.length();
    }
}
