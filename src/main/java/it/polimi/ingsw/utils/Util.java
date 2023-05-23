package it.polimi.ingsw.utils;

import java.util.Optional;

public class Util {
    public static <T> T nullOrElse(T nullable, T orElse) {
        return Optional.ofNullable(nullable).orElse(orElse);
    }
}
