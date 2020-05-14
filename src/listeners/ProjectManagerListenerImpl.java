/**
 *  Classu ProjectManagerListenerImpl som prevzal a pozmenil z diplomovej práce Mareka Bruchatého kde sa nachádza pod menom LivetestProjectManagerListenerImpl.
 *
 * */

package listeners;

import TestOperations.TestCoverageInformations;
import resources.TestDataStore;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import resources.DataStore;

import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectManagerListenerImpl implements VetoableProjectManagerListener {

    private static final Logger log = Logger.getLogger(ProjectManagerListenerImpl.class.getName());

    @Override
    public void projectOpened(Project project) {
        log.info(String.format("Active project name: %s, base path: %s", project.getName(),
                project.getBasePath()));
        DataStore.getInstance().setActiveProject(project);
        initListeners(project); // Init listeners dependent on current project

        ApplicationManager.getApplication().runReadAction(() -> {
            TestDataStore.loadAllTestClasses();

            TestCoverageInformations.initializeCoverageMap();
        });
        //sledovat cas od poslednej modifikacie a podla toho urobit akciu
        //dat to do noveho projektu
/*
        while(true) {
            ApplicationManager.getApplication().runReadAction(() -> {
                TestCoverageTask.getInstance().run();
            });
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        /*TimerTask timerTask = new TimerTask() {
            @Override public void run() {
                //ApplicationManager.getApplication().runReadAction( () -> {
                    TestCoverageTask.getInstance().run();
                //});
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 4000);
*/
    }

    @NotNull
    private String getProjectName() {
        return DataStore.getInstance().getActiveProject().getBaseDir().getName();
    }


    private void logAllJavaFiles() {
        String fileLog = getAllProjectJavaFiles().stream()
                .filter(x -> Objects.requireNonNull(x.getCanonicalPath()).startsWith(getProjectPath()))
                .map(x -> String.format("File name: %s,\tpath: %s\n", x.getName(), x.getCanonicalPath()))
                .reduce("", String::concat);

        log.log(Level.INFO, "All project files: {0}\n", fileLog);
    }

    private String getProjectPath() {
        return DataStore.getInstance().getActiveProject().getBaseDir().getCanonicalPath();
    }

    private Collection<VirtualFile> getAllProjectJavaFiles() {
        return FileBasedIndex.getInstance()
                .getContainingFiles(FileTypeIndex.NAME, JavaFileType.INSTANCE,
                        GlobalSearchScope.projectScope(DataStore.getInstance().getActiveProject()));
    }



    private void initListeners(Project project) {
        PsiManager.getInstance(project)
                .addPsiTreeChangeListener(new PsiTreeChangeListenerImpl());
    }

    @Override public void projectClosing(Project project) {
        //TODO Plugin cleanup
    }

    @Override
    public boolean canClose(@NotNull Project project) {
        return false;
    }
}
