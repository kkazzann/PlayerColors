package pl.semimc.colorpalette.util;

import pl.semimc.colorpalette.database.DbManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaletteUtil {
	private static final Map<String, ColorPalette> palettes = new ConcurrentHashMap<>();

	public static void ensureDefaultPaletteInDb(DbManager dbManager) {
		String workName = "DEFAULT";
		String displayName = "Classic Colors";
		String colors = "ERROR:#ff4d4d|&c,SUCCESS:#4dff88|&a,WARNING:#ffaa00|&e,NO_PERMISSION:#ff6666|&c,INFO:#66ccff|&b,ANNOUNCEMENT:#ffff55|&e,LOOT:#cc99ff|&d,EVENT:#ff55ff|&d,SYSTEM:#999999|&7,LOADING:#9999ff|&9,DEBUG:#00ffff|&b,PRIVATE:#ff99cc|&d,ALERT:#ffcc00|&6";
		dbManager.ensurePaletteExists(workName, displayName, colors);
	}

	public static Map<String, ColorPalette> getPalettes() {
		return palettes;
	}

	public static void loadPalettes() {
		palettes.clear();
		Map<String, ColorPalette> loaded = PluginContext.getDbManager().loadAllPalettes();
		palettes.putAll(loaded);
	}

	public static String buildPaletteHover(ColorPalette palette, String header) {
		StringBuilder hoverBuilder = new StringBuilder();
		if (header != null)
			hoverBuilder.append(header);
		for (var entry : palette.getColors().entrySet()) {
			String type = entry.getKey();
			String[] arr = entry.getValue();
			hoverBuilder.append("<gray>")
					.append(type)
					.append(": <")
					.append(arr[0])
					.append(">")
					.append(arr[0])
					.append("</")
					.append(arr[0])
					.append("><newline>");
		}
		return hoverBuilder.toString().replace("'", "''");
	}
}