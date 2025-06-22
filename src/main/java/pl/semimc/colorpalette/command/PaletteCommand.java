package pl.semimc.colorpalette.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.ColorPalettePlugin;

public class PaletteCommand implements CommandExecutor {

    private final ColorPalettePlugin plugin;

    public PaletteCommand(ColorPalettePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getMessage("only_players", null, null));
            return true;
        }
        plugin.getPaletteGui().open(player);
        return true;
    }
}
