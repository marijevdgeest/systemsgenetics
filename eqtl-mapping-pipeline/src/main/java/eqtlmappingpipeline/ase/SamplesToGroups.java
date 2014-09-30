
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
import java.util.regex.Pattern;

/**
 *
 * @author Marije van der Geest
 */
public class SamplesToGroups {
    private static final Pattern TAB_PATTERN = Pattern.compile("\\t");
    
     /**
     * Parse tab separated file with sample IDs and corresponding groups (tissues)
     * 
     * @author Marije van der Geest
     * @param groupsFile col 1 sample IDs and col 2 groups.
     * @return HashMap with key sample ID and value group
     */
    public static Map<String, ArrayList<String>> readGroups(File groupsFile) throws FileNotFoundException, UnsupportedEncodingException, IOException, Exception {

		HashMap<String, ArrayList<String>> groupsMap = new HashMap<String, ArrayList<String>>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(groupsFile)));

		String line;
		String[] elements;
                
		while ((line = reader.readLine()) != null) {
                        ArrayList<String> sampleList = null;
			elements = TAB_PATTERN.split(line);
                        
			if (elements[12].isEmpty()) {
                            continue;
			} else
                            {
                            if (groupsMap.containsKey(elements[12])){
                                groupsMap.get(elements[12]).add(elements[1]);

                            } else
                            {
                                sampleList.add(elements[1]);
                                groupsMap.put(elements[12], sampleList);
                            }
                        }
		}

		return groupsMap;
	}

    SamplesToGroups() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
}
