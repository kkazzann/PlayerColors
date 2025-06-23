package pl.semimc.colorpalette.command.sub;

import java.util.List;

import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.MessageUtil;
import pl.semimc.colorpalette.util.PluginContext;

public class ReloadSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public boolean execute(Player player, String[] args) {

		PluginContext.reloadAll();

		player.sendMessage(MessageUtil.format(
				MessageUtil.RELOAD_SUCCESS, null, player));
		return true;
	}

	@Override
	public List<String> tabComplete(Player player, String[] args) {
		return List.of();
	}

	@Override
	public String getPermission() {
		return "colorpalette.reload";
	}
}