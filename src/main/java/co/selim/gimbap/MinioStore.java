package co.selim.gimbap;

import co.selim.gimbap.api.StreamingStore;
import co.selim.gimbap.util.IDGenerator;
import co.selim.gimbap.util.IOStreamUtils;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A Minio-backed StreamingStore implementation.
 * This implementation does not allow updates on objects that do not exist.
 */
public class MinioStore implements StreamingStore<byte[]> {
    private final MinioClient minioClient;
    private final String bucketName;
    private final Supplier<String> idSupplier = () -> IDGenerator.generate(24);

    public MinioStore(MinioClient minioClient, String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public String putStream(InputStream inputStream) {
        String id = getNewId();
        return doPutStream(id, inputStream);
    }

    private String getNewId() {
        String id = idSupplier.get();
        while (exists(id)) {
            id = idSupplier.get();
        }
        return id;
    }

    private String doPutStream(String id, InputStream inputStream) {
        try {
            minioClient.putObject(bucketName, id, inputStream, "application/octet-stream");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @Override
    public InputStream getStream(String id) {
        try {
            return minioClient.getObject(bucketName, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<InputStream>> getAllStream() {
        return keySet().stream()
                .map(key -> (Supplier<InputStream>) () -> MinioStore.this.getStream(key))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStream(String id, InputStream inputStream) throws IllegalStateException {
        if (!exists(id)) {
            throw new IllegalStateException("The target object does not exist");
        }
        doPutStream(id, inputStream);
    }

    @Override
    public String put(byte[] object) {
        String id = getNewId();
        return doPutStream(id, new ByteArrayInputStream(object));
    }

    @Override
    public byte[] get(String id) {
        try (InputStream objectInputStream = getStream(id)) {
            return IOStreamUtils.getBytesFromInputStream(objectInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<byte[]>> getAll() {
        return keySet().stream()
                .map(key -> (Supplier<byte[]>) () -> MinioStore.this.get(key))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, byte[] object) throws IllegalStateException {
        if (!exists(id)) {
            throw new IllegalStateException("The target object does not exist");
        }
        doPutStream(id, new ByteArrayInputStream(object));
    }

    @Override
    public void delete(String id) {
        try {
            minioClient.removeObject(bucketName, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> keySet() {
        try {
            Set<String> keySet = new HashSet<>();
            for (Result<Item> itemResult : minioClient.listObjects(bucketName)) {
                keySet.add(itemResult.get().objectName());
            }
            return keySet;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String id) {
        return keySet().contains(id);
    }

    /**
     * Does nothing.
     */
    @Override
    public void close() {
    }
}
