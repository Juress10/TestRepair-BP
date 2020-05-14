package utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ide.highlighter.JavaFileType;
import resources.DataStore;


import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualFileUtils {

    private static final Logger logger = Logger.getLogger(VirtualFileUtils.class.getName());
    static private DataStore ds = DataStore.getInstance();

    private VirtualFileUtils() {
    }

    public static void getAllProjectFiles(Project project) {

        VirtualFile[] children = project.getProjectFile().getChildren();
        Collection<VirtualFile> files = null;
        try {
            files = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        for (VirtualFile f : files) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(f);
            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            final PsiClass[] classes = psiJavaFile.getClasses();
            System.out.println(classes[0].getMethods()[0].getBody().getText());
        }
        System.out.println("Number of files in project: "+ files.size());
    }

    public static VirtualFile getVirtualFile(Document document) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile == null) {
            throw new NullPointerException("NO_VIRTUAL_FILE_FOR_DOCUMENT_FOUND");
        }
        return virtualFile;
    }

    public static Collection<VirtualFile> getAllProjectJavaFiles() {
        Collection<VirtualFile> files = null;
        try {
            files = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(ds.getActiveProject()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return files;
    }

    public static ArrayList<PsiFile> getAllProjectPsiFiles(Project project) {
        Collection<VirtualFile> files = null;
        try {
            files = FileTypeIndex.getFiles(JavaFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        ArrayList<PsiFile> psiFiles = new ArrayList<>();
        for (VirtualFile f : files) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(f);
            psiFiles.add(psiFile);
        }
        return psiFiles;
    }
}
