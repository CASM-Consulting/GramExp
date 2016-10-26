package uk.ac.susx.tag.gramexp;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

/**
 * Created by simon on 15/06/16.
 */
public class Util {

    public static String getUniqueClassName() {
        String unique = null;
        do {
            unique = "_"+UUID.randomUUID().toString().replaceAll("-", "");
            try {
                Class.forName(unique);
                unique = null;
            } catch (ClassNotFoundException e) {
            }

        } while (unique == null);

        return unique;
    }

    public static Path getUniqueTempDir() {

        int limit = 10;

        Path tempDir = null;

        int tries = 0;
        while (tempDir == null && tries < limit) {
            try {
                String unique = UUID.randomUUID().toString();

                tempDir = Files.createTempDirectory(unique);

            } catch (IOException e) {
                ++tries;
            }
        }

        if(tempDir == null) {
            throw new GrammarException("could not create unique temp dir");
        }


        return tempDir;
    }

    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
//                e.printStackTrace(); // replace with more robust error handling
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) return handleException(e);
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
