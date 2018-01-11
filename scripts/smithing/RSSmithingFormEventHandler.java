package j333.scripts.smithing;

import j333.lib.handlers.RSFormEventHandler;

import javax.swing.*;

public interface RSSmithingFormEventHandler extends RSFormEventHandler
{
    void formDidSelectItemAt(RSSmithingForm form, int index);
    void formDidSelectSection(RSSmithingForm form, int section);
    void formDidPressStartButton(RSSmithingForm form, JButton startButton);
}
