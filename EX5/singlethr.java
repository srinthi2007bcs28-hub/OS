import java.util.Arrays;
import java.util.PriorityQueue;

class Solution {
    public int[] getOrder(int[][] tasks) {
        int n = tasks.length;
        int[] result = new int[n];
        
        // Step 1: Augment the tasks array to keep track of original indices
        // store format: [enqueueTime, processingTime, originalIndex]
        int[][] extendedTasks = new int[n][3];
        for (int i = 0; i < n; i++) {
            extendedTasks[i][0] = tasks[i][0];
            extendedTasks[i][1] = tasks[i][1];
            extendedTasks[i][2] = i;
        }
        
        // Step 2: Sort tasks strictly by their arrival (enqueue) time
        Arrays.sort(extendedTasks, (a, b) -> Integer.compare(a[0], b[0]));
        
        // Step 3: PriorityQueue acts as the available task pool (Min-Heap)
        // Sorts by: 1) Shortest processing time, 2) Smallest original index
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> {
            if (a[1] != b[1]) {
                return Integer.compare(a[1], b[1]);
            }
            return Integer.compare(a[2], b[2]);
        });
        
        long currentTime = 0;
        int taskIdx = 0;
        int resultIdx = 0;
        
        // Step 4: Process tasks until all are finished
        while (taskIdx < n || !minHeap.isEmpty()) {
            // If the CPU is idle and no tasks are available in the heap,
            // fast-forward time to the next arriving task's enqueue time
            if (minHeap.isEmpty() && currentTime < extendedTasks[taskIdx][0]) {
                currentTime = extendedTasks[taskIdx][0];
            }
            
            // Push all tasks that have already arrived by the current time into the heap
            while (taskIdx < n && extendedTasks[taskIdx][0] <= currentTime) {
                minHeap.offer(extendedTasks[taskIdx]);
                taskIdx++;
            }
            
            // Pop the best available task from the min-heap to execute it
            int[] currentTask = minHeap.poll();
            result[resultIdx++] = currentTask[2]; // Add its original index to the solution
            currentTime += currentTask[1];       // Advance the timeline by its execution duration
        }
        
        return result;
    }
}
