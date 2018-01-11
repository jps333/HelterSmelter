package j333.scripts.smithing;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import j333.lib.RSTimeSpan;
import j333.lib.invokers.RSActionInvoker;
import j333.scripts.helpers.RSDebugHelper;
import j333.scripts.handlers.RSDebugEventHandler;
import j333.scripts.smithing.actions.*;
import j333.scripts.smithing.models.RSSmithingItem;
import j333.scripts.smithing.models.RSSmithingContext;

import org.powerbot.script.rt6.*;
import org.powerbot.script.Script;
import org.powerbot.script.PollingScript;

import javax.swing.*;

@Script.Manifest(name="Helter Smelter", description="A RS3 script for smelting ores.", properties="author=J333; client=6; topic=1341312;")

public class RSSmithingController extends PollingScript<ClientContext> implements RSSmithingFormEventHandler, RSDebugEventHandler
{
    private static final RSTimeSpan DEFAULT_WAIT = RSTimeSpan.fromSeconds(10);

    private int selectedSection;
    private RSSmithingForm form;
    private float experienceGained;
    private boolean didStart = false;
    private LocalDateTime timeStarted;
    private RSActionInvoker actionManager;
    private RSSmithingItem selectedSmithingItem;
    private RSSmithingWebService smithingWebService = new RSSmithingWebService();
    private float previousExperience = this.ctx.skills.experience(Constants.SKILLS_SMITHING);

    /********* Script *********/

    public void start()
    {
        RSDebugHelper.setEventHandler(this);
        EventQueue.invokeLater(this::setupForm);
        this.actionManager = new RSActionInvoker(this.ctx);
    }

    public void poll()
    {
        if (!this.didStart) { return; }
        if (this.timeStarted == null) { this.timeStarted = LocalDateTime.now(); }

        this.updateUI();
        this.actionManager.executeNextAction();
    }

    /********* Setup *********/

    private void setupForm()
    {
        this.form = new RSSmithingForm(this.getStorageDirectory());
        this.form.load();
        this.form.setEventHandler(this);
        this.form.loadTypes(this.smithingWebService.getSections());
        this.form.loadItems(this.smithingWebService.getItemsInSection(1).stream().map(RSSmithingItem::getName).collect(Collectors.toList()));
    }

    /********* Helpers *********/

    private RSSmithingContext getSmithingContext()
    {
        RSSmithingContext smithingContext = new RSSmithingContext();
        smithingContext.bankIds = this.form.getBankIds();
        smithingContext.smithingItem = this.selectedSmithingItem;
        smithingContext.smelterNames = this.form.getSmelterNames();
        return smithingContext;
    }

    /********* UI *********/

    private void updateUI()
    {
        if (this.form == null) { return; }

        this.updateRunTime();
        this.updateExperienceGained();
        this.updateExperiencePerHour();
    }

    private void updateRunTime()
    {
        long milliseconds = this.ctx.controller.script().getTotalRuntime();
        long second = (milliseconds / 1000) % 60;
        long minute = (milliseconds / (1000 * 60)) % 60;
        long hour = (milliseconds / (1000 * 60 * 60)) % 24;

        this.form.setRunTime(String.format("%02d:%02d:%02d", hour, minute, second));
    }

    private void updateExperienceGained()
    {
        float deltaExperience = this.ctx.skills.experience(Constants.SKILLS_SMITHING) - this.previousExperience;
        this.experienceGained += deltaExperience;
        this.form.setExperienceGained(String.format("%.0f", this.experienceGained));
    }

    private void updateExperiencePerHour()
    {
        double hours = this.ctx.controller.script().getTotalRuntime() / 3600000.0;

        if (hours > 0)
        {
            double rate = this.experienceGained / hours;
            this.form.setExperiencePerHour(String.format("%.02f", rate));
            this.previousExperience = this.ctx.skills.experience(Constants.SKILLS_SMITHING);
        }
    }

    /********* Actions *********/

    private void loadActions(RSSmithingContext context)
    {
        this.actionManager.add(new RSAdjustPitchAction(this.ctx, RSSmithingController.DEFAULT_WAIT));
        this.actionManager.add(new RSGoToClosestAction<>(this.ctx, RSSmithingController.DEFAULT_WAIT, context.bankIds));

        RSOpenBankAction openBank = new RSOpenBankAction(this.ctx, RSSmithingController.DEFAULT_WAIT, context.bankIds, context.smithingItem);
        openBank.setFailureRecoveryAction(new RSFailureRecoveryAction(this.ctx, RSSmithingController.DEFAULT_WAIT));
        this.actionManager.add(openBank);

        this.actionManager.add(new RSDepositAction(this.ctx, RSSmithingController.DEFAULT_WAIT, context.smithingItem.getName()));
        this.actionManager.add(new RSWithdrawAction(this.ctx, RSSmithingController.DEFAULT_WAIT, context.smithingItem.getRecipe()));
        this.actionManager.add(new RSCloseBankAction(this.ctx, RSSmithingController.DEFAULT_WAIT));
        this.actionManager.add(new RSGoToClosestAction<>(this.ctx, RSSmithingController.DEFAULT_WAIT, context.smelterNames));

        RSSmeltAction smelt = new RSSmeltAction(this.ctx, RSTimeSpan.fromMinutes(3), context.smelterNames, context.smithingItem.getRecipe());
        smelt.setFailureRecoveryAction(new RSFailureRecoveryAction(this.ctx, RSSmithingController.DEFAULT_WAIT));
        this.actionManager.add(smelt);
    }

    /********* Debug Event Handler *********/

    @Override
    public void debugDidReceiveMessage(String message) { this.form.log(message); }

    /********* Form Event Handler *********/

    @Override
    public void formDidPressStartButton(RSSmithingForm form, JButton startButton)
    {
        this.didStart = true;
        startButton.setEnabled(false);
    }

    @Override
    public void formDidSelectItemAt(RSSmithingForm form, int index)
    {
        if (index < 0) { return; }

        this.selectedSmithingItem = this.smithingWebService.getItemsInSection(this.selectedSection).get(index);

        this.actionManager.reset();
        this.loadActions(this.getSmithingContext());
    }

    @Override
    public void formDidSelectSection(RSSmithingForm form, int section)
    {
        this.selectedSection = section + 1;
        this.form.loadItems(this.smithingWebService.getItemsInSection(section + 1).stream().map(RSSmithingItem::getName).collect(Collectors.toList()));
    }
}