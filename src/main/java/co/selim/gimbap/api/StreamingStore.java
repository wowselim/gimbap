package co.selim.gimbap.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;


/**
 * A Store extension that is more suitable for large objects.
 *
 * @param <T> the type of objects stored in this Store.
 */
public interface StreamingStore<T> extends Store<T> {
    /**
     * @param inputStream an input stream mapped to the objects data.
     * @return the id corresponding to the stored object.
     */
    String putStream(InputStream inputStream);

    /**
     * Returns an input stream for the object with the specified id.
     *
     * @param id the id of the object to find.
     * @return the input stream for the specified object.
     */
    InputStream getStream(String id);

    /**
     * Returns lazy input stream accessors for all objects stored in this Store.
     *
     * @return lazy input stream accessors for all objects.
     */
    Iterable<Supplier<InputStream>> getAllStream();

    /**
     * Updates an existing object with the specified id.
     * Implementations may choose to throw an IllegalStateException if the id is unknown.
     *
     * @param id          the id of the object to update.
     * @param inputStream an input stream containing the new state of the object.
     * @throws IllegalStateException if the implementation does not allow unknown ids.
     */
    void updateStream(String id, InputStream inputStream) throws IllegalStateException;
}
