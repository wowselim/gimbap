import co.selim.gimbap.BinaryStore;
import co.selim.gimbap.api.StreamingStore;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.util.function.Supplier;

/**
 * Created by Selim on 10.03.2018.
 */
public class BinaryStoreTest {
    private static FileSystem inMemoryFileSystem;
    private static StreamingStore<byte[]> binaryStore;

    @BeforeClass
    public static void initStore() throws Exception {
        inMemoryFileSystem = MemoryFileSystemBuilder.newEmpty().build();
        binaryStore = new BinaryStore(inMemoryFileSystem.getPath("data"));
    }

    @AfterClass
    public static void closeFileSystem() throws Exception {
        inMemoryFileSystem.close();
    }

    @After
    public void cleanup() {
        binaryStore.keySet().forEach(binaryStore::delete);
    }

    @Test
    public void savedObjectShouldExist() {
        String id = binaryStore.put("dog".getBytes());
        boolean exists = binaryStore.exists(id);
        Assert.assertTrue("Object was not stored", exists);
    }

    @Test
    public void savedObjectShouldHaveCorrectData() {
        byte[] data = "ocelot".getBytes();
        String id = binaryStore.put(data);
        Assert.assertArrayEquals("Object was not stored correctly",
                data,
                binaryStore.get(id));
    }

    @Test
    public void savedObjectShouldBeUpdated() {
        String id = binaryStore.put("cat".getBytes());
        byte[] data = "elephant".getBytes();
        binaryStore.update(id, data);
        Assert.assertArrayEquals("Object was not updated correctly",
                data,
                binaryStore.get(id));
    }

    @Test
    public void savedObjectShouldBeDeleted() {
        String id = binaryStore.put("butterfly".getBytes());
        binaryStore.delete(id);
        Assert.assertFalse("Object was not removed", binaryStore.exists(id));
    }

    @Test
    public void keySetSizeShouldBeCorrect() {
        Assert.assertTrue("Store was not empty",
                binaryStore.keySet().size() == 0);
        binaryStore.put("axolotl".getBytes());
        Assert.assertTrue("Number of stored objects was wrong",
                binaryStore.keySet().size() == 1);
    }

    @Test
    public void getAllShouldReturnAllObjects() {
        binaryStore.put("lion".getBytes());
        int objectCount = 0;
        for (Supplier<byte[]> dataSupplier : binaryStore.getAll()) {
            objectCount++;
        }
        Assert.assertTrue("Number of stored objects was wrong",
                objectCount == 1);
        objectCount = 0;
        binaryStore.put("tiger".getBytes());
        for (Supplier<byte[]> dataSupplier : binaryStore.getAll()) {
            objectCount++;
        }
        Assert.assertTrue("Number of stored objects was wrong",
                objectCount == 2);
    }

    /*
     * Streaming tests
     */
    @Test
    public void streamSavedObjectShouldExist() {
        InputStream dataIn = new ByteArrayInputStream("squirrel".getBytes());
        String id = binaryStore.putStream(dataIn);
        boolean exists = binaryStore.exists(id);
        Assert.assertTrue("Object was not stored", exists);
    }

    @Test
    public void streamSavedObjectShouldHaveCorrectData() {
        byte[] data = "chipmunk".getBytes();
        InputStream dataIn = new ByteArrayInputStream(data);
        String id = binaryStore.putStream(dataIn);
        Assert.assertArrayEquals("Object was not stored correctly",
                data,
                binaryStore.get(id));
    }

    @Test
    public void streamSavedObjectShouldBeUpdated() {
        String id = binaryStore.put("chameleon".getBytes());
        byte[] data = "gnu".getBytes();
        binaryStore.updateStream(id, new ByteArrayInputStream(data));
        Assert.assertArrayEquals("Object was not updated correctly",
                data,
                binaryStore.get(id));
    }
}
