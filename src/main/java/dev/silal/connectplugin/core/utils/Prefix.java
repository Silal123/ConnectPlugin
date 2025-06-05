package dev.silal.connectplugin.core.utils;

public enum Prefix {

    HEY("§c§lHey! §7", false),
    NO_X("§c§l[✖] ", false),
    CHECKPOINT_FINISH("§e§l[⭐] ", false),
    SYSTEM("§cConnect", true);

    Prefix(String key, boolean prefix) {
        this.key = key;
        this.prefix = prefix;
    }

    private final String key;
    private final boolean prefix;

    public final String split = "§8: §7";

    public String key() { return key + (prefix ? split : "§7"); }

}
