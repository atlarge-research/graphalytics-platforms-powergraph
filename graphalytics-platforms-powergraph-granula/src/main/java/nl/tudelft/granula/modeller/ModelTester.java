package nl.tudelft.granula.modeller;

import nl.tudelft.granula.archiver.GranulaArchiver;
import nl.tudelft.granula.modeller.entity.BasicType.ArchiveFormat;
import nl.tudelft.granula.modeller.job.JobModel;
import nl.tudelft.granula.modeller.job.Overview;
import nl.tudelft.granula.modeller.platform.Powergraph;
import nl.tudelft.granula.modeller.source.JobDirectorySource;

/**
 * Created by wing on 21-8-15.
 */
public class ModelTester {
    public static void main(String[] args) {

        String inputPath = "/home/wlngai/Workstation/Exec/Granula/debug/archiver/powergraph/log";
        String outputPath = "/home/wlngai/Workstation/Repo/tudelft-atlarge/granula/granula-visualizer/data/";

        // job
        JobDirectorySource jobDirSource = new JobDirectorySource(inputPath);
        jobDirSource.load();

        Overview overview = new Overview();
        overview.setStartTime(1466000574008l);
        overview.setEndTime(1466001551190l);
        overview.setName("Powergraph Job");
        overview.setDescription("A Powergraph Job");

        GranulaArchiver granulaArchiver = new GranulaArchiver(
                jobDirSource, new JobModel(new Powergraph()), outputPath, ArchiveFormat.JS);
        granulaArchiver.setOverview(overview);
        granulaArchiver.archive();

    }
}
