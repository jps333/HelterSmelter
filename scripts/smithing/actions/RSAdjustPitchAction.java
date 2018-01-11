package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSAction;

import org.powerbot.script.rt6.ClientContext;

import java.util.Random;

public class RSAdjustPitchAction extends RSAction
{
    private static final Random RANDOM = new Random();

    private int minPitch = 30;
    private int maxPitch = 50;

    public RSAdjustPitchAction(ClientContext ctx, RSTimeSpan wait) { super(ctx, wait); }

    @Override
    protected void loadCommands()
    {
        this.addCommand(new RSCommand(true, () ->
        {
            int randomPitch = RSAdjustPitchAction.RANDOM.nextInt(this.maxPitch + 1) + this.minPitch;
            return this.ctx.camera.pitch(randomPitch) ? RSExecutionResponse.SUCCESS : RSExecutionResponse.FAILED;
        }));
    }

    @Override
    public boolean canExecute() { return this.ctx.camera.pitch() < this.minPitch || this.ctx.camera.pitch() > this.maxPitch; }

    @Override
    public String getDescription() { return "Adjust camera pitch"; }
}
