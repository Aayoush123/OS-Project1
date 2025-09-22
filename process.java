public class process 
{
    int pid;
    int arrival_time;
    int burst_time;
    int priority; 

    public process(int pid, int arrival_time, int burst_time, int priority) 
    {
        this.pid = pid;
        this.arrival_time = arrival_time;
        this.burst_time = burst_time;
        this.priority = priority;
    }
}
