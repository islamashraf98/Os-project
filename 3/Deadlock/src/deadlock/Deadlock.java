/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadlock;

/**
 *
 * @author Lenovo
 */
import java.util.Scanner;

class Start {

    public final int nor = 3;
    private int[] resources;
    private int numberOfProcesses;
    private int[] available;
    private int[] numberOfInstances;
    private int[][] need;
    private int[][] allocation;
    private int[][] max;
    public boolean deadlock;
    Scanner sc = new Scanner(System.in);

    Start() {
        this.numberOfInstances = new int[nor];
        System.out.println("Enter number of processes");
        this.numberOfProcesses = sc.nextInt();
        for (int i = 0; i < nor; i++) {
            System.out.println("Enter number of instances of resource " + i);
            this.numberOfInstances[i] = sc.nextInt();
        }
    }

    public int[] checkDeadlock() {
        int[] available = this.available;
        int finish[], temp, flag = 1, k, c1 = 0;
        int nothing[] = {};
        int dead[];
        int safe[];
        int i, j;

        finish = new int[this.numberOfProcesses];
        dead = new int[100];
        safe = new int[100];

        for (i = 0; i < this.numberOfProcesses; i++) {
            finish[i] = 0;
        }
        //find need matrix
        this.getAvailable();
        this.getNeed();

        while (flag == 1) {
            flag = 0;
            for (i = 0; i < this.numberOfProcesses; i++) {
                int c = 0;
                for (j = 0; j < this.nor; j++) {
                    if ((finish[i] == 0) && (need[i][j] <= available[j])) {
                        c++;
                        if (c == this.nor) {
                            for (k = 0; k < this.nor; k++) {
                                available[k] += this.allocation[i][j];
                                finish[i] = 1;
                                flag = 1;
                            }
                            if (finish[i] == 1) {
                                i = this.numberOfProcesses;
                            }
                        }
                    }
                }
            }
        }
        j = 0;
        flag = 0;
        for (i = 0; i < this.numberOfProcesses; i++) {
            if (finish[i] == 0) {
                dead[j] = i;
                j++;
                flag = 1;
            }
        }
        if (flag == 1) {
            deadlock = true;
            System.out.println("\n\nSystem is in Deadlock and the Deadlock process are\n");
            for (i = 0; i < this.numberOfProcesses; i++) {
                if (!(i > 0 && dead[i] == 0)) {
                    System.out.println("P" + dead[i]);
                }
            }
            return dead;
        } else {
            deadlock = false;
            System.out.println("No deadlock occure");
            return nothing;
        }
    }

    public void recoverDeadlock(int[] deadlockedProcesses) {
        int p;
        int i = 0;
        while (deadlock) {
            if (deadlockedProcesses[i] == 0 && i > 0) {
                break;
            }
            p = deadlockedProcesses[i];
            for (int j = 0; j < nor; j++) {
                allocation[p][j] = 0;
                max[p][j] = 0;
                available[j]+=allocation[p][j];
            }
            System.out.println("Process " + p + " aborted");
            deadlockedProcesses[i] = 0;
            getAvailable();
            getNeed();
            if (isSafe()) {
                System.out.println("Deadlock recovered");
                deadlock = false;
                break;
            } else {
                checkDeadlock();
            }
            i++;
        }
    }

    public void initializeAllocation() {
        this.allocation = new int[this.numberOfProcesses][this.nor];
        for (int i = 0; i < this.numberOfProcesses; i++) {
            for (int j = 0; j < nor; j++) {
                System.out.println("Enter the allocations for process " + i + " and resource " + j);
                allocation[i][j] = sc.nextInt();
            }
        }
    }

    public void initializeMax() {
        this.max = new int[this.numberOfProcesses][this.nor];
        for (int i = 0; i < this.numberOfProcesses; i++) {
            for (int j = 0; j < nor; j++) {
                System.out.println("Enter the max for process " + i + " and resource " + j);
                max[i][j] = sc.nextInt();
            }
        }
    }

    public int[][] getAllocation() {
        return allocation;
    }

    public int[][] getMax() {
        return max;
    }

    public int[] getAvailable() {
        this.available = new int[nor];
        int sum = 0;
        for (int i = 0; i < nor; i++) {
            sum = 0;
            for (int j = 0; j < this.numberOfProcesses; j++) {
                //sum= the sum of the column (resource instances)
                sum += allocation[j][i];
            }
            /*
            We get the available of each resource by subtracting
            the number of instances with the sum of each column
             */
            this.available[i] = this.numberOfInstances[i] - sum;
        }
        return this.available;
    }

    public int[][] getNeed() {
        this.need = new int[this.numberOfProcesses][this.nor];
        for (int i = 0; i < this.numberOfProcesses; i++) {
            for (int j = 0; j < nor; j++) {
                this.need[i][j] = this.max[i][j] - this.allocation[i][j];
            }
        }
        return this.need;
    }

    public boolean isSafe() {
        int[] available = this.available;
        boolean flag = false;
        boolean visited[] = new boolean[this.numberOfProcesses];
        for (int i = 0; i < numberOfProcesses; i++) {
            visited[i] = false;
        }
        int counter;
        int k = 0;
        int[] sequence = new int[this.numberOfProcesses];
        while (k < numberOfProcesses) {
            flag = false;
            for (int i = 0; i < this.numberOfProcesses; i++) {
                counter = 0;
                if (!visited[i]) {
                    for (int j = 0; j < this.nor; j++) {
                        if (need[i][j]==0 && max[i][j]==0 && allocation[i][j]==0)
                            break;
                        if (need[i][j] <= available[j]) {
                            counter++;
                        }
                    }
                    if (counter == 3) {
                        flag = true;
                        visited[i] = true;
                        sequence[k++] = i;
                        for (int j = 0; j < nor; j++) {
                            available[j] += allocation[i][j];
                        }
                    }
                }
            }
            if (flag == false) {
                break;
            }
        }
        if (k < numberOfProcesses) {
            System.out.println("System is unsafe!");
            return false;
        } else {
            System.out.println("System is safe with sequence");
            for (int i = 0; i < numberOfProcesses; i++) {
                if (!isHere(i)) {
                    continue;
                }
                System.out.println(" P " + sequence[i]);
                if (i != numberOfProcesses - 1) {
                    System.out.println(" -> ");
                }
            }
            return true;
        }
    }

    public boolean isHere(int p) {
        int counter = 0;
        for (int i = 0; i < nor; i++) {
            if (allocation[p][i] == 0 && max[p][i] == 0) {
                counter++;
            }
            if (counter == 3) {
                return false;
            }
        }
        return true;
    }
}

public class Deadlock {

    /**
     * @param args the command line arguments
     */
    public static void prnt1d(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "|");
        }
        System.out.print("\n");
    }

    public static void prnt2d(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + "|");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        Start s = new Start();
        int[] deadlockedProcesses;
        s.initializeAllocation();
        s.initializeMax();
        System.out.println("Need : ");
        prnt2d(s.getNeed());
        System.out.println("Available : ");
        prnt1d(s.getAvailable());
        if (!s.isSafe()) {
            deadlockedProcesses = s.checkDeadlock();
            s.recoverDeadlock(deadlockedProcesses);
        }
    }
}
