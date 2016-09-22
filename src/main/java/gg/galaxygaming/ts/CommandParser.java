package gg.galaxygaming.ts;

import com.google.common.io.Files;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CommandParser {
    public void readFile(String path) {
        File f = new File(path);
        if (!f.exists() || !Files.getFileExtension(f.getName()).equals("pup"))
            return;
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            r.lines().forEach(response::append);
            r.close();
        } catch (Exception ignored) {
        }
        JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
    }
}