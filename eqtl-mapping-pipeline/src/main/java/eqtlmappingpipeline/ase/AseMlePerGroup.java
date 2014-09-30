
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
 *
 * @author Marije van der Geest
 */
public class AseMlePerGroup {
    
//	private final double maxLogLikelihoodP;
//	private final double maxLogLikelihood;
//	private final double ratioD;
//	private final double ratioP;
//        protected static final double[] probabilities;
        
    public AseMlePerGroup(IntArrayList a1Counts, IntArrayList a2Counts, ArrayList sampleIds, HashMap<String, ArrayList<String>> sampleGroups){
        
                ArrayList nullLikelihoodPerGroup = null;
                ArrayList proportionPerGroup = null;
                ArrayList likelihoodPerGroup = null;
                double maxLogLikelihood = 0;
                double maxLogLikelihoodP = 0;
//                double[] probabilities = null;

		//First calculate binominal coefficients
		double[] logBinominalCoefficients = new double[a1Counts.size()];
		for (int i = 0; i < a1Counts.size(); ++i) {
			int a1Count = a1Counts.getQuick(i);
			int totalReads = a1Count + a2Counts.getQuick(i);
			logBinominalCoefficients[i] = lnbico(totalReads, a1Count);
		}

		double logLikelihoodNull = Double.NaN;
                
                // for each group in hashmap
                for (String key : sampleGroups.keySet()){


                    double provisionalMaxLogLikelihood = Double.NEGATIVE_INFINITY;
                    double provisionalMaxLogLikelihoodP = 0.5;
                    double ratioD;
                    double ratioP;

                    // for all probabilities
                    // for each value from group
                    for (int i = 0; i < probabilities.length; ++i) {
                        for(String sample : sampleGroups.get(key)){
                            //If sample in sampleIds, take index from sampleIds
                            if (sampleIds.contains(sample)){
                                int index = sampleIds.indexOf(sample);
                                
                                
                            

                                double sumLogLikelihood = 0;
//                                for (int s = 0; s < a1Counts.size(); ++s) {
                                        sumLogLikelihood += logBinominalCoefficients[index] + (double) a1Counts.getQuick(index) * logProbabilities[i] + (double) a2Counts.getQuick(index) * log1minProbabilities[i];
//                                }

                                if (sumLogLikelihood > provisionalMaxLogLikelihood) {
                                        provisionalMaxLogLikelihood = sumLogLikelihood;
                                        provisionalMaxLogLikelihoodP = probabilities[i];
                                }

                                if(probabilities[i] == 0.5){
                                        logLikelihoodNull = sumLogLikelihood;
                                        nullLikelihoodPerGroup.add(logLikelihoodNull);
                                }
                            

                        }
                        }

                        if(Double.isNaN(logLikelihoodNull)){
                                throw new RuntimeException("Something went wrong during ASE analysis. This should not happen, please contact developers");
                        }

                        //Make sure to use null model in case of tie
                        if (logLikelihoodNull >= provisionalMaxLogLikelihood) {
                                maxLogLikelihood = logLikelihoodNull;
                                maxLogLikelihoodP = 0.5;
                                ratioD = 0;
                                ratioP = 1;
                                
                        } else {

                                maxLogLikelihood = provisionalMaxLogLikelihood;
                                maxLogLikelihoodP = provisionalMaxLogLikelihoodP;

                                double ratioD2 = (-2d * logLikelihoodNull) + (2d * maxLogLikelihood);
                                ratioD = ratioD2 < 0 ? 0 : ratioD2;
                                ratioP = Probability.chiSquareComplemented(1, ratioD);

//                                if (Double.isInfinite(ratioD) || Double.isNaN(ratioD)) {
//                                        Logger.warn("Warning invalid ratio D: " + ratioD2 + ". max log likelihood: " + maxLogLikelihood + " null log likelihood: " + logLikelihoodNull + " max log likelihood p: " + maxLogLikelihoodP);
//                                }
                         }
                        

                    }
                    
                    proportionPerGroup.add(maxLogLikelihoodP); 
                    likelihoodPerGroup.add(maxLogLikelihood);
                    
                }

	}

//	public double getMaxLikelihood() {
//		return maxLogLikelihood;
//	}
//
//	public double getMaxLikelihoodP() {
//		return maxLogLikelihoodP;
//	}
////
//	public double getRatioD() {
//		return ratioD;
//	}
//
//	public double getRatioP() {
//		return ratioP;
//	}
    // Count per group (tissue), after sum by group, take total. 

}
