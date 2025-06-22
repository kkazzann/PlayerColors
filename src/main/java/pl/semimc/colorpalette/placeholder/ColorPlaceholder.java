package pl.semimc.colorpalette.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.semimc.colorpalette.ColorPalettePlugin;
import pl.semimc.colorpalette.util.ColorPalette;

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
        if (player == null) return "";
        // Example: PLAYER_ERROR_HEX or PLAYER_SUCCESS_MCFORMAT
        String[] split = params.split("_");
        // Handle placeholders like %colorpalette_player_error_hex% or %colorpalette_player_success_mcformat%
        if (split.length == 3 && split[0].equalsIgnoreCase("PLAYER")) {
            // e.g. "ERROR", "SUCCESS"
            String type = split[1].toUpperCase();
            // e.g. "HEX", "MCFORMAT"
            String format = split[2].toUpperCase();
            // Get the player's selected palette
            ColorPalette palette = plugin.getPalettes().get(plugin.getPlayerPalette(player));
            // No palette found for player
            if (palette == null) return "";
            // Get the color array for the requested type
            String[] color = palette.getColors().get(type);
            // No color type found in palette
            if (color == null) return "";
            // Return the requested color format
            if (format.equals("HEX")) return color[0];
            if (format.equals("MCFORMAT")) return color[1];
            // If format is not recognized, return empty string
            // (Developers: Supported formats are HEX and MCFORMAT. Add more here if needed.)
        }
        return "";
    }
}
