package cfgroningen.tarski.world;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;

import cfgroningen.tarski.reader.WorldReader;
import lombok.SneakyThrows;

public class WorldReaderTest {

    @Test
    @SneakyThrows
    public void makeSureFilesExist() {
        int worlds = getFilenamesForDirnameFromCP("worlds").size();
        assertNotEquals(0, worlds);
    }

    @Test
    @SneakyThrows
    public void testFullIntegrityFiles() {
        // This will make sure all the files that we have in the
        // classpath are fully valid, as that is how we placed them to be

        List<String> filenames = getFilenamesForDirnameFromCP("worlds");
        for (String filename : filenames) {
            String worldName = filename.substring(filename.lastIndexOf("/") + 1);

            InputStream stream = getWorldTestFile(worldName);
            WorldReader reader = new WorldReader(stream);
            World w = reader.parse();

            System.out.println("World "+worldName+" "+w.getSoftwareVersion()+" "+w.getOperatingSystem()+" "+w.getFileType()+" Shapes: "+w.getShapes().size());
        }
    }

    private InputStream getWorldTestFile(String worldName) {
        if (!worldName.endsWith(".wld"))
            worldName += ".wld";

        String fullPath = "worlds/" + worldName;
        InputStream stream = getClass().getClassLoader().getResourceAsStream(fullPath);
        return stream;
    }

    /**
     * Finds all the files inside a classpath. Used to list all resources available
     * during testing. Obtained from
     * https://stackoverflow.com/questions/3923129/get-a-list-of-resources-from-classpath-directory
     * 
     * @param directoryName - The directory to look in
     * @return - List of files
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private List<String> getFilenamesForDirnameFromCP(String directoryName)
            throws URISyntaxException, UnsupportedEncodingException, IOException {
        List<String> filenames = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(directoryName);
        if (url != null) {
            if (url.getProtocol().equals("file")) {
                File file = Paths.get(url.toURI()).toFile();
                if (file != null) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        for (File filename : files) {
                            filenames.add(filename.toString());
                        }
                    }
                }
            } else if (url.getProtocol().equals("jar")) {
                String dirname = directoryName + "/";
                String path = url.getPath();
                String jarPath = path.substring(5, path.indexOf("!"));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(dirname) && !dirname.equals(name)) {
                            URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
                            filenames.add(resource.toString());
                        }
                    }
                }
            }
        }
        return filenames;
    }
}
