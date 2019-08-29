package util;

import entity.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageUtil {
    public static JSONObject parseImageJSON(Image img) {
        return new JSONObject()
                .put("customName", img.getCustomName())
                .put("factoryName", img.getFactoryName())
                .put("owner", img.getOwner())
                .put("filepath", img.getFilepath())
                .put("metadata", img.getMetadata());
    }

    public static String parseImageList(ArrayList<Image> list) {
        JSONArray jso = new JSONArray();
        for (Image img : list) {
            jso.put(parseImageJSON(img));
        }
        return jso.toString();
    }
}
