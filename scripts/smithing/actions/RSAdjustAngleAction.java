package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSAction;
import org.powerbot.script.rt6.ClientContext;

import java.util.Random;

public class RSAdjustAngleAction extends RSAction
{
    private static final Random RANDOM = new Random();

    private int minAngle = 20;
    private int maxAngle = 40;

    public RSAdjustAngleAction(ClientContext ctx, RSTimeSpan wait) { super(ctx, wait); }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            int randomAngle = RSAdjustAngleAction.RANDOM.nextInt(this.maxAngle + 1) + this.minAngle;
            return this.ctx.camera.angle(randomAngle) ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return true; }

    @Override
    public String getDescription() { return "Adjust camera angle"; }
}
