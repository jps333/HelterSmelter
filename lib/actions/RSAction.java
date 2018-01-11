package j333.lib.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.invokers.RSCommandInvoker;
import j333.lib.RSTimeSpan;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.ClientAccessor;

public abstract class RSAction extends ClientAccessor
{
    protected abstract void loadCommands();

    public abstract boolean canExecute();
    public abstract String getDescription();

    private RSTimeSpan wait;
    private RSAction failureRecoveryAction;
    private RSCommandInvoker commandManager = new RSCommandInvoker();

    public RSAction(ClientContext ctx, RSTimeSpan wait)
    {
        super(ctx);

        this.wait = wait;
    }

    /********* Accessors *********/

    public RSTimeSpan getWait() { return this.wait; }

    public RSAction getFailureRecoveryAction() { return this.failureRecoveryAction; }
    public void setFailureRecoveryAction(RSAction action) { this.failureRecoveryAction = action; }

    /********* Actions *********/

    protected void addCommand(RSCommand command) { this.commandManager.enqueue(command); }

    public RSExecutionResponse execute()
    {
        if (!this.commandManager.hasCommands()) { this.loadCommands(); }

        RSExecutionResponse response = this.commandManager.execute();
        if (response == RSExecutionResponse.FAILED) { this.recoverFromFailure(); }

        return response;
    }

    public void reset() { this.commandManager.reset(); }

    private void recoverFromFailure()
    {
        if (this.failureRecoveryAction != null && this.failureRecoveryAction.canExecute()) {
            this.failureRecoveryAction.execute();
        }
    }
}
