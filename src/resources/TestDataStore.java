package resources;

import ProjectInfo.ClassInfo;
import ProjectInfo.ClassMethodPair;
import ProjectInfo.TestMethodInfo;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import resources.DataStore;
import utils.VirtualFileUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class TestDataStore {

    static private ArrayList<ClassInfo> oldTestClasses = new ArrayList<ClassInfo>();
    static private ArrayList<ClassInfo> oldClasses = new ArrayList<ClassInfo>();
    static private ConcurrentHashMap<Integer, ClassMethodPair> modifiedClassMethods = new ConcurrentHashMap<Integer, ClassMethodPair>();
    static private HashSet<String> projectClassNames = new HashSet<>();
    static private HashMap<String, TestMethodInfo> testMethodsToRepair = new HashMap<>();

    public synchronized static void loadAllTestClasses(){
        System.out.println("----------------------------------- Stage 1 ---------------------------------");
        oldTestClasses = new ArrayList<ClassInfo>();
        oldClasses = new ArrayList<ClassInfo>();
        modifiedClassMethods = new ConcurrentHashMap<Integer, ClassMethodPair>();
        projectClassNames = new HashSet<>();

        loadAllClasses(oldClasses);
        loadAllTests(oldTestClasses);
    }

//--------------------------------------------------------------------------------------------------------------------//

    public synchronized static void printAllTestClasses(){
        System.out.println("number of classes: "+ oldTestClasses.size());
        for (ClassInfo tc : oldTestClasses) {
            tc.printClassInfo();
        }
    }

//--------------------------------------------------------------------------------------------------------------------//

    public static void addTestMethodToRepair(TestMethodInfo testMethod){
        if(testMethodsToRepair == null)
            testMethodsToRepair = new HashMap<>();
        testMethodsToRepair.put(testMethod.getHashCode(),testMethod);
    }

    public static HashMap<String , TestMethodInfo> getTestMethodsToRepair(){
        return testMethodsToRepair;
    }

    public static void removeTestMethod(String hash){
        testMethodsToRepair.remove(hash);
    }

    public static void printAllTestMethodsToRepair(){
        System.out.println("printing All Test Methods To Repair");
        for (TestMethodInfo value : testMethodsToRepair.values()) {
            System.out.println(value.toString());
        }
    }

//--------------------------------------------------------------------------------------------------------------------//

    public static void addModifiedClassMethod(ClassMethodPair pair){
        modifiedClassMethods.put(pair.hashCode(),pair);
    }

    public static boolean findClassMethodPair(ClassMethodPair pair){
        if(modifiedClassMethods.containsKey(pair.hashCode())){
            return true;
        }else
            return false;
    }

    public static void printAllClassMethodPair(){
        for (ClassMethodPair value : modifiedClassMethods.values()) {
            System.out.println(value.toString());
        }
    }

    public static boolean isMapEmpty(){
        return modifiedClassMethods.isEmpty();
    }

    public static Map<Integer, ClassMethodPair> getModifiedClassMethods(){
        return modifiedClassMethods;
    }

//--------------------------------------------------------------------------------------------------------------------//

    public static ArrayList<ClassInfo> getOldTestClasses(){
        return  oldTestClasses;
    }

    public static ArrayList<ClassInfo> getOldClasses(){
        return  oldClasses;
    }

    public static HashSet<String> getProjectClassNames(){
        return projectClassNames;
    }

    public static boolean isProjectClass(String name){
        if(projectClassNames.contains(name))
            return true;
        else
            return false;
    }

    public static void loadAllTests(ArrayList<ClassInfo> testClasses){

        System.out.println("--- loadAllTests ---");
        ApplicationManager.getApplication().runReadAction(() -> {
            for (VirtualFile file : VirtualFileUtils.getAllProjectJavaFiles()) {
                PsiFile psiFile = PsiManager.getInstance(DataStore.getInstance().getActiveProject()).findFile(file);
                if (psiFile instanceof PsiJavaFile) {

                    PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                    final PsiClass[] classes = psiJavaFile.getClasses();

                    for (PsiClass psiClass : classes) {

                        ClassInfo currentClass = new ClassInfo(psiClass);

                        PsiMethod[] methods = psiClass.getMethods();
                        for (PsiMethod method : methods) {

                            PsiCodeBlock codeBlock = method.getBody();

                            if (codeBlock == null)
                                System.out.println("null code block");
                            PsiStatement[] statements = codeBlock.getStatements();

                            if (!AnnotationUtil.isAnnotated(method, "org.junit.jupiter.api.Test", false)) {
                                //System.out.println(method.getName() +" - "+ method.getModifierList());
                            } else {
                                currentClass.addClassMethod(method);
                                if (projectClassNames.contains(psiClass.getQualifiedName()))
                                    projectClassNames.remove(psiClass.getQualifiedName());
                            }
                        }
                        if (!currentClass.getClassMethods().isEmpty()) {
                            testClasses.add(currentClass);
                        }
                    }
                }
            }
        });
    }

    private static void loadAllClasses(ArrayList<ClassInfo> oldClasses) {
        System.out.println("--- loadAllClasses ---");
        ApplicationManager.getApplication().runReadAction(() -> {
            for (VirtualFile file : VirtualFileUtils.getAllProjectJavaFiles()) {
                PsiFile psiFile = PsiManager.getInstance(DataStore.getInstance().getActiveProject()).findFile(file);
                if (psiFile instanceof PsiJavaFile) {

                    PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                    final PsiClass[] classes = psiJavaFile.getClasses();

                    for (PsiClass psiClass : classes) {
                        oldClasses.add(new ClassInfo(psiClass, new ArrayList<PsiMethod>(Arrays.asList(psiClass.getMethods()))));
                        projectClassNames.add(psiClass.getQualifiedName());
                    }
                }
            }
        });
    }

}
