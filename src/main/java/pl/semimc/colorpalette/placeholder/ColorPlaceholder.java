package pl.semimc.colorpalette.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.semimc.colorpalette.ColorPalettePlugin;
import pl.semimc.colorpalette.util.ColorPalette;
import pl.semimc.colorpalette.util.PaletteUtil;
import pl.semimc.colorpalette.util.PluginContext;

public class ColorPlaceholder extends PlaceholderExpansion {

	private final ColorPalettePlugin plugin;

	public ColorPlaceholder(ColorPalettePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "colorpalette";
	}

	@Override
	public @NotNull String getAuthor() {
		return "kznlabs";
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getPluginMeta().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String params) {
		if (player == null)
			return "";
		String[] split = params.split("_");
		if (split.length == 3 && split[0].equalsIgnoreCase("PLAYER")) {
			String type = split[1].toUpperCase();
			String format = split[2];
			String paletteName = PluginContext.getPlayerColorCache()
					.getOrDefault(player.getName(), PluginContext.getDefaultPalette());
			ColorPalette palette = PaletteUtil.getPalettes().get(paletteName);
			if (palette == null)
				return "";
			String[] color = palette.getColors().get(type);
			if (color == null) {
				// Fallback: spróbuj z domyślnej palety
				ColorPalette defaultPalette = PaletteUtil.getPalettes().get(PluginContext.getDefaultPalette());
				if (defaultPalette != null) {
					color = defaultPalette.getColors().get(type);
					if (color == null)
						return "";
				} else {
					return "";
				}
			}
			if (format.equalsIgnoreCase("HEX")) {
				return color[0];
			}
			if (format.equalsIgnoreCase("MCFORMAT")) {
				return color[1].replace('&', '§');
			}
		}
		return "";
	}
}
