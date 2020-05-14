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

}
