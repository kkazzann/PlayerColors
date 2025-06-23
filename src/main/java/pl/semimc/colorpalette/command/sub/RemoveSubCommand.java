package pl.semimc.colorpalette.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

import java.util.List;
import java.util.Map;

public class RemoveSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "remove";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_REMOVE_USAGE, null, player));
			return true;
		}

		String workName = args[1];
		PluginContext.getDbManager().removePalette(workName);
		PaletteUtil.loadPalettes();

		// Zmień paletę na domyślną wszystkim graczom, którzy mieli usuwaną paletę
		Bukkit.getScheduler().runTaskAsynchronously(PluginContext.getPlugin(), () -> {
			try (var conn = PluginContext.getDbManager().getDataSource().getConnection()) {
				var ps = conn.prepareStatement("SELECT player_name FROM player_palette_selection WHERE palette_name = ?");
				ps.setString(1, workName);
				var rs = ps.executeQuery();
				while (rs.next()) {
					String playerName = rs.getString(1);
					// Ustaw domyślną w bazie
					var update = conn
							.prepareStatement("UPDATE player_palette_selection SET palette_name = ? WHERE player_name = ?");
					update.setString(1, PluginContext.getDefaultPalette());
					update.setString(2, playerName);
					update.executeUpdate();
					// Ustaw domyślną w cache jeśli gracz online
					Bukkit.getScheduler().runTask(PluginContext.getPlugin(), () -> {
						PluginContext.getPlayerColorCache().put(playerName, PluginContext.getDefaultPalette());
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		player.sendMessage(MessageUtil.format(
				MessageUtil.PALETTE_REMOVE_SUCCESS,
				Map.of("work_name", workName),
				player));
		return true;
	}

	@Override
	public List<String> tabComplete(Player player, String[] args) {
		if (args.length == 2) {
			return PaletteUtil.getPalettes().keySet().stream()
					.filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
					.toList();
		}
		return List.of();
	}

	@Override
	public String getPermission() {
		return "colorpalette.edit";
	}
}