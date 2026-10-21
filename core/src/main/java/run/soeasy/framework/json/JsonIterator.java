package run.soeasy.framework.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.io.Text;

@RequiredArgsConstructor
public class JsonIterator implements Iterator<JsonValue> {

    @NonNull
    private final Reader reader;

    @NonNull
    private final CharBuffer buffer;

    private final StringBuilder rawBuffer = new StringBuilder(64);
    private final StringBuilder tokenValue = new StringBuilder(64);

    private boolean eof = false;
    private JsonToken currentToken;

    public JsonIterator(@NonNull Reader reader) {
        this(reader, CharBuffer.allocate(Text.DEFAULT_CHAR_BUFFER_SIZE));
    }

    // ==================== 缓冲管理 ====================

    private void ensureBuffered(int minChars) throws IOException {
        while (buffer.remaining() < minChars && !eof) {
            if (buffer.capacity() < minChars) {
                throw new JsonParseException(
                    "Buffer capacity (" + buffer.capacity()
                    + ") is smaller than required minimum (" + minChars + ")");
            }
            buffer.compact();
            int read = reader.read(buffer);
            buffer.flip();
            if (read == -1) {
                eof = true;
            } else if (read == 0) {
                Thread.yield();
            }
        }
    }

    private int peek() throws IOException {
        ensureBuffered(1);
        if (buffer.remaining() == 0) return -1;
        return buffer.get(buffer.position());
    }

    private char readChar() throws IOException {
        ensureBuffered(1);
        if (buffer.remaining() == 0) throw new JsonParseException("Unexpected EOF");
        return buffer.get();
    }

    // ==================== Iterator ====================

    @Override
    public boolean hasNext() {
        if (currentToken == JsonToken.EOF) return false;
        try {
            skipWhitespace();
            if (peek() == -1) {
                currentToken = JsonToken.EOF;
                tokenValue.setLength(0);
                return false;
            }
            return true;
        } catch (IOException e) {
            throw new JsonParseException("I/O error during hasNext", e);
        }
    }

    @Override
    public JsonValue next() {
        if (!hasNext()) throw new NoSuchElementException("No more JSON tokens");
        if (currentToken == null) {
            try {
                currentToken = readNextToken();
            } catch (IOException e) {
                throw new JsonParseException("I/O error during next", e);
            }
        }
        JsonToken result = currentToken;
        if (currentToken != JsonToken.EOF) currentToken = null;
        return new JsonValue(tokenValue.toString(), result);
    }

    // ==================== 核心解析 ====================

    private JsonToken readNextToken() throws IOException {
        while (true) {
            skipWhitespace();
            int ch = peek();
            if (ch == '/') {
                skipComment();
                continue;
            }
            if (ch == -1) {
                tokenValue.setLength(0);
                return JsonToken.EOF;
            }
            switch (ch) {
                case '{': readChar(); tokenValue.setLength(0); return JsonToken.BEGIN_OBJECT;
                case '}': readChar(); tokenValue.setLength(0); return JsonToken.END_OBJECT;
                case '[': readChar(); tokenValue.setLength(0); return JsonToken.BEGIN_ARRAY;
                case ']': readChar(); tokenValue.setLength(0); return JsonToken.END_ARRAY;
                case ':': readChar(); tokenValue.setLength(0); return JsonToken.NAME_SEPARATOR;
                case ',': readChar(); tokenValue.setLength(0); return JsonToken.VALUE_SEPARATOR;
                case '"': return readString();
                case 't': return readTrue();
                case 'f': return readFalse();
                case 'n': return readNull();
                case '-':
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    return readNumber();
                default:
                    throw new JsonParseException("Unexpected character: '" + (char) ch + "' (code " + ch + ")");
            }
        }
    }

    // ==================== 空白 ====================

    private void skipWhitespace() throws IOException {
        while (true) {
            ensureBuffered(1);
            if (buffer.remaining() == 0) break;
            char ch = buffer.get(buffer.position());
            if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') break;
            buffer.get();
        }
    }

    // ==================== 注释 ====================

    private void skipComment() throws IOException {
        readChar(); // '/'
        ensureBuffered(1);
        if (buffer.remaining() == 0) throw new JsonParseException("Unexpected '/' at EOF");

        if (buffer.get(buffer.position()) == '/') {
            readChar();
            while (true) {
                ensureBuffered(1);
                if (buffer.remaining() == 0) break;
                if (readChar() == '\n') break;
            }
        } else if (buffer.get(buffer.position()) == '*') {
            readChar();
            while (true) {
                ensureBuffered(1);
                if (buffer.remaining() == 0) {
                    throw new JsonParseException("Unterminated multi-line comment");
                }
                if (readChar() == '*') {
                    ensureBuffered(1);
                    if (buffer.remaining() > 0 && buffer.get(buffer.position()) == '/') {
                        readChar();
                        break;
                    }
                }
            }
        } else {
            throw new JsonParseException("Unexpected '/' (not a comment).");
        }
    }

    // ==================== 字符串值 ====================

    private JsonToken readString() throws IOException {
        readChar(); // '"'
        tokenValue.setLength(0);

        int start = buffer.position();

        while (buffer.hasRemaining()) {
            char ch = buffer.get(buffer.position());
            if (ch == '"') {
                int end = buffer.position();
                buffer.get(); // 消费 '"'
                if (end > start) {
                    CharBuffer view = buffer.duplicate();
                    view.position(start);
                    view.limit(end);
                    tokenValue.append(view);
                }
                return JsonToken.STRING;
            }
            if (ch == '\\') {
                buffer.position(start);
                return readStringSlowPath();
            }
            if (ch >= 0x00 && ch <= 0x1F) {
                throw new JsonParseException(
                    "Unescaped control character: 0x" + Integer.toHexString(ch).toUpperCase());
            }
            buffer.get();
        }

        buffer.position(start);
        return readStringSlowPath();
    }

    private JsonToken readStringSlowPath() throws IOException {
        rawBuffer.setLength(0);

        while (true) {
            ensureBuffered(1);
            if (buffer.remaining() == 0) throw new JsonParseException("Unterminated string");

            char ch = readChar();
            if (ch == '"') break;
            if (ch >= 0x00 && ch <= 0x1F) {
                throw new JsonParseException(
                    "Unescaped control character: 0x" + Integer.toHexString(ch).toUpperCase());
            }
            rawBuffer.append(ch);
        }

        if (rawBuffer.length() > 0) {
            JsonEscaping.INSTANCE.decoder().transfer(CharBuffer.wrap(rawBuffer), tokenValue::append);
        }
        return JsonToken.STRING;
    }

    // ==================== 字面量 ====================

    private JsonToken readTrue() throws IOException {
        readChar(); tokenValue.setLength(0); tokenValue.append('t');
        ensureBuffered(3);
        if (buffer.remaining() < 3) throw new JsonParseException("Unexpected EOF in 'true'");
        char r = readChar(), u = readChar(), e = readChar();
        if (r != 'r' || u != 'u' || e != 'e') {
            throw new JsonParseException("Expected 'rue' but got '" + r + u + e + "'");
        }
        tokenValue.append("rue");
        return JsonToken.BOOLEAN;
    }

    private JsonToken readFalse() throws IOException {
        readChar(); tokenValue.setLength(0); tokenValue.append('f');
        ensureBuffered(4);
        if (buffer.remaining() < 4) throw new JsonParseException("Unexpected EOF in 'false'");
        char a = readChar(), l = readChar(), s = readChar(), e = readChar();
        if (a != 'a' || l != 'l' || s != 's' || e != 'e') {
            throw new JsonParseException("Expected 'alse' but got '" + a + l + s + e + "'");
        }
        tokenValue.append("alse");
        return JsonToken.BOOLEAN;
    }

    private JsonToken readNull() throws IOException {
        readChar(); tokenValue.setLength(0); tokenValue.append('n');
        ensureBuffered(3);
        if (buffer.remaining() < 3) throw new JsonParseException("Unexpected EOF in 'null'");
        char u1 = readChar(), l1 = readChar(), l2 = readChar();
        if (u1 != 'u' || l1 != 'l' || l2 != 'l') {
            throw new JsonParseException("Expected 'ull' but got '" + u1 + l1 + l2 + "'");
        }
        tokenValue.append("ull");
        return JsonToken.NULL;
    }

    private JsonToken readNumber() throws IOException {
        tokenValue.setLength(0);
        ensureBuffered(1);
        if (buffer.remaining() == 0) throw new JsonParseException("Unexpected EOF after '-' or digit");
        tokenValue.append(readChar());
        while (true) {
            ensureBuffered(1);
            if (buffer.remaining() == 0) break;
            if (!isNumberChar(buffer.get(buffer.position()))) break;
            tokenValue.append(readChar());
        }
        return JsonToken.NUMBER;
    }

    private boolean isNumberChar(char ch) {
        return (ch >= '0' && ch <= '9')
            || ch == '.' || ch == 'e' || ch == 'E'
            || ch == '+' || ch == '-';
    }
}