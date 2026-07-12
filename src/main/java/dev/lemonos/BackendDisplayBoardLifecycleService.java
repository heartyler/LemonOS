/*
 * Backend-side LemonOS display board lifecycle helpers.
 */
package dev.lemonos;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.TextDisplay;

final class BackendDisplayBoardLifecycleService {
    TextDisplay findUniqueDisplay(World world, String role, Function<TextDisplay, String> roleReader) {
        TextDisplay selected = null;
        for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
            String displayRole = roleReader.apply(textDisplay);
            if (!role.equals(displayRole)) {
                continue;
            }
            if (selected == null) {
                selected = textDisplay;
                continue;
            }
            textDisplay.remove();
        }
        return selected;
    }

    void clearDisplays(Iterable<World> worlds, Function<TextDisplay, String> roleReader, Predicate<String> rolePredicate) {
        for (World world : worlds) {
            for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
                String displayRole = roleReader.apply(textDisplay);
                if (displayRole != null && rolePredicate.test(displayRole)) {
                    textDisplay.remove();
                }
            }
        }
    }

    void clearDisplayBoard(World world, Location location, Function<TextDisplay, String> roleReader, Predicate<String> rolePredicate, BiPredicate<TextDisplay, Location> nearBasePredicate) {
        for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
            String displayRole = roleReader.apply(textDisplay);
            if (displayRole != null) {
                if (rolePredicate.test(displayRole)) {
                    textDisplay.remove();
                }
                continue;
            }
            if (nearBasePredicate.test(textDisplay, location)) {
                textDisplay.remove();
            }
        }
    }
}
