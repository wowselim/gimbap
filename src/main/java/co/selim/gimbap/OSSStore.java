package co.selim.gimbap;

import co.selim.gimbap.api.StreamingStore;
import co.selim.gimbap.util.IDGenerator;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static co.selim.gimbap.util.IOStreamUtilsKt.getBytesFromInputStream;

/**
 * An OSS-backed StreamingStore implementation.
 * This implementation does not allow updates on objects that do not exist.
 */
public class OSSStore implements StreamingStore<byte[]> {
    private final OSS client;
    private final String bucketName;
    private final int idLength = 24;

    public OSSStore(OSS client, String bucketName) {
        this.client = client;
        this.bucketName = bucketName;
    }

    @Override
    public String putStream(InputStream inputStream) {
        String id = getNewId();
        return doPutStream(id, inputStream);
    }

    private String getNewId() {
        String id = IDGenerator.generate(idLength);
        while (exists(id)) {
            id = IDGenerator.generate(idLength);
        }
        return id;
    }

    private String doPutStream(String id, InputStream inputStream) {
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, id, inputStream);
        client.putObject(putRequest);
        return id;
    }

    @Override
    public InputStream getStream(String id) {
        OSSObject object = client.getObject(bucketName, id);
        return object.getObjectContent();
    }

    @Override
    public Iterable<Supplier<InputStream>> getAllStream() {
        return keySet().stream()
                .map(key -> (Supplier<InputStream>) () -> OSSStore.this.getStream(key))
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
        return doPut(id, object);
    }

    private String doPut(String id, byte[] object) {
        return doPutStream(id, new ByteArrayInputStream(object));
    }

    @Override
    public byte[] get(String id) {
        try (OSSObject object = client.getObject(bucketName, id)) {
            return getBytesFromInputStream(object.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<byte[]>> getAll() {
        return keySet().stream()
                .map(key -> (Supplier<byte[]>) () -> OSSStore.this.get(key))
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
        client.deleteObject(bucketName, id);
    }

    @Override
    public Set<String> keySet() {
        return client.listObjects(bucketName).getObjectSummaries().stream()
                .map(OSSObjectSummary::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean exists(String id) {
        return client.doesObjectExist(bucketName, id);
    }

    /**
     * Closes the connection to the OSS endpoint and all underlying resources.
     * Behavior after calling close is undefined.
     */
    @Override
    public void close() {
        client.shutdown();
    }
}
