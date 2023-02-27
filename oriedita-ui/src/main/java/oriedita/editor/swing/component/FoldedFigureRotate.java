package oriedita.editor.swing.component;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import oriedita.editor.databinding.FoldedFigureModel;
import oriedita.editor.databinding.MeasuresModel;
import oriedita.editor.service.ButtonService;
import oriedita.editor.swing.OnlyDoubleAdapter;
import origami.crease_pattern.OritaCalc;
import origami.folding.FoldedFigure;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Insets;

public class FoldedFigureRotate extends JPanel {
    private JButton foldedFigureRotateAntiClockwiseButton;
    private JPanel panel1;
    private JTextField foldedFigureRotateTextField;
    private JButton foldedFigureRotateClockwiseButton;
    private JButton foldedFigureRotateSetButton;

    public FoldedFigureRotate(ButtonService buttonService, FoldedFigureModel foldedFigureModel, MeasuresModel measuresModel) {
        add($$$getRootComponent$$$());

        buttonService.registerButton(foldedFigureRotateAntiClockwiseButton, "foldedFigureRotateAntiClockwiseAction");
        buttonService.registerButton(foldedFigureRotateSetButton, "foldedFigureRotateSetAction");
        buttonService.registerButton(foldedFigureRotateClockwiseButton, "foldedFigureRotateClockwiseAction");

        foldedFigureRotateAntiClockwiseButton.addActionListener(e -> {
            if (foldedFigureModel.getState() == FoldedFigure.State.BACK_1) {
                foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(foldedFigureModel.getRotation() - 11.25));
            } else {
                foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(foldedFigureModel.getRotation() + 11.25));
            }
        });

        foldedFigureRotateSetButton.addActionListener(e -> foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(measuresModel.string2double(foldedFigureRotateTextField.getText(), foldedFigureModel.getRotation()))));
        foldedFigureRotateClockwiseButton.addActionListener(e -> {

            if (foldedFigureModel.getState() == FoldedFigure.State.BACK_1) {
                foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(foldedFigureModel.getRotation() + 11.25));
            } else {
                foldedFigureModel.setRotation(OritaCalc.angle_between_m180_180(foldedFigureModel.getRotation() - 11.25));
            }
        });
        foldedFigureRotateTextField.addActionListener(e -> foldedFigureRotateSetButton.doClick());
        foldedFigureRotateTextField.getDocument().addDocumentListener(new OnlyDoubleAdapter(foldedFigureRotateTextField));
    }

    public void setText(String text) {
        foldedFigureRotateTextField.setText(text);
        foldedFigureRotateTextField.setCaretPosition(0);
    }

    public String getText() {
        return foldedFigureRotateTextField.getText();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        foldedFigureRotateAntiClockwiseButton = new JButton();
        foldedFigureRotateAntiClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_p_kaiten.png")));
        panel1.add(foldedFigureRotateAntiClockwiseButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureRotateTextField = new JTextField();
        foldedFigureRotateTextField.setColumns(2);
        panel1.add(foldedFigureRotateTextField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(30, -1), null, null, 0, false));
        foldedFigureRotateSetButton = new JButton();
        foldedFigureRotateSetButton.setText("S");
        panel1.add(foldedFigureRotateSetButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        foldedFigureRotateClockwiseButton = new JButton();
        foldedFigureRotateClockwiseButton.setIcon(new ImageIcon(getClass().getResource("/ppp/oriagari_m_kaiten.png")));
        panel1.add(foldedFigureRotateClockwiseButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
