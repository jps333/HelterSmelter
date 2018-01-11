package j333.lib;

import j333.RSExpectedCondition;

public class RSWait
{
    public boolean until(RSExpectedCondition condition, RSTimeSpan timeSpan)
    {
        int pollRate = 15;
        boolean success = condition.evaluate();
        int remainingTime = timeSpan.getMilliseconds();

        while (remainingTime > 0 && !success)
        {
            success = condition.evaluate();

            if (!success)
            {
                try {
                    Thread.sleep(pollRate);
                } catch (InterruptedException e) { e.printStackTrace(); }

                remainingTime -= pollRate;
            }
        }

        return success;
    }
}
