/**
 *  Classou PluginRegistration som sa inšpiroval podľa classy PluginInitialization v diplomovej práci Mareka Bruchatého.
 *
 * */

package Plugin;

import com.intellij.openapi.components.ApplicationComponent;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.ProjectManager;
import listeners.DocumentListenerImpl;
import listeners.ProjectManagerListenerImpl;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginRegistration implements ApplicationComponent {


    @NotNull
    public String getComponentName() {
        return "TestRepair";
    }

    @Override
    public void initComponent() {
        initProjectManagerListeners();
        initDocumentListeners();
    }

    private void initDocumentListeners() {
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(
                new DocumentListenerImpl());
    }

    private void initProjectManagerListeners() {
        ProjectManager.getInstance()
                .addProjectManagerListener(new ProjectManagerListenerImpl());
    }

    public void disposeComponent() {
    }
}