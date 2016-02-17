package nl.tudelft.pds.granula.modeller.rule.extraction;

import nl.tudelft.pds.granula.archiver.source.DataStream;
import nl.tudelft.pds.granula.archiver.source.record.Record;
import nl.tudelft.pds.granula.archiver.source.record.RecordLocation;
import nl.tudelft.pds.granula.modeller.rule.extraction.ExtractionRule;
import nl.tudelft.pds.granula.util.UuidGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wing on 21-8-15.
 */
public class PowerGraphExtractionRule extends ExtractionRule {

    public PowerGraphExtractionRule(int level) {
        super(level);
    }

    @Override
    public boolean execute() {
        return false;
    }

    public String generateText(String infoName, String infoValue, String actorType, String actorId, String missionType,
                               String missionId, String operationUuid) {
        String text = String.format("GRANULA - InfoName:%s InfoValue:%s ActorType:%s ActorId:%s MissionType:%s MissionId:%s RecordUuid:%s OperationUuid:%s Timestamp:%s\n",
                infoName, infoValue, actorType, actorId, missionType,
                missionId, UuidGenerator.getRandomUUID(), operationUuid, System.currentTimeMillis());
        return text;
    }

    public List<Record> extractRecordFromInputStream(DataStream dataStream) {

        //String parsedLog = parseLog(dataStream);

        List<Record> granularlogList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(dataStream.getInputStream()));

            String line = null;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;

                if(line.contains("GRANULA") ) {
                    Record record = extractRecord(line);

                    RecordLocation trace = new RecordLocation();

                    String codeLocation;
                    String logFilePath;
                    if(false) { //TODO if supported
                        codeLocation = line.split("\\) - Granular")[0].split(" \\(")[1];
                    }

                    codeLocation = "unspecified";
                    logFilePath = "unspecified";

                    trace.setLocation(logFilePath, lineCount, codeLocation);
                    record.setRecordLocation(trace);

                    granularlogList.add(record);
                }
            }

            br.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return granularlogList;
    }



    public Record extractRecord(String line) {
        Record record = new Record();

        String granularLog = line.split("GRANULA ")[1];
        String[] recordAttrs = granularLog.split("\\s+");

        for (String recordAttr : recordAttrs) {
            if (recordAttr.contains(":")) {
                String[] attrKeyValue = recordAttr.split(":");
                if (attrKeyValue.length == 2) {

                    String name = attrKeyValue[0];
                    String value = attrKeyValue[1];
                    String unescapedValue = value.replaceAll("\\[COLON\\]", ":").replaceAll("\\[SPACE\\]", " ");

                    record.addRecordInfo(name, unescapedValue);
                } else {
                    record.addRecordInfo(attrKeyValue[0], "");
                }
            }
        }
        return record;
    }
}
