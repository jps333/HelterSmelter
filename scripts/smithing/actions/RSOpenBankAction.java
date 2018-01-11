package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.helpers.RSGameObjects;
import j333.lib.actions.RSAction;

import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;
import j333.scripts.smithing.models.RSSmithingItem;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RSOpenBankAction extends RSAction
{
    private GameObject bank;
    private List<Integer> bankIds;
    private RSSmithingItem smithingItem;

    public RSOpenBankAction(ClientContext ctx, RSTimeSpan wait, List<Integer> bankIds, RSSmithingItem smithingItem)
    {
        super(ctx, wait);

        this.bankIds = bankIds;
        this.smithingItem = smithingItem;
    }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            RSDebugHelper.debugPrint("Interacting with " + this.getBank().name() + "...");
            boolean success = this.getBank().interact("Use");
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " interact with " + this.getBank().name() + ".");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Waiting until " + this.getBank().name() + " is open...");

            RSWait wait = new RSWait();
            boolean success = wait.until(this.ctx.bank::opened, this.getWait());
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " wait until " + this.getBank().name() + " is open.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute()
    {
        List<String> backpackItemNames = this.backpackItemNames();
        boolean containsRecipe = this.smithingItem.getRecipe().keySet().stream().anyMatch(backpackItemNames::contains);
        return this.getBank().inViewport() && !this.ctx.bank.opened() && !containsRecipe;
    }

    @Override
    public String getDescription() { return "Open bank"; }

    /********* Accessors *********/

    public GameObject getBank()
    {
        GameObject closest = RSGameObjects.getClosestObject(this.ctx, this.bankIds);

        if (this.bank == null || !this.bank.valid() || this.bank.id() != closest.id()) {
            this.bank = RSGameObjects.getClosestObject(this.ctx, this.bankIds);
        }

        return this.bank;
    }

    /********* Helpers *********/

    protected List<String> backpackItemNames() {
        return Arrays.stream(this.ctx.backpack.items()).map(Item::name).collect(Collectors.toList());
    }
}
