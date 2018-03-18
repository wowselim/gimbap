package co.selim.gimbap;

import co.selim.gimbap.api.StreamingStore;
import co.selim.gimbap.util.IDGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * A disk-backed Store implementation. This implementation does not allow updates on objects that do not exist.
 */
public class BinaryStore implements StreamingStore<byte[]> {
    private final Path storePath;
    private final int idLength;

    public BinaryStore() {
        this(Paths.get("data"));
    }

    public BinaryStore(Path storePath) {
        this(storePath, 24);
    }

    public BinaryStore(Path storePath, int idLength) {
        this.storePath = storePath;
        this.idLength = idLength;
        try {
            Files.createDirectories(storePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String put(byte[] object) {
        String id = IDGenerator.generate(idLength);
        while (exists(id)) {
            id = IDGenerator.generate(idLength);
        }

        return doPut(id, object);
    }

    private String doPut(String id, byte[] object) {
        Path objectPath = storePath.resolve(id);
        try {
            Files.write(objectPath, object,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.DSYNC);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public byte[] get(String id) {
        try {
            return Files.readAllBytes(storePath.resolve(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<byte[]>> getAll() {
        return keySet().stream()
                .map(key -> (Supplier<byte[]>) () -> BinaryStore.this.get(key))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, byte[] object) throws IllegalStateException {
        if (!exists(id)) {
            throw new IllegalStateException("The target object does not exist");
        }
        doPut(id, object);
    }

    @Override
    public void delete(String id) {
        try {
            Files.deleteIfExists(storePath.resolve(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> keySet() {
        try {
            return Files.list(storePath)
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String id) {
        return Files.exists(storePath.resolve(id));
    }

    @Override
    public InputStream getStream(String id) {
        try {
            return Files.newInputStream(storePath.resolve(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<InputStream>> getAllStream() {
        return keySet().stream()
                .map(key -> (Supplier<InputStream>) () -> BinaryStore.this.getStream(key))
                .collect(Collectors.toList());
    }

    @Override
    public String putStream(InputStream inputStream) {
        String id = IDGenerator.generate(idLength);
        while (exists(id)) {
            id = IDGenerator.generate(idLength);
        }
        return doPutStream(id, inputStream);
    }

    private String doPutStream(String id, InputStream inputStream) {
        try {
            Files.copy(inputStream, storePath.resolve(id),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public void updateStream(String id, InputStream inputStream) {
        if (!exists(id)) {
            throw new IllegalStateException("The target object does not exist");
        }
        doPutStream(id, inputStream);
    }

    @Override
    public void close() {
    }
}
