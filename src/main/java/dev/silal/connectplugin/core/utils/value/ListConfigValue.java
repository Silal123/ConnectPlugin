package dev.silal.connectplugin.core.utils.value;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListConfigValue<T> {

    private final String key;
    private final Configuration conf;
    private List<T> defaultValue;
    private Class<T> type;

    public ListConfigValue(String key, Class<T> type, Configuration configuration) {
        this.key = key;
        this.type = type;
        this.conf = configuration;
    }

    public ListConfigValue(String key, Class<T> type, Configuration configuration, List<T> defaultValue) {
        this.key = key;
        this.type = type;
        this.conf = configuration;
        this.defaultValue = defaultValue;
    }

    public List<T> get() {
        if (conf.yamlConfiguration().getList(key) == null && defaultValue == null) return new ArrayList<>();
        if (conf.yamlConfiguration().getList(key) == null && defaultValue != null) {
            set(defaultValue);
            return defaultValue;
        }

        if (type == UUID.class) {
            List<UUID> re = new ArrayList<>();
            for (Object o : conf.yamlConfiguration().getList(key)) {
                re.add(UUID.fromString(String.valueOf(o)));
            }
            return (List<T>) re;
        }

        if (Enum.class.isAssignableFrom(type)) {
            List<T> re = new ArrayList<>();
            for (Object o : conf.yamlConfiguration().getList(key)) {
                if (o instanceof String) {
                    T enumValue = (T) Enum.valueOf((Class<? extends Enum>) type, (String) o);
                    re.add(enumValue);
                }
            }
            return re;
        }

        return (List<T>) conf.yamlConfiguration().getList(key);
    }

    public void set(List<T> value) {
        if (type == UUID.class) {
            List<String> save = new ArrayList<>();
            for (Object o : value) {
                save.add(((UUID)o).toString());
            }
            conf.save(key, save);
            return;
        }

        if (Enum.class.isAssignableFrom(type)) {
            List<String> save = new ArrayList<>();
            for (Object o : value) {
                save.add(((Enum)o).name());
            }
            conf.save(key, save);
            return;
        }


        conf.save(key, value);
    }

}
