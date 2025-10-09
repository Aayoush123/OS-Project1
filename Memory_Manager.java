import java.util.*;

public class Memory_Manager 
{
    
    public static class MemoryAllocation 
    {
        
        private static class MemoryBlock 
        {
            int id;
            int size;
            int startAddress;
            int endAddress;
            boolean allocated;
            String processId;
            
            MemoryBlock(int id, int size, int startAddress) 
            {
                this.id = id;
                this.size = size;
                this.startAddress = startAddress;
                this.endAddress = startAddress + size - 1;
                this.allocated = false;
                this.processId = "";
            }
        }
        
        static class ProcessMemory 
        {
            String processId;
            int memoryRequired;
            
            ProcessMemory(String processId, int memoryRequired) 
            {
                this.processId = processId;
                this.memoryRequired = memoryRequired;
            }
        }
        
        public static void firstFit(List<MemoryBlock> memory, List<ProcessMemory> processes)
        {
            System.out.println("\n=== First Fit Memory Allocation ===");
            printMemoryLayout(memory, "Initial Memory Layout");
            
            for (ProcessMemory process : processes) 
            {
                boolean allocated = false;
                for (MemoryBlock block : memory) 
                {
                    if (!block.allocated && block.size >= process.memoryRequired) 
                    {
                        block.allocated = true;
                        block.processId = process.processId;
                        System.out.printf("Allocated Process %s (%d KB) to Block %d (%d KB)\n",
                            process.processId, process.memoryRequired, block.id, block.size);
                        allocated = true;
                        break;
                    }
                }
                if (!allocated) 
                {
                    System.out.printf("Failed to allocate Process %s (%d KB) - No suitable block found\n",
                        process.processId, process.memoryRequired);
                }
            }
            
            printMemoryLayout(memory, "Final Memory Layout after First Fit");
        }
        
        public static void bestFit(List<MemoryBlock> memory, List<ProcessMemory> processes) 
        {
            System.out.println("\n=== Best Fit Memory Allocation ===");
            printMemoryLayout(memory, "Initial Memory Layout");
            
            for (ProcessMemory process : processes) 
            {
                MemoryBlock bestBlock = null;
                for (MemoryBlock block : memory) 
                {
                    if (!block.allocated && block.size >= process.memoryRequired) 
                    {
                        if (bestBlock == null || block.size < bestBlock.size) 
                        {
                            bestBlock = block;
                        }
                    }
                }
                
                if (bestBlock != null) 
                {
                    bestBlock.allocated = true;
                    bestBlock.processId = process.processId;
                    System.out.printf("Allocated Process %s (%d KB) to Block %d (%d KB)\n",
                        process.processId, process.memoryRequired, bestBlock.id, bestBlock.size);
                } 
                else 
                {
                    System.out.printf("Failed to allocate Process %s (%d KB) - No suitable block found\n",
                        process.processId, process.memoryRequired);
                }
            }
            
            printMemoryLayout(memory, "Final Memory Layout after Best Fit");
        }
        
        public static void worstFit(List<MemoryBlock> memory, List<ProcessMemory> processes) 
        {
            System.out.println("\n=== Worst Fit Memory Allocation ===");
            printMemoryLayout(memory, "Initial Memory Layout");
            
            for (ProcessMemory process : processes) 
            {
                MemoryBlock worstBlock = null;
                for (MemoryBlock block : memory) 
                {
                    if (!block.allocated && block.size >= process.memoryRequired) 
                    {
                        if (worstBlock == null || block.size > worstBlock.size) 
                        {
                            worstBlock = block;
                        }
                    }
                }
                
                if (worstBlock != null) 
                {
                    worstBlock.allocated = true;
                    worstBlock.processId = process.processId;
                    System.out.printf("Allocated Process %s (%d KB) to Block %d (%d KB)\n",
                        process.processId, process.memoryRequired, worstBlock.id, worstBlock.size);
                } 
                else 
                {
                    System.out.printf("Failed to allocate Process %s (%d KB) - No suitable block found\n",
                        process.processId, process.memoryRequired);
                }
            }
            
            printMemoryLayout(memory, "Final Memory Layout after Worst Fit");
        }
        
        private static void printMemoryLayout(List<MemoryBlock> memory, String title) 
        {
            System.out.println("\n" + title + ":");
            System.out.println("Block ID | Start Address | End Address | Size (KB) | Status     | Process");
            System.out.println("---------|---------------|-------------|-----------|------------|---------");
            
            for (MemoryBlock block : memory) 
            {
                String status = block.allocated ? "Allocated" : "Free";
                String process = block.allocated ? block.processId : "-";
                System.out.printf("%8d | %13d | %11d | %9d | %-10s | %s\n",
                    block.id, block.startAddress, block.endAddress, 
                    block.size, status, process);
            }
        }
    }
    
    public static class PageReplacement 
    {
        
        public static void fifo(int[] pageReferences, int frameCount) 
        {
            System.out.println("\n=== FIFO Page Replacement ===");
            System.out.println("Page Reference Sequence: " + Arrays.toString(pageReferences));
            System.out.println("Number of Frames: " + frameCount);
            
            Queue<Integer> frames = new LinkedList<>();
            Set<Integer> frameSet = new HashSet<>();
            int pageFaults = 0;
            
            System.out.println("\nStep-by-step execution:");
            System.out.printf("%-10s %-15s %-10s\n", "Step", "Frames", "Page Fault");
            System.out.println("----------|---------------|----------");
            
            for (int i = 0; i < pageReferences.length; i++) 
            {
                int page = pageReferences[i];
                boolean pageFault = false;
                
                if (!frameSet.contains(page)) 
                {
                    pageFault = true;
                    pageFaults++;
                    
                    if (frames.size() == frameCount) 
                    {
                        int removedPage = frames.poll();
                        frameSet.remove(removedPage);
                    }
                    
                    frames.offer(page);
                    frameSet.add(page);
                }
                
                System.out.printf("%-10d %-15s %-10s\n", 
                    i + 1, getFrameState(frames), pageFault ? "Yes" : "No");
            }
            
            System.out.println("\nTotal Page Faults: " + pageFaults);
            System.out.printf("Page Fault Rate: %.2f%%\n", (double) pageFaults / pageReferences.length * 100);
        }
        
        public static void lru(int[] pageReferences, int frameCount) 
        {
            System.out.println("\n=== LRU Page Replacement ===");
            System.out.println("Page Reference Sequence: " + Arrays.toString(pageReferences));
            System.out.println("Number of Frames: " + frameCount);
            
            LinkedHashMap<Integer, Integer> lruCache = new LinkedHashMap<>(frameCount, 0.75f, true) 
            {
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) 
                {
                    return size() > frameCount;
                }
            };
            
            int pageFaults = 0;
            
            System.out.println("\nStep-by-step execution:");
            System.out.printf("%-10s %-15s %-10s\n", "Step", "Frames", "Page Fault");
            System.out.println("----------|---------------|----------");
            
            for (int i = 0; i < pageReferences.length; i++) 
            {
                int page = pageReferences[i];
                boolean pageFault = false;
                
                if (!lruCache.containsKey(page)) 
                {
                    pageFault = true;
                    pageFaults++;
                }
                
                lruCache.put(page, i);
                
                System.out.printf("%-10d %-15s %-10s\n", 
                    i + 1, getLRUFrameState(lruCache), pageFault ? "Yes" : "No");
            }
            
            System.out.println("\nTotal Page Faults: " + pageFaults);
            System.out.printf("Page Fault Rate: %.2f%%\n", (double) pageFaults / pageReferences.length * 100);
        }
        
        public static void optimal(int[] pageReferences, int frameCount) 
        {
            System.out.println("\n=== Optimal Page Replacement ===");
            System.out.println("Page Reference Sequence: " + Arrays.toString(pageReferences));
            System.out.println("Number of Frames: " + frameCount);
            
            List<Integer> frames = new ArrayList<>();
            int pageFaults = 0;
            
            System.out.println("\nStep-by-step execution:");
            System.out.printf("%-10s %-15s %-10s\n", "Step", "Frames", "Page Fault");
            System.out.println("----------|---------------|----------");
            
            for (int i = 0; i < pageReferences.length; i++) 
            {
                int page = pageReferences[i];
                boolean pageFault = false;
                
                if (!frames.contains(page)) 
                {
                    pageFault = true;
                    pageFaults++;
                    
                    if (frames.size() < frameCount) 
                    {
                        frames.add(page);
                    } 
                    else 
                    {
                        int farthest = -1;
                        int replaceIndex = -1;
                        
                        for (int j = 0; j < frames.size(); j++) 
                        {
                            int framePage = frames.get(j);
                            int nextUse = Integer.MAX_VALUE;
                            
                            for (int k = i + 1; k < pageReferences.length; k++) 
                            {
                                if (pageReferences[k] == framePage) 
                                {
                                    nextUse = k;
                                    break;
                                }
                            }
                            
                            if (nextUse > farthest) 
                            {
                                farthest = nextUse;
                                replaceIndex = j;
                            }
                        }
                        
                        frames.set(replaceIndex, page);
                    }
                }
                
                System.out.printf("%-10d %-15s %-10s\n", 
                    i + 1, getOptimalFrameState(frames), pageFault ? "Yes" : "No");
            }
            
            System.out.println("\nTotal Page Faults: " + pageFaults);
            System.out.printf("Page Fault Rate: %.2f%%\n", (double) pageFaults / pageReferences.length * 100);
        }
        
        private static String getFrameState(Queue<Integer> frames) 
        {
            return frames.toString();
        }
        
        private static String getLRUFrameState(LinkedHashMap<Integer, Integer> lruCache) 
        {
            return lruCache.keySet().toString();
        }
        
        private static String getOptimalFrameState(List<Integer> frames) 
        {
            return frames.toString();
        }
    }
}