package pl.semimc.colorpalette.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.ColorPalette;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

import java.util.List;
import java.util.Map;

public class DebugSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		if (args.length < 2) {
			player.sendMessage(MessageUtil.format(MessageUtil.USAGE_DEBUG, null, player));
			return true;
		}

		String paletteName = args[1];

		Bukkit.getScheduler().runTaskAsynchronously(PluginContext.getPlugin(), () -> {
			ColorPalette palette = PaletteUtil.getPalettes().get(paletteName);

			Bukkit.getScheduler().runTask(PluginContext.getPlugin(), () -> {
				if (palette == null) {
					player.sendMessage(MessageUtil.format(
							MessageUtil.PALETTE_NOT_FOUND,
							Map.of("palette_name", paletteName),
							player));
					return;
				}

				String hoverText = PaletteUtil.buildPaletteHover(
						palette,
						"<gray>Podgląd kolorów:<newline>");

				String headerMsg = "<hover:show_text:'" + hoverText + "'>"
						+ MessageUtil.PALETTE_DEBUG_HEADER.replace("<palette_display>", palette.getDisplayName())
						+ "</hover>";
				player.sendMessage(MessageUtil.format(headerMsg, null, player));

				for (Map.Entry<String, String[]> entry : palette.getColors().entrySet()) {
					String type = entry.getKey();
					String[] arr = entry.getValue();
					player.sendMessage(MessageUtil.format(
							MessageUtil.PALETTE_DEBUG_LINE,
							Map.of("type", type, "hex", arr[0], "mcformat", arr[1]),
							player));
				}
			});
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