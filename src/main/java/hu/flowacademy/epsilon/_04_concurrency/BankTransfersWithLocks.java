package hu.flowacademy.epsilon._04_concurrency;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankTransfersWithLocks {
    private static final class BankAccount {
        int amount;
        final Lock lock = new ReentrantLock();

        int getAmount() {
            return amount;
        }
    }

    private static final int ACCOUNTS = 100;
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
        for (BankAccount acc: accounts) {
            acc.lock.lock();
        }
        try {
            return Arrays.stream(accounts).mapToInt(BankAccount::getAmount).sum();
        } finally {
            for (BankAccount acc: accounts) {
                acc.lock.unlock();
            }
        }
    }

    private void simulateTransfers() {
        var rnd = ThreadLocalRandom.current();
        Thread thread = Thread.currentThread();
        while(!thread.isInterrupted()) {
            var idxDebit = rnd.nextInt(accounts.length);
            var accDebit = accounts[idxDebit];
            var idxCredit = rnd.nextInt(accounts.length);
            var accCredit = accounts[idxCredit];
            var a1 = accounts[Math.min(idxDebit, idxCredit)];
            a1.lock.lock();
            try {
                var a2 = accounts[Math.max(idxDebit, idxCredit)];
                a2.lock.lock();
                try {
                    if (accDebit.amount > 0) {
                        var transferAmount = rnd.nextInt(accDebit.amount) + 1;
                        accDebit.amount -= transferAmount;
                        accCredit.amount += transferAmount;
                    }
                } finally {
                    a2.lock.unlock();
                }
            } finally {
              a1.lock.unlock();
            }
            transferCount.increment();
        }
    }
}
