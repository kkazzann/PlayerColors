package pl.semimc.colorpalette.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColorPalette {
    private final String workName;
    private final String displayName;
    private final String item;
    private final Map<String, String[]> colors; // type -> [hex, mcformat]

    public ColorPalette(String workName, String displayName, String item, String colorsString) {
        this.workName = workName;
        this.displayName = displayName;
        this.item = item;
        this.colors = new LinkedHashMap<>();
        for (String entry : colorsString.split(",")) {
            String[] kv = entry.split(":");
            if (kv.length == 2) {
                String[] colorParts = kv[1].split("\\|");
                if (colorParts.length == 2) {
                    colors.put(kv[0].toUpperCase(), new String[]{colorParts[0], colorParts[1]});
                }
            }
        }
    }

    public String getWorkName() { return workName; }
    public String getDisplayName() { return displayName; }
    public String getItem() { return item; }
    public Map<String, String[]> getColors() { return colors; }
}