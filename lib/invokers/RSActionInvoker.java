package j333.lib.invokers;

import j333.lib.RSExecutionResponse;
import j333.lib.actions.RSAction;
import j333.scripts.helpers.RSDebugHelper;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.ClientAccessor;

import java.util.List;
import java.util.ArrayList;

public class RSActionInvoker extends ClientAccessor
{
    private int currentActionIndex = 0;
    private List<RSAction> actions = new ArrayList<>();

    public RSActionInvoker(ClientContext ctx) { super(ctx); }

    /********* Actions *********/

    public void reset()
    {
        this.actions.clear();
        this.currentActionIndex = 0;
    }

    public void add(RSAction action) { this.actions.add(action); }

    private void updateCurrentActionIndex()
    {
        this.currentActionIndex++;

        if (this.currentActionIndex >= this.actions.size()) {
            this.currentActionIndex = 0;
        }
    }

    private RSAction getNextAction()
    {
        RSAction nextAction = this.actions.get(this.currentActionIndex);
        RSDebugHelper.debugPrint("\nEvaluating action: " + nextAction.getDescription());

        while (!nextAction.canExecute())
        {
            RSDebugHelper.debugPrint("Action cannot be executed: " + nextAction.getDescription());
            this.updateCurrentActionIndex();
            nextAction.reset();

            nextAction = this.actions.get(this.currentActionIndex);
            RSDebugHelper.debugPrint("\nEvaluating action: " + nextAction.getDescription());
        }

        return nextAction;
    }

    public void executeNextAction()
    {
        RSAction nextAction = this.getNextAction();
        RSDebugHelper.debugPrint("\nExecuting Action: " + nextAction.getDescription());
        this.process(nextAction, nextAction.execute());
    }

    private void process(RSAction action, RSExecutionResponse response)
    {
        switch (response)
        {
            case SUCCESS:
            case INVALID:
                action.reset();
                this.updateCurrentActionIndex();
                break;

            case FAILED: break;
        }
    }
}
