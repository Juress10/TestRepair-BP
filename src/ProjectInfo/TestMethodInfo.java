package ProjectInfo;

import com.intellij.psi.PsiMethod;

public class TestMethodInfo extends MethodInfo{

    private String valueToReplace;

    public TestMethodInfo(PsiMethod psiMethod,String valueToReplace){
        super(psiMethod);
        this.valueToReplace = valueToReplace;
    }

    @Override
    public String toString() {
        return "test: "+ getMethodName() +" hodnota: "+ valueToReplace;
    }

    public String getHashCode() {
        return getMethodName() + valueToReplace;
    }

    public String getValueToReplace() {
        return valueToReplace;
    }

    public void setValueToReplace(String valueToReplace) {
        this.valueToReplace = valueToReplace;
    }
}
