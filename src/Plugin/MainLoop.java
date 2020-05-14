package Plugin;

import TestOperations.LoadTestToRepair;
import TestOperations.TestDataStore;
import TestOperations.TestRepair;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import resources.DataStore;

import java.util.Timer;
import java.util.TimerTask;

public class MainLoop {

    public static boolean isRunning = false;
    private static final int PERIOD = 5000; // Wait 5 sec. between check
    private DataStore ds = DataStore.getInstance();
    private static long stage = 0;
    private static boolean executeRunning = false;

    public MainLoop() {

        isRunning = true;
        try {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                if(!executeRunning && ds.testDelayElapsed()) {
                    executeRunning = true;

                    execute();
                    if(ds.testRepairDelayElapsed()) {
                        TestRepair.repairAllTests();
                        ds.resetLastChangeTimeMillisForTestRepair();
                    }
                    executeRunning = false;
                }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 0, PERIOD);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void execute(){

        System.out.println("---------------------------- Modified methods -------------------------------");
        TestDataStore.printAllClassMethodPair();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        LoadTestToRepair.prepareTestsForRepair();

        ds.resetLastChangeTimeMillis();
    }

    private void safeAllFiles() {
        ApplicationManager.getApplication().invokeAndWait(() -> ApplicationManager.getApplication()
                .runWriteAction(() -> FileDocumentManager.getInstance().saveAllDocuments()));
    }
}
