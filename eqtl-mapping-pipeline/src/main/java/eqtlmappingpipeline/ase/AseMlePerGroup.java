package eqtlmappingpipeline.ase;

import cern.colt.list.tint.IntArrayList;
import cern.jet.stat.tdouble.Probability;
import static eqtlmappingpipeline.ase.AseMle.lnbico;
import static eqtlmappingpipeline.ase.AseMle.log1minProbabilities;
import static eqtlmappingpipeline.ase.AseMle.logProbabilities;
import static eqtlmappingpipeline.ase.AseMle.probabilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Marije van der Geest
 */
public class AseMlePerGroup {

    ArrayList nullLikelihoodPerGroup = null;
    ArrayList proportionPerGroup = null;
    ArrayList likelihoodPerGroup = null;

    public AseMlePerGroup(IntArrayList a1Counts, IntArrayList a2Counts, ArrayList sampleIds, SamplesToGroups samplesToGroups) {

        nullLikelihoodPerGroup = new ArrayList();
        proportionPerGroup = new ArrayList();
        likelihoodPerGroup = new ArrayList();

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
                groupsIndex[i] = sampleIds.indexOf(sample);
                ++i;
            }
            }


            // test all probabilities
            for (int i = 0; i < probabilities.length; ++i) {

                double sumLogLikelihood = 0;
                for (int s : groupsIndex) {
                    //System.out.println(s);
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
                throw new RuntimeException("Something went wrong during ASE analysis. This should not happen, please contact developers");
            }

            //Make sure to use null model in case of tie
            if (logLikelihoodNull >= provisionalMaxLogLikelihood) {
                maxLogLikelihood = logLikelihoodNull;
                maxLogLikelihoodP = 0.5;

            } else {

                maxLogLikelihood = provisionalMaxLogLikelihood;
                maxLogLikelihoodP = provisionalMaxLogLikelihoodP;

            }

            proportionPerGroup.add(maxLogLikelihoodP);
            likelihoodPerGroup.add(maxLogLikelihood);

        }

        System.out.println("likelihoodPerGroup = " + likelihoodPerGroup);
        System.out.println("proportionPerGroup = " + proportionPerGroup);
    }

}
