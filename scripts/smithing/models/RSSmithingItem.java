package j333.scripts.smithing.models;

import java.util.HashMap;

public class RSSmithingItem
{
    private String name;
    private HashMap<String, Integer> recipe;

    public RSSmithingItem(String name, HashMap<String, Integer> recipe)
    {
        this.name = name;
        this.recipe = recipe;
    }

    /********* Accessors *********/

    public String getName() { return this.name; }

    public HashMap<String, Integer> getRecipe() { return this.recipe; }
}
