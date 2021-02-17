package de.teampresidency.party.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * User: Timo
 * Date: 03.02.2021
 * Time: 21:27
 */
public class Config {

    private final File file;
    private static Configuration configuration;
    private final String path;
    private final String child;

    public Config(String path, String child) {
        this.child = child;
        this.path = path;

        File folder = new File("./plugins/" + path + "/");
        if(!folder.exists())
            folder.mkdirs();

        file = new File(folder, child + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                throw new RuntimeException("Unable to create configuration file", exception);
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException exception) {
            throw new RuntimeException("Failed loading configuration file", exception);
        }
    }

    public File getFile() {
        return file;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getPath() {
        return path;
    }

    public String getChild() {
        return child;
    }
}
