package me.superchirok1.pal.tgaddon;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ConfigData {

    private final Main pl;
    private final boolean enabled;
    private final String token;
    private final List<String> allowedUsers;
    private final String elseMessage;
    private final String parseMode;
    private final String plEmpty;
    private final String btnAdd;
    private final String btnRemove;
    private final String btnList;
    private final String btnWl;
    private final String btnBl;
    private final String btnBack;

    private final String msgStart;
    private final String msgSelectListAdd;
    private final String msgSelectListRemove;
    private final String msgWriteName;
    private final String msgAdded;
    private final String msgRemoved;
    private final String msgList;
    private final String msgBack;
    private final String msgSelectAction;
    private final String msgAlreadyOnTheList;
    private final String msgNotOnTheList;

    public boolean isEnabled() {
        return enabled;
    }

    public String getToken() {
        return token;
    }

    public List<String> getAllowedUsers() {
        return allowedUsers;
    }

    public String getElseMessage() {
        return elseMessage;
    }

    public String getParseMode() {
        return parseMode;
    }

    public String getPlEmpty() {
        return plEmpty;
    }

    public String getBtnAdd() {
        return btnAdd;
    }

    public String getBtnRemove() {
        return btnRemove;
    }

    public String getBtnList() {
        return btnList;
    }

    public String getBtnWl() {
        return btnWl;
    }

    public String getBtnBl() {
        return btnBl;
    }

    public String getBtnBack() {
        return btnBack;
    }

    public String getMsgStart() {
        return msgStart;
    }

    public String getMsgSelectListAdd() {
        return msgSelectListAdd;
    }

    public String getMsgSelectListRemove() {
        return msgSelectListRemove;
    }

    public String getMsgWriteName() {
        return msgWriteName;
    }

    public String getMsgAdded() {
        return msgAdded;
    }

    public String getMsgRemoved() {
        return msgRemoved;
    }

    public String getMsgList() {
        return msgList;
    }

    public String getMsgBack() {
        return msgBack;
    }

    public String getMsgSelectAction() {
        return msgSelectAction;
    }

    public String getMsgAlreadyOnTheList() {
        return msgAlreadyOnTheList;
    }

    public String getMsgNotOnTheList() {
        return msgNotOnTheList;
    }

    public ConfigData(Main pl) {
        this.pl = pl;
        ConfigurationSection conf = pl.getConfig().getConfigurationSection("telegram");
        ConfigurationSection btns = conf.getConfigurationSection("buttons");
        ConfigurationSection msgs = conf.getConfigurationSection("messages");
        this.enabled = conf.getBoolean("enabled", true);
        this.token = conf.getString("token");
        this.allowedUsers = conf.getStringList("allowed-users");
        this.elseMessage = conf.getString("else-message", "");
        this.parseMode = conf.getString("parse-mode", "HTML");
        this.plEmpty = conf.getString("placeholders.empty", "Пусто");
        this.btnAdd = btns.getString("add", "Добавить");
        this.btnRemove = btns.getString("remove", "Удалить");
        this.btnList = btns.getString("list", "Список");
        this.btnWl = btns.getString("whitelist", "Белый список");
        this.btnBl = btns.getString("blacklist", "Черный список");
        this.msgStart = msgs.getString("start", "Добро пожаловать в бота!");
        this.msgSelectListAdd = msgs.getString("select-list-add", "Выберите список для добавления");
        this.msgSelectListRemove = msgs.getString("select-list-remove", "Выберите список для удаления");
        this.msgWriteName = msgs.getString("write-name", "Напишите имя игрока");
        this.msgAdded = msgs.getString("added", "Добавлен в список");
        this.msgRemoved = msgs.getString("removed", "Удалить из списка");
        this.msgList = msgs.getString("list", "Списки\n\nБелый список: {whitelist}\nЧерный список: {blacklist}");
        this.msgBack = msgs.getString("back", "Вы вернулись назад");
        this.msgSelectAction = msgs.getString("select-action", "Выберите действие:");
        this.msgAlreadyOnTheList = msgs.getString("already-on-the-list", "Такой есть в списке");
        this.msgNotOnTheList = msgs.getString("not-on-the-list", "Такого нет в списке");
        this.btnBack = btns.getString("back", "Назад");
    }
}
