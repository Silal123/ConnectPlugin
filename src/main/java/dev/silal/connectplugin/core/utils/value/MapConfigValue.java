package dev.silal.connectplugin.core.utils.value;

import java.util.HashMap;
import java.util.Map;

public class MapConfigValue<T> {

    private final String key;
    private final Configuration conf;
    private final Map<String, T> defaultValue;

    public MapConfigValue(String key, Configuration configuration) {
        this.key = key;
        this.conf = configuration;
        this.defaultValue = null;
    }

    public MapConfigValue(String key, Configuration configuration, Map<String, T> defaultValue) {
        this.key = key;
        this.conf = configuration;
        this.defaultValue = defaultValue;
    }

    public Map<String, T> get() {
        if (conf.yamlConfiguration().getConfigurationSection(key) == null && defaultValue == null) return new HashMap<>();
        if (conf.yamlConfiguration().getConfigurationSection(key) == null) {
            set(defaultValue);
            return defaultValue;
        }
        Map<String, Object> rawMap = conf.yamlConfiguration().getConfigurationSection(key).getValues(false);
        Map<String, T> result = new HashMap<>();
//        for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
//            if (entry.getValue() instanceof Map) {
//                //noinspection unchecked
//                result.put(entry.getKey(), (T) entry.getValue());
//            }
//        }
        return (Map<String, T>) rawMap;
    }

    public void set(Map<String, T> value) {
        if (conf.yamlConfiguration().getConfigurationSection(key) != null) {
            Map<String, Object> rawMap = conf.yamlConfiguration().getConfigurationSection(key).getValues(false);
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                conf.remove(key + "." + entry.getKey());
            }
        }
        for (Map.Entry<String, T> entry : value.entrySet()) {
            conf.save(key + "." + entry.getKey(), entry.getValue());
        }
    }
}
