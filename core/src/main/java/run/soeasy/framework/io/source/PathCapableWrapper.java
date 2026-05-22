package run.soeasy.framework.io.source;

import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Path;

public interface PathCapableWrapper<W extends PathCapable> extends PathCapable, LastModifiedCapableWrapper<W>{
    @Override
    default @NonNull Path getPath(){
        return getSource().getPath();
    }

    @Override
    default long lastModified() throws IOException {
        return getSource().lastModified();
    }
}
