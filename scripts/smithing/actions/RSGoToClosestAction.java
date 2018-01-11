package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.helpers.RSGameObjects;
import j333.lib.actions.RSAction;

import j333.lib.RSWait;
import j333.scripts.helpers.RSDebugHelper;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

import java.util.List;

public class RSGoToClosestAction<T> extends RSAction
{
    private GameObject object;
    private List<T> objectIdentifiers;

    public RSGoToClosestAction(ClientContext ctx, RSTimeSpan wait, List<T> objectIdentifiers)
    {
        super(ctx, wait);

        this.objectIdentifiers = objectIdentifiers;
    }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            RSDebugHelper.debugPrint("Turning to " + this.getObject().name() + "...");
            this.ctx.camera.turnTo(this.getObject());

            return this.object.inViewport() ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(true, () ->
        {
            RSDebugHelper.debugPrint("Stepping to " + this.getObject().name());
            boolean success = this.ctx.movement.step(this.getObject());
            RSDebugHelper.debugPrint((success ? "Successfully did" : "Failed to") + " step to " + this.getObject().name() + ".");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));

        this.addCommand(new RSCommand(false, () ->
        {
            RSDebugHelper.debugPrint("Waiting until " + this.getObject().name() + " is in viewport");

            RSWait wait = new RSWait();
            boolean success = wait.until(this.getObject()::inViewport, this.getWait());
            RSDebugHelper.debugPrint((success ? "Successfully waited" : "Failed to wait") + " until " + this.getObject().name() + " is in viewport.");

            return success ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return !this.getObject().inViewport(); }

    @Override
    public String getDescription() { return "Go to closest object"; }

    /********* Accessors *********/

    private GameObject getObject()
    {
        GameObject closest = RSGameObjects.getClosestObject(this.ctx, this.objectIdentifiers);

        if (this.object == null || !this.object.valid() || this.object.id() != closest.id()) {
            this.object = RSGameObjects.getClosestObject(this.ctx, objectIdentifiers);
        }

        return this.object;
    }
}
