package mccity.plugins.questerext;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class QuesterLogger extends Logger {

    private String prefix;

    public QuesterLogger() {
        super("Quester", null);
        this.prefix = "[Quester] ";
        setParent(Bukkit.getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord logRecord) {
        logRecord.setMessage(prefix + logRecord.getMessage());
        super.log(logRecord);
    }
}
