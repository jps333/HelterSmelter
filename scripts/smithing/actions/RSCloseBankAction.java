package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSAction;
import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;

import org.powerbot.script.rt6.ClientContext;

public class RSCloseBankAction extends RSAction
{
    public RSCloseBankAction(ClientContext ctx, RSTimeSpan wait) { super(ctx, wait); }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            RSDebugHelper.debugPrint("Closing bank...");
            boolean success = this.ctx.bank.close();
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " close bank.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Waiting for bank to be closed...");

            RSWait wait = new RSWait();
            boolean success = wait.until(() -> !this.ctx.bank.opened(), this.getWait());
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " wait until bank closed.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return this.ctx.bank.opened(); }

    @Override
    public String getDescription() { return "Close bank"; }
}
