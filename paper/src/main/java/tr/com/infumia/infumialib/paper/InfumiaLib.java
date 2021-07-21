package tr.com.infumia.infumialib.paper;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.github.slimjar.app.builder.ApplicationBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.infumialib.files.InfumiaLibConfig;
import tr.com.infumia.infumialib.paper.color.CustomColors;
import tr.com.infumia.infumialib.paper.commands.InfumiaPluginCommands;
import tr.com.infumia.infumialib.paper.files.PaperConfig;
import tr.com.infumia.infumialib.paper.hooks.Hooks;
import tr.com.infumia.infumialib.paper.shade.com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import tr.com.infumia.infumialib.paper.shade.com.github.yannicklamprecht.worldborder.plugin.PersistenceWrapper;
import tr.com.infumia.infumialib.paper.smartinventory.SmartInventory;
import tr.com.infumia.infumialib.paper.smartinventory.manager.BasicSmartInventory;
import tr.com.infumia.infumialib.paper.utils.GitHubUpdateChecker;
import tr.com.infumia.infumialib.paper.utils.TaskUtilities;
import tr.com.infumia.infumialib.paper.utils.Versions;

/**
 * a class that represents main class of Infumia Library plugin.
 */
public final class InfumiaLib extends JavaPlugin {

  /**
   * the instance.
   */
  @Nullable
  private static InfumiaLib instance;

  /**
   * the inventory.
   */
  private final SmartInventory inventory = new BasicSmartInventory(this);

  /**
   * teh world border api.
   */
  @Nullable
  private WorldBorderApi worldBorderApi;

  /**
   * creates a new command manager.
   *
   * @param plugin the plugin to create.
   *
   * @return a newly created command manager.
   */
  @NotNull
  public static PaperCommandManager<CommandSender> createCommandManager(@NotNull final Plugin plugin) {
    final PaperCommandManager<CommandSender> manager;
    try {
      manager = new PaperCommandManager<>(
        plugin, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    try {
      manager.registerBrigadier();
    } catch (final Exception e) {
      plugin.getLogger().warning("Couldn't add brigadier support.");
    }
    try {
      manager.registerAsynchronousCompletions();
    } catch (final Exception e) {
      plugin.getLogger().warning("Couldn't add async tab completion support.");
    }
    return manager;
  }

  /**
   * obtains the instance.
   *
   * @return instance.
   */
  @NotNull
  public static InfumiaLib getInstance() {
    return Objects.requireNonNull(InfumiaLib.instance, "not initiated");
  }

  /**
   * obtains the inventory.
   *
   * @return inventory.
   */
  @NotNull
  public static SmartInventory getInventory() {
    return InfumiaLib.getInstance().inventory;
  }

  /**
   * obtains the world border api.
   *
   * @return world border api.
   */
  @NotNull
  public static Optional<WorldBorderApi> getWorldBorderApi() {
    return Optional.ofNullable(InfumiaLib.getInstance().worldBorderApi);
  }

  /**
   * obtains the world border api.
   *
   * @return world border api.
   */
  @NotNull
  public static WorldBorderApi getWorldBorderApiOrThrow() {
    return InfumiaLib.getWorldBorderApi().orElseThrow();
  }

  /**
   * loads Infumia Library plugin's files.
   */
  public void loadFiles() {
    InfumiaLibConfig.loadConfig(this.getDataFolder());
    PaperConfig.loadConfig(this.getDataFolder());
  }

  @Override
  public void onLoad() {
    InfumiaLib.instance = this;
    CustomColors.registerAll();
    TaskUtilities.init(this);
    this.loadDependencies();
    this.loadFiles();
  }

  @Override
  public void onEnable() {
    Hooks.loadHooks();
    this.initiateWorldBorder();
    final var commandManager = InfumiaLib.createCommandManager(this);
    new InfumiaPluginCommands(commandManager, this).register();
    this.inventory.init();
    if (InfumiaLibConfig.checkForUpdate) {
      GitHubUpdateChecker.checkForUpdate(this, "Infumia", "InfumiaLib");
    }
    new Metrics(this, 11422);
  }

  /**
   * initiates the world border.
   */
  private void initiateWorldBorder() {
    if (Versions.MINOR >= 16 && Versions.MICRO == 3) {
      this.worldBorderApi = new tr.com.infumia.infumialib.paper.shade.com.github.yannicklamprecht.worldborder.v1_16.Border();
    }
    this.worldBorderApi = new PersistenceWrapper(this, this.worldBorderApi);
    this.getServer().getServicesManager().register(WorldBorderApi.class, this.worldBorderApi, this, ServicePriority.High);
  }

  /**
   * loads Infumia Library's dependencies.
   */
  private void loadDependencies() {
    this.getLogger().log(Level.INFO, "Loading dependencies, this might take a while...");
    try {
      ApplicationBuilder.appending("InfumiaLibrary")
//        .logger((message, args) -> this.getLogger().log(Level.INFO, message, args))
        .downloadDirectoryPath(Paths.get(this.getDataFolder().getAbsolutePath()).resolve("libs"))
        .build();
    } catch (final IOException | ReflectiveOperationException | URISyntaxException | NoSuchAlgorithmException e) {
      this.getLogger().log(Level.SEVERE, e, () -> String.format("%s-v%s",
        this.getClass().getSimpleName(), this.getDescription().getVersion()));
      this.getLogger().log(Level.SEVERE, "Infumia Library failed to load its dependencies correctly!");
      this.getLogger().log(Level.SEVERE, "This error should be reported at https://github.com/Infumia/InfumiaLib/issues");
      this.onDisable();
    }
  }
}
