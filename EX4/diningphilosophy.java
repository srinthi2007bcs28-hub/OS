import java.util.concurrent.locks.ReentrantLock;

class DiningPhilosophers {
    
    // Create an array of 5 locks representing the 5 forks
    private final ReentrantLock[] forks;

    public DiningPhilosophers() {
        forks = new ReentrantLock[5];
        for (int i = 0; i < 5; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        
        // Step 1: Identify fork positions
        int leftForkId = philosopher;
        int rightForkId = (philosopher + 1) % 5;
        
        // Step 2: Determine strict resource ordering (lower index first)
        int firstFork = Math.min(leftForkId, rightForkId);
        int secondFork = Math.max(leftForkId, rightForkId);
        
        // Step 3: Lock forks sequentially to prevent deadlock
        forks[firstFork].lock();
        forks[secondFork].lock();
        
        try {
            // Step 4: Execute actions inside the Critical Section
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
        } finally {
            // Step 5: Always unlock in the finally block to prevent resource leaks
            forks[secondFork].unlock();
            forks[firstFork].unlock();
        }
    }
}
