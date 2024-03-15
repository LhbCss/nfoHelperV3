package Constant;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UtilAid {
    private static final SimpleDateFormat consoleTimeFormat = new SimpleDateFormat(" HH:mm:ss - ");
    public static void warnConsole(String text) {
        System.out.println("[\\u001B[31m" + AnsiColor.RED + "严重" + AnsiColor.DEFAULT + "]" + consoleTimeFormat.format(new Date()) + text);
    }

    public static void infoConsole(String text) {
        System.out.println("#[\\u001B[36mINFO\\u001B[0m]" + consoleTimeFormat.format(new Date()) + text);
    }
}
