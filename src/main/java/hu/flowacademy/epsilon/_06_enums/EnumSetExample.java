package hu.flowacademy.epsilon._06_enums;

import java.awt.*;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

public class EnumSetExample {
    enum Style {
        BOLD("Bold"),
        ITALIC("Italic"),
        UNDERLINE("Underline"),
        STRIKETHROUGH("Strikethrough");

        private final String description;

        private Style(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    static class Span {
        EnumSet<Style> style;
        Color color;
        String text;
    }

    static class Paragraph {
        List<Span> spans;
    }

    public static void main(String[] args) {
        var textStyle = EnumSet.of(Style.BOLD, Style.ITALIC);
        textStyle.contains(Style.ITALIC);
        Style.ITALIC.getDescription();
   }
}
