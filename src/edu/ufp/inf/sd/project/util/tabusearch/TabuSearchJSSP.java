package edu.ufp.inf.sd.project.util.tabusearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TabuSearchJSSP {

    private static final String PACKAGE_DIR = "edu/ufp/inf/sd/project/util/tabusearch/jssp_ts/";
    private static final String SCRIPT_NAME = PACKAGE_DIR + "main_ts.py";
    private final String jsspInstance;

    /**
     * Create a new object of TabuSearchJSSP
     * @param jsspInstance The path for the JSSP instance to solve
     */
    public TabuSearchJSSP(String jsspInstance) {
        this.jsspInstance = jsspInstance;
    }

    /**
     * Run the Tabu Search
     * @return The makespan value found
     */
    public int run(){

        try {

            ProcessBuilder builder = new ProcessBuilder("python3", SCRIPT_NAME, jsspInstance);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String firstLine = output.readLine();

            process.waitFor();

            int makespan = Integer.valueOf(firstLine);

            output.close();

            return makespan;

        } catch (IOException | InterruptedException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }

        return Integer.MAX_VALUE;
    }

}
