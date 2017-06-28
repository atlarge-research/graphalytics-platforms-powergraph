package science.atlarge.granula.modeller;

import science.atlarge.granula.archiver.GranulaArchiver;
import science.atlarge.granula.archiver.GranulaExecutor;
import science.atlarge.granula.modeller.entity.BasicType.ArchiveFormat;
import science.atlarge.granula.modeller.entity.Execution;
import science.atlarge.granula.modeller.job.JobModel;
import science.atlarge.granula.modeller.job.Overview;
import science.atlarge.granula.modeller.platform.Powergraph;
import science.atlarge.granula.modeller.source.JobDirectorySource;
import science.atlarge.granula.util.FileUtil;
import science.atlarge.granula.util.json.JsonUtil;

import java.nio.file.Paths;

/**
 * Created by wing on 21-8-15.
 */
public class ModelTester {
    public static void main(String[] args) {
        String inputPath = "/home/wlngai/Workstation/Exec/Granula/debug/archiver/powergraph/log";
        String outputPath = "/home/wlngai/Workstation/Exec/Granula/debug/archiver/powergraph/arc";

        Execution execution = (Execution) JsonUtil.fromJson(FileUtil.readFile(
                Paths.get(inputPath + "/execution/execution-log.js")), Execution.class);
        execution.setLogPath(inputPath);
        // Set end time in "log directory"/execution/execution-log.js, or the end time is set as the current time.
        execution.setEndTime(1489804044873l);
        execution.setArcPath(outputPath);
        JobModel jobModel = new JobModel(new Powergraph());

        GranulaExecutor granulaExecutor = new GranulaExecutor();
//        granulaExecutor.setEnvEnabled(false);
        granulaExecutor.setExecution(execution);
        granulaExecutor.buildJobArchive(jobModel);
    }
}
