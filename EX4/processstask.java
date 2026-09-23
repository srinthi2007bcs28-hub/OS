import java.util.PriorityQueue;

class Solution {
    public int[] assignTasks(int[] servers, int[] tasks) {
        int numServers = servers.length;
        int numTasks = tasks.length;
        int[] result = new int[numTasks];

        // Heap 1: Free servers pool
        // Sorted by: 1) Smallest weight, 2) Smallest original index
        PriorityQueue<int[]> freeServers = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(a[0], b[0]); // compare weight
            }
            return Integer.compare(a[1], b[1]);    // compare index
        });

        // Heap 2: Busy servers pool
        // Sorted by: 1) Shortest release time
        PriorityQueue<int[]> busyServers = new PriorityQueue<>((a, b) -> {
            return Integer.compare(a[0], b[0]);     // compare release time
        });

        // Initialize all servers into the free pool
        // Structure: [weight, originalIndex]
        for (int i = 0; i < numServers; i++) {
            freeServers.offer(new int[]{servers[i], i});
        }

        int time = 0;
        int taskIdx = 0;

        while (taskIdx < numTasks) {
            // Rule A: Release all servers that have finished working by the current time
            while (!busyServers.isEmpty() && busyServers.peek()[0] <= time) {
                int[] released = busyServers.poll();
                // Put back into free pool: [weight, index]
                freeServers.offer(new int[]{released[1], released[2]});
            }

            // Rule B: Assign tasks if there are available free servers
            // We can only process tasks that have already arrived (taskIdx <= time)
            while (!freeServers.isEmpty() && taskIdx < numTasks && taskIdx <= time) {
                int[] server = freeServers.poll();
                result[taskIdx] = server[1]; // Store the assigned server's index
                
                // Move server to the busy pool
                // Structure: [releaseTime, weight, originalIndex]
                int releaseTime = time + tasks[taskIdx];
                busyServers.offer(new int[]{releaseTime, server[0], server[1]});
                
                taskIdx++;
            }

            // Rule C: Fast-forward timeline if the CPU is bottlenecked
            if (freeServers.isEmpty() && taskIdx < numTasks && time < taskIdx) {
                // Skip directly to the time when the next busy server becomes free
                time = busyServers.peek()[0];
            } else {
                // Normal incremental clock advance
                time++;
            }
        }

        return result;
    }
}

