package util;
/*
作用：
存放所有管道相关的字符常量（如墙、箭头、直线、拐角等）。
*/
/**
 * A collection of all characters for pipe patterns.
 */
public final class PipePatterns {

    private PipePatterns() {
        // You don't need to instantiate this class to use it!
    }

    public static final char WALL = '\u2588';

    public static class Filled {
        public static final char UP_ARROW = '\u25B2';
        public static final char DOWN_ARROW = '\u25BC';
        public static final char LEFT_ARROW = '\u25C0';
        public static final char RIGHT_ARROW = '\u25B6';

        public static final char HORIZONTAL = '\u2501';
        public static final char VERTICAL = '\u2503';
        public static final char TOP_LEFT = '\u251B';
        public static final char TOP_RIGHT = '\u2517';
        public static final char BOTTOM_LEFT = '\u2513';
        public static final char BOTTOM_RIGHT = '\u250F';
        public static final char CROSS = '\u254B';
    }

    public static class Unfilled {
        public static final char UP_ARROW = '\u25B3';
        public static final char DOWN_ARROW = '\u25BD';
        public static final char LEFT_ARROW = '\u25C1';
        public static final char RIGHT_ARROW = '\u25B7';

        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u255D';
        public static final char TOP_RIGHT = '\u255A';
        public static final char BOTTOM_LEFT = '\u2557';
        public static final char BOTTOM_RIGHT = '\u2554';
        public static final char CROSS = '\u256C';
    }
}
