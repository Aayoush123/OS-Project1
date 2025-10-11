import java.io.*;
import java.util.*;

public class CPU_Scheduler 
{
    public static void main(String[] args) 
    {
        // Handle command-line argument for filename
        String filename = "processes.txt"; // default filename
        if (args.length > 0) {
            filename = args[0];
            System.out.println("Using input file: " + filename);
        } else {
            System.out.println("Using default: processes.txt");
        }
        
        List<Process_Create> processes = readProcessesFromFile(filename);
        
        if (processes.isEmpty()) {
            System.err.println("Error: No processes loaded from file. Exiting.");
            return;
        }
        
        System.out.println("Successfully loaded " + processes.size() + " processes from " + filename);
        
        Scheduling_Algorithms scheduler = new Scheduling_Algorithms();

        // Make copies of the processes data before sorting based on algorithm
        List<Process_Create> FCFSProcesses = new ArrayList<>();
        for (Process_Create p : processes) 
        {
            FCFSProcesses.add(new Process_Create(p.pid, p.arrival_time, p.burst_time, p.priority));
        }

        List<Process_Create> priorityProcesses = new ArrayList<>();
        for (Process_Create p : processes)
        {
            priorityProcesses.add(new Process_Create(p.pid, p.arrival_time, p.burst_time, p.priority));
        }
        List<Process_Create> SJFProcesses = new ArrayList<>();
        for (Process_Create p : processes)
        {
            SJFProcesses.add(new Process_Create(p.pid, p.arrival_time, p.burst_time, p.priority));
        }

        // Execute all CPU scheduling algorithms with separate process copies
        scheduler.FCFS(FCFSProcesses);
        scheduler.Priority_Scheduling(priorityProcesses);
        scheduler.SJF(SJFProcesses);
        
        // ===== MEMORY MANAGEMENT SECTION =====
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("MEMORY MANAGEMENT SIMULATION");
        System.out.println("=".repeat(60));
        
        // Create memory blocks for allocation algorithms
        List<Memory_Manager.MemoryAllocation.MemoryBlock> memoryBlocks1 = createMemoryBlocks();
        List<Memory_Manager.MemoryAllocation.MemoryBlock> memoryBlocks2 = createMemoryBlocks();
        List<Memory_Manager.MemoryAllocation.MemoryBlock> memoryBlocks3 = createMemoryBlocks();
        
        // Create process memory requests
        List<Memory_Manager.MemoryAllocation.ProcessMemory> memoryProcesses = new ArrayList<>();
        memoryProcesses.add(new Memory_Manager.MemoryAllocation.ProcessMemory("P1", 212));
        memoryProcesses.add(new Memory_Manager.MemoryAllocation.ProcessMemory("P2", 417));
        memoryProcesses.add(new Memory_Manager.MemoryAllocation.ProcessMemory("P3", 112));
        memoryProcesses.add(new Memory_Manager.MemoryAllocation.ProcessMemory("P4", 426));
        
        // Run memory allocation algorithms
        Memory_Manager.MemoryAllocation.firstFit(memoryBlocks1, memoryProcesses);
        Memory_Manager.MemoryAllocation.bestFit(memoryBlocks2, memoryProcesses);
        Memory_Manager.MemoryAllocation.worstFit(memoryBlocks3, memoryProcesses);
        
        // ===== PAGE REPLACEMENT SECTION =====
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("PAGE REPLACEMENT SIMULATION");
        System.out.println("=".repeat(60));
        
        // Test Page Replacement algorithms
        int[] pageReferences = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        Memory_Manager.PageReplacement.fifo(pageReferences, 3);
        Memory_Manager.PageReplacement.lru(pageReferences, 3);
        Memory_Manager.PageReplacement.optimal(pageReferences, 3);
    }

    // Creates memory blocks with different sizes for allocation algorithms
    private static List<Memory_Manager.MemoryAllocation.MemoryBlock> createMemoryBlocks() 
    {
        List<Memory_Manager.MemoryAllocation.MemoryBlock> blocks = new ArrayList<>();
        
        // Create instance of the enclosing class
        Memory_Manager.MemoryAllocation memoryAllocator = new Memory_Manager.MemoryAllocation();
        
        // Use the instance to create MemoryBlock objects
        Memory_Manager.MemoryAllocation.MemoryBlock block1 = 
            memoryAllocator.new MemoryBlock(1, 100, 0);
        Memory_Manager.MemoryAllocation.MemoryBlock block2 = 
            memoryAllocator.new MemoryBlock(2, 500, 100);
        Memory_Manager.MemoryAllocation.MemoryBlock block3 = 
            memoryAllocator.new MemoryBlock(3, 200, 600);
        Memory_Manager.MemoryAllocation.MemoryBlock block4 = 
            memoryAllocator.new MemoryBlock(4, 300, 800);
        Memory_Manager.MemoryAllocation.MemoryBlock block5 = 
            memoryAllocator.new MemoryBlock(5, 600, 1100);
        
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        
        return blocks;
    }
        
    // Read process data from input file and create Process_Create objects
    public static List<Process_Create> readProcessesFromFile(String filename) 
    {
        List<Process_Create> processes = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine(); 
            
            while ((line = reader.readLine()) != null) 
            {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 4) 
                {
                    int pid = Integer.parseInt(parts[0]);
                    int arrivalTime = Integer.parseInt(parts[1]);
                    int burstTime = Integer.parseInt(parts[2]);
                    int priority = Integer.parseInt(parts[3]);
                    
                    processes.add(new Process_Create(pid, arrivalTime, burstTime, priority));
                }
            }
            reader.close();
        }
        catch (IOException e) 
        {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing process data: " + e.getMessage());
        }
        return processes;
    }
}