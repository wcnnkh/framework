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
     * 从字节数组指定位置读取16位有符号短整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param b   源字节数组
     * @param off 起始偏移量
     * @return 短整数值
     * @throws IndexOutOfBoundsException 如果偏移量超出数组范围
     */
    public static short getShort(byte[] b, int off) {
        return (short) ((b[off + 1] & 0xFF) + (b[off] << 8));
    }

    /**
     * 从输入流读取2字节并转换为16位有符号短整数。
     * <p>
     * 采用大端字节序，高字节在前。
     * 
     * @param source 输入流
     * @return 短整数值
     * @throws IOException 如果读取失败或已到达流末尾
     */
    public static short readShort(InputStream source) throws EOFException, IOException {
        byte[] buff = new byte[2];
        int size = source.read(buff);
        if (size != buff.length) {
            throw new EOFException("read buff=" + Arrays.toString(buff) + ", size=" + size);
        }
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