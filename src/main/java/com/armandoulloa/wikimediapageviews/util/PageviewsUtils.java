package com.armandoulloa.wikimediapageviews.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Armando
 */
public class PageviewsUtils {

    public static void decompressGzipNio(Path source, Path target) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(source.toFile()))) {
            Files.deleteIfExists(target);
            Files.copy(gis, target);
        }
    }
}
