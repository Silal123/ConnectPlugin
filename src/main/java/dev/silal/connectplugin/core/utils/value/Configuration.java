package dev.silal.connectplugin.core.utils.value;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Configuration {

    private final String path;
    private YamlConfiguration configuration = new YamlConfiguration();

    public Configuration(String path) {
        this.path = path;

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { throw new RuntimeException(e); }
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save(String key, Object value) {
        try {
            configuration.set(key, value);
            File conf = new File(path);
            configuration.save(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File conf = new File(path);
            configuration.save(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteFile() {
        File file = new File(path);
        if (!file.exists()) return false;
        return file.delete();
    }

    public List<String> getKeys(String key) {
        ConfigurationSection section = configuration.getConfigurationSection(key);
        if (section == null) return new ArrayList<>();
        return section.getKeys(false).stream().toList();
    }

    public void editKey(String key, String newKey) {
        Object value = configuration.get(key);
        configuration.set(key, null);
        configuration.set(newKey, value);
        save();
    }

    public void remove(String key) {
        configuration.set(key, null);
        save();
    }

    public String path() { return path; }
    public YamlConfiguration yamlConfiguration() { return configuration; }

}
