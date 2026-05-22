package run.soeasy.framework.io.source;

import run.soeasy.framework.core.domain.Wrapper;

import java.io.IOException;
import java.io.Reader;

@FunctionalInterface
public interface ReaderSourceWrapper<R extends Reader, W extends ReaderSource<R>> extends ReaderSource<R>, Wrapper<W> {

    @Override
    default R getReader() throws IOException{
        return getSource().getReader();
    }
}
