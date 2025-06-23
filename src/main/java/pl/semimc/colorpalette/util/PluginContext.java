package pl.semimc.colorpalette.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.semimc.colorpalette.database.DbManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginContext {
	private static JavaPlugin plugin;
	private static DbManager dbManager;
	private static BukkitAudiences adventure;
	private static final String DEFAULT_PALETTE = "DEFAULT";
	private static final Map<String, String> playerColorCache = new ConcurrentHashMap<>();

	public static void init(JavaPlugin pl) {
		plugin = pl;
		dbManager = DbManager.setupDatabase(plugin.getConfig());
		adventure = BukkitAudiences.create(plugin);
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	public static DbManager getDbManager() {
		return dbManager;
	}

	public static BukkitAudiences getAdventure() {
		return adventure;
	}

	public static String getDefaultPalette() {
		return DEFAULT_PALETTE;
	}

	public static Map<String, String> getPlayerColorCache() {
		return playerColorCache;
	}

	public static void close() {
		if (adventure != null)
			adventure.close();
		if (dbManager != null)
			dbManager.close();
		playerColorCache.clear();
	}

	public static void reloadAll() {
		// Przeładuj config.yml
		plugin.reloadConfig();
		// Przeładuj wiadomości
		MessageUtil.init(plugin.getConfig());
		// Przeładuj palety
		PaletteUtil.loadPalettes();
		// Odśwież cache graczy online i wymuś domyślną jeśli paleta nie istnieje
		Bukkit.getOnlinePlayers().forEach(p -> {
			getDbManager().getPlayerPaletteAsync(p, getDefaultPalette())
					.thenAccept(paletteName -> {
						String toSet = PaletteUtil.getPalettes().containsKey(paletteName)
								? paletteName
								: getDefaultPalette();
						getPlayerColorCache().put(p.getName(), toSet);
						// Jeśli trzeba, popraw w bazie
						if (!toSet.equals(paletteName)) {
							getDbManager().setPlayerPaletteAsync(p, getDefaultPalette());
						}
					})
					.exceptionally(e -> {
						e.printStackTrace();
						return null;
					});
		});
	}
}