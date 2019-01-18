package co.selim.gimbap;

import co.selim.gimbap.api.Store;
import co.selim.gimbap.util.IDGenerator;
import co.selim.gimbap.util.IOStreamUtils;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An S3-backed Store implementation. This implementation does not allow updates on objects that do not exist.
 */
public class S3Store implements Store<byte[]> {
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final int idLength = 24;

    public S3Store(AwsClientBuilder.EndpointConfiguration endpointConfiguration,
                   AWSStaticCredentialsProvider credentialsProvider,
                   String bucketName) {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(credentialsProvider)
                .build();
        this.bucketName = bucketName;
    }

    @Override
    public String put(byte[] object) {
        String id = getNewId();
        doPut(id, object);
        return id;
    }

    private String getNewId() {
        String id = IDGenerator.generate(idLength);
        while (exists(id)) {
            id = IDGenerator.generate(idLength);
        }
        return id;
    }

    @Override
    public byte[] get(String id) {
        try {
            S3Object object = s3Client.getObject(bucketName, id);
            return IOStreamUtils.getBytesFromInputStream(object.getObjectContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Supplier<byte[]>> getAll() {
        return keySet().stream()
                .map(key -> (Supplier<byte[]>) () -> S3Store.this.get(key))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, byte[] object) throws IllegalStateException {
        if (!exists(id)) {
            throw new IllegalStateException("The target object does not exist");
        }
        doPut(id, object);
    }

    private void doPut(String id, byte[] object) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(object.length);
        s3Client.putObject(bucketName, id, new ByteArrayInputStream(object), objectMetadata);
    }

    @Override
    public void delete(String id) {
        s3Client.deleteObject(bucketName, id);
    }

    @Override
    public Set<String> keySet() {
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean exists(String id) {
        return s3Client.doesObjectExist(bucketName, id);
    }

    /**
     * Closes the connection to the S3 endpoint and all underlying resources.
     * Behavior after calling close is undefined.
     */
    @Override
    public void close() {
        s3Client.shutdown();
    }
}
