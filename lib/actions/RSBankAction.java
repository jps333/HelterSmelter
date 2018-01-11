package j333.lib.actions;

import j333.lib.RSTimeSpan;

import org.powerbot.script.rt6.Item;
import org.powerbot.script.rt6.ClientContext;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RSBankAction extends RSAction
{
    public RSBankAction(ClientContext ctx, RSTimeSpan wait) { super(ctx, wait); }

    @Override
    protected void loadCommands() { }

    @Override
    public boolean canExecute() { return false; }

    @Override
    public String getDescription() { return ""; }

    /********* Helpers *********/

    protected List<String> backpackItemNames() {
        return Arrays.stream(this.ctx.backpack.items()).map(Item::name).collect(Collectors.toList());
    }

    protected int occurrencesOfElement(String name, List<String> names)
    {
        int count = 0;

        for (String itemName : names) {
            if (itemName.equals(name)) { count++; }
        }

        return count;
    }

    protected int getSumOfAmounts(HashMap<String, Integer> items)
    {
        int sum = 0;
        for (String itemName : items.keySet()) { sum += items.get(itemName); }

        return sum;
    }
}
