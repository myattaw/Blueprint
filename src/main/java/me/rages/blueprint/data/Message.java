package me.rages.blueprint.data;

import lombok.Getter;
import me.lucko.helper.text3.Text;

import java.util.Arrays;

public enum Message {


    // Listeners
    BLUEPRINT_TASK_STARTED("blueprint-task-started", "", "&7You have started a new blueprint generator.", "&7The estimated time is &9{time} &7to complete.", ""),
    BLUEPRINT_TASK_FINISHED("blueprint-task-finished", "&7Your blueprint task has successfully completed."),

    // Commands
    BLUEPRINT_ITEM_RECEIVED("blueprint-item.received", "&7You have received a &9{name} blueprint&7."),
    BLUEPRINT_ITEM_GIVEN("blueprint-item.given", "&7You have given &9{player} &7a &9{name} blueprint&7.");

    @Getter
    private final String config;
    private String[] text;

    Message(String config, String... text) {
        this.config = config;
        this.text = text;
    }

    public void setMessage(String text) {
        this.text = new String[]{text};
    }

    public void setMessages(String[] text) {
        this.text = text;
    }

    public String get() {
        return this.text[0];
    }

    public String getColorized() {
        return Text.colorize(this.text[0]);
    }

    public String[] all() {
        return this.text;
    }

    public String[] getAllColorized() {
        return Arrays.stream(this.text).map(Text::colorize).toArray(String[]::new);
    }

}