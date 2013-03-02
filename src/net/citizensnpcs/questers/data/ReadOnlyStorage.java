package net.citizensnpcs.questers.data;

import net.citizensnpcs.properties.Storage;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

public interface ReadOnlyStorage {

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean value);

    double getDouble(String key);

    double getDouble(String key, double value);

    int getInt(String key);

    int getInt(String key, int value);

    List<Integer> getIntegerKeys(String string);

    Collection<String> getKeys(String string);

    long getLong(String key);

    long getLong(String key, long value);

    Object getRaw(String string);

    String getString(String key);

    String getString(String key, String value);

    Location getLocation(String path, boolean shortened);

    void load();

    boolean pathExists(String path);

    boolean pathExists(int path);

    /**
     * @return string list or empty list if path not exists
     */
    List<String> getStringList(String path);
}
