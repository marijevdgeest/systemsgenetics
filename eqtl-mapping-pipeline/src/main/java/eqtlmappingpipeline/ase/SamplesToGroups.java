package eqtlmappingpipeline.ase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Create hashmap where groups (tissues) are linked to the corresponding sample
 * IDs.
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
     * Parse tab separated file with groups and corresponding sample IDs Returns
     * HashMap with key a group and value an ArrayList with sample IDs
     *
     * @author Marije van der Geest
     * @param groupsFile col 0 groups and col 1 sample IDs
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public SamplesToGroups(File groupsFile) throws FileNotFoundException, IOException {

        groupsMap = new HashMap<String, ArrayList<String>>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(groupsFile)));

        String line;
        String[] elements;

        while ((line = reader.readLine()) != null) {

            elements = TAB_PATTERN.split(line);

            //If groups is not empty
//            if (elements[0] != null && !elements[0].isEmpty()) {
            ArrayList sampleList;
            sampleList = new ArrayList<String>();
            if (elements[0] == null && elements[0].isEmpty()) {
                elements[0] = "other";
            }

            //If the map doesn't contain the group yet, add sample to sample list and put group and samplelist to map.
            if (!groupsMap.containsKey(elements[0].toLowerCase())) {

                sampleList.add(elements[1]);
                groupsMap.put(elements[0].toLowerCase(), sampleList);

                //If the group already exists, retrieve samplelist from group and add new sample. 
            } else {
                sampleList = groupsMap.get(elements[0].toLowerCase());
                sampleList.add(elements[1]);

                //Add updated samplelist to map. 
                groupsMap.put(elements[0].toLowerCase(), sampleList);

            }

        }

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
