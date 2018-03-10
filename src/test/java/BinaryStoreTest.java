import co.selim.gimbap.BinaryStore;
import co.selim.gimbap.Store;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.FileSystem;

/**
 * Created by Selim on 10.03.2018.
 */
public class BinaryStoreTest {
    private static FileSystem inMemoryFileSystem;
    private static Store<byte[]> binaryStore;

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
    public void savedObjectShouldBeCorrect() {
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
    public void keySetShouldBeCorrect() {
        Assert.assertTrue("Store was not empty",
                binaryStore.keySet().size() == 0);
        binaryStore.put("axolotl".getBytes());
        Assert.assertTrue("Number of stored objects was wrong",
                binaryStore.keySet().size() == 1);
    }

    @Test
    public void getAllShouldBeCorrect() {
    }
}
