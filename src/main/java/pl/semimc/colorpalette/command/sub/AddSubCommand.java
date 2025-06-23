package pl.semimc.colorpalette.command.sub;

import java.util.List;

import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

import java.util.Map;

public class AddSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "add";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		if (args.length < 4) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_ADD_USAGE, null, player));
			return true;
		}
		String workName = args[1];

		// Parsowanie displayName z cudzysłowami
		String displayName;
		int displayNameStart = 2;
		int displayNameEnd = 2;
		if (args[displayNameStart].startsWith("\"")) {
			StringBuilder sb = new StringBuilder(args[displayNameStart]);
			displayNameEnd = displayNameStart;
			while (!args[displayNameEnd].endsWith("\"") && displayNameEnd + 1 < args.length) {
				displayNameEnd++;
				sb.append(" ").append(args[displayNameEnd]);
			}
			displayName = sb.toString();
			// Usuń cudzysłowy
			if (displayName.length() >= 2 && displayName.startsWith("\"") && displayName.endsWith("\"")) {
				displayName = displayName.substring(1, displayName.length() - 1);
			}
		} else {
			displayName = args[displayNameStart];
		}

		// kolory od displayNameEnd+1 do końca
		int colorsIndex = displayNameEnd + 1;
		if (colorsIndex >= args.length) {
			player.sendMessage(MessageUtil.format(
					MessageUtil.PALETTE_ADD_USAGE, null, player));
			return true;
		}
		StringBuilder colorsBuilder = new StringBuilder();
		for (int i = colorsIndex; i < args.length; i++) {
			if (i > colorsIndex)
				colorsBuilder.append(" ");
			colorsBuilder.append(args[i]);
		}
		String colors = colorsBuilder.toString();

		PluginContext.getDbManager().ensurePaletteExists(workName, displayName, colors);
		PaletteUtil.loadPalettes();

		player.sendMessage(MessageUtil.format(
				MessageUtil.PALETTE_ADD_SUCCESS,
				Map.of("display_name", displayName, "work_name", workName),
				player));
		return true;
	}

	@Override
	public List<String> tabComplete(Player player, String[] args) {
		// /paleta add <work_name> <display_name> <kolory_w_ciagu>
		if (args.length == 2) {
			return List.of("<work_name>");
		}
		if (args.length == 3) {
			return List.of("<display_name>");
		}
		if (args.length == 4) {
			return List.of("<kolory_w_ciagu>");
		}
		return List.of();
	}

	@Override
	public String getPermission() {
		return "colorpalette.edit";
	}
}