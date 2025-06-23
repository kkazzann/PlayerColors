package pl.semimc.colorpalette.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.semimc.colorpalette.util.PluginContext;
import pl.semimc.colorpalette.util.PaletteUtil;

public class PlayerPaletteCacheListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		PluginContext.getDbManager().getPlayerPaletteAsync(event.getPlayer(), PluginContext.getDefaultPalette())
				.thenAccept(paletteName -> {
					String toSet = PaletteUtil.getPalettes().containsKey(paletteName)
							? paletteName
							: PluginContext.getDefaultPalette();
					PluginContext.getPlayerColorCache().put(event.getPlayer().getName(), toSet);
					if (!toSet.equals(paletteName)) {
						PluginContext.getDbManager().setPlayerPaletteAsync(event.getPlayer(), PluginContext.getDefaultPalette());
					}
				})
				.exceptionally(e -> {
					e.printStackTrace();
					return null;
				});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		// Usuń z cache po wyjściu
		PluginContext.getPlayerColorCache().remove(event.getPlayer().getName());
	}
}
