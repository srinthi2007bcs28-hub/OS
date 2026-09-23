import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;

class H2O {
    private final Semaphore hSem;
    private final Semaphore oSem;
    private final CyclicBarrier barrier;

    public H2O() {
        // Permit 2 hydrogen threads at a time
        hSem = new Semaphore(2);
        // Permit 1 oxygen thread at a time
        oSem = new Semaphore(1);
        // Wait for exactly 3 threads (2H + 1O) to gather
        barrier = new CyclicBarrier(3);
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hSem.acquire(); // Grab 1 of the 2 available H slots
        try {
            barrier.await(); // Wait for the remaining parts of the molecule
            // releaseHydrogen.run() outputs "H". Do not change or remove this line.
            releaseHydrogen.run();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            hSem.release(); // Release the slot for the next molecule
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oSem.acquire(); // Grab the single available O slot
        try {
            barrier.await(); // Wait for 2 H threads to join
            // releaseOxygen.run() outputs "O". Do not change or remove this line.
            releaseOxygen.run();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            oSem.release(); // Release the slot for the next molecule
        }
    }
}

