package eqtlmappingpipeline.ase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Marije van der Geest
 */
public class SamplesToGroups {

    private static final Pattern TAB_PATTERN = Pattern.compile("\t");
    private final HashMap<String, ArrayList<String>> groupsMap;

    public SamplesToGroups(HashMap<String, ArrayList<String>> samplesToGroups) {
        this.groupsMap = samplesToGroups;
    }

    /**
     * Parse tab separated file with sample IDs and corresponding groups
     * (tissues) Returns HashMap with key a group and value an ArrayList with
     * sample IDs
     *
     * @author Marije van der Geest
     * @param groupsFile col 1 sample IDs and col 12 groups.
     */
    public SamplesToGroups(File groupsFile) throws FileNotFoundException, IOException {

        groupsMap = new HashMap<String, ArrayList<String>>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(groupsFile)));

        String line;
        String[] elements;

        while ((line = reader.readLine()) != null) {
            
            elements = TAB_PATTERN.split(line);

            if (elements[12] != null && !elements[12].isEmpty()) {
                ArrayList sampleList;
                sampleList = new ArrayList<String>();
                if (!groupsMap.containsKey(elements[12].toLowerCase())) {
                    
                    sampleList.add(elements[1]);
                    groupsMap.put(elements[12].toLowerCase(), sampleList);
//                    groupsMap.put(elements[12].toLowerCase(), sampleList);
                    
//                    System.out.println("Key element[12]: " + elements[12]);
//                    System.out.println("sample id: " + elements[1]);
                    
                } else {
                    sampleList = groupsMap.get(elements[12].toLowerCase());
                    sampleList.add(elements[1]);
                    
                    
                    groupsMap.put(elements[12].toLowerCase(), sampleList);
                    
                }
//                System.out.println("Value: "+groupsMap.get(elements[12].toLowerCase()));
                    
            }

        }
//        for(Map.Entry<String, ArrayList<String>> i : groupsMap.entrySet()){
//            System.out.println(i.getKey());
//            System.out.println(i.getValue());
////        System.out.println(groupsMap.entrySet());
//        }
        
    }

    public Set<String> getGroups() {
        return groupsMap.keySet();
    }

    public ArrayList<String> getGroupSamples(String group) {
        return groupsMap.get(group.toLowerCase());
    }

    public int getGroupCounts() {
        return groupsMap.keySet().size();
    }

}
