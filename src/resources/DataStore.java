package resources;

import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
//import model.ChangedFile;
//import model.CoverageMapping;
//import model.coverage.CovFile;
//import model.coverage.CovTest;

import java.util.*;

public class DataStore {

    private static final int TEST_DELAY = 10000;
    private static final int REPAIR_DELAY = 20000;
    private static DataStore ourInstance = new DataStore();
    private static String pomXmlPath = null;
    // Current active project
    private Project activeProject;
    // Timestamp of last source code change in editor
    private static long lastChangeTimeMillis = 0;
    public static synchronized DataStore getInstance() {
        return ourInstance;
    }

    private DataStore() {
    }

    /*
     * Active project
     * */
    public Project getActiveProject() {
        return activeProject;
    }

    public void setActiveProject(Project project) {
        this.activeProject = project;
    }

    public String getPomXmlPath(){
        return activeProject.getBasePath()+"/pom.xml";
    }

    //TODO doplnit vyhladanie pom.xml suboru
    public void loadPomXmlFile(){
        Collection<VirtualFile> xmlFiles = FilenameIndex.getAllFilesByExt(activeProject, "xml");
        for (VirtualFile file : xmlFiles) {
            System.out.println(file.getCanonicalPath());
        }
    }

    /*
     * Change timestamp
     * */
    public long getLastChangeTimeMillis() {
        return lastChangeTimeMillis;
    }

    public void setLastChangeTimeMillis(long lastChangeTimeMillis) {
        this.lastChangeTimeMillis = lastChangeTimeMillis;
    }

    public void resetLastChangeTimeMillis() {
        this.lastChangeTimeMillis = System.currentTimeMillis();
    }

    public long getDelay(){
        return System.currentTimeMillis() - lastChangeTimeMillis;
    }

    public boolean testDelayElapsed() {
        return System.currentTimeMillis() - lastChangeTimeMillis > TEST_DELAY;
    }

    public boolean repairDelayElapsed() {
        return System.currentTimeMillis() - lastChangeTimeMillis > REPAIR_DELAY;
    }
}
