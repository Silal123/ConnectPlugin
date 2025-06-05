package dev.silal.connectplugin.core.utils.value;

import java.util.UUID;

public class ConfigValue<T> {
    private Configuration conf;
    private final String key;
    private final Class<T> type;
    private T defaultValue = null;

    public ConfigValue(String key, Class<T> type, Configuration configuration) {
        this.key = key;
        this.type = type;
        this.conf =  configuration;
    }

    public ConfigValue(String key, Class<T> type, Configuration configuration, T defaultValue) {
        this.key = key;
        this.type = type;
        this.conf =  configuration;
        this.defaultValue = defaultValue;

        if (configuration.yamlConfiguration().get(key) != null) return;
        set(defaultValue);
    }

    public T defaultValue() {
        return defaultValue;
    }
    public void set(T value) {
        if (type == UUID.class) {
            this.conf.save(key, ((UUID)value).toString());
            return;
        }

        this.conf.save(key, value);
    }


    public T get() {
        if (this.conf.yamlConfiguration().get(key) == null && defaultValue != null) {
            this.conf.save(key, defaultValue);
            return defaultValue;
        }

        if (type == UUID.class) return (T) UUID.fromString(this.conf.yamlConfiguration().getString(key));
        if (type == Long.class) return (T) Long.valueOf(this.conf.yamlConfiguration().getLong(key));

        return (T) this.conf.yamlConfiguration().get(key);
    }

}
