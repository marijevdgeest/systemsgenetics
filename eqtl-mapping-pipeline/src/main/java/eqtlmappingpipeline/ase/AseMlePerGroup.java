package eqtlmappingpipeline.ase;

import cern.colt.list.tint.IntArrayList;
import cern.jet.stat.tdouble.Probability;
import static eqtlmappingpipeline.ase.AseMle.lnbico;
import static eqtlmappingpipeline.ase.AseMle.log1minProbabilities;
import static eqtlmappingpipeline.ase.AseMle.logProbabilities;
import static eqtlmappingpipeline.ase.AseMle.probabilities;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Marije van der Geest
 */
public class AseMlePerGroup {

    private ArrayList<Double> nullLikelihoodPerGroup = null;
    private ArrayList<Double> proportionPerGroup = null;
    private ArrayList<Double> likelihoodPerGroup = null;
    private final double ratioD;
    private final double ratioP;
    private double sumLikelihoodPerGroup = 0;
    private double sumNullLikelihoodPerGroup = 0;
    private HashMap<String,ArrayList<Double>> groupLikelihoods = null;
    private static final Logger LOGGER = Logger.getLogger(AseMle.class);
    private double sumProportionPerGroup;
    

    public AseMlePerGroup(IntArrayList a1Counts, IntArrayList a2Counts, ArrayList sampleIds, SamplesToGroups samplesToGroups) {

        nullLikelihoodPerGroup = new ArrayList();
        proportionPerGroup = new ArrayList();
        likelihoodPerGroup = new ArrayList();
        groupLikelihoods = new HashMap(samplesToGroups.getGroupCounts());
        ArrayList<Double> groupLikelihoodsList = new ArrayList();

        double maxLogLikelihood = 0;
        double maxLogLikelihoodP = 0;

        double logLikelihoodNull = Double.NaN;

        //First calculate binominal coefficients
        double[] logBinominalCoefficients = new double[a1Counts.size()];
        for (int i = 0; i < a1Counts.size(); ++i) {
            int a1Count = a1Counts.getQuick(i);
            int totalReads = a1Count + a2Counts.getQuick(i);
            logBinominalCoefficients[i] = lnbico(totalReads, a1Count);
        }

        // for each group
        for (String groupName : samplesToGroups.getGroups()) {

            double provisionalMaxLogLikelihood = Double.NEGATIVE_INFINITY;
            double provisionalMaxLogLikelihoodP = 0.5;

            ArrayList<String> groupSamples = samplesToGroups.getGroupSamples(groupName);
            int groupsIndex[] = new int[groupSamples.size()];

            {
                int i = 0;
                for (String sample : samplesToGroups.getGroupSamples(groupName)) {
                    if (sampleIds.contains(sample)){
                    groupsIndex[i] = sampleIds.indexOf(sample);
                    ++i;} else{
                        LOGGER.warn("Sample " +sample + " is not available.");
                        
                    }
                    
                }
            }

            // test all probabilities
            for (int i = 0; i < probabilities.length; ++i) {

                double sumLogLikelihood = 0;
                for (int s : groupsIndex) {
                    sumLogLikelihood += logBinominalCoefficients[s] + (double) a1Counts.getQuick(s) * logProbabilities[i] + (double) a2Counts.getQuick(s) * log1minProbabilities[i];
                }

                if (sumLogLikelihood > provisionalMaxLogLikelihood) {
                    provisionalMaxLogLikelihood = sumLogLikelihood;
                    provisionalMaxLogLikelihoodP = probabilities[i];
                }

                if (probabilities[i] == 0.5) {
                    logLikelihoodNull = sumLogLikelihood;
                    nullLikelihoodPerGroup.add(logLikelihoodNull);
                }

            }

            if (Double.isNaN(logLikelihoodNull)) {
                throw new RuntimeException("Something went wrong during ASE analysis. This should not happen, please contact developers.");
            }

            //Make sure to use null model in case of tie
            if (logLikelihoodNull >= provisionalMaxLogLikelihood) {
                maxLogLikelihood = logLikelihoodNull;
                maxLogLikelihoodP = 0.5;

            } else {

                maxLogLikelihood = provisionalMaxLogLikelihood;
                maxLogLikelihoodP = provisionalMaxLogLikelihoodP;

            }
            
            groupLikelihoodsList.add(maxLogLikelihoodP);
            groupLikelihoodsList.add(maxLogLikelihood);

            
            groupLikelihoods.put(groupName, groupLikelihoodsList);

            proportionPerGroup.add(maxLogLikelihoodP);
            likelihoodPerGroup.add(maxLogLikelihood);

        }

        for (double i : likelihoodPerGroup) {
            sumLikelihoodPerGroup = sumLikelihoodPerGroup + i;
        }

        for (double i : nullLikelihoodPerGroup) {
            sumNullLikelihoodPerGroup = sumNullLikelihoodPerGroup + i;
        }
        
        for (double i : proportionPerGroup) {
            sumProportionPerGroup = sumProportionPerGroup + i;
        }
        //Calcukate likelihood ratio 
        double ratioD2 = (-2d * sumNullLikelihoodPerGroup) + (2d * sumLikelihoodPerGroup);
        ratioD = ratioD2 < 0 ? 0 : ratioD2;
        ratioP = Probability.chiSquareComplemented(1, ratioD);



    }

    public ArrayList<Double> getNullLikelihoodPerGroup() {
        return nullLikelihoodPerGroup;
    }

    public ArrayList<Double> getProportionPerGroup() {
        return proportionPerGroup;
    }

    public ArrayList<Double> getLikelihoodPerGroup() {
        return likelihoodPerGroup;
    }

    public HashMap<String,ArrayList<Double>> getGroupLikelihoods() {
        return groupLikelihoods;
    }

    public double getRatioD() {
        return ratioD;
    }

    public double getRatioP() {
        return ratioP;
    }

    public double getSumLikelihoodPerGroup() {
        return sumLikelihoodPerGroup;
    }

    public double getSumNullLikelihoodPerGroup() {
        return sumNullLikelihoodPerGroup;
    }

    public double getSumProportionPerGroup() {
        return sumProportionPerGroup;
    }

}
