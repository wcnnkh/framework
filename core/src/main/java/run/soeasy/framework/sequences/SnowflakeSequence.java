package run.soeasy.framework.sequences;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Clock; // 引入你提供的 Clock 接口

/**
 * 一个基于雪花算法（Snowflake）的高度可配置、可扩展的分布式唯一ID生成器。
 */
public final class SnowflakeSequence implements Sequence<Long> {

    // =========================================================================
    // 公共常量
    // =========================================================================

    /** 默认纪元 (Epoch)：2024-01-01 00:00:00. */
    public static final long DEFAULT_EPOCH = 1704067200000L;

    /** 默认字段位数分配. */
    public static final long DEFAULT_TIMESTAMP_BITS = 41L;
    public static final long DEFAULT_WORKER_ID_BITS = 10L;
    public static final long DEFAULT_SEQUENCE_BITS = 12L;

    /** 默认时钟：使用系统当前时间。 */
    public static final Clock DEFAULT_CLOCK = Clock.SYSTEM;

    /** 严格的时间回拨策略：一旦检测到时间回拨，立即抛出 {@link NoSuchElementException}. */
    public static final ClockBackwardsPolicy STRICT_CLOCK_BACKWARDS_POLICY = (current, last) -> {
        if (current < last) {
            throw new NoSuchElementException(
                String.format("系统时间回拨，无法生成ID。上次生成时间: %d, 当前时间: %d", last, current));
        }
    };

    /** 宽容的时间回拨策略：如果时间回拨在容忍范围内（默认5毫秒），则等待时间追上。 */
    public static final ClockBackwardsPolicy TOLERANT_CLOCK_BACKWARDS_POLICY = new TolerantClockBackwardsPolicy(5);

    /** 序列号初始值策略：从0开始. */
    public static final SequenceInitializer ZERO_SEQUENCE_INITIALIZER = (workerId, maxSequence) -> 0L;

    /** 序列号初始值策略：从一个0到{@code maxSequence}之间的随机数开始. */
    public static final SequenceInitializer RANDOM_SEQUENCE_INITIALIZER = 
        (workerId, maxSequence) -> maxSequence <= 0 ? 0 : ThreadLocalRandom.current().nextLong(maxSequence + 1);

    /** 序列号初始值策略：从1或2之间随机选择一个作为初始值。 */
    public static final SequenceInitializer RANDOM_START_SEQUENCE_1_2 = 
        (workerId, maxSequence) -> ThreadLocalRandom.current().nextInt(2) + 1;

    // =========================================================================
    // SnowflakeSequence 私有成员变量
    // =========================================================================

    private final long epoch;
    private final long workerId;
    private final Clock clock; // 使用 Clock 接口
    private final ClockBackwardsPolicy clockBackwardsPolicy;
    private final SequenceInitializer sequenceInitializer;

    // 派生常量
    private final long maxTimestamp;
    private final long maxSequence;
    private final long workerIdShift;
    private final long timestampShift;

    // 状态变量
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    /**
     * 私有构造函数，执行所有参数校验并计算派生常量。
     */
    private SnowflakeSequence(long epoch, long workerId, long timestampBits, long workerIdBits, long sequenceBits,
                              Clock clock, ClockBackwardsPolicy clockBackwardsPolicy,
                              SequenceInitializer sequenceInitializer) {
        
        // 1. 校验位数
        if (timestampBits + workerIdBits + sequenceBits != 63) {
            throw new IllegalArgumentException("时间戳位数 + 机器ID位数 + 序列号位数 必须等于 63");
        }
        if (timestampBits <= 0 || workerIdBits <= 0 || sequenceBits <= 0) {
            throw new IllegalArgumentException("时间戳、机器ID、序列号位数均必须大于 0");
        }

        // 2. 校验纪元
        this.epoch = epoch;
        if (epoch >= clock.millis()) { // 使用 clock.millis()
            throw new IllegalArgumentException("纪元时间戳必须早于当前时间。");
        }

        // 3. 校验并计算 workerId
        this.workerId = workerId;
        long maxWorkerId = (1L << workerIdBits) - 1;
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(String.format("机器ID超出范围 [0, %d]", maxWorkerId));
        }

        // 4. 计算派生常量
        this.maxTimestamp = (1L << timestampBits) - 1;
        this.maxSequence = (1L << sequenceBits) - 1;
        this.workerIdShift = sequenceBits;
        this.timestampShift = sequenceBits + workerIdBits;

        // 5. 校验其他参数非空
        this.clock = Objects.requireNonNull(clock, "clock 不能为空");
        this.clockBackwardsPolicy = Objects.requireNonNull(clockBackwardsPolicy, "clockBackwardsPolicy 不能为空");
        this.sequenceInitializer = Objects.requireNonNull(sequenceInitializer, "sequenceInitializer 不能为空");
    }

    /**
     * 获取下一个唯一ID。
     */
    @Override
    @NonNull
    public synchronized Long next() throws NoSuchElementException {
        long currentTimestamp = clock.millis(); 
        clockBackwardsPolicy.handle(currentTimestamp, lastTimestamp);

        validateTimestamp(currentTimestamp);

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
                validateTimestamp(currentTimestamp);
            }
        } else {
            sequence = sequenceInitializer.getInitialValue(workerId, maxSequence);
        }

        lastTimestamp = currentTimestamp;
        long relativeTimestamp = currentTimestamp - epoch;
        return (relativeTimestamp << timestampShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 校验当前时间戳是否超出了最大可表示范围。
     */
    private void validateTimestamp(long currentTimestamp) {
        long relativeTimestamp = currentTimestamp - epoch;
        if (relativeTimestamp > maxTimestamp) {
            throw new NoSuchElementException(
                String.format("时间戳超出最大值。当前相对时间戳: %d, 最大值: %d", relativeTimestamp, maxTimestamp));
        }
    }

    /**
     * 等待并获取下一个毫秒的时间戳。
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = clock.millis(); 
        while (timestamp <= lastTimestamp) {
            Thread.yield();
            timestamp = clock.millis();
        }
        return timestamp;
    }

    // Getters
    public long getEpoch() { return epoch; }
    public long getWorkerId() { return workerId; }
    public long getMaxSequence() { return maxSequence; }

    // =========================================================================
    // 可复用的 Builder
    // =========================================================================

    /**
     * 创建一个默认配置的 {@code SnowflakeSequence} 实例。
     */
    public static SnowflakeSequence create() {
        return builder().build();
    }

    /**
     * 获取一个 {@code Builder} 实例。
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 用于构建 {@link SnowflakeSequence} 的构建者 (可复用)。
     */
    public static class Builder {
        // 可配置的参数
        private long epoch = DEFAULT_EPOCH;
        private long workerId = -1;
        private long timestampBits = DEFAULT_TIMESTAMP_BITS;
        private long workerIdBits = DEFAULT_WORKER_ID_BITS;
        private long sequenceBits = DEFAULT_SEQUENCE_BITS;
        private Clock clock = DEFAULT_CLOCK; // 使用 Clock 接口
        private ClockBackwardsPolicy clockBackwardsPolicy = STRICT_CLOCK_BACKWARDS_POLICY;
        private SequenceInitializer sequenceInitializer = ZERO_SEQUENCE_INITIALIZER;

        public Builder() {}

        // --- 配置方法 ---
        public Builder epoch(long epoch) { this.epoch = epoch; return this; }
        public Builder workerId(long workerId) { this.workerId = workerId; return this; }
        public Builder timestampBits(long timestampBits) { this.timestampBits = timestampBits; return this; }
        public Builder workerIdBits(long workerIdBits) { this.workerIdBits = workerIdBits; return this; }
        public Builder sequenceBits(long sequenceBits) { this.sequenceBits = sequenceBits; return this; }
        public Builder clock(@NonNull Clock clock) { this.clock = clock; return this; } // 使用 Clock 接口
        public Builder clockBackwardsPolicy(@NonNull ClockBackwardsPolicy policy) { this.clockBackwardsPolicy = policy; return this; }
        public Builder sequenceInitializer(@NonNull SequenceInitializer initializer) { this.sequenceInitializer = initializer; return this; }

        /**
         * 构建 {@link SnowflakeSequence} 实例。
         */
        public SnowflakeSequence build() {
            // Builder 仅负责 workerId 的自动生成和参数传递
            long workerIdToUse = this.workerId;
            if (workerIdToUse == -1) {
                workerIdToUse = generateWorkerId(this.workerIdBits);
            }

            SnowflakeSequence sequence = new SnowflakeSequence(
                this.epoch,
                workerIdToUse,
                this.timestampBits,
                this.workerIdBits,
                this.sequenceBits,
                this.clock,
                this.clockBackwardsPolicy,
                this.sequenceInitializer
            );

            // 重置 Builder 以便复用
            reset();

            return sequence;
        }

        /**
         * 重置 Builder 状态，以便复用。
         */
        public Builder reset() {
            this.epoch = DEFAULT_EPOCH;
            this.workerId = -1;
            this.timestampBits = DEFAULT_TIMESTAMP_BITS;
            this.workerIdBits = DEFAULT_WORKER_ID_BITS;
            this.sequenceBits = DEFAULT_SEQUENCE_BITS;
            this.clock = DEFAULT_CLOCK;
            this.clockBackwardsPolicy = STRICT_CLOCK_BACKWARDS_POLICY;
            this.sequenceInitializer = ZERO_SEQUENCE_INITIALIZER;
            return this;
        }

        /**
         * 基于IP地址和PID生成一个 workerId。
         */
        private static long generateWorkerId(long workerIdBits) {
            if (workerIdBits <= 0) return 0;
            long maxWorkerId = (1L << workerIdBits) - 1;
            if (maxWorkerId == 0) return 0;

            long ipHash = 0;
            try {
                InetAddress localHost = getFirstNonLoopbackAddress();
                if (localHost != null) {
                    byte[] address = localHost.getAddress();
                    for (byte b : address) ipHash = (ipHash << 8) | (b & 0xFF);
                }
            } catch (Exception e) {
                ipHash = new Random().nextLong();
            }

            long pid = getPid();
            long combined = ipHash ^ pid;
            return Math.abs(combined) % (maxWorkerId + 1);
        }

        /**
         * 获取第一个非回环地址。
         */
        private static InetAddress getFirstNonLoopbackAddress() throws SocketException, UnknownHostException {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) return addr;
                }
            }
            return InetAddress.getLocalHost();
        }

        /**
         * 获取当前进程的PID。
         */
        private static long getPid() {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            int index = name.indexOf('@');
            if (index > 0) {
                try {
                    return Long.parseLong(name.substring(0, index));
                } catch (NumberFormatException e) { /* ignore */ }
            }
            return new Random().nextLong();
        }
    }

    // =========================================================================
    // 策略接口与实现
    // =========================================================================

    /**
     * 时间回拨处理策略。
     */
    @FunctionalInterface
    public interface ClockBackwardsPolicy {
        void handle(long currentTimestamp, long lastTimestamp) throws NoSuchElementException;
    }

    /**
     * 序列号初始值生成器。
     */
    @FunctionalInterface
    public interface SequenceInitializer {
        long getInitialValue(long workerId, long maxSequence);
    }

    /**
     * 宽容的时间回拨策略的具体实现。
     */
    private static class TolerantClockBackwardsPolicy implements ClockBackwardsPolicy {
        private final long toleranceMillis;
        public TolerantClockBackwardsPolicy(long toleranceMillis) { this.toleranceMillis = Math.max(toleranceMillis, 0); }
        
        @Override
        public void handle(long current, long last) {
            if (current >= last) return;
            
            long offset = last - current;
            if (offset <= toleranceMillis) {
                try {
                    Thread.sleep(offset + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    NoSuchElementException ex = new NoSuchElementException("等待时间回拨恢复时线程被中断。");
                    ex.initCause(e);
                    throw ex;
                }
            } else {
                throw new NoSuchElementException(
                    String.format("系统时间回拨超出容忍范围（%dms）。上次: %d, 当前: %d", toleranceMillis, last, current));
            }
        }
    }
}