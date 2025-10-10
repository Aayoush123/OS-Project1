import java.util.*;

public class Scheduling_Algorithms 
{
    class GanttEntry 
    {
        int pid;
        int startTime;
        int endTime;
        
        GanttEntry(int pid, int startTime, int endTime) 
        {
            this.pid = pid;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    
    public void FCFS(List<Process_Create> processes)
    {
        System.out.println("\n=== First Come First Served (FCFS) ===");
        // Sort the processes based on the arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

        int currentTime = 0;
        List<GanttEntry> ganttChart = new ArrayList<>();
        for (Process_Create p : processes)
        {
            // If the current time is less than the next processes arrival time,
            // then the CPU is assumed to wait until the next process has arrived so that it can run
            if (currentTime < p.arrival_time)
            {
                currentTime = p.arrival_time;
            }
            int startTime = currentTime;
            currentTime += p.burst_time;
            int endTime = currentTime;
            ganttChart.add(new GanttEntry(p.pid, startTime, endTime));
            p.Calculate_Times(currentTime);
        }
        printGanttChart(ganttChart);
        Print_Results("First Come First Served Algorithm", processes, ganttChart);
    }

    // This is non-preemptive priority scheduling

    public void Priority_Scheduling(List<Process_Create> processes)
    {
        System.out.println("\n=== Priority Scheduling ===");
        // Sort processes based on arrival time first
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

        int currentTime = 0;
        List<Process_Create> completed = new ArrayList<>();
        List<Process_Create> readyList = new ArrayList<>();
        List<GanttEntry> ganttChart = new ArrayList<>();

        while (completed.size() < processes.size())
        {
            // adding arriving processes that come while the CPU is in use
            for (Process_Create p : processes)
            {
                if (p.arrival_time <= currentTime && !completed.contains(p) && !readyList.contains(p))
                {
                    readyList.add(p);
                }
            }

            // if the CPU is not in use, but there aren't any processes that have arrived
            // then we will increment time by 1
            if (readyList.isEmpty())
            {
                currentTime++;
                continue;
            }
            
            // picking process with highest priority in Ready List
            Process_Create next = readyList.stream().max(Comparator.comparingInt(p -> p.priority)).get(); 
            readyList.remove(next);

            // ensure CPU time doesn't go backwards if process arrives later
            if (currentTime < next.arrival_time)
            {
                currentTime = next.arrival_time;
            }

            int startTime = currentTime;
            currentTime += next.burst_time;
            int endTime = currentTime;
            
            ganttChart.add(new GanttEntry(next.pid, startTime, endTime));
            next.Calculate_Times(currentTime);
            completed.add(next);
        }
        printGanttChart(ganttChart);
        Print_Results("Priority Scheduling", completed, ganttChart);
    }
    
    public void SJF(List<Process_Create> processes) 
    {
        System.out.println("\n=== Shortest Job First ===");
        
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

        int currentTime = 0;
        List<Process_Create> completed = new ArrayList<>();
        List<Process_Create> readyList = new ArrayList<>();
        List<GanttEntry> ganttChart = new ArrayList<>();

        while (completed.size() < processes.size()) {
            // add processes that have arrived to ready list
            for (Process_Create p : processes) 
            {
                if (p.arrival_time <= currentTime && !completed.contains(p) && !readyList.contains(p)) {
                    readyList.add(p);
                }
            }

            // handle idle CPU time when no processes are ready
            if (readyList.isEmpty()) 
            {
                currentTime++;
                continue;
            }

            // find process with shortest burst time
            Process_Create next = readyList.stream().min(Comparator.comparingInt(p -> p.burst_time)).get();
            readyList.remove(next);

            if (currentTime < next.arrival_time) 
            {
                currentTime = next.arrival_time;
            }

            int startTime = currentTime;
            currentTime += next.burst_time;
            int endTime = currentTime;
            
            ganttChart.add(new GanttEntry(next.pid, startTime, endTime));
            next.Calculate_Times(currentTime);
            completed.add(next);
        }

        printGanttChart(ganttChart);
        Print_Results("Shortest Job First (SJF)", completed, ganttChart);
    }

    private void printGanttChart(List<GanttEntry> ganttChart) {
        System.out.println("\nGantt Chart:");
        System.out.println("Execution Order:");
        
        // print process bars
        for (GanttEntry entry : ganttChart) {
            System.out.print("+");
            for (int i = 0; i < 6; i++) System.out.print("-");
        }
        System.out.println("+");
        
        // display process IDs in their execution order
        for (GanttEntry entry : ganttChart) {
            System.out.printf("| P%-3d", entry.pid);
        }
        System.out.println("|");
        
        for (GanttEntry entry : ganttChart) {
            System.out.print("+");
            for (int i = 0; i < 6; i++) System.out.print("-");
        }
        System.out.println("+");
        
        // print timeline with completion times
        System.out.print(ganttChart.get(0).startTime);
        for (GanttEntry entry : ganttChart) {
            System.out.printf("%6d", entry.endTime);
        }
        System.out.println("\n");
    }

    private void Print_Results(String algorithm, List<Process_Create> processes, List<GanttEntry> ganttChart)
    {
        System.out.println("\n" + algorithm);
        System.out.printf("%-5s %-15s %-15s %-15s %-10s %-10s%n", 
            "PID", "Arrival_Time", "Burst_Time", "Priority", "WT", "TAT");
        int totalWT = 0;
        int totalTAT = 0;
        int totalBurstTime = 0;
        for (Process_Create p : processes) 
        {
            System.out.printf("%-5d %-15d %-15d %-15d %-10d %-10d%n",
                p.pid, p.arrival_time, p.burst_time, p.priority, p.waiting_time, p.turnaround_time);
            totalWT += p.waiting_time;
            totalTAT += p.turnaround_time;
            totalBurstTime += p.burst_time;
        }

        double avgWT = (double) totalWT / processes.size();
        double avgTAT = (double) totalTAT / processes.size();
        
        // Calculate total execution time from Gantt chart
        int totalTime = ganttChart.get(ganttChart.size() - 1).endTime;
        int firstArrival = ganttChart.get(0).startTime;
        int actualTotalTime = totalTime - firstArrival;
        
        // Calculate CPU utilization percentage
        double cpuUtilization = ((double) totalBurstTime / actualTotalTime) * 100;
        
        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
        System.out.printf("Total Execution Time: %d time units\n", totalTime);
    }
}