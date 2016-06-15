package uk.ac.susx.tag.peg.parboiled;

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

    public static Path getTempUniqueDir() {

        int limit = 10;

        String unqiue = UUID.randomUUID().toString();

        Path tempDir = null;

        int tries = 0;
        while (tempDir == null && tries < limit) {

            try {

                tempDir = Files.createTempDirectory(unqiue);

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
                e.printStackTrace(); // replace with more robust error handling
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
