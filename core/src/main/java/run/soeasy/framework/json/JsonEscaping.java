package run.soeasy.framework.json;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;

import lombok.NonNull;
import run.soeasy.framework.codec.io.StringCodec;
import run.soeasy.framework.codec.io.StringDecoder;
import run.soeasy.framework.codec.io.StringEncoder;
import run.soeasy.framework.io.IOUtils;
import run.soeasy.framework.io.buffer.BufferPool;
import run.soeasy.framework.io.buffer.BufferReader;
import run.soeasy.framework.io.buffer.BufferWriter;

public final class JsonEscaping implements StringCodec {
    public static final JsonEscaping INSTANCE = new JsonEscaping();

    private static final char BACKSLASH = '\\';
    private static final char UNICODE_ESCAPE_MARKER = 'u';
    private static final char CONTROL_CHAR_END = '\u001F';
    private static final int UNICODE_HEX_LENGTH = 4;
    private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };
    private static final int ESCAPE_MAP_LEN = 128;

    private static final char[][] ESCAPE_MAP;
    private static final char[] UNESCAPE_MAP;
    private static final byte[] HEX_VALUE_MAP;

    static {
        ESCAPE_MAP = new char[ESCAPE_MAP_LEN][];
        Arrays.fill(ESCAPE_MAP, null);
        ESCAPE_MAP['"'] = new char[] { '\\', '"' };
        ESCAPE_MAP['\\'] = new char[] { '\\', '\\' };
        ESCAPE_MAP['\b'] = new char[] { '\\', 'b' };
        ESCAPE_MAP['\f'] = new char[] { '\\', 'f' };
        ESCAPE_MAP['\n'] = new char[] { '\\', 'n' };
        ESCAPE_MAP['\r'] = new char[] { '\\', 'r' };
        ESCAPE_MAP['\t'] = new char[] { '\\', 't' };

        UNESCAPE_MAP = new char[ESCAPE_MAP_LEN];
        Arrays.fill(UNESCAPE_MAP, (char) -1);
        UNESCAPE_MAP['"'] = '"';
        UNESCAPE_MAP['\\'] = '\\';
        UNESCAPE_MAP['b'] = '\b';
        UNESCAPE_MAP['f'] = '\f';
        UNESCAPE_MAP['n'] = '\n';
        UNESCAPE_MAP['r'] = '\r';
        UNESCAPE_MAP['t'] = '\t';

        HEX_VALUE_MAP = new byte[ESCAPE_MAP_LEN];
        Arrays.fill(HEX_VALUE_MAP, (byte) -1);
        for (int i = '0'; i <= '9'; i++)
            HEX_VALUE_MAP[i] = (byte) (i - '0');
        for (int i = 'a'; i <= 'f'; i++)
            HEX_VALUE_MAP[i] = (byte) (10 + i - 'a');
        for (int i = 'A'; i <= 'F'; i++)
            HEX_VALUE_MAP[i] = (byte) (10 + i - 'A');
    }

    private final BufferPool<CharBuffer> bufferPool;

    @Override
    public StringEncoder encoder() {
        return encoder;
    }

    @Override
    public StringDecoder decoder() {
        return decoder;
    }

    private JsonEscaping() {
        this(IOUtils.text());
    }

    public JsonEscaping(BufferPool<CharBuffer> bufferPool) {
        this.bufferPool = bufferPool;
    }

    private final StringEncoder encoder = new StringEncoder() {
        @Override
        public <R extends Throwable, W extends Throwable> void transfer(
                @NonNull BufferReader<? super CharBuffer, ? extends R> reader,
                @NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
            transferImpl(reader, writer, true);
        }
    };

    private final StringDecoder decoder = new StringDecoder() {
        @Override
        public <R extends Throwable, W extends Throwable> void transfer(
                @NonNull BufferReader<? super CharBuffer, ? extends R> reader,
                @NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, R, W {
            transferImpl(reader, writer, false);
        }
    };

    private <R extends Throwable, W extends Throwable> void transferImpl(
            @NonNull BufferReader<? super CharBuffer, ? extends R> reader,
            @NonNull BufferWriter<? super CharBuffer, ? extends W> writer, boolean encoding) throws IOException, R, W {

        CharBuffer input = bufferPool.get();
        try {
            CharBuffer output = bufferPool.get();
            try {
                // 直接判断 -1，不关心 0 的情况（按你的要求忽略）
                while (reader.read(input) != -1) {
                    // 切换为读模式
                    input.flip();

                    if (encoding) {
                        encodeTo(input, output, writer);
                    } else {
                        decodeTo(input, output, writer);
                    }

                    // 将未消费的数据移到 buffer 头部，等待后续数据
                    input.compact();

                    // 刷出 output 中的数据
                    flushOutput(output, writer);
                }

                // 流结束后，解码器不应有未完成的转义序列
                if (!encoding && input.position() > 0) {
                    throw new IllegalArgumentException("Incomplete escape sequence at end of input stream");
                }

                // 最终刷出残余数据
                flushOutput(output, writer);
            } finally {
                bufferPool.close(output);
            }
        } finally {
            bufferPool.close(input);
        }
    }

    /**
     * 编码：纯 CharBuffer 操作
     */
    private <W extends Throwable> void encodeTo(@NonNull CharBuffer input, @NonNull CharBuffer output,
            @NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, W {

        while (input.hasRemaining()) {
            char c = input.get();

            if (c >= ESCAPE_MAP_LEN || (ESCAPE_MAP[c] == null && c > CONTROL_CHAR_END)) {
                if (output.remaining() < 1) {
                    flushOutput(output, writer);
                }
                output.put(c);
            } else {
                char[] escaped = ESCAPE_MAP[c];
                if (escaped != null) {
                    if (output.remaining() < 2) {
                        flushOutput(output, writer);
                    }
                    output.put(escaped[0]);
                    output.put(escaped[1]);
                } else {
                    // 控制字符，Unicode 转义占 6 位，防御性 flush
                    while (output.remaining() < 6) {
                        flushOutput(output, writer);
                    }
                    output.put(BACKSLASH);
                    output.put(UNICODE_ESCAPE_MARKER);
                    output.put(HEX_CHARS[(c >>> 12) & 0x0F]);
                    output.put(HEX_CHARS[(c >>> 8) & 0x0F]);
                    output.put(HEX_CHARS[(c >>> 4) & 0x0F]);
                    output.put(HEX_CHARS[c & 0x0F]);
                }
            }
        }
    }

    /**
     * 解码：支持跨 buffer，遇到不完整序列时回退 position
     */
    private <W extends Throwable> void decodeTo(@NonNull CharBuffer input, @NonNull CharBuffer output,
            @NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, W {

        while (input.remaining() > 0) {
            char c = input.get();

            if (c != BACKSLASH) {
                if (output.remaining() < 1) {
                    flushOutput(output, writer);
                }
                output.put(c);
                continue;
            }

            // 遇到反斜杠，检查后面是否还有数据
            if (input.remaining() == 0) {
                // 不完整：只有 '\'，回退并等待下一个 buffer
                input.position(input.position() - 1);
                break;
            }

            char escapeChar = input.get();

            if (escapeChar < ESCAPE_MAP_LEN && UNESCAPE_MAP[escapeChar] != (char) -1) {
                if (output.remaining() < 1) {
                    flushOutput(output, writer);
                }
                output.put(UNESCAPE_MAP[escapeChar]);
            } else if (escapeChar == UNICODE_ESCAPE_MARKER) {
                // Unicode 转义，需要 4 位十六进制
                if (input.remaining() < UNICODE_HEX_LENGTH) {
                    // 不完整：回退 '\' 和 'u'，等待后续 buffer
                    input.position(input.position() - 2);
                    break;
                }

                char h0 = input.get();
                char h1 = input.get();
                char h2 = input.get();
                char h3 = input.get();

                if (h0 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h0] == -1 ||
                    h1 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h1] == -1 ||
                    h2 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h2] == -1 ||
                    h3 >= ESCAPE_MAP_LEN || HEX_VALUE_MAP[h3] == -1) {
                    throw new IllegalArgumentException("Invalid Unicode hex: \\u" + h0 + h1 + h2 + h3);
                }

                int codePoint = (HEX_VALUE_MAP[h0] << 12) | (HEX_VALUE_MAP[h1] << 8)
                              | (HEX_VALUE_MAP[h2] << 4) | HEX_VALUE_MAP[h3];

                if (output.remaining() < 1) {
                    flushOutput(output, writer);
                }
                output.put((char) codePoint);
            } else {
                throw new IllegalArgumentException("Invalid escape: \\" + escapeChar);
            }
        }
    }

    /**
     * 将 output 当前内容写出到 writer。
     * 如果 writer 未完全消费，保留剩余数据；否则清空 buffer。
     */
    private <W extends Throwable> void flushOutput(@NonNull CharBuffer output,
            @NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException, W {
        if (output.position() == 0) {
            return;
        }
        output.flip();
        writer.write(output);
        if (output.hasRemaining()) {
            output.compact();
        } else {
            output.clear();
        }
    }
}