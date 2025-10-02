public class Process_Create 
{
    int pid;
    int arrival_time;
    int burst_time;
    int priority;
    int waiting_time;
    int turnaround_time; 

    public Process_Create(int pid, int arrival_time, int burst_time, int priority) 
    {
        this.pid = pid;
        this.arrival_time = arrival_time;
        this.burst_time = burst_time;
        this.priority = priority;
        this.waiting_time = 0;
        this.turnaround_time = 0;
    }
    public void Calcualte_Times(int CompletionTime)
    {
        this.turnaround_time = CompletionTime - this.arrival_time;
        this.waiting_time = this.turnaround_time - this.burst_time;
    }
}
