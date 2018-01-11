package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.helpers.RSGameObjects;
import j333.lib.actions.RSAction;

import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.Component;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RSSmeltAction extends RSAction
{
    private static final int SMELT_BUTTON_ID = 40;
    private static final int SMITHING_WIDGET = 1370;
    private static final Random RANDOM = new Random();
    private static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();

    private GameObject smelter;
    private List<String> smelterNames;
    private HashMap<String, Integer> items;

    public RSSmeltAction(ClientContext ctx, RSTimeSpan wait, List<String> smelterNames, HashMap<String, Integer> items)
    {
        super(ctx, wait);

        this.items = items;
        this.smelterNames = smelterNames;
    }

    @Override
    protected void loadCommands()
    {
        RSTimeSpan intermediateTimeSpan = RSTimeSpan.fromSeconds(10);
        Widget widget = this.ctx.widgets.select().id(RSSmeltAction.SMITHING_WIDGET).peek();
        Component button = widget.component(RSSmeltAction.SMELT_BUTTON_ID);

        RSDebugHelper.debugPrint("Determining if smelter interface is visible...");

        if (button.visible())
        {
            this.addCommand(new RSCommand(true, ()->
            {
                RSDebugHelper.debugPrint("Smelter is visible.");
                RSDebugHelper.debugPrint("Clicking smelt button...");

                boolean success = button.click();

                RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " click smelt button.");

                return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
            }));
        }
        else {

            this.addCommand(new RSCommand(true, () ->
            {
                RSDebugHelper.debugPrint("Smelter is not visible.");
                RSDebugHelper.debugPrint("Interacting with " + this.getSmelter().name() + "...");

                boolean success = this.getSmelter().interact("Smelt");

                RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " interact with " + this.getSmelter().name() + ".");

                return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
            }));

            this.addCommand(new RSCommand(false, () ->
            {
                RSDebugHelper.debugPrint("Waiting for " + this.getSmelter().name() + " interface to be open...");
                boolean success = this.waitForInterface(intermediateTimeSpan, true);
                RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " wait until " + this.getSmelter().name() + " interface is open.");

                return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
            }));

            this.addCommand(new RSCommand(true, () ->
            {
                RSDebugHelper.debugPrint("Clicking smelt button...");
                boolean success = button.click();
                RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " click smelt button.");

                return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
            }));
        }

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Waiting for " + this.getSmelter().name() + " interface to be closed...");
            boolean success = this.waitForInterface(intermediateTimeSpan, false);
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " wait until " + this.getSmelter().name() + " interface is closed.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Moving mouse to random position...");
            int minX = RSSmeltAction.RANDOM.nextInt(RSSmeltAction.SCREEN_DIMENSION.width);
            int maxX = RSSmeltAction.RANDOM.nextInt(RSSmeltAction.SCREEN_DIMENSION.width);
            int minY = RSSmeltAction.RANDOM.nextInt(RSSmeltAction.SCREEN_DIMENSION.height);
            int maxY = RSSmeltAction.RANDOM.nextInt(RSSmeltAction.SCREEN_DIMENSION.height);
            int x = RSSmeltAction.RANDOM.nextInt(maxX) + minX;
            int y = RSSmeltAction.RANDOM.nextInt(maxY) + minY;
            this.ctx.input.move(x, y);
            return RSExecutionResponse.SUCCESS;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Waiting until finished smelting...");
            boolean success = this.waitUntilItemsNotInBackpack(this.getItemNames(this.items), this.getWait());
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " wait until finished smelting.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            int a = RSSmeltAction.RANDOM.nextInt(33);
            int b = RSSmeltAction.RANDOM.nextInt(13);
            int c = RSSmeltAction.RANDOM.nextInt(26);

            if (a % b == c)
            {
                try {
                    Thread.sleep(a + c);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }

            return RSExecutionResponse.SUCCESS;
        }));
    }

    @Override
    public boolean canExecute() { return this.backpackContainsItems(this.getItemNames(this.items)); }

    @Override
    public String getDescription() { return "Smelt"; }

    /********* Accessors *********/

    private GameObject getSmelter()
    {
        GameObject closest = this.getClosestSmelter(this.smelterNames);

        if (this.smelter == null || !this.smelter.valid() || this.smelter.id() != closest.id()) {
            this.smelter = this.getClosestSmelter(this.smelterNames);
        }

        return this.smelter;
    }

    /********* Helpers *********/

    private List<String> getItemNames(HashMap<String, Integer> items) { return new ArrayList<>(items.keySet()); }

    private GameObject getClosestSmelter(List<String> smelterNames) { return RSGameObjects.getClosestObject(this.ctx, smelterNames); }

    private List<String> backpackItemNames() {
        return Arrays.stream(this.ctx.backpack.items()).map(Item::name).collect(Collectors.toList());
    }

    private boolean backpackContainsItems(List<String> itemNames)
    {
        List<String> backpackItemNames = this.backpackItemNames();
        return itemNames.stream().anyMatch(backpackItemNames::contains);
    }

    /********* Actions *********/

    private boolean waitForInterface(RSTimeSpan wait, boolean visible)
    {
        return new RSWait().until(() ->
        {
            Widget widget = this.ctx.widgets.select().id(RSSmeltAction.SMITHING_WIDGET).peek();
            Component button = widget.component(RSSmeltAction.SMELT_BUTTON_ID);
            return button.valid() == visible && button.visible() == visible;

        }, wait);
    }

    private boolean waitUntilItemsNotInBackpack(List<String> itemNames, RSTimeSpan wait) {
        return new RSWait().until(() -> !this.backpackContainsItems(itemNames), wait);
    }
}
