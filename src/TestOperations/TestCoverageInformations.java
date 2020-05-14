package TestOperations;

import ProjectInfo.ClassInfo;
import ProjectInfo.MethodInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import resources.DataStore;

import java.util.*;

public class TestCoverageInformations {
                // String=nazov metody , HashSet su vsetky testy v ktorych sa volala
    static private HashMap<String, ArrayList<PsiMethod>> methodsTestCoverageMap = new HashMap<String, ArrayList<PsiMethod>>();
    static private HashMap<String, HashSet<String>> methodsTestCoverage = new HashMap<String, HashSet<String>>();
    static private DataStore ds = DataStore.getInstance();

    //TODO pridat este kontrolu parametrov funkciea ine overenia testCoverage...
    public static void initializeCoverageMap(){
        System.out.println("--- testCoverageMap ---");
        methodsTestCoverageMap = new HashMap<String, ArrayList<PsiMethod>>();
        ApplicationManager.getApplication().runReadAction(() -> {
            try {
                for (ClassInfo testClass : TestDataStore.getOldTestClasses()) {
                    for (MethodInfo testMethod : testClass.getClassMethods()) {
                        PsiCodeBlock codeBlock = testMethod.getPsiMethod().getBody();

                        if (codeBlock == null)
                            continue;
                        PsiStatement[] statements = codeBlock.getStatements();

                        for (PsiStatement statement : statements) {

                            if (checkForMethodCall(statement.getOriginalElement())) {
                                if (classExistanceInProject(findMethodCall(statement.getOriginalElement()).resolveMethod().getContainingClass().getName())) {
                                    try {
                                        addPsiMethodtestCoverage(findMethodCall(statement.getOriginalElement()).resolveMethod().getContainingClass().getName() + "#" + findMethodCall(statement.getOriginalElement()).resolveMethod().getName(), testMethod.getPsiMethod());
                                    } catch (Exception e) {
                                        System.out.println("PsiMethodAddError --- " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
                for (String name : methodsTestCoverageMap.keySet()) {
                    String key = name.toString();
                    for (PsiMethod psiMethod : methodsTestCoverageMap.get(key)) {
                        try {
                            System.out.println("method: " + key + " --- " + psiMethod.getContainingClass().getName() + "#" + psiMethod.getName());
                        } catch (Exception e) {
                            System.out.println("iteratingError --- " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("initializeCoverageMapError --- " + e.getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------//
    //                                          String coverage
    //----------------------------------------------------------------------------------------------------------------//

    public static HashSet<String> getTestsNameByMethodName(String methodName){
        if(methodsTestCoverage.containsKey(methodName))
            return methodsTestCoverage.get(methodName);
        else
            return null;
    }

    private static void addMethodtestCoverage(String methodName, String testName){
        if(methodsTestCoverage.containsKey(methodName)){
            Iterator<String> i = methodsTestCoverage.get(methodName).iterator();
            while (i.hasNext()){
                if(i.next() == testName)
                return;
            }
            methodsTestCoverage.get(methodName).add(testName);
        }else{
            HashSet<String> testSet = new HashSet<>(Arrays.asList(testName));
            methodsTestCoverage.put(methodName, testSet);
        }
    }

    //----------------------------------------------------------------------------------------------------------------//
    //                                          PsiMethod coverage
    //----------------------------------------------------------------------------------------------------------------//

    public static ArrayList<PsiMethod> getTestMethodsNameByMethodName(String methodName){
        if(methodsTestCoverageMap.containsKey(methodName))
            return methodsTestCoverageMap.get(methodName);
        else
            return null;
    }

    private static void addPsiMethodtestCoverage(String methodName, PsiMethod testName){
        if(methodsTestCoverageMap.containsKey(methodName)){
            for (PsiMethod psiMethod : methodsTestCoverageMap.get(methodName)) {
                if(psiMethod.getName() == testName.getName())
                    return;
            }
            methodsTestCoverageMap.get(methodName).add(testName);
        }else{
            ArrayList<PsiMethod> testSet = new ArrayList<>(Arrays.asList(testName));
            methodsTestCoverageMap.put(methodName, testSet);
        }
    }

    //----------------------------------------------------------------------------------------------------------------//

    private static boolean classExistanceInProject(String name){
        if(TestDataStore.isProjectClass(name))
            return true;
        else
            return false;
    }

    private static boolean checkForMethodCall(PsiElement element){
        try {
            if (element instanceof PsiExpressionStatement) {
                PsiExpression expression = ((PsiExpressionStatement) element).getExpression();
                if (!(expression instanceof PsiMethodCallExpression))
                    return false;
                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                String referenceName = referenceExpression.getReferenceName();

                if ("assertEquals".equals(referenceName)) {
                    PsiExpressionList expressionList = methodCallExpression.getArgumentList();
                    PsiExpression[] parameterExpressions = expressionList.getExpressions();
                    PsiExpression actual = parameterExpressions[1];
                    if (actual instanceof PsiMethodCallExpression) {
                        return true;
                    }
                }else if ("assertNull".equals(referenceName) || "assertNotNull".equals(referenceName)){
                    PsiExpressionList expressionList = methodCallExpression.getArgumentList();
                    PsiExpression[] parameterExpressions = expressionList.getExpressions();
                    PsiExpression actual = parameterExpressions[0];
                    if (actual instanceof PsiMethodCallExpression) {
                        return true;
                    }
                }else return false;
            }
            if (element instanceof PsiDeclarationStatement) {
                PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) element;
                for (PsiElement declarationElement : declarationStatement.getDeclaredElements()) {
                    if (declarationElement instanceof PsiVariable) {
                        PsiVariable variable = (PsiVariable) declarationElement;
                        PsiExpression initializer = variable.getInitializer();
                        if (initializer instanceof PsiMethodCallExpression) {
                            return true;
                        }
                    }
                }
            }
            if (element instanceof PsiExpressionStatement) {
                PsiExpression expression = ((PsiExpressionStatement) element).getExpression();
                if (expression instanceof PsiMethodCallExpression) {
                    return true;
                }
            }
        }catch (Exception e){
            System.out.println("checkForMethodCallError --- "+ e.getMessage());
        }
        return false;
    }

    private static PsiMethodCallExpression findMethodCall(PsiElement element) {

        try {
            if (element instanceof PsiExpressionStatement) {
                PsiExpression expression = ((PsiExpressionStatement) element).getExpression();
                if (!(expression instanceof PsiMethodCallExpression))
                    return null;
                PsiMethodCallExpression methodCallExpression = (PsiMethodCallExpression) expression;
                PsiReferenceExpression referenceExpression = methodCallExpression.getMethodExpression();
                String referenceName = referenceExpression.getReferenceName();
                if ("assertEquals".equals(referenceName)) {
                    PsiExpressionList expressionList = methodCallExpression.getArgumentList();
                    PsiExpression[] parameterExpressions = expressionList.getExpressions();
                    PsiExpression actual = parameterExpressions[1];
                    if (actual instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression actualMethodCallExpression = (PsiMethodCallExpression) actual;
                        return actualMethodCallExpression;
                    }
                }else if ("assertNull".equals(referenceName) || "assertNotNull".equals(referenceName)){
                    PsiExpressionList expressionList = methodCallExpression.getArgumentList();
                    PsiExpression[] parameterExpressions = expressionList.getExpressions();
                    PsiExpression actual = parameterExpressions[0];
                    if (actual instanceof PsiMethodCallExpression) {
                        PsiMethodCallExpression actualMethodCallExpression = (PsiMethodCallExpression) actual;
                        return actualMethodCallExpression;
                    }
                }
            }
            if (element instanceof PsiDeclarationStatement) {
                PsiDeclarationStatement declarationStatement = (PsiDeclarationStatement) element;
                for (PsiElement declarationElement : declarationStatement.getDeclaredElements()) {
                    if (declarationElement instanceof PsiVariable) {
                        PsiVariable variable = (PsiVariable) declarationElement;
                        PsiExpression initializer = variable.getInitializer();
                        if (initializer instanceof PsiMethodCallExpression) {
                            return (PsiMethodCallExpression) initializer;
                        }
                    }
                }
            }
            if (element instanceof PsiExpressionStatement) {
                PsiExpression expression = ((PsiExpressionStatement) element).getExpression();
                if (expression instanceof PsiMethodCallExpression) {
                    return (PsiMethodCallExpression) expression;
                }
            }
        }catch (Exception e){
            System.out.println("findMethodCallError --- "+ e.getMessage());
        }
        return null;
    }
}
