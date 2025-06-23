package pl.semimc.colorpalette.command;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.command.sub.*;
import pl.semimc.colorpalette.util.MessageUtil;

import java.util.*;

public class ColorPaletteCommand implements CommandExecutor, TabCompleter {

	private final Map<String, SubCommand> subCommands = new HashMap<>();

	public ColorPaletteCommand() {
		register(new HelpSubCommand());
		register(new ListSubCommand());
		register(new DebugSubCommand());
		register(new EditSubCommand());
		register(new AddSubCommand());
		register(new RemoveSubCommand());
		register(new SetSubCommand());
		register(new ReloadSubCommand());
	}

	private void register(SubCommand cmd) {
		subCommands.put(cmd.getName().toLowerCase(), cmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (sender instanceof Player p) ? p : null;
		if (player == null) {
			sender.sendMessage(MessageUtil.format(MessageUtil.ONLY_PLAYERS, null, null));
			return true;
		}

		if (args.length == 0) {
			player.sendMessage(MessageUtil.format(MessageUtil.HELP, null, player));
			return true;
		}

		SubCommand sub = subCommands.get(args[0].toLowerCase());

		if (sub != null) {
			return sub.handle(player, args);
		}

		player.sendMessage(MessageUtil.format(MessageUtil.HELP, null, player));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player player))
			return Collections.emptyList();
		if (args.length == 1) {
			List<String> completions = new ArrayList<>(subCommands.keySet());
			completions.remove("");
			return filter(completions, args[0]);
		}
		SubCommand sub = subCommands.get(args[0].toLowerCase());
		if (sub != null) {
			return sub.tabComplete(player, args);
		}
		return Collections.emptyList();
	}

	private List<String> filter(List<String> options, String arg) {
		if (arg == null || arg.isEmpty())
			return options;
		List<String> filtered = new ArrayList<>();
		for (String opt : options) {
			if (opt.toLowerCase().startsWith(arg.toLowerCase())) {
				filtered.add(opt);
			}
		}
		return filtered;
	}
}
