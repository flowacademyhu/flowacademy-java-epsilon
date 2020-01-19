package hu.flowacademy.epsilon._04_concurrency;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is very similar to {@link BankTransfers}. It shows you how you
 * can have more flexible locking strategies with using {@link Lock}
 * (specifically {@link ReentrantLock}) instances. Its {@link #printStats()}
 * method can both print precise results and also not overflow the stack,
 * something that {@link BankTransfers} couldn't do with either of its
 * summarizing methods.
 */
public class BankTransfersWithLocks {
    private static final class BankAccount {
        int amount;
        // Every bank account will have its own
        // reentrant lock instance.
        final Lock lock = new ReentrantLock();

        int getAmount() {
            return amount;
        }
    }

    // This variant has no problem correctly locking 15000 accounts.
    private static final int ACCOUNTS = 15000;
    private static final int INITIAL_AMOUNT = 1000;

    private final LongAdder transferCount = new LongAdder();

    private final BankAccount[] accounts;

    BankTransfersWithLocks(int accountNum, int initialAmount) {
        accounts = new BankAccount[accountNum];
        for (int i = 0; i < accounts.length; ++i) {
            accounts[i] = new BankAccount();
            accounts[i].amount = initialAmount;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var parallelism = Runtime.getRuntime().availableProcessors();
        var exec = Executors.newScheduledThreadPool(parallelism + 1);
        var transfers = new BankTransfersWithLocks(ACCOUNTS, INITIAL_AMOUNT);
        for (int i = 0; i < parallelism; ++i) {
            exec.execute(transfers::simulateTransfers);
        }
        exec.scheduleAtFixedRate(transfers::printStats, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(15000);
        exec.shutdownNow();
    }

    private void printStats() {
        var t1 = System.nanoTime();
        var sum = sumAccounts(0);
        var t2 = System.nanoTime() - t1;
        System.out.println(sum + "\t" + transferCount.longValue() + "\t" + t2);
    }

    private int sumAccounts(int from) {
        // First, lock all accounts in the array from smaller indices to larger
        // ones. (A for loop over an array will always run from 0 to length-1.)
        for (BankAccount acc: accounts) {
            acc.lock.lock();
        }
        try {
            // Now sum it, using stream API
            return Arrays.stream(accounts)
                .mapToInt(BankAccount::getAmount)
                .sum();
        } finally {
            // Finally, unlock them all again. It doesn't actually matter in
            // what order we unlock them, but using a for loop just provides
            // us a natural order.
            for (BankAccount acc: accounts) {
                acc.lock.unlock();
            }
        }
    }

    // This is similar to the logic in BankTransfers, but instead of synchronized
    // on accounts uses Lock.lock and Lock.unlock on each account's lock.
    private void simulateTransfers() {
        var rnd = ThreadLocalRandom.current();
        Thread thread = Thread.currentThread();
        while(!thread.isInterrupted()) {
            var idxDebit = rnd.nextInt(accounts.length);
            var accDebit = accounts[idxDebit];
            var idxCredit = rnd.nextInt(accounts.length);
            var accCredit = accounts[idxCredit];
            // We still need to lock the one with smaller index first.
            var a1 = accounts[Math.min(idxDebit, idxCredit)];
            // Instead of synchronized(a1) we'll use a1.lock.lock()
            a1.lock.lock();
            try {
                var a2 = accounts[Math.max(idxDebit, idxCredit)];
                a2.lock.lock();
                try {
                    if (accDebit.getAmount() > 0) {
                        var transferAmount = rnd.nextInt(accDebit.amount) + 1;
                        accDebit.amount -= transferAmount;
                        accCredit.amount += transferAmount;
                    }
                } finally {
                    // Instead of simply exiting a synchronized block, we must
                    // use Lock.unlock in try/finally.
                    a2.lock.unlock();
                }
            } finally {
              a1.lock.unlock();
            }
            transferCount.increment();
        }
    }
}
