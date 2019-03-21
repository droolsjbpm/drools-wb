/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.backend.server.importexport;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;

public class ScenarioCsvImportExport {

    private static int HEADER_SIZE = 3;

    public static String exportData(Simulation simulation) {
        StringBuilder stringBuilder = new StringBuilder();
        List<FactMapping> factMappings = simulation.getSimulationDescriptor().getUnmodifiableFactMappings();

        CSVPrinter printer;
        try {
            printer = new CSVPrinter(stringBuilder, CSVFormat.EXCEL);
        } catch (IOException e) {
            throw ExceptionUtilities.handleException(e);
        }



        generateHeader(factMappings, printer);


        // FIXME what to do with index and description? skip?

        for (Scenario unmodifiableScenario : simulation.getUnmodifiableScenarios()) {
            for (int i = 0; i < factMappings.size(); i += 1) {

            }
        }
        // FIXME
        return null;
    }

    private static void generateHeader(List<FactMapping> factMappings, CSVPrinter printer) throws IOException {
        List<String> firstLineHeader = new ArrayList<>();
        List<String> secondLineHeader = new ArrayList<>();
        List<String> thirdLineHeader = new ArrayList<>();

        for (FactMapping factMapping : factMappings) {
            firstLineHeader.add(factMapping.getExpressionIdentifier().getType().name());
            secondLineHeader.add(factMapping.getFactAlias());
            thirdLineHeader.add(factMapping.getExpressionAlias());
        }

        printer.printRecord(firstLineHeader.toArray());
        printer.printRecord(secondLineHeader.toArray());
        printer.printRecord(thirdLineHeader.toArray());
    }

    public static Simulation importData(String raw, Simulation originalSimulation) {

        CSVParser csvParser;
        try {
            csvParser = CSVFormat.EXCEL.parse(new StringReader(raw));
        } catch (IOException e) {
            throw ExceptionUtilities.handleException(e);
        }

        Simulation toReturn = originalSimulation.cloneSimulation();
        IntStream.range(0, toReturn.getUnmodifiableScenarios().size())
                .forEach(toReturn::removeScenarioByIndex);

        List<FactMapping> factMappings = toReturn.getSimulationDescriptor().getUnmodifiableFactMappings();

        List<CSVRecord> csvRecords;
        try {
            csvRecords = csvParser.getRecords();
            csvRecords = csvRecords.subList(HEADER_SIZE, csvRecords.size());
        } catch (IOException e) {
            throw ExceptionUtilities.handleException(e);
        }

        for (CSVRecord csvRecord : csvRecords) {
            Scenario scenarioToFill = toReturn.addScenario();
            if (csvRecord.size() != factMappings.size()) {
                throw new IllegalArgumentException("Malformed row " + csvRecord);
            }
            for (int i = 0; i < factMappings.size(); i += 1) {
                FactMapping factMapping = factMappings.get(i);
                scenarioToFill.addMappingValue(factMapping.getFactIdentifier(),
                                               factMapping.getExpressionIdentifier(),
                                               csvRecord.get(i));
            }
        }
        return toReturn;
    }
}
