package pl.semimc.colorpalette.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.ColorPalette;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

import java.util.List;
import java.util.Map;

public class SetSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "set";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.SET_USAGE, null, player));
			return true;
		}

		String paletteName = args[1];
		ColorPalette palette = PaletteUtil.getPalettes().get(paletteName);

		if (palette == null) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_NOT_FOUND,
					Map.of("palette_name", paletteName),
					player));
			return true;
		}

		Bukkit.getScheduler().runTaskAsynchronously(PluginContext.getPlugin(), () -> {
			PluginContext.getDbManager().setPlayerPaletteAsync(player, paletteName)
					.thenRun(() -> Bukkit.getScheduler().runTask(PluginContext.getPlugin(), () -> {
						PluginContext.getPlayerColorCache().put(player.getName(), paletteName);
						player.sendMessage(MessageUtil.format(
								MessageUtil.PALETTE_SELECTED,
								Map.of("palette_display", palette.getDisplayName()),
								player));
					}));
		});
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
}