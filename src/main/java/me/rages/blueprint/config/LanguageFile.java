package me.rages.blueprint.config;

import me.rages.blueprint.BlueprintPlugin;
import me.rages.blueprint.data.Message;

import java.util.Arrays;
import java.util.logging.Level;

public class LanguageFile extends ConfigFile {

    public LanguageFile(BlueprintPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    @Override
    public LanguageFile init() {
        this.getPlugin().getLogger().log(Level.INFO, "Successfully created language.yml");
        for (Message message : Message.values()) {
            if (!getConfig().getStringList(message.getConfig()).isEmpty()) {
                message.setMessages(getConfig().getStringList(message.getConfig()).toArray(new String[0]));
            } else if (getConfig().contains(message.getConfig())) {
                message.setMessage(getConfig().getString(message.getConfig()));
            } else {
                if (message.all().length > 1) {
                    getConfig().set(message.getConfig(), Arrays.asList(message.all()));
                } else {
                    getConfig().set(message.getConfig(), message.get());
                }
            }
        }
        this.save();
        return this;
    }
}