package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import config.PluginConfigComponent;

import static decompiler.Decompiler.decompile;

/**
 * Action for triggering decompilation.
 * Dmitriy Zabranskiy, 2013
 */

public class DecompileAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        PluginConfigComponent config = ApplicationManager.getApplication().getComponent(PluginConfigComponent.class);

        VirtualFile decompiledFile = decompile(config, DataKeys.VIRTUAL_FILE.getData(e.getDataContext()));

        if (decompiledFile != null) {
            if (!config.isShowPrettyEnabled()) {
                assert project != null;
                final PsiFile psiFile = PsiManager.getInstance(project).findFile(decompiledFile);
                // Reformat decompiled code by IDEA
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        assert psiFile != null;
                        CodeStyleManager.getInstance(project).reformat(psiFile);
                    }
                });
            }
            FileEditorManager.getInstance(project).openFile(decompiledFile, true);
        }
    }
}
