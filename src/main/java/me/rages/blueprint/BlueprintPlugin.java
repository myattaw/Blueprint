package me.rages.blueprint;

import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.rages.blueprint.config.LanguageFile;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.generator.BlueprintGenerator;
import me.rages.blueprint.modules.BlueprintModule;
import me.rages.blueprint.service.ServiceManager;
import me.rages.blueprint.service.impl.WorldEditService;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@Plugin(
        name = "Blueprint",
        hardDepends = {"helper"},
        softDepends = {"FastAsyncWorldEdit", "WorldEdit"},
        authors = {"Rages"},
        description = "Placeable schematic items."
)
public final class BlueprintPlugin extends ExtendedJavaPlugin {

    @Getter private Map<String, Blueprint> blueprintDataMap = new HashMap<>();
    @Getter private List<BlueprintGenerator> blueprintGenerators = new ArrayList<>();

    @Getter private File schematicsFolder;

    @Getter private ServiceManager serviceManager;
    @Getter private LanguageFile languageFile;


    @Override
    protected void enable() {
        this.saveDefaultConfig();
        this.serviceManager = ServiceManager.createServiceManager(this)
                .registerService("worldedit", new WorldEditService());

        this.bindModule(new BlueprintModule(this));
        this.languageFile = new LanguageFile(this, "lang").init();

        schematicsFolder = new File(getDataFolder(), "schematics");
        if (!schematicsFolder.exists()) {
            if (!schematicsFolder.mkdir()) {
                this.getLogger().log(Level.SEVERE, "Failed to create schematics directory!");
                this.getPluginLoader().disablePlugin(this);
                return;
            } else {
                this.getLogger().log(Level.INFO, "Created schematics directory: /plugins/SchemBucket/schematics");
            }
        }

        loadSchematics();

    }

    public void loadSchematics() {
        this.getLogger().log(Level.INFO, "Importing schematics");
        File[] files = schematicsFolder.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> getServiceManager().getWorldEdit().readSchematic(file, getBlueprintDataMap()));
        }
    }

}
