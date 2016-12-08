package gg.galaxygaming.ts.CommandHandler;

import com.google.common.io.Files;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

class CommandParser {
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
        if (!json.containsKey("className")) //Should display an error message
            return;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    }
}