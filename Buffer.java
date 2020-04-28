import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Buffer {
    private final int MaxBuffSize;
    private final String[] store;
    private int BufferStart, BufferEnd, BufferSize;

    public Buffer(final int size) {
        MaxBuffSize = size;
        BufferEnd = -1;
        BufferStart = 0;
        BufferSize = 0;
        store = new String[MaxBuffSize];
    }

    public synchronized void insert(final String ch) {
        try {
            while (BufferSize == MaxBuffSize) {
                wait();
            }
            BufferEnd = (BufferEnd + 1) % MaxBuffSize;
            store[BufferEnd] = ch;
            BufferSize++;
            notifyAll();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized String delete() {
        try {
            while (BufferSize == 0) {
                wait();
            }
            final String ch = store[BufferStart];
            BufferStart = (BufferStart + 1) % MaxBuffSize;
            BufferSize--;
            notifyAll();
            return ch;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return "END";
        }
    }
}

class Consumer extends Thread {

    private final Buffer buffer;
    private final int order;
    private final String name;

    public Consumer(final Buffer b, final int num, final String company) {
        buffer = b;
        order = num;
        name = company;
    }

    public void run() {
        try {
            sleep(5000);
        } catch (final InterruptedException ex) {
            Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < order; i++) {
            try {
                sleep(5000);
            } catch (final InterruptedException ex) {
                Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
            }
            final String c = buffer.delete();
            System.out.println("\n" + name + " ได้รับอาหาร" + " : " + c);

        }
        System.out.println("ได้รับออเดอร์ครบถ้วนแล้ว");
        System.out.println("==============================");
    }
}

class Producer extends Thread {
    private final Buffer buffer;
    private final String name;

    private final String[] food;

    public Producer(final Buffer b, final String[] menu, final String conpany) {
        buffer = b;
        food = menu;
        name = conpany;

    }

    public void run() {
        for (int i = 0; i < food.length; i++) {
            System.out.println(name + " " + " : " + food[i]);
            buffer.insert(food[i]);
        }

        while (true) {
        }
    }
}

class BoundedBuffer {

    public static void main(final String[] args) {
        System.out.println("-----------------------------");
        System.out.println("W e l c o m e  :-) 🍛");
        String[] menu1 = { "ข้าวมันไก่", "ข้าวผัด", "ข้าวขาหมู", "ข้าวหมูแดง", "ก๋วยเตี๋ยวไก่", "หมูตุ๋น", "ต้มยำกุ้ง",
                "แกงเขียวหวานม,ข้าวคลุกกะปิ" };
        String[] menu2 = { "ไก่กระเทียม", "ราดหน้า", "ผัดซีอิ้ว", "หมูกรอบ", "พะโล้", "ราเมง", "ผัดเผ็ดพริกหมู",
                "แกงไตปลา,แกงฮังเร" };
        // random x
        Random rand = new Random();
        int max = 10;
        int min = 1;
        int rand_int1 = rand.nextInt((max - min) + 1) + min;
        int rand_int2 = rand.nextInt((max - min) + 1) + min;
        ;
        System.out.println("Grab Food รับออเดอร์ : " + rand_int1 + " เมนู");
        System.out.println("Panda Food รับออเดอร์ : " + rand_int2 + " เมนู");
        System.out.println("-----------------------------");
        final Buffer buffer = new Buffer(4); // buffer has size 5
        final Producer prod1 = new Producer(buffer, menu1, "cheif 1 cooking ");
        final Producer prod2 = new Producer(buffer, menu2, "cheif 2 cooking ");
        final Consumer cons = new Consumer(buffer, rand_int1, "Grab");
        final Consumer cons2 = new Consumer(buffer, rand_int2, "Foodpanda");
        prod1.start();
        prod2.start();
        cons.start();
        cons2.start();

        try {
            prod1.join();
            prod2.join();
            cons.interrupt();
            cons2.interrupt();
        } catch (final InterruptedException e) {
        }

        System.out.println("End of Program");
    }
}