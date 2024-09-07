package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "TELEGRAM_BOT_TOKEN"; //TODO: añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "OPENAI  _TOKEN"; //TODO: añadir el token de ChatGPT entre comillas

    private DialogMode mode;
    private ChatGPTService ChatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    private ArrayList<String> list = new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //TODO: escribiremos la funcionalidad principal del bot aquí

    public void start_command(){
        mode = DialogMode.MAIN;
        String text=loadMessage("main");
        sendPhotoMessage("main");
        sendTextMessage(text);

        showMainMenu(
                "start", "menú principal del bot",
                "profile", "generación de perfil de Tinder \uD83D\uDE0E",
                "opener", "mensaje para iniciar conversación \uD83E\uDD70",
                "message", "correspondencia en su nombre \uD83D\uDE08",
                "date", "correspondencia con celebridades \uD83D\uDD25",
                "gpt", "hacer una pregunta a chat GPT \uD83E\uDDE0"
        );
    }

    public void gpt_command(){
        mode = DialogMode.GPT;

        String text = loadMessage("gpt");
        sendPhotoMessage("gpt");
        sendTextMessage(text);
    }

    public void gpt_dialog(){
        String text = getMessageText();
        String prompt = "Responde a esto de manera clara y concisa por favor: ";
        String answer = ChatGPT.sendMessage(prompt, text);
        var myMessage = sendTextMessage("GPT is typing...");
        //sendTextMessage(answer);
        updateTextMessage(myMessage, answer);
    }

    public void date_command (){
        mode = DialogMode.DATE;

        String text = loadMessage("date");
        sendPhotoMessage("date");
        sendTextMessage(text);
        sendTextButtonsMessage(text, "date_grande", "Ariana Grande \uD83D\uDD25", "date_robbie", "Margot Robbie \uD83D\uDD25\uD83D\uDD25", "date_zendaya", "Zendaya \uD83D\uDD25\uD83D\uDD25\uD83D\uDD25",
                "date_gosling", "Ryan Gosling \uD83D\uDE0E (dificultad 7/10)", "date_hardy", "Tom Hardy \uD83D\uDE0E\uD83D\uDE0E");
    }

    public void date_button (){
        String key = getButtonKey();
        sendPhotoMessage(key);
        sendHtmlMessage(key);

        String prompt = loadPrompt(key);
        ChatGPT.setPrompt(prompt);
    }

    public void date_dialog (){
        var myMessage = sendTextMessage("User is typing...");
        String text = getMessageText();
        String answer = ChatGPT.addMessage(text);
        //sendTextMessage(answer);
        updateTextMessage(myMessage, answer);
    }

    public void message_command (){
        mode = DialogMode.MESSAGE;
        String text = loadMessage("message");
        sendPhotoMessage("message");
        sendTextButtonsMessage(text, "message_next", "Write next message.",
                "message_date", "Ask the person out on a date.");

        list.clear();
    }

    public void message_button (){
        String key = getButtonKey();
        String prompt = loadPrompt(key);
        String history = String.join("\n\n", list);

        var myMessage = sendTextMessage("GPT is typing...");
        String answer = ChatGPT.sendMessage(prompt, history);
        updateTextMessage(myMessage, answer);
    }

    public void message_dialog (){
        String text =   getMessageText();
        list.add(text);
    }

    public void profileCommand() {
        mode = DialogMode.PROFILE;
        String text = loadMessage("profile");
        sendPhotoMessage("profile");
        sendTextMessage(text);

        sendTextMessage("Write your name: ");
        user = new UserInfo();
        questionCount = 0;
    }

    private UserInfo user = new UserInfo();
    private int questionCount = 0;

    public void profileDialog() {
        String text = getMessageText();
        questionCount++;
        if (questionCount == 1) {
            user.name = text;
            sendTextMessage("What is your age: ");
        } else if (questionCount == 2) {
            user.age = text;
            sendTextMessage("What is your hobby: ");
        } else if (questionCount == 3) {
            user.hobby = text;
            sendTextMessage("What is your goal in life: ");
        } else if (questionCount == 4) {
            user.goals = text;
            sendTextMessage("What do you do for a living: ");
        } else if (questionCount == 5) {
            user.occupation = text;

            String prompt = loadPrompt("profile");
            String userInfo = user.toString();
            var myMessage = sendTextMessage("Chat GPT is typing...");
            String answer = ChatGPT.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);
        }
    }

    public void openerCommand() {
        mode = DialogMode.OPENER;
        String text = loadMessage("opener");
        sendPhotoMessage("opener");
        sendTextMessage(text);

        sendTextMessage("What is their name: ");
        user = new UserInfo();
        questionCount = 0;
    }

    public void openerDialog() {
        String text = getMessageText();
        questionCount++;

        if (questionCount == 1) {
            user.name = text;
            sendTextMessage("What is their age: ");
        } else if (questionCount == 2) {
            user.age = text;
            sendTextMessage("What is their hobby: ");
        } else if (questionCount == 3) {
            user.hobby = text;
            sendTextMessage("What is their goal in life: ");
        } else if (questionCount == 4) {
            user.goals = text;
            sendTextMessage("What do they do for a living: ");
        } else if (questionCount == 5) {
            user.occupation = text;

            String prompt = loadPrompt("opener");
            String userInfo = user.toString();
            var myMessage = sendTextMessage("Chat GPT is typing...");
            String answer = ChatGPT.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);
        }
    }

    public void hello(){
        if (mode == DialogMode.GPT) {
            gpt_dialog();
        } else if (mode == DialogMode.DATE) {
            date_dialog();
        } else if (mode == DialogMode.MESSAGE) {
            message_dialog();
        } else if (mode == DialogMode.OPENER) {
            openerDialog();
        } else if (mode == DialogMode.PROFILE) {
            profileDialog();
        } else {

            String text = getMessageText();
            sendTextMessage("*Hello*");
            sendTextMessage("_How are you?_");
            sendTextMessage("You wrote: " + text);

            sendPhotoMessage("avatar_main");
            sendTextButtonsMessage("Launch process", "start", "Empezar", "stop", "Parar");
            sendTextButtonsMessage("¿Necesitas ayuda?", "ayuda", "Sí, ayúdame", "tonoto", "Tonto.");
        }
    }

    public void helloButton(){
        String key = getButtonKey();
        if (key.equals("start")){
            sendTextMessage("The process has been launched.");
        } else if (key.equals("stop")) {
            sendTextMessage("The process has been stopped.");
        }
    }

    public void ayuda_button(){
        String key = getButtonKey();
        if (key.equals("ayuda")){
            sendTextMessage("Ahorita no grax.");
        } else if (key.equals("tonoto")) {
            sendTextMessage("Tonoto.");
            sendPhotoMessage("tonoto");
        }
    }

    public void poetry(){
        sendTextMessage("I see that you have a good taste in art. Let me recite you a poem:");
        sendTextMessage("_Amor empieza por desasosiego,\n" +  "solicitud, ardores y desvelos;\n" +  "crece con riesgos, lances y recelos;\n" +
                "susténtase de llantos y de ruego.\n" + "\n" + "Doctrínanle tibiezas y despego,\n" + "conserva el ser entre engañosos velos,\n" +
                "hasta que con agravios o con celos\n" + "apaga con sus lágrimas su fuego.\n" + "\n" +
                "Su principio, su medio y fin es éste:\n" + "¿pues por qué, Alcino, sientes el desvío\n" + "de Celia, que otro tiempo bien te quiso?\n" +
                "\n" + "¿Qué razón hay de que dolor te cueste?\n" + "Pues no te engañó amor, Alcino mío,\n" + "sino que llegó el término preciso._");
        sendPhotoMessage("sorJuana");
        sendHtmlMessage("https://www.zendalibros.com/5-poemas-sor-juana-ines-la-cruz/");
    }

    @Override
    public void onInitialize() {
        addCommandHandler("poetry", this::poetry);
        addCommandHandler("start", this::start_command);
        addCommandHandler("gpt", this::gpt_command);
        addCommandHandler("date", this::date_command);
        addCommandHandler("message", this::message_command);
        addCommandHandler("profile", this::profileCommand);
        addCommandHandler("opener", this::openerCommand);
        addMessageHandler(this::hello);

    //    addButtonHandler("^.*", this::helloButton);
    //    addButtonHandler("^.*", this::ayuda_button);
        addButtonHandler("date_.*", this::date_button);
        addButtonHandler("message_.*", this::message_button);

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
