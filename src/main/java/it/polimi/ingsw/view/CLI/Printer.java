package it.polimi.ingsw.view.CLI;

public class Printer {

    public void clearScreen() {
        System.out.print("\n".repeat(60));
    }

    public void print(String s){
        System.out.println(s);
    }

}
