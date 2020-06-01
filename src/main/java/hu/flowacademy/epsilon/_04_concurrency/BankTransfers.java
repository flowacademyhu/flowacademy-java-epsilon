package hu.flowacademy.epsilon._04_concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

/**
 * This class demonstrates the dangers of a deadlock. You can run the method
 * {@link #simulateTransfersDeadlock()} and observe the program can't complete as the
 * threads running that method will deadlock. "Deadlock" is a situation where
 * a thread hold a synchronization lock on an object A and is trying to acquire
 * a lock on object B, but another thread has object B already locked and is
 * trying to acquire a lock on A. Neither thread can make progress. Deadlocks
 * are bugs from which the program can not recover; you should write your code
 * so that it can not deadlock. A deadlock can arise with more than two threads
 * in which every thread holds an object the next thread waits on, and the last
 * thread waits on an object held by the first thread, forming a circular
 * locking dependency.
 *
 * In our case, the cause of the deadlock is that different threads might attempt
 * to obtain the synchronized locks on {@link BankAccount} objects in different
 * order. The method {@link #simulateTransfersNoDeadlock()} shows a solution to
 * the issue where the method always ensures bank account with smaller index is
 * locked first.
 */
public class BankTransfers {
    private static final class BankAccount {
        int amount;
    }

    private static final int ACCOUNTS = 100;
    private static final int INITIAL_AMOUNT = 1000;

    private final LongAdder transferCount = new LongAdder();

    private final BankAccount[] accounts;

    BankTransfers(int accountNum, int initialAmount) {
        accounts = new BankAccount[accountNum];
        for (int i = 0; i < accounts.length; ++i) {
            accounts[i] = new BankAccount();
            accounts[i].amount = initialAmount;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var parallelism = Runtime.getRuntime().availableProcessors();
        var exec = Executors.newScheduledThreadPool(parallelism + 1);
        var transfers = new BankTransfers(ACCOUNTS, INITIAL_AMOUNT);
        for (int i = 0; i < parallelism; ++i) {
            exec.execute(() ->
                // Replace simulateTransfersDeadlock and simulateTransfersNoDeadlock
                // and re-run to observe the difference in behavior.
                transfers.simulateTransfers(transfers::simulateTransfersDeadlock));
        }
        exec.scheduleAtFixedRate(transfers::printStats, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(15000);
        exec.shutdownNow();
    }

    private void printStats() {
        try {
            var t1 = System.nanoTime();
            // sumAccountsOneByOneLocking will provide imprecise sums, see the
            // documentation of that method for explanation why. On the other
            // hand sumAccountsRecursiveLocking will provide a precise sum, but
            // if you increase the number of ACCOUNTS to, say, 15000, it will
            // produce a StackOverflowError.
            var sum = sumAccountsOneByOneLocking();
            var t2 = System.nanoTime() - t1;
            System.out.println(sum + "\t" + transferCount.longValue() + "\t" + t2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * This method summarizes accounts locking them one by one. While it
     * traverses the elements of the @{link #accounts} array, transactions
     * can (and will) happen on unlocked elements, so when {@link #printStats}
     * uses this method, the sum will not be precise; it will almost always be
     * either more or less than {@code ACCOUNTS * INITIAL_AMOUNT}.
     *
     * Try using {@link #sumAccountsRecursiveLocking(int)} for a version
     */
    private int sumAccountsOneByOneLocking() {
        var sum = 0;
        for (var acc: accounts) {
            synchronized (acc) {
                sum += acc.amount;
            }
        }
        return sum;
    }

    /**
     * This method summarizes accounts locking them all from left to right. As
     * such, if you use it from {@link #printStats()} it will always return the
     * correct amount of {@code ACCOUNTS * INITIAL_AMOUNT}. However, because of
     * the way synchronization is implemented in Java, this method must be
     * recursive. That unfortunately means it can overflow the stack; if you
     * increase the number of accounts to over 10000 (say, try 15000), it will
     * throw a {@link StackOverflowError}. You will need to look into the
     * {@link BankTransfersWithLocks} example for a solution that is both precise
     * and doesn't overflow the stack.
     */
    private int sumAccountsRecursiveLocking() {
        return sumAccountsRecursiveLocking(0);
    }

    private int sumAccountsRecursiveLocking(int from) {
        if (from >= accounts.length) {
            return 0;
        }
        var acc = accounts[from];
        synchronized (acc) {
            return acc.amount + sumAccountsRecursiveLocking(from + 1);
        }
    }

    // Simulates bank transfers repeatedly. Uses a BiConsumer as the actual
    // strategy to synchronize on the accounts and perform the transfers.
    // Typically, you can pass this::simulateTransfersDeadlock and
    // this::simulateTransfersNoDeadlock as possible strategies.
    private void simulateTransfers(BiConsumer<Integer, Integer> transfer) {
        var rnd = ThreadLocalRandom.current();
        Thread thread = Thread.currentThread();
        // When we call shutdownNow on the ExecutorService, it will interrupt
        // all of its threads. We check for interruption as our sign to end
        // processing.
        while(!thread.isInterrupted()) {
            var idxDebit = rnd.nextInt(accounts.length);
            var idxCredit = rnd.nextInt(accounts.length);
            transfer.accept(idxDebit, idxCredit);
            transferCount.increment();
        }
    }

    // This strategy always first locks the debit account and then the credit
    // account. It can deadlock if multiple threads try to lock the same accounts
    // but in different orders. In simplest case, one thread transferring from
    // account A to account B And another transferring from B to A at the same time
    // will deadlock.
    private void simulateTransfersDeadlock(int idxDebit, int idxCredit) {
        var accDebit = accounts[idxDebit];
        var accCredit = accounts[idxCredit];
        synchronized (accDebit) {
            synchronized (accCredit) {
                doTransfer(accDebit, accCredit);
            }
        }
    }

    private void simulateTransfersNoDeadlock(int idxDebit, int idxCredit) {
        synchronized (accounts[Math.min(idxDebit, idxCredit)]) {
            synchronized (accounts[Math.max(idxDebit, idxCredit)]) {
                doTransfer(accounts[idxDebit], accounts[idxCredit]);
            }
        }
    }

    /**
     * Core functionality of both {@link #simulateTransfersDeadlock(int, int)} and
     * {@link #simulateTransfersNoDeadlock(int, int)} that performs a transfer of an
     * amount from debit account to credit account once they're both locked.
     */
    private void doTransfer(BankAccount accDebit, BankAccount accCredit) {
        if (accDebit.amount > 0) {
            var transferAmount = ThreadLocalRandom.current().nextInt(accDebit.amount) + 1;
            accDebit.amount -= transferAmount;
            accCredit.amount += transferAmount;
        }
    }
}
