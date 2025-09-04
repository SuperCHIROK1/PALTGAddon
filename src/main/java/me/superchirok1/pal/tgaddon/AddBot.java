package me.superchirok1.pal.tgaddon;

import me.superchirok1.playeraccesslist.Lists.Blacklist;
import me.superchirok1.playeraccesslist.Lists.Whitelist;
import me.superchirok1.playeraccesslist.PlayerAccessList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.*;

public class AddBot extends TelegramLongPollingBot {

    private final Main plugin;
    private final PlayerAccessList pal;

    private final Map<Long, String> state = new HashMap<>();

    private static final String CHOOSE_LIST_ADD = "CHOOSE_LIST_ADD";
    private static final String CHOOSE_LIST_DEL = "CHOOSE_LIST_DEL";
    private static final String WAIT_ADD_WHITE   = "WAIT_ADD_WHITE";
    private static final String WAIT_ADD_BLACK   = "WAIT_ADD_BLACK";
    private static final String WAIT_DEL_WHITE   = "WAIT_DEL_WHITE";
    private static final String WAIT_DEL_BLACK   = "WAIT_DEL_BLACK";


    public AddBot(Main plugin) {
        this.plugin = plugin;
        this.pal = (PlayerAccessList) Bukkit.getPluginManager().getPlugin("PlayerAccessList");
    }

    @Override
    public void onUpdateReceived(Update update) {

        FileConfiguration config = plugin.getConfig();

        List<String> whitelist = new Whitelist(pal).getList();
        List<String> blacklist = new Blacklist(pal).getList();

        ConfigurationSection msgs = config.getConfigurationSection("telegram.messages");
        ConfigurationSection btns = config.getConfigurationSection("telegram.buttons");

        if (!(update.hasMessage() && update.getMessage().hasText())) return;

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        String not_allowed = config.getString("telegram.else-message");

        String username = update.getMessage().getFrom().getUserName();
        List<String> allowed = config.getStringList("telegram.allowed-users");

        if (username == null || !allowed.contains(username)) {
            if (not_allowed.equalsIgnoreCase("null")) {
                return;
            }
            sendMsg(chatId, not_allowed);
            return;
        }

        if ("/start".equalsIgnoreCase(text)) {
            sendMainMenu(chatId, config.getString("telegram.messages.start"));
            return;
        }

        if (state.containsKey(chatId)) {
            String s = state.get(chatId);

            if (CHOOSE_LIST_ADD.equals(s) || CHOOSE_LIST_DEL.equals(s)) {
                if (text.equalsIgnoreCase(btns.getString("whitelist"))) {
                    if (CHOOSE_LIST_ADD.equals(s)) {
                        state.put(chatId, WAIT_ADD_WHITE);
                        sendMsg(chatId, msgs.getString("write-name"));
                    } else {
                        state.put(chatId, WAIT_DEL_WHITE);
                        sendMsg(chatId, msgs.getString("write-name"));
                    }
                    return;
                } else if (text.equalsIgnoreCase(btns.getString("blacklist"))) {
                    if (CHOOSE_LIST_ADD.equals(s)) {
                        state.put(chatId, WAIT_ADD_BLACK);
                        sendMsg(chatId, msgs.getString("write-name"));
                    } else {
                        state.put(chatId, WAIT_DEL_BLACK);
                        sendMsg(chatId, msgs.getString("write-name"));
                    }
                    return;
                } else if (text.equalsIgnoreCase(btns.getString("back"))) {
                    state.remove(chatId);
                    sendMainMenu(chatId, msgs.getString("back"));
                    return;
                } else {
                    sendChooseList(chatId, s);
                    return;
                }
            }

            switch (s) {
                case WAIT_ADD_WHITE -> {
                    if (text.equalsIgnoreCase(btns.getString("back"))) {
                        state.remove(chatId);
                        sendMainMenu(chatId, msgs.getString("back"));
                        return;
                    }
                    if (!new Whitelist(pal).has(text)) {
                        new Whitelist(pal).add(text);
                        sendMainMenu(chatId, msgs.getString("added")
                                .replace("{player}", text)
                                .replace("{list}", btns.getString("whitelist")));
                    } else {
                        sendMainMenu(chatId, msgs.getString("already-on-the-list"));
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_ADD_BLACK -> {
                    if (text.equalsIgnoreCase(btns.getString("back"))) {
                        state.remove(chatId);
                        sendMainMenu(chatId, msgs.getString("back"));
                        return;
                    }
                    if (!new Blacklist(pal).has(text)) {
                        new Blacklist(pal).add(text);
                        sendMainMenu(chatId, msgs.getString("added")
                                .replace("{player}", text)
                                .replace("{list}", btns.getString("blacklist")));
                    } else {
                        sendMainMenu(chatId, msgs.getString("already-on-the-list"));
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_DEL_WHITE -> {
                    if (text.equalsIgnoreCase(btns.getString("back"))) {
                        state.remove(chatId);
                        sendMainMenu(chatId, msgs.getString("back"));
                        return;
                    }
                    if (new Whitelist(pal).has(text)) {
                        new Whitelist(pal).remove(text);
                        sendMainMenu(chatId, msgs.getString("removed")
                                .replace("{player}", text)
                                .replace("{list}", btns.getString("whitelist")));
                    } else {
                        sendMainMenu(chatId, msgs.getString("not-on-the-list"));
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_DEL_BLACK -> {
                    if (text.equalsIgnoreCase(btns.getString("back"))) {
                        state.remove(chatId);
                        sendMainMenu(chatId, msgs.getString("back"));
                        return;
                    }
                    if (new Blacklist(pal).has(text)) {
                        new Blacklist(pal).remove(text);
                        sendMainMenu(chatId, msgs.getString("removed")
                                .replace("{player}", text)
                                .replace("{list}", btns.getString("blacklist")));
                    } else {
                        sendMainMenu(chatId, msgs.getString("not-on-the-list"));
                    }

                    state.remove(chatId);
                    return;
                }
            }
        }

        if (text.equalsIgnoreCase(btns.getString("add"))) {
            state.put(chatId, CHOOSE_LIST_ADD);
            sendChooseList(chatId, CHOOSE_LIST_ADD);
        }
        else if (text.equalsIgnoreCase(btns.getString("remove"))) {
            state.put(chatId, CHOOSE_LIST_DEL);
            sendChooseList(chatId, CHOOSE_LIST_DEL);
        }
        else if (text.equalsIgnoreCase(btns.getString("list"))) {

            sendMsg(chatId, msgs.getString("list")
                    .replace("{whitelist}", whitelist.isEmpty()
                            ? Objects.requireNonNull(config.getString("telegram.placeholders.empty")) : String.join(", ", whitelist))
                    .replace("{blacklist}", blacklist.isEmpty()
                            ? Objects.requireNonNull(config.getString("telegram.placeholders.empty")) : String.join(", ", blacklist)));

        } else {
            sendMainMenu(chatId, msgs.getString("select-action"));
        }


    }

    private void sendMainMenu(long chatId, String text) {

        FileConfiguration config = plugin.getConfig();

        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow r1 = new KeyboardRow();
        r1.add(config.getString("telegram.buttons.add"));
        r1.add(config.getString("telegram.buttons.remove"));
        r1.add(config.getString("telegram.buttons.list"));
        rows.add(r1);

        kb.setKeyboard(rows);

        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        parse(msg);
        msg.setReplyMarkup(kb);
        safeSend(msg);
    }

    private void sendChooseList(long chatId, String fromState) {

        FileConfiguration config = plugin.getConfig();

        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow r1 = new KeyboardRow();
        r1.add(config.getString("telegram.buttons.whitelist"));
        r1.add(config.getString("telegram.buttons.blacklist"));
        rows.add(r1);

        KeyboardRow r2 = new KeyboardRow();
        r2.add(config.getString("telegram.buttons.back"));
        rows.add(r2);

        kb.setKeyboard(rows);

        String prompt = "";
        if (CHOOSE_LIST_ADD.equals(fromState)) prompt = config.getString("telegram.messages.select-list-add");
        if (CHOOSE_LIST_DEL.equals(fromState)) prompt = config.getString("telegram.messages.select-list-remove");

        SendMessage msg = new SendMessage(String.valueOf(chatId), prompt);
        parse(msg);
        msg.setReplyMarkup(kb);
        safeSend(msg);
    }

    private void sendMsg(long chatId, String text) {
        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        parse(msg);
        safeSend(msg);
    }


    private void safeSend(SendMessage msg) {
        try { execute(msg); } catch (TelegramApiException e) { e.printStackTrace(); }
    }

    @Override
    public String getBotUsername() {
        return " ";
    }

    @Override
    public String getBotToken() {
        return plugin.getConfig().getString("telegram.token");
    }

    private void parse(SendMessage text) {

        FileConfiguration config = plugin.getConfig();

        if (Objects.requireNonNull(config.getString("telegram.parse-mode"))
                .equalsIgnoreCase("MARKDOWN")) {
            text.setParseMode("Markdown");
        } else if (Objects.requireNonNull(config.getString("telegram.parse-mode"))
                .equalsIgnoreCase("MARKDOWN2")) {
            text.setParseMode("MarkdownV2");
        } else if (Objects.requireNonNull(config.getString("telegram.parse-mode"))
                .equalsIgnoreCase("HTML")) {
            text.setParseMode("HTML");
        } else {
            // Null
        }

    }

}
