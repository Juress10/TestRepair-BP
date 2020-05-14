package Plugin;

import TestOperations.TestCoverageInformations;
import TestOperations.TestModel;
import TestOperations.TestRepair;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import resources.DataStore;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainLoop {

    public static boolean isRunning = false;
    private static final int PERIOD = 5000; // Wait 5 sec. between check
    private DataStore ds = DataStore.getInstance();
    private static long stage = 0;
    private static boolean executeRunning = false;

    public MainLoop() {
        // Wait for project to load
        //whileProjectNotReady();

        isRunning = true;
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                   // ApplicationManager.getApplication().runReadAction(() -> {
                    if(!executeRunning && ds.testDelayElapsed()) {
                        executeRunning = true;

                        execute();
                        TestRepair.repairAllTests();

                        executeRunning = false;
                    }

                   // });
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 0, PERIOD);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void execute(){

        /*if(stage == 1) {
            System.out.println("----------------------------------- Stage 2 ---------------------------------");
            TestCoverageInformations.initializeCoverageMap();
        }else if(stage > 2){
            //if (!TestModel.isMapEmpty())//TODO dopln kontrolu ci je mapa prazdna
        */
            System.out.println("---------------------------- Modified methods -------------------------------");
            TestModel.printAllClassMethodPair();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            TestRepair.repairTestsByModifiedMethods();

        ds.resetLastChangeTimeMillis();
    }

    private void safeAllFiles() {
        ApplicationManager.getApplication().invokeAndWait(() -> ApplicationManager.getApplication()
                .runWriteAction(() -> FileDocumentManager.getInstance().saveAllDocuments()));
    }
/*
    private void whileProjectNotReady() {
        while (ds.getActiveProject() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                new TestCoverageThread().start();
                LOGGER.log(Level.SEVERE, "Thread {0} interrupted forcefully. Thread restarted.",
                        Thread.currentThread().getName());
                Thread.currentThread().interrupt();
            }
        }
    }
*/
}
