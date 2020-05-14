package ProjectInfo;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;

public class ClassInfo {

    private PsiClass testClass;
    private String className;
    private ArrayList<MethodInfo> classMethods;

    public ClassInfo(){}

    public ClassInfo(PsiClass testClass){
        this.testClass = testClass;
        this.className = testClass.getQualifiedName();
        this.classMethods = new ArrayList<MethodInfo>();
    }

    public ClassInfo(PsiClass testClass,ArrayList<PsiMethod> classMethods){
        this.testClass=testClass;
        this.className = testClass.getQualifiedName();
        this.classMethods = new ArrayList<MethodInfo>();
        for(PsiMethod method : classMethods){
            this.classMethods.add(new MethodInfo(method));
        }
    }

    @Override
    public String toString() {
        return className;
    }

    public void printClassInfo(){
        System.out.println("[class name: "+testClass.getQualifiedName()+" ]\n");
        for (MethodInfo method: classMethods) {
            System.out.println("method: "+ method.toString());
        }
        System.out.println("\n");
    }

    public PsiClass getTestClass() {
        return testClass;
    }

    public void setTestClass(PsiClass testClass) {
        this.testClass = testClass;
    }

    public ArrayList<MethodInfo> getClassMethods() {
        return classMethods;
    }

    public void setClassMethods(ArrayList<PsiMethod> classMethods) {
        this.classMethods = new ArrayList<MethodInfo>();
        for(PsiMethod method : classMethods){
            this.classMethods.add(new MethodInfo(method));
        }
    }

    public void addClassMethod(PsiMethod classMethod) {
        this.classMethods.add(new MethodInfo(classMethod));
    }
}
