/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eqtlmappingpipeline.util;

import eqtlmappingpipeline.binarymeta.Main;
import eqtlmappingpipeline.textmeta.FixedEffectMetaAnalysis;
import eqtlmappingpipeline.metaqtl3.FDR;
import eqtlmappingpipeline.metaqtl3.FDR.FDRMethod;
import eqtlmappingpipeline.pcaoptimum.PCAOptimum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import umcg.genetica.console.ConsoleGUIElems;
import umcg.genetica.io.Gpio;
import umcg.genetica.io.trityper.util.ChrAnnotation;
import umcg.genetica.math.matrix.DoubleMatrixDataset;

/**
 *
 * @author harmjan
 */
public class UtilConsoleGUI {

    public static enum MODE {

        GETSNPSFROMREGION, GETSNPSINPROBEREGION, FDR, GETMAF, MERGE, REGRESS, GETSNPSTATS, PROXYSEARCH, DOTPLOT, META,
        SORTFILE, CONVERTBINARYMATRIX, GETSNPPROBECOMBINATIONS, NONGENETICPCACORRECTION, REGRESSKNOWN, CREATTTFROMDOUBLEMAT
    };
    MODE run;

    public UtilConsoleGUI(String[] args) {

        String settingsfile = null;
        String settingstexttoreplace = null;
        String settingstexttoreplacewith = null;
        String in = null;
        String in2 = null;
        String out = null;
        boolean cis = false;
        boolean trans = false;
        int perm = 1;
        String outtype = "text";
        String inexp = null;
        String inexpplatform = null;
        String inexpannot = null;
        String gte = null;
        String snpfile = null;
        Integer threads = null;
        String probefile = null;
        String region = "";
        
        String annot = null;
        String snpselectionlist = null;
        Integer stepSize = 5;
        Integer max = 5;
        String fileQtlsToRegressOut = null;
        
        Double threshold = null;
        Integer nreqtls = null;

        Double r2 = null;
        Double maf = 0.05;
        Double cr = 0.95;
        Double hwep = 0.001;
        Integer dist = 1000000;

        Integer minnrdatasets = null;
        Integer minnrsamples = null;

        String snpprobeselectionlist = null;
        boolean createQQPlot = true;
        boolean createLargeFdrFile = true;

        FDRMethod FdrMethod = FDRMethod.ALL;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String val = null;

            if (i + 1 < args.length) {
                val = args[i + 1];
            }

            if (arg.equals("--convertbinarymatrix")) {
                region = val;
                run = MODE.CONVERTBINARYMATRIX;
            } else if (arg.equals("--getsnpsinregion")) {
                region = val;
                run = MODE.GETSNPSFROMREGION;
            } else if (arg.equals("--sortfile")) {
                region = val;
                run = MODE.SORTFILE;
            } else if (arg.equals("--findproxy")) {
                region = val;
                run = MODE.PROXYSEARCH;
            } else if (arg.equals("--getmaf")) {
                region = val;
                run = MODE.GETMAF;
            } else if (arg.equals("--getsnpsinproberegion")) {
                region = val;
                run = MODE.GETSNPSINPROBEREGION;
            } else if (arg.equals("--merge")) {
                run = MODE.MERGE;
            } else if (arg.equals("--fdr")) {
                region = val;
                run = MODE.FDR;
            } else if (arg.equals("--dotplot")) {
                region = val;
                run = MODE.DOTPLOT;
            } else if (arg.equals("--regress")) {
                run = MODE.REGRESS;
            } else if (arg.equals("--snpstats")) {
                run = MODE.GETSNPSTATS;
            } else if (arg.equals("--meta")) {
                run = MODE.META;
            } else if (arg.equals("--regressknown")) {
                run = MODE.REGRESSKNOWN;
            } else if (arg.equals("--getSNPProbeCombinatios")) {
                run = MODE.GETSNPPROBECOMBINATIONS;
            } else if (arg.equals("--nonGeneticPCaCorrection")) {
                run = MODE.NONGENETICPCACORRECTION;
            } else if (arg.equals("--formatAsTT")) {
                run = MODE.CREATTTFROMDOUBLEMAT;
            } else if (arg.equals("--settings")) {
                settingsfile = val;
            } else if (arg.equals("--replacetext")) {
                settingstexttoreplace = val;
            } else if (arg.equals("--replacetextwith")) {
                settingstexttoreplacewith = val;
            } else if (arg.equals("--in")) {
                in = val;
            } else if (arg.equals("--in2")) {
                in2 = val;
            } else if (arg.equals("--out")) {
                out = val;
            } else if (arg.equals("--inexp")) {
                inexp = val;
            } else if (arg.equals("--inexpplatform")) {
                inexpplatform = val;
            } else if (arg.equals("--inexpannot")) {
                inexpannot = val;
            } else if (arg.equals("--gte")) {
                gte = val;
            } else if (args[i].equals("--annot")) {
                annot = val;
            } else if (args[i].equals("--FdrMethod")) {
                val = val.toLowerCase();
                if(val.equals("probe")){
                    FdrMethod = FDRMethod.PROBELEVEL;
                } else if(val.equals("gene")){
                    FdrMethod = FDRMethod.GENELEVEL;
                }
            } else if (arg.equals("--snps")) {
                snpfile = val;
            } else if (arg.equals("--probes")) {
                probefile = val;
            } else if (arg.equals("--perm")) {
                perm = Integer.parseInt(val);
            } else if (arg.equals("--nreqtls")) {
                nreqtls = Integer.parseInt(val);
            } else if (arg.equals("--threshold")) {
                threshold = Double.parseDouble(val);
            } else if (arg.equals("--r2")) {
                r2 = Double.parseDouble(val);
            } else if (arg.equals("--maf")) {
                maf = Double.parseDouble(val);
            } else if (arg.equals("--hwep")) {
                hwep = Double.parseDouble(val);
            } else if (arg.equals("--dist")) {
                dist = Integer.parseInt(val);
            } else if (arg.equals("--skipqqplot")) {
                createQQPlot = false;
            } else if (arg.equals("--skipLargeFDRFile")) {
                createLargeFdrFile = false;
            } else if (args[i].equals("--snpselectionlist")) {
                snpselectionlist = val;
            } else if (args[i].equals("--snpprobeselectionlist")) {
                snpprobeselectionlist = val;
            } else if (args[i].equals("--stepsizepcaremoval")) {
                stepSize = Integer.parseInt(val);
            } else if (args[i].equals("--maxnrpcaremoved")) {
                max = Integer.parseInt(val);
            } else if (args[i].equals("--QTLS")) {
                fileQtlsToRegressOut = val;
            }

        }
        if (run == null) {
            System.err.println("Please specify an util.");
            printUsage();
        } else {
            try {
                switch (run) {
                    case CONVERTBINARYMATRIX:
                        if (in == null || out == null) {
                            System.out.println("Usage: --util --convertbinarymatrix --in /path/to/matrix.binary --out /path/to/textoutput.txt");
                        } else {
                            if (in.endsWith(".txt")) {
                                System.out.println("The file provided with --in is already a text file: " + in);
                            } else {
                                if (in.endsWith(".dat")) {
                                    in = in.substring(0, in.length() - 4);
                                }
                                System.out.println("Converting: " + in);
                                DoubleMatrixDataset<String, String> ds = new DoubleMatrixDataset<String, String>(in);
                                ds.save(out);
                            }
                        }
                        break;


                    case REGRESS:

                        RegressCisEffectsFromGeneExpressionData r = new RegressCisEffectsFromGeneExpressionData(args);
                        break;
                    case PROXYSEARCH:

                        if (in == null || snpfile == null || out == null || r2 == null) {
                            System.out.println("Usage: --mode util --findproxy --r2 0.8 --snps snpfile.txt --out outfile --in /Path/To/TriTyperReference/ [--hwep 0.001] [--maf 0.05] [--cr 0.95]");
                        } else {
                            LDCalculator.proxyLookUpInReferenceDataset(in, snpfile, maf, hwep, cr, r2, out, dist);
                        }
                        break;
                    case MERGE:

                        if (in == null || region == null) {
                            System.out.println("USAGE: --merge --in dataset --in2 dataset2 --out outdir [--snps snpfile]");
                        } else {
                            GenotypeDataMerger m = new GenotypeDataMerger();
                            m.merge(in, in2, out, snpfile);
                        }
                        break;
                    case GETMAF:

                        if (in == null || region == null) {
                            System.out.println("USAGE: --getmaf snplistfile --in dataset");
                        } else {
                            GenotypeDataQuery dq = new GenotypeDataQuery();
                            dq.getSNPMAF(in, region);
                        }
                        break;
                    case GETSNPSTATS:

                        if (in == null || region == null) {
                            System.out.println("USAGE: --in dataset");
                        } else {
                            GenotypeDataQuery dq = new GenotypeDataQuery();
                            if (in2 != null) {
                                dq.getSNPStatsForAllSNPs(in, in2);
                            } else {
                                dq.getSNPStatsForAllSNPs(in);
                            }
                        }
                        break;
                    case SORTFILE:
                        if (in == null) {
                            System.out.println("USAGE: --in eQTLFile --out eQTLFile");
                        } else {
                            EQTLFileSorter f = new EQTLFileSorter();
                            f.run(in, out);
                        }
                        break;
                    case GETSNPSFROMREGION:
                        if (in == null || region == null) {
                            System.out.println("To use --getsnpsfromregion, please use --in to point to the genotype data and supply a region to query.");
                            printUsage();
                        } else {
                            int chr = -1;
                            int chrposA = -1;
                            int chrposB = -1;
                            GenotypeDataQuery q = new GenotypeDataQuery();
                            try {
                                String[] elems = region.split(":");
                                chr = ChrAnnotation.parseChr(elems[0]);
                                elems = elems[1].split("-");
                                chrposA = Integer.parseInt(elems[0]);
                                chrposB = Integer.parseInt(elems[1]);
                            } catch (Exception e) {
                                System.err.println("Error: malformed query: " + region);;
                            }
                            q.getSNPsInRegion(in, chr, chrposA, chrposB);
                        }

                        break;

                    case GETSNPSINPROBEREGION:
                        if (snpfile == null || inexpannot == null || probefile == null) {
                            System.out.println("To use --getsnpsinproberegion, please use --snps, --probes, and --inexpannot");
                            printUsage();
                        } else {
                            ProbeSNPMapper psm = new ProbeSNPMapper();
                            psm.mapprobes(snpfile, inexpannot, probefile);
                        }

                        break;
                    case FDR:
                        if (in == null || threshold == null || nreqtls == null) {
                            System.out.println("To use --fdr, please use --in, --threshold, and --perm and --nreqtls");
                            printUsage();
                        } else {
                            if (snpselectionlist != null || snpprobeselectionlist != null) {
                                try {
                                    FDR.calculateFDRAdvance(in, perm, nreqtls, threshold, createQQPlot, null, null, FdrMethod, createLargeFdrFile, snpselectionlist, snpprobeselectionlist);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            } else {
                                try {
                                    FDR.calculateFDR(in, perm, nreqtls, threshold, createQQPlot, null, null, FDRMethod.ALL, createLargeFdrFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        }

                        break;
                    case META:
                        if (in == null || out == null) {
                            System.out.println("To use --meta, please use --in, and --out");
                            printUsage();
                        } else {
                            FixedEffectMetaAnalysis f = new FixedEffectMetaAnalysis();
                            f.run(in, out, minnrdatasets, minnrsamples);
                        }

                        break;
                    case DOTPLOT:
                        if (in == null) {
                            System.out.println("Usage: --dotplot --in /path/to/file.txt");
                        } else {
                            eQTLDotPlotter d = new eQTLDotPlotter();
                            d.plot(in);
                        }
                        break;
                    case GETSNPPROBECOMBINATIONS:

                        try {
                            NoLdSnpProbeListCreator.main(Arrays.copyOfRange(args, 2, args.length));
                        } catch (UnsupportedEncodingException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        break;
                    case NONGENETICPCACORRECTION:


                        if (in == null || out == null || inexp == null || gte == null) {
                            System.out.println("Please specify --in, --out, --stepsizepcaremoval, --maxnrpcaremoved, --gte, --ing and --nreqtls");
                        } else {
                            try {
                                PCAOptimum p = new PCAOptimum();
//            public void alternativeInitialize(String ingt, String inexp, String inexpplatform, String inexpannot, String gte, String out, boolean cis, boolean trans, int perm, String snpfile, Integer threads) throws IOException, Exception {

                                p.alternativeInitialize(in, inexp, null, annot, gte, out, true, true, 10, snpselectionlist, 1);
                                File file = new File(inexp);

                                p.performeQTLMappingOverEigenvectorMatrixAndReNormalize(inexp, out, file.getAbsoluteFile().getParent(), stepSize, max, nreqtls);
                            } catch (IOException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Exception ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        break;
                    case REGRESSKNOWN:
                        if (!Gpio.exists(fileQtlsToRegressOut)) {
                            System.err.println("ERROR: you have specified an eQTL file to regress out, but the file was not found " + fileQtlsToRegressOut);
                            System.exit(0);
                        }
                        RegressCisEffectsFromGeneExpressionData regress = new RegressCisEffectsFromGeneExpressionData(settingsfile, fileQtlsToRegressOut);
                        break;
                    case CREATTTFROMDOUBLEMAT:
                        String[] argsNew = {inexpannot,in,out};
                        umcg.genetica.io.trityper.ConvertDoubleMatrixDataToTriTyper.main(argsNew);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void printUsage() {
        System.out.print("\tUtil\n" + ConsoleGUIElems.LINE);
        System.out.println("Util contains small utilities.");

        System.out.println("");
        System.out.print("Available Utilities:\n" + ConsoleGUIElems.LINE);
     
        System.out.println("--getsnpsinregion\t\tGet SNPs in a certain region: chr positionA positionB: Y:12000-13000 would get all SNPs on chr Y between 12000 and 13000 bp\n"
                + "--getsnpsinproberegion\t\tGet SNPs in a certain set of probes (specify with --probes)\n"
                + "--fdr\t\t\t\tCalculated FDR.\n"
                + "--getmaf\t\t\tGets maf for snp\n"
                + "--merge\t\t\t\tMerges two datasets\n"
                + "--snpstats\t\t\tGets HWE, MAF, and CR for all SNPs\n"
                + "--findproxy\t\t\tSearches for a proxy given a list of SNPs\n"
                + "--dotplot\t\t\tCreates dotplot from eQTL result file\n"
                + "--regress\t\t\tRemoves eQTL effects from gene expression data.\n"
                + "--regressknown\t\t\tRemoves known cis-eQTL effects from gene expression data.\n"
                + "--sortfile\t\t\tSort eQTL files.\n"
                + "--meta\t\t\t\tFixed effect meta analysis.\n"
                + "--nonGeneticPCaCorrection\tCorrect expression data for non-genetic components.\n"
                + "--getSNPProbeCombinatios\tCreate list of valid SNP-Probe combinations to test.\n"
                + "--formatAsTT\t\t\tConverte a doublematrix dataset to a TriTyper genotype file.\n"
                + "--convertbinarymatrix\t\tConverts binary matrix to text\n");
        System.out.println("");

//        System.out.print("Command line options:\n" + ConsoleGUIElems.LINE);
//        System.out.println("--in\t\t\tdir\t\tLocation of the genotype data\n"
//                + "--out\t\t\tdir\t\tLocation where the output should be stored\n"
//                + "--inexp\t\t\tstring\t\tLocation of expression data\n"
//                + "--inexpplatform\t\tstring\t\tGene expression platform\n"
//                + "--inexpannot\t\tstring\t\tLocation of annotation file for gene expression data\n"
//                + "--gte\t\t\tstring\t\tLocation of genotype to expression coupling file\n"
//                + "--snps\t\t\tstring\t\tLocation of snp file\n"
//                + "--probes\t\tstring\t\tLocation of probe file\n");
//
//        System.out.println("");
    }
}
