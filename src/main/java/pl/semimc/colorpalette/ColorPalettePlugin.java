package pl.semimc.colorpalette;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.semimc.colorpalette.command.ColorPaletteAdminCommand;
import pl.semimc.colorpalette.command.PaletteCommand;
import pl.semimc.colorpalette.database.DbManager;
import pl.semimc.colorpalette.gui.PaletteGui;
import pl.semimc.colorpalette.placeholder.ColorPlaceholder;
import pl.semimc.colorpalette.util.ColorPalette;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.HashMap;
import java.util.Map;

public class ColorPalettePlugin extends JavaPlugin implements Listener {

  private DbManager dbManager;
  private BukkitAudiences adventure;
  private Map<String, ColorPalette> palettes = new HashMap<>();
  private String defaultPalette;
  private PaletteGui paletteGui;
  private final Map<String, Map<String, String>> playerColorCache = new HashMap<>();

  public PaletteGui getPaletteGui() {
    return paletteGui;
  }

  public Map<String, ColorPalette> getPalettes() {
    return palettes;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    setupDatabase();
    ensureDefaultPaletteInDb();
    loadPalettes();
    adventure = BukkitAudiences.create(this);
    paletteGui = new PaletteGui(this);
    Bukkit.getPluginManager().registerEvents(paletteGui, this);
    new ColorPlaceholder(this).register();

    PluginCommand wybierzCmd = getCommand("wybierz-kolor");
    if (wybierzCmd != null)
      wybierzCmd.setExecutor(new PaletteCommand(this));

    PluginCommand adminCmd = getCommand("colorpalette");
    if (adminCmd != null)
      adminCmd.setExecutor(new ColorPaletteAdminCommand(this));
  }

  @Override
  public void onDisable() {
    if (adventure != null)
      adventure.close();
    if (dbManager != null)
      dbManager.close();
  }

  private void setupDatabase() {
    FileConfiguration cfg = getConfig();
    try {
      dbManager = new DbManager(
          "jdbc:mysql://" + cfg.getString("database.host") + ":" + cfg.getInt("database.port") + "/"
              + cfg.getString("database.database"),
          cfg.getString("database.user"),
          cfg.getString("database.password"));
    } catch (Exception e) {
      getLogger().severe("Nie można połączyć z MySQL: " + e.getMessage());
    }
  }

  private void loadPalettes() {
    palettes.clear();
    Map<String, ColorPalette> loaded = dbManager.loadAllPalettes();
    palettes.putAll(loaded);
  }

  private void ensureDefaultPaletteInDb() {
    String workName = "DEFAULT";
    String displayName = "Classic Colors";
    String item = "DIAMOND";
    String colors = "ERROR:#ff4d4d|&c,SUCCESS:#4dff88|&a,WARNING:#ffaa00|&e,NO_PERMISSION:#ff6666|&c,INFO:#66ccff|&b,ANNOUNCEMENT:#ffff55|&e,LOOT:#cc99ff|&d,EVENT:#ff55ff|&d,SYSTEM:#999999|&7,LOADING:#9999ff|&9,DEBUG:#00ffff|&b,PRIVATE:#ff99cc|&d,ALERT:#ffcc00|&6";
    dbManager.ensurePaletteExists(workName, displayName, item, colors);
  }

  // Delegate DB calls
  public void setPlayerPalette(Player player, String palette) {
    dbManager.setPlayerPalette(player, palette);
  }

  public String getPlayerPalette(Player player) {
    return dbManager.getPlayerPalette(player, defaultPalette);
  }

  public void reloadPlayerPalette(Player player) {
    Map<String, Map<String, String>> allPlayerColors = dbManager.getPlayerColors(player.getName());
    Map<String, String> playerColors = allPlayerColors.getOrDefault(player.getName(), new HashMap<>());
    playerColorCache.put(player.getName(), playerColors);
  }

  public Map<String, String> getPlayerPaletteColors(Player player) {
    return playerColorCache.getOrDefault(player.getName(), new HashMap<>());
  }

  public Map<String, Map<String, String>> getPlayerColorCache() {
    return playerColorCache;
  }

  public DbManager getDbManager() {
    return dbManager;
  }

  public String getRawMessage(String key, Map<String, String> placeholders, Player player) {
    String msg = getConfig().getString("messages." + key, "&cBrak wiadomości: " + key);
    if (placeholders != null) {
      for (Map.Entry<String, String> entry : placeholders.entrySet()) {
        msg = msg.replace("<" + entry.getKey() + ">", entry.getValue());
      }
    }
    if (player != null) {
      msg = PlaceholderAPI.setPlaceholders(player, msg);
    }
    return msg;
  }

  public Component getMessage(String key, Map<String, String> placeholders, Player player) {
    return LegacyComponentSerializer.legacyAmpersand().deserialize(getRawMessage(key, placeholders, player));
  }

  public Component paletteLine(String type, String hex, String mcformat) {
    Component base = Component.text("  — ", NamedTextColor.DARK_GRAY)
        .append(Component.text("[" + type + "] ", NamedTextColor.WHITE));

    // HEX jako tekst i kolor
    Component hexCol = Component.text(hex)
        .color(TextColor.fromHexString(hex))
        .decoration(TextDecoration.BOLD, false);

    // MCFormat jako tekst i kolor (np. c, 4, e)
    TextColor mcColor = LegacyComponentSerializer.legacyAmpersand()
        .deserialize(mcformat + "x").color();
    if (mcColor == null) {
        mcColor = NamedTextColor.GRAY;
    }
    Component mcCol = Component.text(mcformat).color(mcColor);

    return base
        .append(hexCol)
        .append(Component.text(", ", NamedTextColor.GRAY))
        .append(mcCol);
  }
}
