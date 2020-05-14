/**
 *  Funkcie repairTest a getExpectedTypeFromMethod som prevzal a pozmenil z bakalárskej práce Veroniky Klacíkovej.
 *
 * */
package TestOperations;

import ProjectInfo.ClassMethodPair;
import ProjectInfo.TestMethodInfo;
import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.*;
import resources.DataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRepair {

    public static void repairTestsByModifiedMethods(){

        //try {
                Map<Integer, ClassMethodPair> modifiedClassMethods = TestModel.getModifiedClassMethods();
            for ( Iterator<ClassMethodPair> iterator = modifiedClassMethods.values().iterator(); iterator.hasNext();) {
                ClassMethodPair classMethodPair = iterator.next();
                try {
                    ApplicationManager.getApplication().runReadAction( () -> {
                        ArrayList<PsiMethod> testPsiMethods = TestCoverageInformations.getTestMethodsNameByMethodName(classMethodPair.toString());
                        if (testPsiMethods != null) {
                            for (PsiMethod testPsiMethod : testPsiMethods) {
                                String result = JUnit.executeTest(testPsiMethod.getContainingClass().getName() + "#" + testPsiMethod.getName());
                                if (result != null) {
                                    System.out.println("Test Name : [" + testPsiMethod.getName() + "]");
                                    System.out.println("Return type : [" + getExpectedTypeFromMethod(testPsiMethod) + "] expected value : [" + result + "]");

                                    String correctValue = prepareExpectedValueByPsiType(result, getExpectedTypeFromMethod(testPsiMethod));
                                    //TODO mozno bude error ked bude null correctValue
                                    if(correctValue == null)
                                        correctValue = "null";
                                    TestModel.addTestMethodToRepair(new TestMethodInfo(testPsiMethod,correctValue));
                                }
                            }
                        }
                        iterator.remove();
                    });
                } catch (Exception e) {
                    System.out.println("repairTestPsiMethodError --- " + e.getMessage());
                }
            }
        /*}catch (Exception e){
            System.out.println("repairTestError --- " + e.getMessage() + e.getStackTrace());
        }*/
    }

    public static void repairAllTests(){
        System.out.println("--- All Test Repairing ---");

        for ( Iterator<TestMethodInfo> iterator = TestModel.getTestMethodsToRepair().values().iterator(); iterator.hasNext();) {
            TestMethodInfo testMethod = iterator.next();

            System.out.println("repairing: "+testMethod.getMethodName()+" with: "+testMethod.getValueToReplace());
            ApplicationManager.getApplication().runReadAction( () -> {
                repairTest(testMethod.getPsiMethod(), testMethod.getValueToReplace());
                iterator.remove();
            });
        }
    }

    private static boolean repairTest(PsiMethod testMethod, String correctValue){

        PsiCodeBlock codeBlock = testMethod.getBody();
        if (codeBlock == null)
            return false;
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

            if ("assertEquals".equals(referenceName)) {

                PsiExpressionList expressionList = methodCallExpression.getArgumentList();
                PsiExpression[] parameterExpressions = expressionList.getExpressions();
                PsiExpression expectedExpression = parameterExpressions[0];

                PsiElementFactory factory = JavaPsiFacade.getInstance(DataStore.getInstance().getActiveProject()).getElementFactory();
                PsiExpression newExpression = (PsiExpression) factory.createExpressionFromText(correctValue, null);

                ApplicationManager.getApplication().invokeLater(() -> {
                    WriteCommandAction.runWriteCommandAction(DataStore.getInstance().getActiveProject(), () -> {
                        expectedExpression.replace(newExpression);
                    });
                });
                return true;
            }else if ("assertNull".equals(referenceName) || "assertNotNull".equals(referenceName)){//TODO spojit ich do 1
                PsiElementFactory factory = JavaPsiFacade.getInstance(DataStore.getInstance().getActiveProject()).getElementFactory();
                String newExpressionText = "assertNull".equals(referenceName) ? "assertNotNull" : "assertNull";
                PsiReferenceExpression newExpression = (PsiReferenceExpression) factory.createExpressionFromText(newExpressionText, null);

                ApplicationManager.getApplication().invokeLater(() -> {
                    WriteCommandAction.runWriteCommandAction(DataStore.getInstance().getActiveProject(), () -> {
                        referenceExpression.replace(newExpression);
                    });
                });
                return true;
            }
        }
        return false;
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
            if (!("assertEquals".equals(referenceName)))                       //TODO funguje to na predpoklade ze assert testuje funkciu
                continue;
            PsiExpressionList expressionList = methodCallExpression.getArgumentList();
            PsiExpression[] parameterExpressions = expressionList.getExpressions();
            PsiExpression actualParameter = parameterExpressions[1];

            //overenie ci je vysledok aktualnyParameter metoda metoda - problem lebo ocakava len funkciu, pridat premennu
            if (actualParameter instanceof PsiMethodCallExpression) {
                PsiMethodCallExpression actualMethodCallExpression = (PsiMethodCallExpression) actualParameter;
                PsiReferenceExpression actualReferenceExpression = actualMethodCallExpression.getMethodExpression();
                String returnType = actualMethodCallExpression.resolveMethod().getReturnType().toString();
                return returnType;
            }
            if (actualParameter instanceof PsiReferenceExpression) {
                PsiReferenceExpression variableReference = (PsiReferenceExpression) actualParameter;
                return variableReference.getType().toString();  //todo overit dobre tuto funkcnost
            }
            return actualParameter.getType().toString();
        }
        return null;
    }
}
