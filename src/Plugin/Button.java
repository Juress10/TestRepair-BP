package Plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import resources.DataStore;

public class Button extends AnAction {

    public Button() {
        super("button");
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //System.out.println(DataStore.getInstance().getDelay());

        if (MainLoop.isRunning == false) {
            MainLoop main = new MainLoop();
        }
    }

}
