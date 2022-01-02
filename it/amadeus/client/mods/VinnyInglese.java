package it.amadeus.client.mods;

import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.ChatMessage;
import it.amadeus.client.event.events.PacketReceive;
import it.amadeus.client.module.Module;
import it.amadeus.client.utilities.ChatUtil;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;

public final class VinnyInglese extends Module {

    private String fromTranslate;

    private String toTranslate;

    @Override
    public String getName() {
        return "Translate";
    }

    @Override
    public String getDescription() {
        return "Vinny Non Sa l'inglese";
    }

    @Override
    public int getKey() {
        return 0;
    }

    @Override
    public Category getCategory() {
        return Category.FUN;
    }


    @Override
    public void onEvent(Event event) {
        if (event instanceof ChatMessage) {
            String message = ((ChatMessage) event).getMessage();
            if (message.startsWith("#t")) {
                ((ChatMessage) event).setCancelled(true);
                String[] args = message.replaceFirst("#t", "").trim().split(" ");
                if (args.length == 3 && args[0].equalsIgnoreCase("language")) {
                    this.fromTranslate = args[1];
                    this.toTranslate = args[2];
                    ChatUtil.print("§bFrom Language §e" + this.fromTranslate);
                    ChatUtil.print("§bTo Language §e" + this.toTranslate);
                } else if (args.length > 0) {
                    if (this.toTranslate == null || this.fromTranslate == null) {
                        ChatUtil.print("§6set the Languages: #t language <From> <To>");
                        return;
                    }
                    StringBuilder msg = new StringBuilder();
                    for (String arg : args)
                        msg.append(arg).append(" ");
                    String finalMsg = msg.toString();
                    (new Thread(() -> {
                        String translation = getTranslation(this.fromTranslate, this.toTranslate, finalMsg);
                        if (translation != null)
                            mc.thePlayer.sendChatMessage(translation);
                    })).start();
                } else {
                    ChatUtil.print("#t <Message...>");
                    ChatUtil.print("#t language <From> <To>");
                }
            }
        } else if (event instanceof PacketReceive) {
            if (this.toTranslate == null || this.fromTranslate == null)
                return;
            if (((PacketReceive) event).getPacket() instanceof S02PacketChat) {
                S02PacketChat chat = (S02PacketChat) ((PacketReceive) event).getPacket();
                String messageRaw = chat.getChatComponent().getUnformattedText();
                String[] split = messageRaw.split(" ");
                for (int i = 0; i < split.length &&
                        i < 3; i++) {
                    if (split[i].contains(mc.thePlayer.getName()))
                        return;
                }
                String messageFormatted = chat.getChatComponent().getFormattedText();
                ((PacketReceive) event).setCancelled(true);
                (new Thread(() -> sendTranslation(messageRaw))).start();
            }
        }
    }

    private String getTranslation(String from, String to, String totranslate) {
        String translation = null;
        try {
            translation = com.darkprograms.speech.translator.GoogleTranslate.translate(from, to, totranslate);
        } catch (Exception ex) {
            ex.printStackTrace();
            ChatUtil.print("error while translating " + this.toTranslate + " from " + from + " to " + to + "!");
        }
        return translation;
    }

    private void sendTranslation(String from) {
        String translation = getTranslation(this.toTranslate, this.fromTranslate, from);
        if (translation != null) mc.thePlayer.addChatMessage(new ChatComponentText(translation));
    }
}
