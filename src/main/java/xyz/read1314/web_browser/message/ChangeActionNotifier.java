package xyz.read1314.web_browser.message;


import com.intellij.openapi.project.Project;
import com.intellij.util.messages.Topic;

public interface ChangeActionNotifier {

    @Topic.ProjectLevel
    Topic<ChangeActionNotifier> CHANGE_ACTION_TOPIC =
            Topic.create("menu click msg", ChangeActionNotifier.class);

    void beforeAction(Project project);

    void afterAction(Project project);
}
