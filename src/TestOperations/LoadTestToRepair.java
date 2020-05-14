package TestOperations;

import ProjectInfo.ClassMethodPair;
import ProjectInfo.TestMethodInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import resources.TestDataStore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class LoadTestToRepair {

    public static void prepareTestsForRepair(){

        Map<Integer, ClassMethodPair> modifiedClassMethods = TestDataStore.getModifiedClassMethods();
        for ( Iterator<ClassMethodPair> iterator = modifiedClassMethods.values().iterator(); iterator.hasNext();) {
            ClassMethodPair classMethodPair = iterator.next();
            try {
                ApplicationManager.getApplication().runReadAction( () -> {
                    ArrayList<PsiMethod> testPsiMethods = TestCoverageInformations.getTestMethodsNameByMethodName(classMethodPair.toString());
                    if (testPsiMethods != null) {
                        for (PsiMethod testPsiMethod : testPsiMethods) {
                            String result = MavenTestExecution.executeTest(testPsiMethod.getContainingClass().getName() + "#" + testPsiMethod.getName());
                            if (result != null) {
                                System.out.println("Test Name : [" + testPsiMethod.getName() + "]");
                                System.out.println("Return type : [" + getExpectedTypeFromMethod(testPsiMethod) + "] expected value : [" + result + "]");

                                String correctValue = prepareExpectedValueByPsiType(result, getExpectedTypeFromMethod(testPsiMethod));
                                if(correctValue == null)
                                    correctValue = "null";
                                TestDataStore.addTestMethodToRepair(new TestMethodInfo(testPsiMethod,correctValue));
                            }
                        }
                    }
                    iterator.remove();
                });
            } catch (Exception e) {
                System.out.println("repairTestPsiMethodError --- " + e.getMessage());
            }
        }
    }

    private static String prepareExpectedValueByPsiType(String expectedValue, String returnType){
        System.out.println(returnType);
        if(expectedValue == null || returnType == null)
            return expectedValue;
        else if(returnType.equals("PsiType:String")){
            return "\""+expectedValue+"\"";
        }else if (returnType.equals("PsiType:Integer")){
            return "Integer.valueOf("+expectedValue+")";
        }else if (returnType.equals("PsiType:Double")){
            return "Double.valueOf("+expectedValue+")";
        }return expectedValue;
    }

    private static String getExpectedTypeFromMethod(PsiMethod testMethod){
        PsiCodeBlock codeBlock = testMethod.getBody();
        if (codeBlock == null)
            return null;
        PsiStatement[] statements = codeBlock.getStatements();
        for (PsiStatement statement : statements) {
            if (!(statement instanceof PsiExpressionStatement))
                continue;
            PsiExpression expression = ((PsiExpressionStatement) statement).getExpression();
            if (!(expression instanceof PsiMethodCallExpression))
                continue;
            PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
            PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
            String referenceName = referenceExpression.getReferenceName();
            if (!("assertEquals".equals(referenceName)))
                continue;
            PsiExpressionList expressionList = methodCallExpression.getArgumentList();
            PsiExpression[] parameterExpressions = expressionList.getExpressions();
            PsiExpression actualParameter = parameterExpressions[1];

            if (actualParameter instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression actualMethodCallExpression = (PsiMethodCallExpression) actualParameter;
                PsiReferenceExpression actualReferenceExpression = actualMethodCallExpression.getMethodExpression();
                String returnType = actualMethodCallExpression.resolveMethod().getReturnType().toString();
                return returnType;
            }
            if (actualParameter instanceof PsiReferenceExpression) {
                PsiReferenceExpression variableReference = (PsiReferenceExpression) actualParameter;
                return variableReference.getType().toString();
            }
            return actualParameter.getType().toString();
        }
        return null;
    }
}
