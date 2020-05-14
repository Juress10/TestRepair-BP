package TestOperations;

import ProjectInfo.ClassInfo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaStubIndexKeys;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndexImpl;
import resources.DataStore;
import utils.VirtualFileUtils;

import java.util.Collection;
import java.util.stream.Stream;

public class TestExecution {


    public static void loadAllTests(){

        System.out.println("loadAllTests");


        for (VirtualFile file : VirtualFileUtils.getAllProjectJavaFiles()) {
            PsiFile psiFile = PsiManager.getInstance(DataStore.getInstance().getActiveProject()).findFile(file);
            if (psiFile instanceof PsiJavaFile) {

                PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                final PsiClass[] classes = psiJavaFile.getClasses();

                for (PsiClass psiClass : classes) {

                    PsiMethod[] methods = psiClass.getMethods();
                    for (PsiMethod method : methods) {

                        System.out.println(method.getName());
                        /*if(!AnnotationUtil.isAnnotated(method, "org.junit.jupiter.api.Test", false)){
                            System.out.println(method.getName() +" - "+ method.getModifierList());
                        }*/

                        /*System.out.println(method.getName());
                        if(hasAnnotation(method,"org.junit.jupiter.api.Test")){
                            System.out.println(method.getName());
                        }
                        PsiModifierList modifierList = method.getModifierList();
                        for (PsiAnnotation psiAnnotation : modifierList.getAnnotations()) {
                            System.out.println(psiAnnotation.getQualifiedName());
                        }
                        */

                    }
                }
            }
        }

    }

    private static boolean hasAnnotation(PsiMethod psiMethod, String fqn) {
        return Stream.of(psiMethod.getAnnotations())
                .anyMatch(psiAnnotation -> fqn.equals(psiAnnotation.getQualifiedName()));
    }



}
