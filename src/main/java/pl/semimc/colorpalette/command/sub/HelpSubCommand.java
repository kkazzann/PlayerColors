package pl.semimc.colorpalette.command.sub;

import java.util.List;

import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.MessageUtil;

public class HelpSubCommand implements SubCommand {
	@Override
	public String getName() {
		return "help";
	}

	@Override
	public boolean execute(Player player, String[] args) {
		player.sendMessage(MessageUtil.format(MessageUtil.HELP, null, player));
		return true;
	}

	@Override
	public List<String> tabComplete(Player player, String[] args) {
		return List.of();
	}
}