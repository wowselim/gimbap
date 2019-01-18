package co.selim.gimbap.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOStreamUtils {
    public static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (inputStream) {
            inputStream.transferTo(buffer);
        }

        return buffer.toByteArray();
    }
}
