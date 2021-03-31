package edu.ufp.inf.sd.project.util.geneticalgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneticAlgorithmJSSP {

    private static final String PACKAGE_DIR = "edu/ufp/inf/sd/project/util/geneticalgorithm/jssp_ga/";
    private static final String SCRIPT_NAME = PACKAGE_DIR + "main_ga.py";
    private final String jsspInstance;
    private final String queue;
    private final CrossoverStrategies crossoverStrategy;

    /**
     * Create a new object of TabuSearchJSSP
     * @param jsspInstance The path for the JSSP instance to solve
     * @param crossoverStrategy The crossover strategy (enum)
     * @param queue The name of the queue to communicate via RabbitMQ
     */
    public GeneticAlgorithmJSSP(String jsspInstance, String queue, CrossoverStrategies crossoverStrategy) {
        this.jsspInstance = jsspInstance;
        this.queue = queue;
        this.crossoverStrategy = crossoverStrategy;
    }

    /**
     * Run the Genetic Algorithm forever
     */
    public void run(){

        try {
            ProcessBuilder builder = new ProcessBuilder("python3",
                    SCRIPT_NAME,
                    jsspInstance,
                    queue,
                    String.valueOf(crossoverStrategy.strategy));

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = output.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            output.close();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "GA stopped for {0}", new Object[]{jsspInstance});

        } catch (IOException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

    }


}
