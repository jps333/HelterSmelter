package j333.scripts.helpers;

import j333.scripts.handlers.RSDebugEventHandler;

public class RSDebugHelper
{
    private static final boolean DEBUG = true;

    private static RSDebugEventHandler EVENT_HANDLER;

    public static void setEventHandler(RSDebugEventHandler eventHandler) {
        RSDebugHelper.EVENT_HANDLER = eventHandler;
    }

    public static void debugPrint(String message)
    {
        if (!RSDebugHelper.DEBUG) { return; }

        System.out.println(message);
        RSDebugHelper.EVENT_HANDLER.debugDidReceiveMessage(message);
    }
}
