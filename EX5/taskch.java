import java.util.Arrays;

class Solution {
    public int leastInterval(char[] tasks, int n) {
        // Step 1: Count the frequency of each task (A-Z)
        int[] frequencies = new int[26];
        for (char task : tasks) {
            frequencies[task - 'A']++;
        }
        
        // Step 2: Sort frequencies to easily find the most frequent task
        Arrays.sort(frequencies);
        
        // The highest frequency dictates the minimum chunk size we must manage
        int maxFrequency = frequencies[25];
        
        // Calculate the number of empty slot blocks created by the max frequency task
        // For example, if maxFrequency is 3, we have (3 - 1) = 2 groups of slots
        int idlePartitions = maxFrequency - 1;
        
        // Each group of slots has a capacity equal to the cooling window 'n'
        int idleSlotsPerPartition = n;
        int totalIdleSlots = idlePartitions * idleSlotsPerPartition;
        
        // Step 3: Iterate backwards to fill these idle slots with other remaining tasks
        for (int i = 24; i >= 0 && frequencies[i] > 0; i--) {
            // A task cannot fill more slots than the number of partitions.
            // If another task has the same maxFrequency, it spills past the partitions,
            // so we can only subtract at most 'idlePartitions' from our count.
            totalIdleSlots -= Math.min(idlePartitions, frequencies[i]);
        }
        
        // If we filled all slots and have extra tasks left over, total idle slots becomes negative.
        // In that scenario, we don't need any idle time at all; the answer is simply the total task count.
        int finalIdleCount = Math.max(0, totalIdleSlots);
        
        return tasks.length + finalIdleCount;
    }
}
