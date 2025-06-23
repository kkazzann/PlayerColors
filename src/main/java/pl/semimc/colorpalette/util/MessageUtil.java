package pl.semimc.colorpalette.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageUtil {
	// Klucze
	public static final String ONLY_PLAYERS_KEY = "only_players";
	public static final String PALETTE_SELECTED_KEY = "palette_selected";
	public static final String LOADED_PALETTES_KEY = "loaded_palettes";
	public static final String PALETTE_NOT_FOUND_KEY = "palette_not_found";
	public static final String PALETTE_DEBUG_HEADER_KEY = "palette_debug_header";
	public static final String PALETTE_DEBUG_LINE_KEY = "palette_debug_line";
	public static final String USAGE_DEBUG_KEY = "usage_debug";
	public static final String PALETTE_EDIT_SUCCESS_KEY = "palette_edit_success";
	public static final String PALETTE_EDIT_NOT_FOUND_KEY = "palette_edit_not_found";
	public static final String NO_PERMISSION_KEY = "no_permission";
	public static final String PALETTE_EDIT_USAGE_KEY = "palette_edit_usage";
	public static final String PALETTE_ADD_SUCCESS_KEY = "palette_add_success";
	public static final String PALETTE_REMOVE_SUCCESS_KEY = "palette_remove_success";
	public static final String PALETTE_ADD_USAGE_KEY = "palette_add_usage";
	public static final String PALETTE_REMOVE_USAGE_KEY = "palette_remove_usage";
	public static final String HELP_KEY = "help";
	public static final String RELOAD_SUCCESS_KEY = "reload_success";
	public static final String SET_USAGE_KEY = "set_usage";

	// Statyczne surowe stringi
	public static String ONLY_PLAYERS;
	public static String PALETTE_SELECTED;
	public static String LOADED_PALETTES;
	public static String PALETTE_NOT_FOUND;
	public static String PALETTE_DEBUG_HEADER;
	public static String PALETTE_DEBUG_LINE;
	public static String USAGE_DEBUG;
	public static String PALETTE_EDIT_SUCCESS;
	public static String PALETTE_EDIT_NOT_FOUND;
	public static String NO_PERMISSION;
	public static String PALETTE_EDIT_USAGE;
	public static String PALETTE_ADD_SUCCESS;
	public static String PALETTE_REMOVE_SUCCESS;
	public static String PALETTE_ADD_USAGE;
	public static String PALETTE_REMOVE_USAGE;
	public static String HELP;
	public static String RELOAD_SUCCESS;
	public static String SET_USAGE;

	// Inicjalizacja - wywołaj raz po starcie pluginu!
	public static void init(FileConfiguration config) {
		// Pobierz missing z configa lub ustaw domyślną wartość
		String missing = config.getString("messages.missing", "<dark_red>❌ <red>Brak wiadomości w <dark_red>config.yml<red>.");

		ONLY_PLAYERS = config.getString("messages." + ONLY_PLAYERS_KEY, missing);
		PALETTE_SELECTED = config.getString("messages." + PALETTE_SELECTED_KEY, missing);
		LOADED_PALETTES = config.getString("messages." + LOADED_PALETTES_KEY, missing);
		PALETTE_NOT_FOUND = config.getString("messages." + PALETTE_NOT_FOUND_KEY, missing);
		PALETTE_DEBUG_HEADER = config.getString("messages." + PALETTE_DEBUG_HEADER_KEY, missing);
		PALETTE_DEBUG_LINE = config.getString("messages." + PALETTE_DEBUG_LINE_KEY, missing);
		USAGE_DEBUG = config.getString("messages." + USAGE_DEBUG_KEY, missing);
		PALETTE_EDIT_SUCCESS = config.getString("messages." + PALETTE_EDIT_SUCCESS_KEY, missing);
		PALETTE_EDIT_NOT_FOUND = config.getString("messages." + PALETTE_EDIT_NOT_FOUND_KEY, missing);
		NO_PERMISSION = config.getString("messages." + NO_PERMISSION_KEY, missing);
		PALETTE_EDIT_USAGE = config.getString("messages." + PALETTE_EDIT_USAGE_KEY, missing);
		PALETTE_ADD_SUCCESS = config.getString("messages." + PALETTE_ADD_SUCCESS_KEY, missing);
		PALETTE_REMOVE_SUCCESS = config.getString("messages." + PALETTE_REMOVE_SUCCESS_KEY, missing);
		PALETTE_ADD_USAGE = config.getString("messages." + PALETTE_ADD_USAGE_KEY, missing);
		PALETTE_REMOVE_USAGE = config.getString("messages." + PALETTE_REMOVE_USAGE_KEY, missing);
		HELP = config.getString("messages." + HELP_KEY, missing);
		RELOAD_SUCCESS = config.getString("messages." + RELOAD_SUCCESS_KEY, missing);
		SET_USAGE = config.getString("messages." + SET_USAGE_KEY, missing);
	}

	// Zwraca Component z podmienionymi placeholderami i MiniMessage
	public static Component format(String raw, Map<String, String> replacements, Player player) {
		String msg = raw;

		if (replacements != null) {
			for (Map.Entry<String, String> entry : replacements.entrySet()) {
				msg = msg.replace("<" + entry.getKey() + ">", entry.getValue());
			}
		}

		if (player != null) {
			msg = PlaceholderAPI.setPlaceholders(player, msg);
		}

		return MiniMessage.miniMessage().deserialize(msg);
	}
}