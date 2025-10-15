package run.soeasy.framework.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import lombok.experimental.UtilityClass;

/**
 * 字节操作工具类，提供基本数据类型与字节数组之间的相互转换功能。
 * 该类采用大端字节序（Big Endian）实现数据的打包和解包操作，
 * 支持从字节数组读取基本类型数据，以及将基本类型数据写入字节数组或输出流。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li>字节序处理：固定使用大端字节序（高位在前）</li>
 *   <li>高效转换：通过位移和按位运算实现高性能数据转换</li>
 *   <li>流操作支持：提供从输入流读取和向输出流写入的方法</li>
 *   <li>异常处理：读取不足时抛出EOFException，确保数据完整性</li>
 * </ul>
 *
 * <p><b>使用注意事项：</b>
 * <ul>
 *   <li>所有方法均为静态方法，不支持实例化</li>
 *   <li>操作字节数组时需确保足够的长度，避免IndexOutOfBoundsException</li>
 *   <li>流操作方法会读取固定字节数，不足时抛出EOFException</li>
 *   <li>浮点类型转换基于IEEE 754标准</li>
 * </ul>
 *
 * @author soeasy.run
 * @see java.nio.ByteBuffer
 */
@UtilityClass
public class Bits {
    /*
     * 从字节数组指定偏移位置解包基本数据类型的方法。
     */

    /**
     * 从字节数组指定位置读取布尔值。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 布尔值（非零字节表示true，零字节表示false）
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    /**
     * 从输入流读取一个字节并转换为布尔值。
     * 
     * @param source 输入流
     * @return 布尔值（非零字节表示true，零字节表示false）
     * @throws IOException 如果读取失败或已到达流末尾
     */
    public static boolean readBoolean(InputStream source) throws IOException {
        int value = source.read();
        if (value == -1) {
            throw new EOFException();
        }
        return value != 0;
    }

    /**
     * 从字节数组指定位置读取16位Unicode字符。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 字符值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static char getChar(byte[] b, int off) {
        return (char) ((b[off + 1] & 0xFF) + (b[off] << 8));
    }

    /**
     * 从字节数组的指定位置读取2字节，转换为16位有符号短整数（大端字节序）。
     * 
     * @param buffer 字节数组（不可为null，且长度需至少为offset+2）
     * @param offset 起始偏移量（从该位置开始读取2字节）
     * @return 转换后的短整数
     * @throws IndexOutOfBoundsException 若offset越界（如offset+1超出数组长度）
     */
    public static short getShort(byte[] buffer, int offset) {
        // 高8位：buffer[offset] 转换为无符号值后左移8位
        // 低8位：buffer[offset+1] 转换为无符号值
        return (short) (
            ((buffer[offset] & 0xFF) << 8) | 
            (buffer[offset + 1] & 0xFF)
        );
    }

    /**
     * 从输入流读取2字节并转换为16位有符号短整数（采用大端字节序，高字节在前）。
     * 
     * <p>大端字节序（Big-Endian）处理逻辑：
     * 输入流中先读取的字节为高8位，后读取的字节为低8位，拼接为短整数：
     * <pre>
     * 字节序列：[b0, b1] → 短整数 = (b0 &lt;&lt; 8) | b1
     * 其中，b0为高8位，b1为低8位（均需先转换为无符号字节值）
     * </pre>
     * 
     * @param source 输入流（不可为null，否则抛出{@link NullPointerException}）
     * @return 转换后的16位有符号短整数（范围：-32768 ~ 32767）
     * @throws NullPointerException 若source为null（未传入有效输入流）
     * @throws EOFException 若输入流已到达末尾，无法读取2字节（如实际读取字节数小于2）
     * @throws IOException 若读取过程中发生IO错误（如流关闭、读取中断等）
     * @see #getShort(byte[], int) 字节数组转短整数的具体实现（封装大端字节序转换逻辑）
     */
    public static short readShort(InputStream source) throws EOFException, IOException {
        // 参数非空校验，避免后续调用NPE
        if (source == null) {
            throw new NullPointerException("InputStream source cannot be null");
        }
        
        byte[] buff = new byte[2];
        int readSize = source.read(buff);
        
        // 校验读取长度：必须读取到完整的2字节，否则视为流结束
        if (readSize != buff.length) {
            throw new EOFException(String.format(
                "Failed to read 2 bytes (expected 2, actual %d), buffer content: %s",
                readSize, Arrays.toString(buff)
            ));
        }
        
        // 委托getShort方法完成字节数组到短整数的转换（大端字节序）
        return getShort(buff, 0);
    }

    /**
     * 从字节数组指定位置读取32位有符号整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + ((b[off]) << 24);
    }

    /**
     * 从输入流读取4字节并转换为32位有符号整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 输入流
     * @return 整数值
     * @throws IOException 如果读取失败或已到达流末尾
     */
    public static int readInt(InputStream source) throws IOException {
        byte[] buff = new byte[4];
        int size = source.read(buff);
        if (size != buff.length) {
            throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
        }
        return getInt(buff, 0);
    }

    /**
     * 从字节数组指定位置读取32位IEEE 754单精度浮点数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 浮点数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static float getFloat(byte[] b, int off) {
        return Float.intBitsToFloat(getInt(b, off));
    }

    /**
     * 从字节数组指定位置读取64位有符号长整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 长整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16)
                + ((b[off + 4] & 0xFFL) << 24) + ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40)
                + ((b[off + 1] & 0xFFL) << 48) + (((long) b[off]) << 56);
    }

    /**
     * 从输入流读取8字节并转换为64位有符号长整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 输入流
     * @return 长整数值
     * @throws IOException 如果读取失败或已到达流末尾
     */
    public static long readLong(InputStream source) throws IOException {
        byte[] buff = new byte[8];
        int size = source.read(buff);
        if (size != buff.length) {
            throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
        }
        return getLong(buff, 0);
    }

    /**
     * 从字节数组指定位置读取64位IEEE 754双精度浮点数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 双精度浮点数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static double getDouble(byte[] b, int off) {
        return Double.longBitsToDouble(getLong(b, off));
    }

    /*
     * 将基本数据类型打包到字节数组指定位置的方法。
     */

    /**
     * 将布尔值写入字节数组指定位置。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 布尔值（true写1，false写0）
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }

    /**
     * 将布尔值写入输出流。
     * 
     * @param source 布尔值（true写1，false写0）
     * @param target 输出流
     * @throws IOException 如果写入失败
     */
    public static void writeBoolean(boolean source, OutputStream target) throws IOException {
        target.write(source ? 1 : 0);
    }

    /**
     * 将16位Unicode字符写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 字符值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putChar(byte[] b, int off, char val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    /**
     * 将16位有符号短整数写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 短整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val);
        b[off] = (byte) (val >>> 8);
    }

    /**
     * 将16位有符号短整数写入输出流。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 短整数值
     * @param target 输出流
     * @throws IOException 如果写入失败
     */
    public static void writeShort(short source, OutputStream target) throws IOException {
        byte[] buff = new byte[2];
        putShort(buff, 0, source);
        target.write(buff);
    }

    /**
     * 将32位有符号整数写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val);
        b[off + 2] = (byte) (val >>> 8);
        b[off + 1] = (byte) (val >>> 16);
        b[off] = (byte) (val >>> 24);
    }

    /**
     * 将32位有符号整数写入输出流。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 整数值
     * @param target 输出流
     * @throws IOException 如果写入失败
     */
    public static void writeInt(int source, OutputStream target) throws IOException {
        byte[] buff = new byte[4];
        putInt(buff, 0, source);
        target.write(buff);
    }

    /**
     * 将32位IEEE 754单精度浮点数写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 浮点数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putFloat(byte[] b, int off, float val) {
        putInt(b, off, Float.floatToIntBits(val));
    }

    /**
     * 将64位有符号长整数写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 长整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val);
        b[off + 6] = (byte) (val >>> 8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off] = (byte) (val >>> 56);
    }

    /**
     * 将64位有符号长整数写入输出流。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 长整数值
     * @param target 输出流
     * @throws IOException 如果写入失败
     */
    public static void writeLong(long source, OutputStream target) throws IOException {
        byte[] buff = new byte[8];
        putLong(buff, 0, source);
        target.write(buff);
    }

    /**
     * 将64位IEEE 754双精度浮点数写入字节数组指定位置。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   目标字节数组
     * @param off 起始偏移量
     * @param val 双精度浮点数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static void putDouble(byte[] b, int off, double val) {
        putLong(b, off, Double.doubleToLongBits(val));
    }
}