import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.udojava.evalex.Expression;
public class Main {

    public static void main(String[] args) {

        if(args.length > 0){
            String code[] = readFile(args[0]).split("\n");
            Interpreter inter = new Interpreter(code);
            inter.run();

        }
    }

    // define vars
    // print vars and expressions


    public static String readFile(String filepath) {
        String text = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //if(line.contains("radio.1.chanbw=20") || line.contains("radio.1.chanbw=40"))
                //    line = "radio.1.chanbw=10";
                text += line + "\n";
            }
        } catch (IOException x) {

            return null;
        }

        return text;
    }
}


