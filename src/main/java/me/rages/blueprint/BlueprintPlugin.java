package me.rages.blueprint;

import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.rages.blueprint.config.BlueprintFile;
import me.rages.blueprint.config.LanguageFile;
import me.rages.blueprint.data.blueprint.Blueprint;
import me.rages.blueprint.generator.BlueprintGenerator;
import me.rages.blueprint.modules.BlueprintModule;
import me.rages.blueprint.service.ServiceManager;
import me.rages.blueprint.service.impl.BuildCheckService;
import me.rages.blueprint.service.impl.WorldEditService;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@Plugin(
        name = "Blueprint",
        hardDepends = {"helper"},
        softDepends = {"FastAsyncWorldEdit", "WorldEdit", "Factions", "ASkyBlock", "SuperiorSkyblock2", "ProtocolLib"},
        authors = {"Rages"},
        apiVersion = "1.18",
        description = "Placeable schematic items."
)
public final class BlueprintPlugin extends ExtendedJavaPlugin {

    @Getter
    private Map<String, Blueprint> blueprintDataMap = new HashMap<>();
    @Getter
    private List<BlueprintGenerator> blueprintGenerators = new ArrayList<>();

    @Getter
    private File schematicsFolder;

    @Getter
    private ServiceManager serviceManager;
    @Getter
    private LanguageFile languageFile;
    @Getter
    private BlueprintFile blueprintFile;
    @Getter
    private BlueprintModule blueprintModule;

    @Getter
    private static BlueprintPlugin instance;

    @Getter
    private Set<Material> ignoredTypes = new HashSet<>();

    @Override
    protected void enable() {
        instance = this;
        this.saveDefaultConfig();
        this.serviceManager = ServiceManager.createServiceManager(this)
                .registerService("build", new BuildCheckService())
                .registerService("worldedit", new WorldEditService());
        this.schematicsFolder = new File(getDataFolder(), "schematics");
        this.loadSchematics();

        this.blueprintModule = new BlueprintModule(this);

        this.bindModule(blueprintModule);
        this.languageFile = new LanguageFile(this, "lang").init();
        this.blueprintFile = new BlueprintFile(this, "blueprints").init();

        if (!schematicsFolder.exists()) {
            if (!schematicsFolder.mkdir()) {
                this.getLogger().log(Level.SEVERE, "Failed to create schematics directory!");
                this.getPluginLoader().disablePlugin(this);
                return;
            } else {
                this.getLogger().log(Level.INFO, "Created schematics directory: /plugins/Blueprint/schematics");
            }
        }

        // load ignored types
        ignoredTypes.addAll(Tag.TRAPDOORS.getValues());
        ignoredTypes.addAll(Tag.DOORS.getValues());
    }

    public void loadSchematics() {
        this.getLogger().log(Level.INFO, "Importing schematics");
        File[] files = schematicsFolder.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> getServiceManager().getWorldEdit().readSchematic(file, getBlueprintDataMap()));
        }
    }

}
