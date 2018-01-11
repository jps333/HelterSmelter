package j333.scripts.smithing.actions;

import j333.lib.RSCommand;
import j333.lib.RSTimeSpan;
import j333.lib.actions.RSAction;
import org.powerbot.script.rt6.ClientContext;

public class RSFailureRecoveryAction extends RSAction
{
    private RSAdjustPitchAction pitchAction;
    private RSAdjustAngleAction angleAction;

    public RSFailureRecoveryAction(ClientContext ctx, RSTimeSpan wait)
    {
        super(ctx, wait);

        this.pitchAction = new RSAdjustPitchAction(this.ctx, this.getWait());
        this.angleAction = new RSAdjustAngleAction(this.ctx, this.getWait());
    }

    @Override
    protected void loadCommands()
    {
        if (this.pitchAction.canExecute()) { this.addCommand(new RSCommand(true, this.pitchAction::execute)); }
        if (this.angleAction.canExecute()) { this.addCommand(new RSCommand(true, this.angleAction::execute)); }
    }

    @Override
    public boolean canExecute() { return true; }

    @Override
    public String getDescription() { return "Failure recovery"; }
}
