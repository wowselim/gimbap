package co.selim.gimbap.api;

import java.util.Set;
import java.util.function.Supplier;

/**
 * A Store provides storage for objects of a specific type.
 *
 * @param <T> the type of objects stored in this Store.
 */
public interface Store<T> extends AutoCloseable {
    /**
     * Stores an object in this Store.
     *
     * @param object the object to store.
     * @return the id corresponding to the stored object.
     */
    String put(T object);

    /**
     * Returns an object with the specified id.
     *
     * @param id the id of the object to find.
     * @return the object with the specified id.
     */
    T get(String id);

    /**
     * Returns lazy accessors for all objects stored in this Store.
     *
     * @return lazy accessors for all objects.
     */
    Iterable<Supplier<T>> getAll();

    /**
     * Updates an existing object with the specified id.
     * Implementations may choose to throw an IllegalStateException if the id is unknown.
     *
     * @param id     the id of the object to update.
     * @param object the new state of the object.
     * @throws IllegalStateException if the implementation does not allow unknown ids.
     */
    void update(String id, T object) throws IllegalStateException;

    /**
     * Deletes an object with the specified id.
     *
     * @param id the id of the object to delete.
     */
    void delete(String id);

    /**
     * A set all ids stored in this Store.
     *
     * @return a set all ids stored in this Store.
     */
    Set<String> keySet();

    /**
     * Returns true if an object with the specified id exists in this store.
     *
     * @param id the id of the object to check.
     * @return true if the object exists otherwise false.
     */
    boolean exists(String id);
}
