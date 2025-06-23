package pl.semimc.colorpalette.command.sub;

import org.bukkit.entity.Player;

import pl.semimc.colorpalette.util.MessageUtil;

import java.util.List;

public interface SubCommand {
	String getName();

	default String getPermission() {
		return null;
	}

	boolean execute(Player player, String[] args);

	default List<String> tabComplete(Player player, String[] args) {
		return List.of();
	}

	// Template method to handle permission check and execution
	default boolean handle(Player player, String[] args) {
		String perm = getPermission();

		if (perm != null && !player.hasPermission(perm)) {
			player.sendMessage(MessageUtil.format(MessageUtil.NO_PERMISSION, null, player));
			return true;
		}

		return execute(player, args);
	}
}