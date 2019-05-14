package hu.flowacademy.epsilon._04_concurrency;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

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
            exec.execute(transfers::simulateTransfers);
        }
        exec.scheduleAtFixedRate(transfers::printStats, 0, 1, TimeUnit.SECONDS);
        Thread.sleep(15000);
        exec.shutdownNow();
    }

    private void printStats() {
        var sum = 0;
        for(BankAccount acc: accounts) {
            synchronized (acc) {
                sum += acc.amount;
            }
        }
        System.out.println(sum + "\t" + transferCount.longValue());
    }

    private void simulateTransfers() {
        var rnd = ThreadLocalRandom.current();
        Thread thread = Thread.currentThread();
        while(!thread.isInterrupted()) {
            var idxDebit = rnd.nextInt(accounts.length);
            var accDebit = accounts[idxDebit];
            var idxCredit = rnd.nextInt(accounts.length);
            var accCredit = accounts[idxCredit];
            synchronized (accounts[Math.min(idxDebit, idxCredit)]) {
                synchronized (accounts[Math.max(idxDebit, idxCredit)]) {
                    if (accDebit.amount > 0) {
                        var transferAmount = rnd.nextInt(accDebit.amount) + 1;
                        accDebit.amount -= transferAmount;
                        accCredit.amount += transferAmount;
                    }
                }
            }
            transferCount.increment();
        }
    }
}
