package me.superchirok1.pal.tgaddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public final class Main extends JavaPlugin {

    private AddBot bot;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("PlayerAccessList") == null) {
            getLogger().severe("Дополнение выключено так-как нет плагина PlayerAccessList (зачем вы тогда скачали дополнение)");
            getLogger().warning("Установить -> https://spigotmc.ru/resources/playeraccesslist-belyj-chernyj-spisok.4243/");
            this.setEnabled(false);
            return;
        }

        if (getConfig().getBoolean("telegram.enabled")) {
            if (getConfig().getString("telegram.token")
                    .equalsIgnoreCase("YOUR-BOT-TOKEN")) {
                getLogger().warning("Укажи токен бота в конфиге!");
                return;
            }

            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

                this.bot = new AddBot(this);

                botsApi.registerBot(bot);

                getLogger().info("Telegram бот успешно загружен!");
            } catch (TelegramApiException e) {
                getLogger().severe("Ошибка при запуске Telegram бота!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        if (bot != null) {
            try {
                bot.clearWebhook();
                getLogger().info("Telegram бот остановлен.");
            } catch (TelegramApiRequestException e) {
                getLogger().severe("Ошибка при остановке Telegram бота!");
                e.printStackTrace();
            }
        }
    }
}
