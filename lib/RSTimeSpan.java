package j333.lib;

public class RSTimeSpan
{
    private int days = 0;
    private int hours = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int milliseconds = 0;

    /********* Accessors *********/

    public int getDays() { return this.days; }

    public int getHours() { return this.hours; }

    public int getSeconds() { return this.seconds; }

    public int getMinutes() { return this.minutes; }

    public int getMilliseconds() { return this.milliseconds; }

    /********* Factory Constructors *********/

    public static RSTimeSpan fromMilliseconds(int milliseconds)
    {
        RSTimeSpan timeSpan = new RSTimeSpan();
        timeSpan.milliseconds = milliseconds;
        timeSpan.seconds = timeSpan.milliseconds / 1000;
        timeSpan.minutes = timeSpan.seconds / 60;
        timeSpan.hours = timeSpan.minutes / 60;
        timeSpan.days = timeSpan.hours % 24;
        return timeSpan;
    }

    public static RSTimeSpan fromSeconds(int seconds)
    {
        RSTimeSpan timeSpan = new RSTimeSpan();
        timeSpan.seconds = seconds;
        timeSpan.milliseconds = timeSpan.seconds * 1000;
        timeSpan.minutes = timeSpan.seconds / 60;
        timeSpan.hours = timeSpan.minutes / 60;
        timeSpan.days = timeSpan.hours % 24;
        return timeSpan;
    }

    public static RSTimeSpan fromMinutes(int minutes)
    {
        RSTimeSpan timeSpan = new RSTimeSpan();
        timeSpan.minutes = minutes;
        timeSpan.seconds = timeSpan.minutes * 60;
        timeSpan.milliseconds = timeSpan.seconds * 1000;
        timeSpan.hours = timeSpan.minutes / 60;
        timeSpan.days = timeSpan.hours % 24;
        return timeSpan;
    }

    public static RSTimeSpan fromHours(int hours)
    {
        RSTimeSpan timeSpan = new RSTimeSpan();
        timeSpan.hours = hours;
        timeSpan.minutes = timeSpan.hours * 60;
        timeSpan.seconds = timeSpan.minutes * 60;
        timeSpan.milliseconds = timeSpan.seconds * 1000;
        timeSpan.days = timeSpan.hours % 24;
        return timeSpan;
    }

    public static RSTimeSpan fromDays(int days)
    {
        RSTimeSpan timeSpan = new RSTimeSpan();
        timeSpan.days = days;
        timeSpan.hours = timeSpan.days * 24;
        timeSpan.minutes = timeSpan.hours * 60;
        timeSpan.seconds = timeSpan.minutes * 60;
        timeSpan.milliseconds = timeSpan.seconds * 1000;
        return timeSpan;
    }
}
