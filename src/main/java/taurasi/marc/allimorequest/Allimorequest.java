package taurasi.marc.allimorequest;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import taurasi.marc.allimorecore.CustomConfig;
import taurasi.marc.allimorecore.GUI.GUIEventRouter;
import taurasi.marc.allimorequest.Commands.CommandManager;
import taurasi.marc.allimorequest.Commands.QuestCommandTabComplete;
import taurasi.marc.allimorequest.Config.ConfigWrapper;
import taurasi.marc.allimorequest.Database.DatabaseManager;
import taurasi.marc.allimorequest.Observers.BlockListener;
import taurasi.marc.allimorequest.Observers.EventListener;
import taurasi.marc.allimorequest.Observers.PlayerConnectionListener;
import taurasi.marc.allimorequest.ProcGen.DifficultyManager;
import taurasi.marc.allimorequest.ProcGen.QuestFactory;
import taurasi.marc.allimorequest.Professions.ProfessionMaterials;

public final class Allimorequest extends JavaPlugin {

    public static Allimorequest INSTANCE;
    public static ProfessionMaterials PROFESSION_MATERIALS;
    public static PlayerDataIndex PLAYER_DATA;
    public static EventListener EVENT_LISTENER;
    public static GUIEventRouter GUI_ROUTER;
    public static QuestFactory QUEST_FACTORY;
    public static DifficultyManager DIFFICULTY_MANAGER;
    public static CommandManager cmdManager;

    private PlayerConnectionListener playerConnectionListener;
    private BlockListener blockListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConfigWrapper.ReadFromConfig(getConfig());

        INSTANCE = this;
        PROFESSION_MATERIALS = new ProfessionMaterials();

        PLAYER_DATA = new PlayerDataIndex( new CustomConfig("PlayerData.yml", getDataFolder().getPath(), this));
        DIFFICULTY_MANAGER = new DifficultyManager();
        QUEST_FACTORY = new QuestFactory();

        RegisterListeners();
        RegisterCommands();
    }

    @Override
    public void onDisable() {
        UnregisterListeners();

        PLAYER_DATA.WriteData();
    }

    private void RegisterCommands() {
        cmdManager = new CommandManager(PLAYER_DATA);
        QuestCommandTabComplete tabComplete = new QuestCommandTabComplete();

        this.getCommand("Quest").setExecutor(cmdManager);
        this.getCommand("Quest").setTabCompleter(tabComplete);
    }

    private void InitListeners() {
        EVENT_LISTENER = new EventListener();
        playerConnectionListener = new PlayerConnectionListener();
        blockListener = new BlockListener();
    }
    private void RegisterListeners(){
        InitListeners();

        getServer().getPluginManager().registerEvents(EVENT_LISTENER, this);
        GUI_ROUTER = new GUIEventRouter(this);
        getServer().getPluginManager().registerEvents(playerConnectionListener, this);
        getServer().getPluginManager().registerEvents(blockListener, this);
    }

    private void UnregisterListeners(){
        HandlerList.unregisterAll(EVENT_LISTENER);
        HandlerList.unregisterAll(GUI_ROUTER);
        HandlerList.unregisterAll(playerConnectionListener);
        HandlerList.unregisterAll(blockListener);

    }
}
