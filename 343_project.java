import java.util.Arrays;
import java.util.Random;

public class Main {
    
    // Define the maximum temperature and cooling rate for the annealing process
    private static final double MAX_TEMPERATURE = 10000;
    private static final double COOLING_RATE = 0.03;
    private static int[] values = {68, 64, 47, 55, 72, 53, 81, 60, 72, 80, 62, 42, 48, 47, 68, 51, 48, 68, 83, 55, 48, 44, 49, 68, 63, 71, 82, 55, 60, 63, 56, 75, 42, 76, 42, 60, 75, 68, 67, 42, 71, 58, 66, 72, 67, 78, 49, 50, 51};
    private static int[] weights = {21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10, 16, 17, 19, 12, 12, 16, 16, 13, 17, 12, 16, 13, 21, 11, 11, 10, 14, 12, 12, 14, 17, 13, 11, 13, 17, 14, 16, 10, 18, 10, 16};

    private static int knapsackCapacity = 300;
    
    // Define the solution state variables
    private static boolean[] currentSolution;
    private static boolean[] bestSolution;
    private static int currentValue;
    private static int bestValue;

    public static void main(String[] args) {
        // Initialize the solution state variables
        currentSolution = new boolean[values.length];
        bestSolution = new boolean[values.length];
        currentValue = 0;
        bestValue = 0;

        // Find the initial feasible solution using a greedy approach
        initializeGreedySolution();

        // Print the initial greedy solution
        System.out.println("Initial Greedy Solution: " + Arrays.toString(currentSolution));
        System.out.println("Initial Greedy Value: " + currentValue);

        // Start the simulated annealing process
        double temperature = MAX_TEMPERATURE;
        double thresholdTemperature = 1;

        Random random = new Random();

        while (temperature > thresholdTemperature) {
            // Generate a new neighbor solution
            boolean[] neighborSol = Arrays.copyOf(currentSolution, currentSolution.length);
            int randomIndex = random.nextInt(neighborSol.length);

            if (neighborSol[randomIndex]) {
                neighborSol[randomIndex] = false;
                currentValue -= values[randomIndex];
            } else {
                neighborSol[randomIndex] = true;
                currentValue += values[randomIndex];
            }

            int neighborValue = calculateValue(neighborSol);
            int currentCapacity = calculateCapacity(neighborSol);

            if (currentCapacity < 0) {
                if (neighborSol[randomIndex]) {
                    neighborSol[randomIndex] = false;
                    currentValue -= values[randomIndex];
                } else {
                    neighborSol[randomIndex] = true;
                    currentValue += values[randomIndex];
                }
            }

            double acceptanceProbability = calculateAcceptanceProbability(currentValue, neighborValue, temperature);

            if (acceptanceProbability > random.nextDouble()) {
                currentSolution = neighborSol;
                currentValue = neighborValue;
            }

            if (currentValue > bestValue) {
                bestSolution = Arrays.copyOf(currentSolution, currentSolution.length);
                bestValue = currentValue;
            }

            // Print the current and best values
            System.out.println("Current Value: " + currentValue + " | Best Value: " + bestValue);

            // Decrease the temperature
            temperature *= 1 - COOLING_RATE;
        }

        // Print the best solution found
        System.out.println("Best Solution: " + Arrays.toString(bestSolution));
        System.out.println("Best Value: " + bestValue);
    }

    private static void initializeGreedySolution() {
        double[] ratio = new double[values.length];
        for (int i = 0; i < ratio.length; i++) {
            ratio[i] = (double) values[i] / weights[i];
        }

        boolean[] flag = Arrays.copyOf(currentSolution, currentSolution.length);
        int currentCapacity = knapsackCapacity;
        int count = flag.length;

        while (count != 0) {
            double max = 0;
            int index = 0;

            int i = 0;
            while (i < ratio.length) {
                if (max <= ratio[i] && !flag[i]) {
                    max = ratio[i];
                    index = i;
                }
                i++;
            }

            if (currentCapacity - weights[index] >= 0) {
                currentSolution[index] = true;
                currentValue += values[index];
                currentCapacity -= weights[index];
            }

            flag[index] = true;
            count--;
        }

        bestValue = currentValue;
        bestSolution = currentSolution;
    }

    private static int calculateValue(boolean[] solution) {
        int value = 0;
        int i = 0;
        while (i < solution.length) {
            if (solution[i]) {
                value += values[i];
            }
            i++;
        }
        return value;
    }

    private static double calculateAcceptanceProbability(int currentValue, int neighborValue, double temperature) {
        if (neighborValue >= currentValue) {
            return 1.0;
        } else {
            return Math.exp((neighborValue - currentValue) / temperature);
        }
    }

    private static int calculateCapacity(boolean[] solution) {
        int totalWeight = 0;
        int i = 0;
        while (i < solution.length) {
            if (solution[i]) {
                totalWeight += weights[i];
            }
            i++;
        }
        return knapsackCapacity - totalWeight;
    }
}