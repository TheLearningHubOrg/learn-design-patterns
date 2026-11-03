package org.thelearninghub.patterns.solid;

/**
 * The Liskov-safe answer to the Rectangle/Square problem: no inheritance between the two,
 * no setters, and a closed set of shapes the compiler knows about.
 */
public sealed interface Shape permits Shape.Rectangle, Shape.Square {

    long area();

    record Rectangle(long width, long height) implements Shape {
        @Override
        public long area() {
            return width * height;
        }

        public Rectangle withWidth(long newWidth) {
            return new Rectangle(newWidth, height);
        }
    }

    record Square(long side) implements Shape {
        @Override
        public long area() {
            return side * side;
        }
    }
}
