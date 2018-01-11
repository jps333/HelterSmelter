package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSBankAction;

import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;
import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ClientContext;

import java.util.List;
import java.util.HashMap;

public class RSWithdrawAction extends RSBankAction
{
    private HashMap<String, Integer> items;

    public RSWithdrawAction(ClientContext ctx, RSTimeSpan wait, HashMap<String, Integer> items)
    {
        super(ctx, wait);

        this.items = items;
    }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            boolean success = false;

            RSDebugHelper.debugPrint("Calculating total amount of items...");
            int sumOfAmounts = this.getSumOfAmounts(this.items);
            RSDebugHelper.debugPrint("Total amount of items: " + sumOfAmounts);

            for (String itemName : this.items.keySet())
            {
                int desiredAmount = this.getAmountToWithdraw(itemName, this.items.get(itemName), sumOfAmounts);
                int withdrawnAmount = this.occurrencesOfElement(itemName, this.backpackItemNames());
                int delta = Math.abs(withdrawnAmount - desiredAmount);

                Item item;

                if (withdrawnAmount == 0) {
                    item = this.ctx.bank.select().name(itemName).peek();
                }
                else { item = this.ctx.backpack.select().name(itemName).peek(); }

                if (withdrawnAmount > desiredAmount)
                {
                    RSDebugHelper.debugPrint("Depositing " + item.name() + "...");
                    success = this.ctx.bank.deposit(item.id(), delta);
                    RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " deposit " + item.name());
                }
                else if (withdrawnAmount < desiredAmount)
                {
                    RSDebugHelper.debugPrint("Withdrawing " + item.name() + "...");
                    success = this.ctx.bank.withdraw(item.id(), delta);
                    RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " withdraw " + item.name());
                }

                if (!success) { break; }
            }

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Checking if withdraw is valid...");

            RSWait wait = new RSWait();
            boolean success = wait.until(() -> this.isValidWithdraw(this.items), this.getWait());
            RSDebugHelper.debugPrint("Withdraw is " + (success ? "valid." : "invalid."));

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return this.ctx.bank.opened() && !this.isValidWithdraw(this.items); }

    @Override
    public String getDescription() { return "Withdraw"; }

    /********* Helpers *********/

    private boolean isValidWithdraw(HashMap<String, Integer> items)
    {
        boolean isValid = true;
        int sumOfAmounts = this.getSumOfAmounts(this.items);
        List<String> backpackItemNames = this.backpackItemNames();

        for (String itemName : items.keySet())
        {
            int withdrawnAmount = this.occurrencesOfElement(itemName, backpackItemNames);
            int desiredAmount = this.getAmountToWithdraw(itemName, items.get(itemName), sumOfAmounts);

            if (desiredAmount != 0 && withdrawnAmount <= 0)
            {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    private int getNumberOfFreeSlotsExcludingItem(String itemName)
    {
        int numberOfFreeSlots = this.ctx.backpack.items().length;

        for (Item item : this.ctx.backpack.items())
        {
            if (item.valid() && !(itemName.equals(item.name()) || this.items.containsKey(item.name()))) {
                numberOfFreeSlots--;
            }
        }

        return numberOfFreeSlots;
    }

    private int getAmountToWithdraw(String itemName, int itemAmount, int totalItemAmounts)
    {
        int numberOfFreeSlots = this.getNumberOfFreeSlotsExcludingItem(itemName);
        double maxAmount = Math.floor(numberOfFreeSlots / totalItemAmounts);

        return (int)Math.round(itemAmount * maxAmount);
    }
}
