import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Main class demonstrating thread-based process simulation and Producer-Consumer synchronization
public class ProcessSimulation {
    public static void main(String[] args) {
        System.out.println("=== Operating Systems Project 2: Thread-Based Process Simulation ===");
        System.out.println("=== Producer-Consumer Problem Implementation ===\n");
        
        // Read processes from file and create threads
        List<ProcessThread> processes = readProcessesFromFile("processes.txt");
        
        System.out.println("=== Simulating Processes as Threads ===");
        for (ProcessThread process : processes) {
            process.start();
        }
        
        // Wait for all process threads to complete
        for (ProcessThread process : processes) {
            try {
                process.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n=== Starting Producer-Consumer Simulation ===");
        runProducerConsumerSimulation();
        
        System.out.println("\n=== Simulation Complete ===");
    }
    
    // Reads process data from file (PID, Arrival_Time, Burst_Time, Priority format)
    private static List<ProcessThread> readProcessesFromFile(String filename) {
        List<ProcessThread> processes = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header line
            }
            
            // Parse each line to create ProcessThread objects
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    int pid = Integer.parseInt(parts[0]);
                    int burstTime = Integer.parseInt(parts[2]); // Burst_Time is 3rd column
                    processes.add(new ProcessThread(pid, burstTime));
                }
            }
            
            System.out.println("Loaded " + processes.size() + " processes from " + filename);
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename + ". Using default processes.");
            
            // Create default processes if file doesn't exist
            processes.add(new ProcessThread(1, 2));
            processes.add(new ProcessThread(2, 3));
            processes.add(new ProcessThread(3, 1));
            processes.add(new ProcessThread(4, 4));
            processes.add(new ProcessThread(5, 2));
            
            System.out.println("Loaded " + processes.size() + " default processes");
        }
        
        return processes;
    }
    
    // Runs Producer-Consumer simulation with semaphores and locks for synchronization
    private static void runProducerConsumerSimulation() {
        BoundedBuffer buffer = new BoundedBuffer(5);
        
        // Create multiple producers and consumers to demonstrate concurrency
        Producer producer1 = new Producer(buffer, 1);
        Producer producer2 = new Producer(buffer, 2);
        Consumer consumer1 = new Consumer(buffer, 1);
        Consumer consumer2 = new Consumer(buffer, 2);
        Consumer consumer3 = new Consumer(buffer, 3);
        
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
        consumer3.start();
        
        try {
            Thread.sleep(10000); // Run simulation for 10 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Signal all threads to stop and wait for them to finish
        producer1.stopRunning();
        producer2.stopRunning();
        consumer1.stopRunning();
        consumer2.stopRunning();
        consumer3.stopRunning();
        
        try {
            producer1.join();
            producer2.join();
            consumer1.join();
            consumer2.join();
            consumer3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Represents a process as a thread, simulating CPU burst time
class ProcessThread extends Thread {
    private int pid;
    private int burstTime;
    
    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }
    
    @Override
    public void run() {
        System.out.println("[Process " + pid + "] Started. Burst time: " + burstTime + "s");
        try {
            Thread.sleep(burstTime * 1000); // Simulate CPU burst
        } catch (InterruptedException e) {
            System.out.println("[Process " + pid + "] Interrupted!");
        }
        System.out.println("[Process " + pid + "] Finished.");
    }
}

// Bounded buffer implementing Producer-Consumer synchronization with semaphores and locks
class BoundedBuffer {
    private final Queue<Integer> buffer;
    private final int capacity;
    private final Lock lock;
    private final Semaphore empty; // Tracks empty slots in buffer
    private final Semaphore full;  // Tracks filled slots in buffer
    
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.empty = new Semaphore(capacity); // All slots initially empty
        this.full = new Semaphore(0);         // No items initially available
    }
    
    // Producer adds item to buffer with proper synchronization
    public void put(int item, int producerId) throws InterruptedException {
        System.out.println("[Producer " + producerId + "] Waiting to put item " + item + "...");
        
        empty.acquire(); // Wait if buffer is full
        lock.lock();     // Get exclusive access to buffer
        
        try {
            buffer.add(item);
            System.out.println("[Producer " + producerId + "] Put item " + item + " into buffer. Buffer size: " + buffer.size());
        } finally {
            lock.unlock();  // Release lock in finally block to ensure it's always released
            full.release(); // Signal that an item is available
        }
    }
    
    // Consumer removes item from buffer with proper synchronization
    public int take(int consumerId) throws InterruptedException {
        System.out.println("[Consumer " + consumerId + "] Waiting to take item...");
        
        full.acquire();  // Wait if buffer is empty
        lock.lock();     // Get exclusive access to buffer
        
        int item;
        try {
            item = buffer.remove();
            System.out.println("[Consumer " + consumerId + "] Took item " + item + " from buffer. Buffer size: " + buffer.size());
        } finally {
            lock.unlock();   // Release lock in finally block
            empty.release(); // Signal that a slot is now empty
        }
        
        return item;
    }
    
    public int getSize() {
        lock.lock();
        try {
            return buffer.size();
        } finally {
            lock.unlock();
        }
    }
}

// Producer thread that continuously adds items to the buffer
class Producer extends Thread {
    private final BoundedBuffer buffer;
    private final int producerId;
    private volatile boolean running = true; // Volatile ensures visibility across threads
    private int itemCounter = 1;
    
    public Producer(BoundedBuffer buffer, int producerId) {
        this.buffer = buffer;
        this.producerId = producerId;
    }
    
    @Override
    public void run() {
        System.out.println("[Producer " + producerId + "] Started.");
        
        while (running) {
            try {
                int item = itemCounter++;
                buffer.put(item, producerId);
                
                Thread.sleep((int)(Math.random() * 1000) + 500); // Simulate production time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("[Producer " + producerId + "] Finished.");
    }
    
    public void stopRunning() {
        running = false;
        this.interrupt();
    }
}

// Consumer thread that continuously removes and processes items from buffer
class Consumer extends Thread {
    private final BoundedBuffer buffer;
    private final int consumerId;
    private volatile boolean running = true; // Volatile ensures visibility across threads
    
    public Consumer(BoundedBuffer buffer, int consumerId) {
        this.buffer = buffer;
        this.consumerId = consumerId;
    }
    
    @Override
    public void run() {
        System.out.println("[Consumer " + consumerId + "] Started.");
        
        while (running) {
            try {
                int item = buffer.take(consumerId);
                
                System.out.println("[Consumer " + consumerId + "] Processing item " + item + "...");
                Thread.sleep((int)(Math.random() * 1500) + 500); // Simulate processing time
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("[Consumer " + consumerId + "] Finished.");
    }
    
    public void stopRunning() {
        running = false;
        this.interrupt();
    }
}