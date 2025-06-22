package pl.semimc.colorpalette.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import pl.semimc.colorpalette.ColorPalettePlugin;
import pl.semimc.colorpalette.util.ColorPalette;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaletteGui implements Listener {

    private final ColorPalettePlugin plugin;

    public PaletteGui(ColorPalettePlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Gui gui = Gui.gui()
                .title(Component.text("Choose Color Palette"))
                .rows(1)
                .disableAllInteractions()
                .create();

        String selected = plugin.getPlayerPalette(player);

        for (ColorPalette palette : plugin.getPalettes().values()) {
            List<Component> lore = new ArrayList<>();
            palette.getColors().forEach((type, arr) -> {
                String icon = switch (type) {
                    case "ERROR" -> "❌";
                    case "SUCCESS" -> "✅";
                    case "WARNING" -> "⚠️";
                    case "INFO" -> "ℹ️";
                    case "NO_PERMISSION" -> "🚫";
                    case "ANNOUNCEMENT" -> "📢";
                    case "LOOT" -> "🎁";
                    case "EVENT" -> "🎉";
                    case "SYSTEM" -> "💻";
                    case "LOADING" -> "⏳";
                    case "DEBUG" -> "🐞";
                    case "PRIVATE" -> "🔒";
                    case "ALERT" -> "🚨";
                    default -> "⬛";
                };
                lore.add(MiniMessage.miniMessage().deserialize(
                        "<white>" + icon + " " + type + ": <" + arr[0] + ">" + arr[0] + "</" + arr[0] + ">, " + arr[1])
                        .decoration(TextDecoration.ITALIC, false));
            });

            ItemBuilder builder = ItemBuilder.from(Material.valueOf(palette.getItem()))
                    .name(Component.text("> " + palette.getDisplayName() + " <"))
                    .lore(lore);

            // Enchantuj wybraną paletę
            if (palette.getWorkName().equalsIgnoreCase(selected)) {
                builder = builder
                        .enchant(Enchantment.UNBREAKING, 10)
                        .flags(ItemFlag.HIDE_ENCHANTS);
            }

            GuiItem item = new GuiItem(
                    builder.build(),
                    event -> {
                        plugin.setPlayerPalette(player, palette.getWorkName());
                        player.sendMessage(plugin.getMessage(
                                "palette_selected",
                                Map.of("palette_display", palette.getDisplayName()),
                                player));
                        gui.close(player);
                    });
            gui.addItem(item);
        }

        gui.open(player);
    }
}
