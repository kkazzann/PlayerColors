package pl.semimc.colorpalette.command.sub;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.ColorPalette;
import pl.semimc.colorpalette.util.PaletteUtil;
import me.clip.placeholderapi.PlaceholderAPI;

public class ListSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "list";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		MiniMessage mm = MiniMessage.miniMessage();
		String header = PlaceholderAPI.setPlaceholders(player,
				"<%colorpalette_player_info_hex%> » <white>Dostępne palety:");
		player.sendMessage(mm.deserialize(header));

		for (ColorPalette palette : PaletteUtil.getPalettes().values()) {
			String hoverText = PaletteUtil.buildPaletteHover(
				palette,
				"Ustaw paletę kolorów na <green>" + palette.getDisplayName() + "</green><newline><newline>"
			);
			String msg = "<hover:show_text:'" + hoverText + "'>"
					+ "<click:run_command:'/paleta set " + palette.getWorkName() + "'>"
					+ palette.getDisplayName() + " <gray>(" + palette.getWorkName() + ")"
					+ "</click></hover>";
			player.sendMessage(mm.deserialize(msg));
		}
		return true;
	}

	@Override
	public List<String> tabComplete(Player player, String[] args) {
		return List.of();
	}
}