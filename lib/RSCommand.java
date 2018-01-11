package j333.lib;

public class RSCommand
{
    private RSExecution action;
    private boolean canRollback = false;

    public RSCommand(boolean canRollback, RSExecution action)
    {
        this.action = action;
        this.canRollback = canRollback;
    }

    /********* Accessors *********/

    public boolean getCanRollback() { return this.canRollback; }

    /********* Actions *********/

    public RSExecutionResponse execute() { return this.action.run(); }
}
