import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    HashMap<String, String> stable = new HashMap<>();

    public  void add(String name, String val){

        if(!isExisted(name))
            stable.put(name, val);
        else{
            System.err.println(name+" "+"Identifier is already existed");
        }

    }

    public String getVal(String name){
        return stable.get(name);
    }

    public void setVal(String name, String val){
        stable.put(name, val);
    }


    private boolean isExisted(String name){

        for (Map.Entry<String, String> entry : stable.entrySet()  ) {
            if(name.equals(entry.getKey()))return true;
        }
        return false;
    }

    public boolean has(String name){
        return isExisted(name);
    }


    public void printTable(){
        System.out.println("Name\t\tValue");

        for (Map.Entry<String, String> entry : stable.entrySet()  ) {
            System.out.println(entry.getKey()+"\t\t"+entry.getValue());
        }
    }


    public ArrayList<String> sortByLength(){

        String max = "";
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (Map.Entry<String, String> entry : stable.entrySet()) {
            list.add(entry.getKey());
        }

        for (int i = 0; i < stable.size(); i++) {

            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).length() > max.length())
                    max = list.get(j);
            }

            if(! names.contains(max))
                names.add(max);
            list.remove(max);
            max = "";
        }

        return names;
    }

}

