package dev.lemonos;

import java.util.ArrayList;
import java.util.List;

final class BackendPasscodeLayout {
    private static final int[] DIGIT_SLOTS = new int[]{2, 3, 4, 11, 12, 13, 20, 21, 22, 23};
    private static final String[] DIGIT_LABELS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    List<DigitButton> digitButtons() {
        List<DigitButton> buttons = new ArrayList<DigitButton>();
        for (int i = 0; i < DIGIT_SLOTS.length; ++i) {
            buttons.add(new DigitButton(DIGIT_SLOTS[i], DIGIT_LABELS[i]));
        }
        return List.copyOf(buttons);
    }

    Integer digit(int slot) {
        for (int i = 0; i < DIGIT_SLOTS.length; ++i) {
            if (DIGIT_SLOTS[i] == slot) {
                return Integer.parseInt(DIGIT_LABELS[i]);
            }
        }
        return null;
    }

    record DigitButton(int slot, String label) {
    }
}
