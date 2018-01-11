package j333.lib.helpers;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.util.Collections.min;

public class RSGameObjects
{
    public static double distanceFrom(ClientContext ctx, GameObject object)
    {
        Point destination = object.centerPoint().getLocation();
        return ctx.players.local().centerPoint().getLocation().distance(destination.x, destination.y);
    }

    public static <T> GameObject gameObjectForIdentifier(ClientContext ctx, T identifier)
    {
        GameObject object = null;

        if (identifier instanceof String)  {
            object = ctx.objects.select().name((String)identifier).nearest().peek();
        }
        else if (identifier instanceof Integer) {
            object = ctx.objects.select().id((Integer)identifier).nearest().peek();
        }
        else { ctx.game.logout(true); }

        return object;
    }

    public static <T> HashMap<GameObject, Double> calculateDistances(ClientContext ctx, List<T> identifiers)
    {
        HashMap<GameObject, Double> distances = new HashMap<>();

        for (T identifier : identifiers)
        {
            GameObject object = RSGameObjects.gameObjectForIdentifier(ctx, identifier);
            if (object.valid()) { distances.put(object, RSGameObjects.distanceFrom(ctx, object)); }
        }

        return distances;
    }

    public static <T> GameObject getClosestObject(ClientContext ctx, List<T> identifiers)
    {
        HashMap<GameObject, Double> distances = RSGameObjects.calculateDistances(ctx, identifiers);
        return min(distances.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
    }
}
