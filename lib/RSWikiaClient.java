package j333.lib;

import j333.lib.gson.JsonElement;
import j333.lib.gson.JsonObject;
import j333.lib.gson.JsonParser;

import org.powerbot.script.AbstractScript;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class RSWikiaClient extends AbstractScript
{
    private static final String BASE_URL = "http://runescape.wikia.com/api.php?";

    public JsonObject request(HashMap<String, String> params)
    {
        String uri = this.flattenAndSanitizeParams(params);
        String response = this.downloadString(RSWikiaClient.BASE_URL + uri);
        response = response.replaceAll("&quot;", "\"");

        String content = response.substring(response.indexOf("<pre>") + 5, response.indexOf("</pre>")).trim();
        JsonElement element = new JsonParser().parse(content);

        return element.getAsJsonObject();
    }

    private String flattenAndSanitizeParams(HashMap<String, String> params)
    {
        String[] components = params.toString().replaceAll("(\\{|\\})", "").split(",");

        StringBuilder uri = new StringBuilder();

        try
        {
            for (String component : components)
            {
                if (!component.equals(components[0])) { uri.append("&"); }

                String[] keyValue = component.split("=");
                uri.append(keyValue[0].trim());
                uri.append("=");
                uri.append(URLEncoder.encode(keyValue[1].trim(), "UTF-8"));
            }

        } catch (UnsupportedEncodingException e) { e.printStackTrace(); }

        return uri.toString();
    }
}