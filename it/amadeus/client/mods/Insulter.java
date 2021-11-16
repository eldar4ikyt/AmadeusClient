package it.amadeus.client.mods;

import it.amadeus.client.clickgui.util.values.valuetypes.ModeValue;
import it.amadeus.client.clickgui.util.values.valuetypes.NumberValue;
import it.amadeus.client.event.Event;
import it.amadeus.client.event.events.Update;
import it.amadeus.client.module.Module;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Insulter extends Module {

    private static final ArrayList<String> players = new ArrayList<>();
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.ENGLISH, this);
    private final NumberValue<Double> delay = new NumberValue<>("Chat Speed", 87.0D, 5.0D, 250.0D, this);
    private final String[] PORTUGUES_LIST = new String[]{"deve ir para um campo de concentração merdas", "hax lixo a sua irmã aquela cabra", "KKKKKKKKKKKKKKKKKK hax lixo rekt eZ noob merda brazil",
            "dar um tiro na boca. brazil merda", "és pobre como a merda, não tens merda nenhuma", "Pare de se queixar dos trapaceiros de crianças autistas",
            "lixo você e a sua família de ratos imundos", "assusta a merda do EZZZZZZZZZZZZZ", "lixo hax merda!11!!!! Vai-te foder, seu pedaço de merda brasileiro!",
            "BR Raça inferior", "Vai-te embora daqui, És uma merda", "a sua prostituta mãe BR inferior", "espero que sua familia morra", "Eu vou esmagar você como um rato. Merda!", "Pare de chorar, seu filho da puta perdido, seu merdas.",
            "lixo lixo lixO!!11! relatar este maldito demente", "você é um aborto, você deixa sua mãe doente. AHHAHAHAH"};
    private final String[] ENGLISH_LIST = new String[]{"here's your tickets to the juice wrld concert", "hey look! it's a fortnite player", "i bet you probably shop at Costco", "i don't cheat, you just need to click faster",
            "i hope you fall off a cliff", "even something like you can win with that", "i speak english not your gibberish", "i understand why your parents abused you", "i'd tell you to uninstall, but your aim is so bad you wouldn't hit the button",
            "im not saying you're worthless, but i'd unplug ur life support to charge my phone", "need some pvp advice?", "you do be lookin' kinda bad at the game", "you look like you were drawn with my left hand", "you pressed the wrong button when you installed Minecraft",
            "you should look into buying a client", "you're so white that you don't play on vanilla, you play on clear", "your difficulty settings must be stuck on easy", "drown in your own salt", "even your mom is better than you in this game",
            "go back to fortnite you degenerate", "go commit stop breathing plz", "go play roblox you worthless fucking degenerate", "go take a long walk on a short bridge", "i swear on jhalt, you got shit on hard than archy",
            "if the body is 70% water then how is your body 100% salt?", "lol you probably speak dog eater", "mans probably got an error on his hello world program lmao", "mans probably plays fortnite lmao", "no top hat, you're more trash than my garbage can",
            "plz no repotr i no want ban plesae!", "final come home", "report me im really scared", "seriously? go back to fortnite monkey brain", "some kids were dropped at birth, but you got thrown at the wall", "you really like taking L's",
            "you're the type of guy to quickdrop irl", "you're the type to get 3rd place in a 1v1", "your iq is that of a steve", "your parents abandoned you, then the orphanage did the same", "you go to the doctors and they say you shrunk",
            "princekin, drop kicking lil kids since 2017", "who would win? $400,000 per year anticheat or a single packet", "is watchdog watching a dog or a dog watching a watch?", "yo mama so fat, she sat on an iphone and it became an ipad",
            "on black friday, black people die", "search up blue waffle on google, it's so cute", "your skills are disabled as you, fucking vegetable", "you smell like a moldy ballsack", "your grandmother has chlamydia",
            "your skills are like a toddler with parkinson's trying to aim a water gun", "welcome to my basement", "i'd insult you after that death but it's funnier to let you imagine what i'm calling you", "yo whens the documentary crew coming to your house to film the next episode of my 600 pound life?",
            "you have the iq of a table", "you are the type of person to think FOV increases reach", "you're so gay you bought the iphone 5c instead of a newer phone because of the colors", "your iq is the same of a rock",
            "you probably bought vape v4", "you shouldn't be running away with all these monkeys coming after you", "yes, record me, send the footage straight to child lover tenebrous", "your killaura was coded in scratch with help from zhn",
            "you deserved to be bhopped on", "your birth certificate was an apology letter from the condom factory", "always remember you're unique - just like everyone else", "how do you keep an idiot amused? watch this message until it fades away",
            "if practice makes perfect, and nobody's perfect, why practice?", "if i could rearrange the alphabet, i'd put U and I as far away as possible", "i'd smack you, but that would be animal abuse",
            "if i wanted to kill myself, i'd climb your ego and jump to your IQ", "this kid is so annoying, he made his happy meal cry", "your face makes onions cry", "you are like a cloud, when you disappear it's a beautiful day",
            "you bring everyone so much joy! you know, when you leave the room. but, still", "you are missing a brain", "are you a primate?", "you're so ugly your portraits hang themselves"};
    private final String[] ITALIAN_LIST = new String[]{"fai cosi cagare che in un 1vs1 arrivi terzo", "sei un cazzo di vegetale, il pvp non fa per te", "non sei portato, proprio come colui che e' nato nel 98.", "da quando gli stronzi fanno pvp?",
            "sei di un patetico che fa spavento", "la tua faccia e' pari alle tue skill in pvp", "l'anticheat l'avete trovato su blackspigot?", "come sei uscito dal libro della giungla?", "omar ti ha venduto la bamba tarocca", "credo di aver capito il motivo della tua calvizia", "sei un completo disabile, sembri una scimmia"
            , "non so se ha piu' peli malachiel o le tue ascelle", "stai aspettando malachiel per il corso di pedofilia?", "immagina giocare legit su sto server", "immagina essere un cazzo di terrone", "spostati devo parlare con malachiel, sono il capo del crack.", "il tuo naso sempra un aspirapolvere", "ma sei paralizzato o sei 2008?"
            , "io l'ho sempre detto che comprando le mozzarelle blu della lidl si diventa come te", "non un cazzo, nasconditi", "sei stato adottato, mi dispiace dirtelo", "sei come una nuola, quando sparisci e' un bellissimo giorno", "ho finito le scorte di bianca, ci vediamo settimana prossima", "che confusione la raccolta differenziata, non so dove buttarti xo",
            "sei talmente brutto che a carnevale non ti serve travestirti", "serio?! ti hanno gia' accettato la richiesta per la pensione di invalidita'!?", "sei una donna per tanto non hai diritti lol"};

    private List<String> getAllPlayers() {
        NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        for (Object o : nethandlerplayclient.getPlayerInfoMap()) {
            NetworkPlayerInfo NPI = (NetworkPlayerInfo) o;
            if (!NPI.getGameProfile().getName().equalsIgnoreCase(mc.getSession().getUsername())) {
                players.add(NPI.getGameProfile().getName());
            }
        }
        return players;
    }

    @Override
    public String getName() {
        return "Insulter";
    }

    @Override
    public String getDescription() {
        return "Insulta i giocatori a casaccio";
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
        if (event instanceof Update) {
            if(mc.isSingleplayer())return;
            if (mc.thePlayer.ticksExisted % this.delay.getValue().intValue() == 0) {
                switch (this.mode.getValue()) {
                    case ENGLISH:
                        mc.thePlayer.sendChatMessage(getAllPlayers().get(RandomUtils.nextInt(0, getAllPlayers().size() -1)) + ENGLISH_LIST[RandomUtils.nextInt(0, ENGLISH_LIST.length - 1)]);
                        break;
                    case ITALIAN:
                        mc.thePlayer.sendChatMessage(getAllPlayers().get(RandomUtils.nextInt(0, getAllPlayers().size() -1)) + ITALIAN_LIST[RandomUtils.nextInt(0, ITALIAN_LIST.length - 1)]);
                        break;
                    case PORTUGUES:
                        mc.thePlayer.sendChatMessage(getAllPlayers().get(RandomUtils.nextInt(0, getAllPlayers().size() -1)) + PORTUGUES_LIST[RandomUtils.nextInt(0, PORTUGUES_LIST.length - 1)]);
                        break;
                }
            }
        }
    }

    private enum Mode {ITALIAN, PORTUGUES, ENGLISH}
}
