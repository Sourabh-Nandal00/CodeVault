public class SnakeGame {
    public static void main(String[] args) {
        System.out.println("Welcome to the Snake Game!!");
        System.out.println("Use W, A, S, D to move. Type Q to quit.");

        SnakeGameLogic snakeGame = new SnakeGameLogic();
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (snakeGame.isRunning()) {
            snakeGame.render();
            System.out.print("Your move: ");
            String input = scanner.nextLine().trim().toLowerCase();
            snakeGame.changeDirection(input);
            snakeGame.update();
        }

        System.out.println("Game over! Score: " + snakeGame.getScore());
        System.out.println("Thanks for playing!");
    }

    private static class SnakeGameLogic {
        private final int width = 20, height = 10;
        private final java.util.Deque<int[]> snake = new java.util.ArrayDeque<>();
        private final java.util.Random random = new java.util.Random();
        private int foodX, foodY, dx = 1, dy, score;
        private boolean running = true;

        SnakeGameLogic() {
            snake.add(new int[] { width / 2, height / 2 });
            placeFood();
        }

        boolean isRunning() {
            return running;
        }

        int getScore() {
            return score;
        }

        void changeDirection(String input) {
            if (input.equals("q")) {
                running = false;
            } else if (input.equals("w") && dy == 0) {
                dx = 0;
                dy = -1;
            } else if (input.equals("s") && dy == 0) {
                dx = 0;
                dy = 1;
            } else if (input.equals("a") && dx == 0) {
                dx = -1;
                dy = 0;
            } else if (input.equals("d") && dx == 0) {
                dx = 1;
                dy = 0;
            }
        }

        void update() {
            int[] head = snake.peekFirst();
            int[] next = { head[0] + dx, head[1] + dy };
            if (next[0] < 0 || next[0] >= width || next[1] < 0 || next[1] >= height || contains(next)) {
                running = false;
                return;
            }
            snake.addFirst(next);
            if (next[0] == foodX && next[1] == foodY) {
                score++;
                placeFood();
            } else {
                snake.removeLast();
            }
        }

        void render() {
            System.out.println();
            System.out.print("+");
            for (int x = 0; x < width; x++) {
                System.out.print("--");
            }
            System.out.println("+");

            for (int y = 0; y < height; y++) {
                System.out.print("|");
                for (int x = 0; x < width; x++) {
                    char cell = (x == foodX && y == foodY) ? '*' : ' ';
                    for (int[] part : snake) {
                        if (part[0] == x && part[1] == y) cell = '#';
                    }
                    System.out.print(cell + " ");
                }
                System.out.println("|");
            }

            System.out.print("+");
            for (int x = 0; x < width; x++) {
                System.out.print("--");
            }
            System.out.println("+");
            System.out.println("Score: " + score + " | Food: * | Snake: #");
        }

        private boolean contains(int[] position) {
            for (int[] part : snake) {
                if (part[0] == position[0] && part[1] == position[1]) return true;
            }
            return false;
        }

        private void placeFood() {
            do {
                foodX = random.nextInt(width);
                foodY = random.nextInt(height);
            } while (contains(new int[] { foodX, foodY }));
        }
    }
}