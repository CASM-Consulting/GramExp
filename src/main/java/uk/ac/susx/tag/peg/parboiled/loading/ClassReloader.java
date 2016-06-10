package uk.ac.susx.tag.peg.parboiled.loading;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by simon on 25/05/16.
 */
public class ClassReloader extends ClassLoader {

    private Path path;

    public ClassReloader(Path path)  {
        super();
        this.path = path;
    }

    @Override
    public Class<?> loadClass(String s) {
        try {
            byte[] bytes = loadClassData(s);
            return defineClass(s, bytes, 0, bytes.length);
        } catch (IOException ioe) {
            try {
                return super.loadClass(s);
            } catch (ClassNotFoundException ignore) { }
//            ioe.printStackTrace(System.out);
            return null;
        }
    }

    private byte[] loadClassData(String className) throws IOException {
        File f = path.resolve(className.replaceAll("\\.", "/") + ".class").toFile();
        int size = (int) f.length();
        byte[] buff = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }
}
