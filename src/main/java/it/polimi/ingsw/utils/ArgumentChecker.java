package it.polimi.ingsw.utils;

public class ArgumentChecker {
    public static void checkArguments(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments\nArguments should be:\n - connection type (socket/rmi)\n - server IP (x.x.x.x)");
            System.exit(-1);
        }

        String connectionType = args[0].toLowerCase();
        if(!connectionType.equals("rmi") && !connectionType.equals(("socket"))){
            System.out.println("Invalid connection type. Valid values are: socket, rmi");
            System.exit(-1);
        }

        String IP = args[1].toLowerCase();

        boolean validIP = true;
        if (!IP.equals("localhost")) {
            String[] numbers = IP.split("\\.");
            if (numbers.length != 4) {
                validIP = false;
            } else {
                for (String number : numbers) {
                    try {
                        if (Integer.parseInt(number) < 0 || Integer.parseInt(number) > 255)
                            validIP = false;
                    } catch (NumberFormatException e) {
                        validIP = false;
                    }
                }
            }
        }
        if (!validIP) {
            System.out.println("Invalid server IP address");
            System.exit(-1);
        }
    }
}
