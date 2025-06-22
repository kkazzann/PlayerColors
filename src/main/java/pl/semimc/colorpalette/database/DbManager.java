package pl.semimc.colorpalette.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.entity.Player;
import pl.semimc.colorpalette.util.ColorPalette;

import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DbManager {
    private final MysqlDataSource dataSource;

    public DbManager(String url, String user, String password) throws SQLException {
        dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS player_palette_selection (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_name VARCHAR(32) NOT NULL," +
                "palette_name VARCHAR(32) NOT NULL," +
                "last_changed TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ")"
            );
            conn.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS palette_definitions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "work_name VARCHAR(32) NOT NULL UNIQUE," +
                "display_name VARCHAR(64) NOT NULL," +
                "item VARCHAR(32) NOT NULL," +
                "colors TEXT NOT NULL" +
                ")"
            );
        }
    }

    public void setPlayerPalette(Player player, String palette) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "REPLACE INTO player_palette_selection (player_name, palette_name, last_changed) VALUES (?, ?, ?)");
            ps.setString(1, player.getName());
            ps.setString(2, palette);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerPalette(Player player, String defaultPalette) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT palette_name FROM player_palette_selection WHERE player_name = ?");
            ps.setString(1, player.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return defaultPalette;
    }

    public void setPlayerColor(Player player, String colorType, String hex, String mcFormat) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "REPLACE INTO player_colors (PLAYER_NAME, COLOR_TYPE, HEX, MINECRAFT) VALUES (?, ?, ?, ?)");
            ps.setString(1, player.getName());
            ps.setString(2, colorType.toUpperCase());
            ps.setString(3, hex);
            ps.setString(4, mcFormat);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, String>> getPlayerColors(String playerName) {
        Map<String, Map<String, String>> colors = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COLOR_TYPE, HEX, MINECRAFT FROM player_colors WHERE PLAYER_NAME = ?");
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> colorData = new HashMap<>();
                colorData.put("HEX", rs.getString("HEX"));
                colorData.put("MINECRAFT", rs.getString("MINECRAFT"));
                colors.put(rs.getString("COLOR_TYPE"), colorData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colors;
    }

    // Dodaje paletę jeśli nie istnieje
    public void ensurePaletteExists(String workName, String displayName, String item, String colors) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement check = conn.prepareStatement(
                "SELECT id FROM palette_definitions WHERE work_name = ?");
            check.setString(1, workName);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return; // już istnieje

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO palette_definitions (work_name, display_name, item, colors) VALUES (?, ?, ?, ?)");
            ps.setString(1, workName);
            ps.setString(2, displayName);
            ps.setString(3, item);
            ps.setString(4, colors);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ładuje wszystkie palety z bazy
    public Map<String, ColorPalette> loadAllPalettes() {
        Map<String, ColorPalette> map = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM palette_definitions");
            while (rs.next()) {
                String workName = rs.getString("work_name");
                String displayName = rs.getString("display_name");
                String item = rs.getString("item");
                String colors = rs.getString("colors");
                map.put(workName, new ColorPalette(workName, displayName, item, colors));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void close() {
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
