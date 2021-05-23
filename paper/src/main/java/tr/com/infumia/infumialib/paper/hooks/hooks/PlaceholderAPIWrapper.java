package tr.com.infumia.infumialib.paper.hooks.hooks;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import tr.com.infumia.infumialib.hooks.Wrapped;

@RequiredArgsConstructor
public final class PlaceholderAPIWrapper implements Wrapped {

  @NotNull
  private final PlaceholderAPIPlugin placeholderAPI;

  @NotNull
  public String apply(@NotNull final OfflinePlayer player, @NotNull final String string) {
    return PlaceholderAPI.setPlaceholders(player, string);
  }
}