package co.selim.gimbap.util;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Selim on 10.03.2018.
 */
public final class IDGenerator {
    private static final String ALPHABET = "abcdeghkmptuvxyzABCDEGHKMPTUVXYZ12346789-";
    private static final int ALPHABET_LENGTH = ALPHABET.length();
    private static final Supplier<Integer> RANDOM_INT_SUPPLIER = () ->
            ThreadLocalRandom.current().nextInt(ALPHABET_LENGTH);

    private IDGenerator() {
    }

    public static String generate(final int length) {
        return Stream.generate(RANDOM_INT_SUPPLIER)
                .limit(length)
                .map(ALPHABET::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
