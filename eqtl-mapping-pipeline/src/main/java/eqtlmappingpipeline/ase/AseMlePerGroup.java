package eqtlmappingpipeline.ase;

import cern.colt.list.tint.IntArrayList;
import cern.jet.stat.tdouble.Probability;
import static eqtlmappingpipeline.ase.AseMle.lnbico;
import static eqtlmappingpipeline.ase.AseMle.log1minProbabilities;
import static eqtlmappingpipeline.ase.AseMle.logProbabilities;
import static eqtlmappingpipeline.ase.AseMle.probabilities;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Calculate mle per group.
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
    private HashMap<String, Double> groupLikelihoods = null;
    private HashMap<String, Double> groupLikelihoodsP = null;
    private static final Logger LOGGER = Logger.getLogger(AseMle.class);
    private double sumProportionPerGroup;

    /**
     * Retrieves ArrayLists with a1counts, a2counts and sample IDs and an object
     * with groups and corresponding samples.
     * Calculates mle for every group that contains samples from the sampleIds list.
     * Calculates (null)likelihood ratio on the sum of the maximum (null)likelihoods.
     *
     * @author Marije van der Geest
     * @param a1Counts
     * @param a2Counts
     * @param samplesToGroups
     * @param sampleIds
     */
    public AseMlePerGroup(IntArrayList a1Counts, IntArrayList a2Counts, ArrayList sampleIds, SamplesToGroups samplesToGroups) {

        nullLikelihoodPerGroup = new ArrayList();
        proportionPerGroup = new ArrayList();
        likelihoodPerGroup = new ArrayList();
        groupLikelihoods = new HashMap(samplesToGroups.getGroupCounts());
        groupLikelihoodsP = new HashMap(samplesToGroups.getGroupCounts());

        double maxLogLikelihood;
        double maxLogLikelihoodP;
        double logLikelihoodNull = Double.NaN;

        //First calculate binominal coefficients
        double[] logBinominalCoefficients = new double[a1Counts.size()];
        for (int i = 0; i < a1Counts.size(); ++i) {
            int a1Count = a1Counts.getQuick(i);
            int totalReads = a1Count + a2Counts.getQuick(i);
            logBinominalCoefficients[i] = lnbico(totalReads, a1Count);
        }

        //For each group (tissue) from samplesToGroups object.
        for (String groupName : samplesToGroups.getGroups()) {

            double provisionalMaxLogLikelihood = Double.NEGATIVE_INFINITY;
            double provisionalMaxLogLikelihoodP = 0.5;

            ArrayList<Integer> groupsIndex = new ArrayList();

            {
                int i = 0;
                //For each sample that belongs to the group.
                for (String sample : samplesToGroups.getGroupSamples(groupName)) {
                    //Check if tissue is present in sampleIds list.
                    if (sampleIds.contains(sample)) {
                        //If present, save index in order to find the corresponding a1 and a2 counts.
                        groupsIndex.add(sampleIds.indexOf(sample));
                        ++i;
                    }
                }

                //If none of the samples from a group is present in the sampleIds,
                //continue to the next tissue.
                if (groupsIndex.isEmpty()) {
                    continue;
                }
            }

            //Test all probabilities.
            for (int i = 0; i < probabilities.length; ++i) {

                double sumLogLikelihood = 0;
                //For each sample from group, binomial test.
                for (int s = 0; s < groupsIndex.size(); ++s) {
                    sumLogLikelihood += logBinominalCoefficients[groupsIndex.get(s)] + (double) a1Counts.getQuick(groupsIndex.get(s)) * logProbabilities[i] + (double) a2Counts.getQuick(groupsIndex.get(s)) * log1minProbabilities[i];
                }

                if (sumLogLikelihood > provisionalMaxLogLikelihood) {
                    provisionalMaxLogLikelihood = sumLogLikelihood;
                    provisionalMaxLogLikelihoodP = probabilities[i];
                }

                //If probability is 0.5, save null likelihood.
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

            //Create hashmap where group is linked to the maximum likelihood.
            groupLikelihoods.put(groupName, maxLogLikelihood);
            //Create hashmap where group is linked to the proportion.
            groupLikelihoodsP.put(groupName, maxLogLikelihoodP);

            //Add maximum likelihoods to list.
            likelihoodPerGroup.add(maxLogLikelihood);
            //Add proportions to list.
            proportionPerGroup.add(maxLogLikelihoodP);

        }

        //Calculate sum of likelihoods per group
        for (double i : likelihoodPerGroup) {
            sumLikelihoodPerGroup = sumLikelihoodPerGroup + i;
        }

        //Calculate sum of nulllikelihoods per group
        for (double i : nullLikelihoodPerGroup) {
            sumNullLikelihoodPerGroup = sumNullLikelihoodPerGroup + i;
        }

        //Calculate likelihood ratio.
        double ratioD2 = (-2d * sumNullLikelihoodPerGroup) + (2d * sumLikelihoodPerGroup);
        ratioD = ratioD2 < 0 ? 0 : ratioD2;
        ratioP = Probability.chiSquareComplemented(1, ratioD);
        System.out.println(groupLikelihoods);
        System.out.println(groupLikelihoodsP);
        System.out.println(ratioD);
        System.out.println(ratioP);
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

    public HashMap<String, Double> getGroupLikelihoods() {
        return groupLikelihoods;
    }

    public HashMap<String, Double> getGroupLikelihoodsP() {
        return groupLikelihoodsP;
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
