package j333.lib.invokers;

import j333.lib.RSCommand;
import j333.lib.RSExecutionResponse;

import java.util.ArrayList;
import java.util.List;

public class RSCommandInvoker
{
    private int lastExecutedCommandIndex = 0;
    private List<RSCommand> commands = new ArrayList<>();

    /********* Helpers *********/

    public boolean hasCommands() { return this.lastExecutedCommandIndex < this.commands.size(); }

    /********* Actions *********/

    public void enqueue(RSCommand command) { this.commands.add(command); }

    public RSExecutionResponse execute()
    {
        boolean success = true;
        RSExecutionResponse response = RSExecutionResponse.SUCCESS;

        for (int i = this.lastExecutedCommandIndex; i < this.commands.size() && success; i++)
        {
            this.lastExecutedCommandIndex = i;
            response = this.commands.get(i).execute();
            success = response == RSExecutionResponse.SUCCESS;
        }

        if (!success)
        {
            if (this.rollback()) {
                response = RSExecutionResponse.FAILED;
            }
            else { response = RSExecutionResponse.INVALID; }
        }

        return response;
    }

    public void reset() { this.lastExecutedCommandIndex = 0; }

    private boolean rollback()
    {
        boolean canRollback = false;

        for (int i = this.lastExecutedCommandIndex - 1; i > 0 && !canRollback; i--)
        {
            this.lastExecutedCommandIndex = i;
            canRollback = this.commands.get(i).getCanRollback();
        }

        return canRollback;
    }
}
