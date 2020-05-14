package TestOperations;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import resources.DataStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JUnit {
//maven way
//mvn -f "C:\subory\Bakalarka\bakalarka implementacia\pom.xml" test -Dtest=StringUtilsTest#testToString

    private static final String EOL = "\n";
    private static final String TEST_SUCCESS = "[INFO] Tests run: 1, Failures: 0, Errors: 0";
    private static final String BUILD_FAILURE = "[INFO] Tests run: 1, Failures: 1, Errors: 0";

    public static String executeTest(String testName) {

        String cmd = "mvn -f \""+ DataStore.getInstance().getPomXmlPath() +"\" test -Dtest="+ testName;
        Process process;

        try {
            process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd});
        } catch (IOException e) {
            e.printStackTrace();
            process = null;
        }

        String output = getStdInput(process);
        int exitVal = 0;
        try {
            exitVal = process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!output.contains(TEST_SUCCESS)) {
            return processOutput(output);
        }else
            return null;
    }

    private static String processOutput(String output){
        String lines[] = output.split("\\r?\\n");
        String expectedValueLine = null;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].contains("[ERROR] Failures:")) {
                expectedValueLine=lines[i + 1];
                break;
            }
        }
        return getExpectedValueFromLine(expectedValueLine);
    }

    private static String getExpectedValueFromLine(String line){
        int startIndex =  line.indexOf(">",1);
        int frontIndex = line.indexOf("<",startIndex + 1);
        int backIndex =  line.indexOf(">",startIndex + 1);
        if((frontIndex >= 0 && frontIndex <= line.length()) && (backIndex >= 0 && backIndex <= line.length()))
            return line.substring(frontIndex + 1, backIndex);
        else
            return "null";
    }

    private static String getStdInput(Process process) {
        BufferedReader stdInput =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        return stdInput.lines().collect(Collectors.joining(EOL));
    }

    private static String getStdError(Process process) {
        BufferedReader stdError =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        return stdError.lines().collect(Collectors.joining(EOL));
    }

}
