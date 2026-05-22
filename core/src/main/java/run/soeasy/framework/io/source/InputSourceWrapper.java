package run.soeasy.framework.io.source;

import jdk.nashorn.internal.objects.annotations.Function;
import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

@FunctionalInterface
public interface InputSourceWrapper<I extends InputStream, W extends InputSource<I>> extends InputSource<I>, ReaderSourceWrapper<Reader, W> {
    @Override
    @NonNull
    default I getInputStream() throws IOException{
        return getSource().getInputStream();
    }

    @Override
    @NonNull
    default Reader getReader() throws IOException {
        return getSource().getReader();
    }

    @Override
    default InputSource<I> decode(@NonNull Charset charset) {
        return getSource().decode(charset);
    }

    @Override
    default <R extends Reader> ReaderSource<R> decode(@NonNull ThrowingFunction<? super I, ? extends R, ? extends IOException> decoder) {
        return getSource().decode(decoder);
    }

    @Override
    default @NonNull ReadableByteChannel readableChannel() throws IOException {
        return getSource().readableChannel();
    }
}
