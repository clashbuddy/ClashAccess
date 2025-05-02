package studio.clashbuddy.clashaccess.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


class VersionReader {


    private static final String VERSION;

    static {
        String version1;
        Properties properties = new Properties();
        try (InputStream input = VersionReader.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input != null) {
                properties.load(input);
                version1 = properties.getProperty("version", "UNKNOWN");
            } else {
                version1 = "UNKNOWN";
            }
        } catch (IOException e) {
            version1 = "";
        }
        VERSION = version1;
    }

    static String getVersion() {
        return "v"+VERSION;
    }

}
