package ProjectInfo;

import com.intellij.psi.PsiMethod;

public class MethodInfo {

    private PsiMethod psiMethod;
    private String methodName;
    private String methodBody;

    public MethodInfo(){}

    public MethodInfo(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        this.methodName = psiMethod.getName();
        this.methodBody = psiMethod.getText();
    }

    public MethodInfo(PsiMethod psiMethod, String methodName) {
        this.psiMethod = psiMethod;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return methodName;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }
}
