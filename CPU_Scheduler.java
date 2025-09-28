import java.io.*;
import java.util.*;

public class CPU_Scheduler 
{
    public static void main(String[] args) 
    {
        List<Process> processes = readProcessesFromFile("processes.txt");

    }
        
        
    public static List<Process> readProcessesFromFile(String filename) 
    {
        List<Process> processes = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine(); // Skip header line
            
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
