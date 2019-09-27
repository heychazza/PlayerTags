package io.felux.playertags.storage.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.felux.playertags.PlayerTags;
import io.felux.playertags.storage.StorageHandler;
import io.felux.playertags.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.UUID;

public class MySQLHandler implements StorageHandler {

    private ConnectionSource connectionSource;
    private Dao<MySQLPlayerData, String> accountDao;

    public MySQLHandler(String prefix, String host, int port, String database, String username, String password) {
        try {
            connectionSource = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            accountDao = DaoManager.createDao(connectionSource, MySQLPlayerData.class);
            TableUtils.createTableIfNotExists(connectionSource, MySQLPlayerData.class);
            connectionSource.close();

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        accountDao.queryForAll();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(PlayerTags.class), 20L * 30, 20L * 30);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pullData(UUID uuid) {
        try {
            MySQLPlayerData user = accountDao.queryForId(uuid.toString());
            if (user == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                user = new MySQLPlayerData();
                user.setUuid(uuid.toString());
                user.setUsername(offlinePlayer.getName());
                user.setTag(null);

                PlayerData.get().put(uuid, user);
                //accountDao.create(user);
            } else {
                PlayerData.get().put(uuid, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushData(UUID player) {
        PlayerData playerData = PlayerData.get().get(player);
        try {
            accountDao.createOrUpdate((MySQLPlayerData) playerData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
