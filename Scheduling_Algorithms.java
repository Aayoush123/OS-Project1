import java.util.*;

public class Scheduling_Algorithms {
    public void FCFS(List<Process_Create> processes)
    {
        // Sort the processes based on the arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

        int currentTime = 0;
        for (Process_Create p : processes)
        {
            // If the current time is less than the next processes arrival time,
            // then the CPU is assumed to wait until the next process has arrived so that it can run
            if (currentTime < p.arrival_time)
            {
                currentTime = p.arrival_time;
            }

            currentTime += p.burst_time;
            p.Calcualte_Times(currentTime);
        }

        Print_Results("First Come First Served Algorithm", processes);
    }

    // This is non-preemptive priority scheduling
    // No interrupts on running processes
    public void Priority_Scheduling(List<Process_Create> processes)
    {
        // Sort processes based on arrival time first
        // because the scheduler can't schedule a processes that hasn't yet arrived
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

        int currentTime = 0;
        List<Process_Create> completed = new ArrayList<>();
        List<Process_Create> readyList = new ArrayList<>();

        while (completed.size() < processes.size())
        {
            // Adding arriving processes that come while the CPU is in use
            for (Process_Create p : processes)
            {
                if (p.arrival_time <= currentTime && !completed.contains(p) && !readyList.contains(p))
                {
                    readyList.add(p);
                }
            }

            // If the CPU is not in use, but there aren't any processes that have arrived
            // then we will increment time by 1
            if (readyList.isEmpty())
            {
                currentTime++;
                continue;
            }
            
            // Picking process with highest priority in Ready List
            Process_Create next = readyList.stream().max(Comparator.comparingInt(p -> p.priority)).get();
            readyList.remove(next);


            if (currentTime < next.arrival_time)
            {
                currentTime = next.arrival_time;
            }

            currentTime += next.burst_time;
            next.Calcualte_Times(currentTime);
            completed.add(next);
        }

        Print_Results("Priority Scheduling", completed);
    }

    private void Print_Results(String algorithm, List<Process_Create> processes)
    {
        System.out.println("\n" + algorithm);
        System.out.printf("%-5s %-15s %-15s %-15s %-10s %-10s%n", 
            "PID", "Arrival_Time", "Burst_Time", "Priority", "WT", "TAT");

        for (Process_Create p : processes) 
        {
            System.out.printf("%-5d %-15d %-15d %-15d %-10d %-10d%n",
                p.pid, p.arrival_time, p.burst_time, p.priority, p.waiting_time, p.turnaround_time);
        }
    }
}
