package pl.semimc.colorpalette.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.ColorPalettePlugin;
import pl.semimc.colorpalette.util.ColorPalette;

import java.util.Map;

public class ColorPaletteAdminCommand implements CommandExecutor {
    private final ColorPalettePlugin plugin;

    public ColorPaletteAdminCommand(ColorPalettePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player p) ? p : null;

        if (args.length == 0) {
            String loaded = String.join(", ", plugin.getPalettes().keySet());
            sender.sendMessage(plugin.getMessage("loaded_palettes", Map.of("palette_list", loaded), player));
            return true;
        }
        if (args[0].equalsIgnoreCase("debug") && args.length == 2) {
            String paletteName = args[1].toUpperCase();
            ColorPalette palette = plugin.getPalettes().get(paletteName);
            if (palette == null) {
                sender.sendMessage(plugin.getMessage("palette_not_found", Map.of("palette_name", paletteName), player));
                return true;
            }
            sender.sendMessage(plugin.getMessage("palette_debug_header", Map.of("palette_display", palette.getDisplayName()), player));
            for (Map.Entry<String, String[]> entry : palette.getColors().entrySet()) {
                String type = entry.getKey();
                String[] arr = entry.getValue();
                sender.sendMessage(plugin.paletteLine(type, arr[0], arr[1]));
            }
            return true;
        }
        sender.sendMessage(plugin.getMessage("usage_debug", null, player));
        return true;
    }
}
