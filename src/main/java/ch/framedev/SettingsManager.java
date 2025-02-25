package ch.framedev;



/*
 * ch.framedev
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.02.2025 23:09
 */

import ch.framedev.simplejavautils.SimpleJavaUtils;
import ch.framedev.yamlutils.FileConfiguration;

import java.io.File;
import java.util.Map;

public class SettingsManager {

    private final FileConfiguration configuration;
    private final Map<String, Object> data;

    public SettingsManager() {
        final SimpleJavaUtils utils = new SimpleJavaUtils();
        this.configuration = new FileConfiguration(utils.getFromResourceFile("config.yml", Main.class),
                new File(utils.getFilePath(Main.class), "config.yml"));
        this.data = configuration.getData();
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void saveSettings() {
        configuration.save();
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
