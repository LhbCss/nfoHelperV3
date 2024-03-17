package Constant;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UtilAid {
    private static final SimpleDateFormat consoleTimeFormat = new SimpleDateFormat(" HH:mm:ss:SSS - ");
    public static void warnConsole(String text) {
        System.out.println("[严重]" + consoleTimeFormat.format(new Date()) + text);
    }

    public static void infoConsole(String text) {
        System.out.println("[INFO]" + consoleTimeFormat.format(new Date()) + text);
    }
}
