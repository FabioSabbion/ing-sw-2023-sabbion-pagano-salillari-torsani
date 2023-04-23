package it.polimi.ingsw.view.CLI.utils;

/**
 * Contains the ANSI escape code for command line colors
 */
public enum Color {

    GREEN ("\u001B[32m"),
    PURPLE ("\u001B[35m"),
    RED("\u001B[31m"),
    YELLOW("\u001B[33m"),
    GRAY("\u001B[0;37m"),
    WHITE("\u001B[37m"),
    CYAN("\033[96m"),
    BLUE("\u001B[34m");




    public static final String RESET = "\u001B[0m";

    private String escape;

    Color(String escape) {
        this.escape = escape;
    }

    /**
     * @return ANSI escape for the color.
     */
    public String escape() {
        return escape;
    }
}
