package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSBankAction;
import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;

import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ClientContext;

public class RSDepositAction extends RSBankAction
{
    private String itemName;

    public RSDepositAction(ClientContext ctx, RSTimeSpan wait, String itemName)
    {
        super(ctx, wait);

        this.itemName = itemName;
    }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            RSDebugHelper.debugPrint("Calculating amount to deposit...");
            int remainingAmount = this.getRemainingAmount(this.itemName);
            Item item = this.ctx.backpack.select().name(this.itemName).peek();
            RSDebugHelper.debugPrint("Amount to deposit: " + remainingAmount);

            RSDebugHelper.debugPrint("Depositing " + remainingAmount + " " + item.name());
            boolean success = this.ctx.bank.deposit(item.id(), remainingAmount);
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " deposit " + remainingAmount + " " + item.name() + ".");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Checking if deposit is valid...");

            RSWait wait = new RSWait();
            boolean success = wait.until(() -> this.isValidDeposit(this.itemName), this.getWait());
            RSDebugHelper.debugPrint("Deposit is " + (success ? "valid." : "invalid."));

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return this.ctx.bank.opened() && !this.isValidDeposit(this.itemName); }

    @Override
    public String getDescription() { return "Deposit"; }

    /********* Helpers *********/

    private boolean isValidDeposit(String itemName) {
        return this.getRemainingAmount(itemName) == 0;
    }

    private int getRemainingAmount(String itemName) {
        return this.occurrencesOfElement(itemName, this.backpackItemNames());
    }
}
