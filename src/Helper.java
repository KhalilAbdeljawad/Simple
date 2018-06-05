import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static void printMap(HashMap map){
        System.out.println("Key\t\tValue");

        for (Object entry : map.entrySet()  ) {
            System.out.println(((Map.Entry<Object, Object>)entry).getKey()+"\t\t"+((Map.Entry<Object, Object>)entry).getValue());
        }
    }
}
