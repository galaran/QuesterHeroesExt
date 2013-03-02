package net.citizensnpcs.questers.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Set;

public class ReadOnlyYamlStorage implements ReadOnlyStorage {

    private final File file;
    private final FileConfiguration config = new YamlConfiguration();

    public ReadOnlyYamlStorage(File file) {
        this.file = file;
    }

    @Override
    public boolean getBoolean(String path) {
        if (pathExists(path)) {
            if (config.getString(path) == null) {
                return config.getBoolean(path);
            }
            return Boolean.parseBoolean(config.getString(path));
        }
        return false;
    }

    @Override
    public boolean getBoolean(String path, boolean value) {
        return config.getBoolean(path, value);
    }

    @Override
    public double getDouble(String path) {
        if (pathExists(path)) {
            if (config.getString(path) == null) {
                if (config.get(path) instanceof Integer) {
                    return config.getInt(path);
                }
                return config.getDouble(path);
            }
            return Double.parseDouble(config.getString(path));
        }
        return 0;
    }

    @Override
    public double getDouble(String path, double value) {
        return config.getDouble(path, value);
    }

    @Override
    public int getInt(String path) {
        if (pathExists(path)) {
            if (config.getString(path) == null) {
                return config.getInt(path);
            }
            return Integer.parseInt(config.getString(path));
        }
        return 0;
    }

    @Override
    public int getInt(String path, int value) {
        return config.getInt(path, value);
    }

    @Override
    public List<Integer> getIntegerKeys(String string) {
        if (config.getConfigurationSection(string) == null) {
            return Lists.newArrayList();
        }
        Set<String> keys = config.getConfigurationSection(string).getKeys(false);
        List<Integer> parsed = Lists.newArrayList();
        for (String key : keys) {
            if (!StringUtils.isNumber(key)) continue;
            parsed.add(Integer.parseInt(key));
        }
        return parsed;
    }

    @Override
    public Set<String> getKeys(String path) {
        if (path == null || path.isEmpty()) {
            return config.getRoot().getKeys(false);
        }
        if (config.getConfigurationSection(path) == null) {
            return Sets.newHashSet();
        }
        return config.getConfigurationSection(path).getKeys(false);
    }

    @Override
    public long getLong(String path) {
        if (pathExists(path)) {
            if (config.getString(path) == null) {
                if (config.get(path) instanceof Integer) {
                    return config.getInt(path);
                }
                return config.getLong(path);
            }
            return Long.parseLong(config.getString(path));
        }
        return 0;
    }

    @Override
    public long getLong(String path, long value) {
        return config.getInt(path, (int) value);
    }

    @Override
    public Object getRaw(String path) {
        return config.get(path);
    }

    @Override
    public String getString(String path) {
        if (pathExists(path)) {
            return config.get(path).toString();
        }
        return "";
    }

    @Override
    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    @Override
    public Location getLocation(String path, boolean shortened) {
        String world;
        double x, y, z;
        float pitch, yaw;
        if (shortened) {
            String[] read = getString(path + ".location").split(",");
            world = read[0];
            x = Double.parseDouble(read[1]);
            y = Double.parseDouble(read[2]);
            z = Double.parseDouble(read[3]);
            pitch = Float.parseFloat(read[4]);
            yaw = Float.parseFloat(read[5]);
        } else {
            world = getString(path + ".location.world");
            x = getDouble(path + ".location.x");
            y = getDouble(path + ".location.y");
            z = getDouble(path + ".location.z");
            pitch = (float) getDouble(path + ".location.pitch");
            yaw = (float) getDouble(path + ".location.yaw");
        }
        return new Location(Bukkit.getServer().getWorld(world), x, y, z, pitch, yaw);
    }

    @Override
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public boolean pathExists(String path) {
        return config.get(path) != null;
    }

    @Override
    public boolean pathExists(int path) {
        return pathExists(String.valueOf(path));
    }

    @Override
    public void load() {
        try {
            config.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
