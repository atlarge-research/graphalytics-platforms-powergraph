package nl.tudelft.pds.granula;

import nl.tudelft.pds.granula.archiver.source.JobDirectorySource;
import nl.tudelft.pds.granula.modeller.powergraph.job.PowerGraph;
import nl.tudelft.pds.granula.util.JobListGenerator;
import nl.tudelft.pds.granula.util.UuidGenerator;

/**
 * Created by wing on 21-8-15.
 */
public class PowerGraphArchiver {
    public static void main(String[] args) {

        // output
        String outputPath = Configuration.repoPath + "/data/archive/"+ UuidGenerator.getRandomUUID() + ".xml";
//        String outputPath = String.format(\"/home/wing/Workstation/Dropbox/Repo/granula/data/output/graphx.xml\", workloadLog.getName());

        // workload
//        String workloadDirPath = Configuration.repoPath + "/data/input/";
//        File workloadFile = new File(workloadDirPath + "/graphx.tar.gz");
//        WorkloadFileSource workloadSource = new WorkloadFileSource(workloadFile.getAbsolutePath());
//        workloadSource.load();
//
//        for (JobSource jobSource : workloadSource.getEmbeddedJobSources()) {
//            GranulaArchiver granulaArchiver = new GranulaArchiver(jobSource, new PowerGraph(), outputPath);
//            granulaArchiver.archive();
//        }

        // job
        JobDirectorySource jobDirSource = new JobDirectorySource("data/log/");
        jobDirSource.load();

        GranulaArchiver granulaArchiver = new GranulaArchiver(jobDirSource, new PowerGraph(), outputPath);
        granulaArchiver.archive();

        // generate list
        (new JobListGenerator()).generateRecentJobsList();

    }
}
