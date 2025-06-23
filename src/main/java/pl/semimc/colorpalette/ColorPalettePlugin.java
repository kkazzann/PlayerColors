package pl.semimc.colorpalette;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.command.ColorPaletteCommand;
import pl.semimc.colorpalette.database.DbManager;
import pl.semimc.colorpalette.listeners.PlayerPaletteCacheListener;
import pl.semimc.colorpalette.placeholder.ColorPlaceholder;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

public class ColorPalettePlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		saveDefaultConfig();

		// Inicjalizacja singletona kontekstu pluginu
		PluginContext.init(this);

		DbManager dbManager = PluginContext.getDbManager();
		PaletteUtil.ensureDefaultPaletteInDb(dbManager);
		PaletteUtil.loadPalettes();

		Bukkit.getPluginManager().registerEvents(new PlayerPaletteCacheListener(), this);
		new ColorPlaceholder(this).register();

		PluginCommand adminCmd = getCommand("colorpalette");
		if (adminCmd != null) {
			ColorPaletteCommand cmd = new ColorPaletteCommand();
			adminCmd.setExecutor(cmd);
			adminCmd.setTabCompleter(cmd);
		}

		MessageUtil.init(getConfig());

		// Po rejestracji kontekstu i bazy:
		for (Player player : Bukkit.getOnlinePlayers()) {
			PluginContext.getDbManager().getPlayerPaletteAsync(player, PluginContext.getDefaultPalette())
					.thenAccept(paletteName -> PluginContext.getPlayerColorCache().put(player.getName(), paletteName))
					.exceptionally(e -> {
						e.printStackTrace();
						return null;
					});
		}
	}

	@Override
	public void onDisable() {
		PluginContext.close();
	}
}
