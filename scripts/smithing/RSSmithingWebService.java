package j333.scripts.smithing;

import j333.lib.RSWikiaClient;
import j333.lib.gson.JsonArray;
import j333.lib.gson.JsonElement;
import j333.lib.gson.JsonObject;
import j333.lib.gson.JsonParser;
import j333.scripts.smithing.models.RSSmithingItem;

import java.util.*;

public class RSSmithingWebService
{
    private RSWikiaClient wikiaClient = new RSWikiaClient();

    private List<String> sections;
    private HashMap<Integer, List<RSSmithingItem>> items;

    public List<String> getSections()
    {
        if (this.sections == null) { this.sections = this.fetchSections(); }

        return this.sections;
    }

    public List<RSSmithingItem> getItemsInSection(int section)
    {
        if (this.items == null) { items = new HashMap<>(); }

        if (section >= this.items.size())
        {
            List<RSSmithingItem> items = this.fetchItemsInSection(section);
            this.items.put(section, items);
        }

        return this.items.get(section);
    }

    private List<String> fetchSections()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "parse");
        params.put("contentmodel", "wikitext");
        params.put("prop", "sections");
        params.put("format", "jsonfm");
        params.put("page", "Calculator:Standard smithing");
        params.put("formatversion", "2");

        String json = this.wikiaClient.request(params).getAsJsonObject("parse").getAsJsonArray("sections").toString();
        return this.parseSections(json);
    }

    private List<RSSmithingItem> fetchItemsInSection(int section)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "parse");
        params.put("contentmodel", "wikitext");
        params.put("prop", "wikitext");
        params.put("format", "jsonfm");
        params.put("page", "Calculator:Standard smithing");
        params.put("formatversion", "2");
        params.put("section", "" + section);

        List<RSSmithingItem> items;
        String json = this.wikiaClient.request(params).getAsJsonObject("parse").getAsJsonObject("wikitext").get("*").toString();

        if (section > 1) {
            items = this.parseForgeItems(json);
        }
        else { items = this.parseSmeltItems(json); }

        return items;
    }

    private List<String> parseSections(String json)
    {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(json).getAsJsonArray();

        List<String> sections = new ArrayList<>();
        Iterator<JsonElement> iterator = array.iterator();

        while (iterator.hasNext())
        {
            JsonObject object = iterator.next().getAsJsonObject();
            String section = object.get("line").toString().replace("\"", "");
            sections.add(section);
        }

        return sections;
    }

    private List<RSSmithingItem> parseSmeltItems(String json)
    {
        List<RSSmithingItem> data = new ArrayList<>();
        List<String> lines = Arrays.asList(json.split("\\[\\[File:[aA-zZ0-9 .']+\\]\\]"));

        for (int i = 1; i < lines.size(); i++)
        {
            String line = lines.get(i);

            for (String content : line.split("\\\\n\\|-\\\\n\\|"))
            {
                String[] itemInfo = content.trim().split("\\|\\|");

                if (itemInfo.length < 5) { continue; }

                String itemName = itemInfo[1];
                if (!itemName.contains("[[") || !itemName.contains("]]")) { continue; }

                String[] primaryOreInfo = itemInfo[3].split("\\[\\[");
                String primaryOreName = primaryOreInfo[1].trim();
                Integer primaryOreAmount = Integer.parseInt(primaryOreInfo[0].trim());

                List<String> oreNames = new ArrayList<>();
                List<Integer> oreAmounts = new ArrayList<>();

                if (primaryOreName.contains("]]"))
                {
                    primaryOreName = primaryOreName.substring(0, primaryOreName.indexOf("]]")).trim();
                    oreNames.add(primaryOreName);
                    oreAmounts.add(primaryOreAmount);
                }

                String[] secondaryOreInfo = itemInfo[4].split("\\[\\[");

                if (secondaryOreInfo.length >= 2)
                {
                    String secondaryOreName = secondaryOreInfo[1].trim();
                    Integer secondaryOreAmount = Integer.parseInt(secondaryOreInfo[0].trim());

                    if (secondaryOreName.contains("]]"))
                    {
                        secondaryOreName = secondaryOreName.substring(0, secondaryOreName.indexOf("]]")).trim();
                        oreNames.add(secondaryOreName);
                        oreAmounts.add(secondaryOreAmount);
                    }
                }

                HashMap<String, Integer> recipe = new HashMap<>();

                for (int j = 0; j < oreNames.size(); j++)
                {
                    String name = oreNames.get(j);
                    Integer amount = oreAmounts.get(j);
                    recipe.put(name, amount);
                }

                String barName = itemName.trim().substring(itemName.indexOf("[[") + 1, itemName.indexOf("]]") - 1).trim();
                RSSmithingItem item = new RSSmithingItem(barName, recipe);
                data.add(item);
            }
        }

        return data;
    }

    private List<RSSmithingItem> parseForgeItems(String json)
    {
        List<RSSmithingItem> data = new ArrayList<>();
        List<String> lines = Arrays.asList(json.split("\\[\\[File:[aA-zZ0-9 .']+\\]\\]"));

        for (int i = 1; i < lines.size(); i++)
        {
            String line = lines.get(i);

            for (String content : line.split("\\\\n\\|-\\\\n\\|"))
            {
                String[] item = content.trim().split("\\|\\|");
                if (item.length < 5) { continue; }

                String name = item[1].trim();
                String bars = item[4].trim();
                if (!name.contains("[[") || !name.contains("]]") || bars.split(" ").length > 1) { continue; }

                String[] nameInfo = name.substring(name.indexOf("[[") + 2, name.indexOf("]]") - 1).trim().split("\\|");

                HashMap<String, Integer> recipe = new HashMap<>();
                recipe.put("", Integer.parseInt(bars));

                String itemName = nameInfo[0].trim();
                RSSmithingItem forgeItem = new RSSmithingItem(itemName, recipe);
                data.add(forgeItem);
            }
        }

        return data;
    }
}
