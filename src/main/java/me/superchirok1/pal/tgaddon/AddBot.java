package me.superchirok1.pal.tgaddon;

import me.superchirok1.playeraccesslist.AccessLists;
import me.superchirok1.playeraccesslist.lists.Blacklist;
import me.superchirok1.playeraccesslist.lists.Whitelist;
import me.superchirok1.playeraccesslist.PlayerAccessList;
import org.bukkit.Bukkit;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    private final Whitelist whitelist;
    private final Blacklist blacklist;

    public AddBot(Main plugin) {
        this.plugin = plugin;
        this.pal = (PlayerAccessList) Bukkit.getPluginManager().getPlugin("PlayerAccessList");

        this.whitelist = pal.getAccessLists().getWhitelist();
        this.blacklist = pal.getAccessLists().getBlacklist();
    }


    @Override
    public void onUpdateReceived(Update update) {

        ConfigData data = new ConfigData(plugin);

        if (!(update.hasMessage() && update.getMessage().hasText())) return;

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        String not_allowed = data.getElseMessage();

        String username = update.getMessage().getFrom().getUserName();
        List<String> allowed = data.getAllowedUsers();

        if (username == null || !allowed.contains(username)) {
            if (not_allowed.equalsIgnoreCase("null")) {
                return;
            }
            sendMsg(chatId, not_allowed);
            return;
        }

        if ("/start".equalsIgnoreCase(text)) {
            sendMainMenu(chatId, data.getMsgStart());
            return;
        }

        if (state.containsKey(chatId)) {
            String s = state.get(chatId);

            if (CHOOSE_LIST_ADD.equals(s) || CHOOSE_LIST_DEL.equals(s)) {
                if (text.equalsIgnoreCase(data.getBtnWl())) {
                    if (CHOOSE_LIST_ADD.equals(s)) {
                        state.put(chatId, WAIT_ADD_WHITE);
                        sendMsg(chatId, data.getMsgWriteName());
                    } else {
                        state.put(chatId, WAIT_DEL_WHITE);
                        sendMsg(chatId, data.getMsgWriteName());
                    }
                    return;
                } else if (text.equalsIgnoreCase(data.getBtnBl())) {
                    if (CHOOSE_LIST_ADD.equals(s)) {
                        state.put(chatId, WAIT_ADD_BLACK);
                        sendMsg(chatId, data.getMsgWriteName());
                    } else {
                        state.put(chatId, WAIT_DEL_BLACK);
                        sendMsg(chatId, data.getMsgWriteName());
                    }
                    return;
                } else if (text.equalsIgnoreCase(data.getBtnBack())) {
                    state.remove(chatId);
                    sendMainMenu(chatId, data.getMsgBack());
                    return;
                } else {
                    sendChooseList(chatId, s);
                    return;
                }
            }

            switch (s) {
                case WAIT_ADD_WHITE -> {
                    if (text.equalsIgnoreCase(data.getBtnBack())) {
                        state.remove(chatId);
                        sendMainMenu(chatId, data.getMsgBack());
                        return;
                    }
                    if (!whitelist.has(text)) {
                        whitelist.add(text);
                        sendMainMenu(chatId, data.getMsgAdded()
                                .replace("{player}", text)
                                .replace("{list}", data.getBtnWl()));
                    } else {
                        sendMainMenu(chatId, data.getMsgAlreadyOnTheList());
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_ADD_BLACK -> {
                    if (text.equalsIgnoreCase(data.getBtnBack())) {
                        state.remove(chatId);
                        sendMainMenu(chatId, data.getMsgBack());
                        return;
                    }
                    if (!blacklist.has(text)) {
                        blacklist.add(text);
                        sendMainMenu(chatId, data.getMsgAdded()
                                .replace("{player}", text)
                                .replace("{list}", data.getBtnBl()));
                    } else {
                        sendMainMenu(chatId, data.getMsgAlreadyOnTheList());
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_DEL_WHITE -> {
                    if (text.equalsIgnoreCase(data.getBtnBack())) {
                        state.remove(chatId);
                        sendMainMenu(chatId, data.getMsgBack());
                        return;
                    }
                    if (whitelist.has(text)) {
                        whitelist.remove(text);
                        sendMainMenu(chatId, data.getMsgRemoved()
                                .replace("{player}", text)
                                .replace("{list}", data.getBtnWl()));
                    } else {
                        sendMainMenu(chatId, data.getMsgNotOnTheList());
                    }

                    state.remove(chatId);
                    return;
                }
                case WAIT_DEL_BLACK -> {
                    if (text.equalsIgnoreCase(data.getBtnBack())) {
                        state.remove(chatId);
                        sendMainMenu(chatId, data.getMsgBack());
                        return;
                    }
                    if (blacklist.has(text)) {
                        blacklist.remove(text);
                        sendMainMenu(chatId, data.getMsgRemoved()
                                .replace("{player}", text)
                                .replace("{list}", data.getBtnBl()));
                    } else {
                        sendMainMenu(chatId, data.getMsgNotOnTheList());
                    }

                    state.remove(chatId);
                    return;
                }
            }
        }

        if (text.equalsIgnoreCase(data.getBtnAdd())) {
            state.put(chatId, CHOOSE_LIST_ADD);
            sendChooseList(chatId, CHOOSE_LIST_ADD);
        }
        else if (text.equalsIgnoreCase(data.getBtnRemove())) {
            state.put(chatId, CHOOSE_LIST_DEL);
            sendChooseList(chatId, CHOOSE_LIST_DEL);
        }
        else if (text.equalsIgnoreCase(data.getBtnList())) {

            sendMsg(chatId, data.getMsgList()
                    .replace("{whitelist}", whitelist.getList().isEmpty()
                            ? Objects.requireNonNull(data.getPlEmpty()) : String.join(", ", whitelist.getList()))
                    .replace("{blacklist}", blacklist.getList().isEmpty()
                            ? Objects.requireNonNull(data.getPlEmpty()) : String.join(", ", blacklist.getList())));

        } else {
            sendMainMenu(chatId, data.getMsgSelectAction());
        }


    }

    private void sendMainMenu(long chatId, String text) {

        ConfigData data = new ConfigData(plugin);

        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow r1 = new KeyboardRow();
        r1.add(data.getBtnAdd());
        r1.add(data.getBtnRemove());
        r1.add(data.getBtnList());
        rows.add(r1);

        kb.setKeyboard(rows);

        SendMessage msg = new SendMessage(String.valueOf(chatId), text);
        parse(msg);
        msg.setReplyMarkup(kb);
        safeSend(msg);
    }

    private void sendChooseList(long chatId, String fromState) {

        ConfigData data = new ConfigData(plugin);

        ReplyKeyboardMarkup kb = new ReplyKeyboardMarkup();
        kb.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow r1 = new KeyboardRow();
        r1.add(data.getBtnWl());
        r1.add(data.getBtnBl());
        rows.add(r1);

        KeyboardRow r2 = new KeyboardRow();
        r2.add(data.getBtnBack());
        rows.add(r2);

        kb.setKeyboard(rows);

        String prompt = "";
        if (CHOOSE_LIST_ADD.equals(fromState)) prompt = data.getMsgSelectListAdd();
        if (CHOOSE_LIST_DEL.equals(fromState)) prompt = data.getMsgSelectListRemove();

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

        ConfigData data = new ConfigData(plugin);

        if (Objects.requireNonNull(data.getParseMode())
                .equalsIgnoreCase("MARKDOWN")) {
            text.setParseMode("Markdown");
        } else if (Objects.requireNonNull(data.getParseMode())
                .equalsIgnoreCase("MARKDOWN2")) {
            text.setParseMode("MarkdownV2");
        } else if (Objects.requireNonNull(data.getParseMode())
                .equalsIgnoreCase("HTML")) {
            text.setParseMode("HTML");
        } else {
            // Null
        }

    }

}
