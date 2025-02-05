package oriedita.editor.service;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import java.awt.Container;
import java.util.Map;

public interface ButtonService {
    void setOwner(JFrame owner);

    void registerLabel(JLabel label, String key);

    void registerButton(AbstractButton button, String key);

    void registerButton(AbstractButton button, String key, boolean wantToReplace);

    void Button_shared_operation(boolean resetLineStep);

    default void Button_shared_operation() {
        Button_shared_operation(true);
    }

    Map<KeyStroke, AbstractButton> getHelpInputMap();

    Map<String, AbstractButton> getPrefHotkeyMap();

    void addDefaultListener(Container root);

    void addDefaultListener(Container root, boolean wantToReplace);

    void addKeyStroke(KeyStroke keyStroke, AbstractButton button, String key, boolean addToHelpMap);

    void setTooltip(AbstractButton button, String key);
}
