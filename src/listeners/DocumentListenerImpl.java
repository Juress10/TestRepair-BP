/**
 *  Classu DocumentListenerImpl som prevzal a pozmenil z diplomovej práce Mareka Bruchatého kde sa nachádza pod menom LivetestDocumentListener.
 *
 * */

package listeners;

import ProjectInfo.ClassInfo;
import ProjectInfo.ClassMethodPair;
import ProjectInfo.MethodInfo;
import TestOperations.TestModel;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import resources.DataStore;
import utils.VirtualFileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DocumentListenerImpl implements DocumentListener {

    private static final Logger log = Logger.getLogger(DocumentListenerImpl.class.getName());
    private DataStore ds = DataStore.getInstance();

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        VirtualFile virtualFile = null;
        try {
            virtualFile = VirtualFileUtils.getVirtualFile(event.getDocument());
        }catch (Exception e){
            return;
        }
        if (!isJavaProjectFile(virtualFile)) {
            log.log(Level.INFO, String
                    .format("%s is and underscored file, not a java file or project file. Moving on...",
                            virtualFile.getName()));
            return;
        }

        ds.resetLastChangeTimeMillis();
        ds.resetLastChangeTimeMillisForTestRepair();
        System.out.println("zmena");

        ArrayList<ClassInfo> oldFiles= TestModel.getOldClasses();
        final PsiFileFactory factory = PsiFileFactory.getInstance(ds.getInstance().getActiveProject());
        PsiFile file = factory.createFileFromText("subor.java", JavaFileType.INSTANCE, event.getDocument().getText());

        if (file instanceof PsiJavaFile) {

            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiClass[] javaClasses = psiJavaFile.getClasses();

            for (ClassInfo old : oldFiles) {
                for (PsiClass psiClass : javaClasses) {
                    if (old.toString().equals(psiClass.getQualifiedName())) {
                        ArrayList<MethodInfo> oldMethods = old.getClassMethods();
                        PsiMethod[] newMethods = psiClass.getAllMethods();  //tu byva error
                        for (MethodInfo oldMethod : oldMethods) {
                            for (PsiMethod newMethod : newMethods) {
                                if(newMethod.getName().equals(oldMethod.getMethodName())){
                                    if(!newMethod.getText().equals(oldMethod.getMethodBody())) {
                                        if (!TestModel.findClassMethodPair(new ClassMethodPair(old.toString(), newMethod.getName()))) {
                                            TestModel.addModifiedClassMethod(new ClassMethodPair(old.toString(), newMethod.getName()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean isJavaProjectFile(VirtualFile virtualFile) {
        return virtualFile.getPath()
                .startsWith(Objects.requireNonNull(ds.getActiveProject().getBasePath())) && virtualFile
                .getName().toLowerCase().endsWith(".java") && !virtualFile.getName().startsWith("__");
    }

    private int getLineNumber(String fileContent, int offset) {
        return StringUtils.countMatches(fileContent.substring(0, offset), "\n");
    }

}
