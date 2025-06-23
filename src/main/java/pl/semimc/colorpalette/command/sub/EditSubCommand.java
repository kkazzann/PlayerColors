package pl.semimc.colorpalette.command.sub;

import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.ColorPalette;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

import java.util.List;
import java.util.Map;

public class EditSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "edit";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		if (args.length != 5) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_EDIT_USAGE, null, player));
			return true;
		}

		String paletteName = args[1];
		String type = args[2].toUpperCase();
		String hex = args[3];
		String mcformat = args[4];

		ColorPalette palette = PaletteUtil.getPalettes().get(paletteName);
		if (palette == null) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_EDIT_NOT_FOUND,
					Map.of("palette_name", paletteName),
					player));
			return true;
		}

		palette.getColors().put(type, new String[] { hex, mcformat });

		StringBuilder colorsBuilder = new StringBuilder();
		for (Map.Entry<String, String[]> entry : palette.getColors().entrySet()) {
			if (colorsBuilder.length() > 0)
				colorsBuilder.append(",");
			colorsBuilder.append(entry.getKey()).append(":").append(entry.getValue()[0]).append("|")
					.append(entry.getValue()[1]);
		}

		PluginContext.getDbManager().ensurePaletteExists(
				palette.getWorkName(),
				palette.getDisplayName(),
				colorsBuilder.toString());

		PaletteUtil.loadPalettes();

		player.sendMessage(MessageUtil.format(
				MessageUtil.PALETTE_EDIT_SUCCESS,
				Map.of("type", type, "palette", paletteName, "hex", hex, "mcformat", mcformat),
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
		if (args.length == 3) {
			String paletteName = args[1];
			ColorPalette palette = PaletteUtil.getPalettes().get(paletteName);
			if (palette != null) {
				return palette.getColors().keySet().stream()
						.filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
						.toList();
			}
		}
		if (args.length == 4) {
			return List.of("<hex>");
		}
		if (args.length == 5) {
			return List.of("<mcformat>");
		}
		return List.of();
	}

	@Override
	public String getPermission() {
		return "colorpalette.edit";
	}
}