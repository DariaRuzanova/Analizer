import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final int NUMBER_OF_TEXTS = 10_000;
    public static final int LENGTH_TEXT = 100_000;
    public static final String SYMBOLS = "abc";
    public static final int LIMIT = 100;

    public static final int QUEUE_COUNT = 3;
    public static final List<BlockingQueue<String>> queues = new ArrayList<>();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < QUEUE_COUNT; i++) {
            BlockingQueue<String> queue = new ArrayBlockingQueue<>(LIMIT);
            queues.add(queue);

            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < NUMBER_OF_TEXTS; j++) {
                        queue.put(generateText(SYMBOLS, LENGTH_TEXT));
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            
            threads.add(thread);
        }

        Thread thread1 = new Thread(() -> {
            int max = findMax(queues.get(0), 'a');
            System.out.println("Текст, в котором содержится максимальное количество символов 'а': " + max);
        });
        threads.add(thread1);

        Thread thread2 = new Thread(() -> {
            int max = findMax(queues.get(1), 'b');
            System.out.println("Текст, в котором содержится максимальное количество символов 'b': " + max);
        });
        threads.add(thread2);

        Thread thread3 = new Thread(() -> {
            int max = findMax(queues.get(2), 'c');
            System.out.println("Текст, в котором содержится максимальное количество символов 'c': " + max);
        });
        threads.add(thread3);

        for (Thread thread : threads) {
            thread.start();
        }
    }

    public static int findMax(BlockingQueue<String> blockingQueue, char letter) {
        int maxCount = 0;

        try {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                String text = blockingQueue.take();
                int count = 0;
                for (int j = 0; j < LENGTH_TEXT; j++) {
                    if (text.charAt(j) == letter) {
                        count++;
                    }
                }
                if (maxCount < count) {
                    maxCount = count;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return maxCount;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
