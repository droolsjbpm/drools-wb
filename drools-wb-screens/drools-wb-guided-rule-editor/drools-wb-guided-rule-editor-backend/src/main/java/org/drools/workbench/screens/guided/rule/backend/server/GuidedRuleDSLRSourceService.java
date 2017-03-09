/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.backend.server;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.compiler.lang.Expander;
import org.drools.compiler.lang.dsl.DSLMappingFile;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.workbench.models.commons.backend.rule.RuleModelDRLPersistenceImpl;
import org.drools.workbench.models.commons.backend.rule.RuleModelIActionPersistenceExtension;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDSLRResourceTypeDefinition;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.kie.workbench.common.services.backend.file.DSLFileFilter;
import org.kie.workbench.common.services.backend.source.BaseSourceService;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.source.SourceGenerationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class GuidedRuleDSLRSourceService
        extends BaseSourceService<RuleModel> {

    private static final Logger logger = LoggerFactory.getLogger(GuidedRuleDSLRSourceService.class);

    private static final DSLFileFilter FILTER_DSLS = new DSLFileFilter();

    @Inject
    private GuidedRuleDSLRResourceTypeDefinition resourceType;

    @Inject
    private GuidedRuleEditorService guidedRuleEditorService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private FileDiscoveryService fileDiscoveryService;

    @Inject
    private KieProjectService projectService;

    private Collection<RuleModelIActionPersistenceExtension> persistenceExtensions = new ArrayList<>();

    @Inject
    public GuidedRuleDSLRSourceService(final Instance<RuleModelIActionPersistenceExtension> persistenceExtensionInstance) {
        persistenceExtensionInstance.forEach(persistenceExtensions::add);
    }

    @Override
    public String getPattern() {
        return resourceType.getSuffix();
    }

    @Override
    public String getSource(final Path path,
                            final RuleModel model) throws SourceGenerationFailedException {
        try {
            final String dslr = RuleModelDRLPersistenceImpl.getInstance().marshal(model,
                                                                                  persistenceExtensions);
            final Expander expander = getDSLExpander(path);
            final String drl = expander.expand(dslr);
            return drl;
        } catch (Exception e) {
            throw new SourceGenerationFailedException(e.getMessage());
        }
    }

    @Override
    public String getSource(final Path path) throws SourceGenerationFailedException {
        return getSource(path,
                         guidedRuleEditorService.load(Paths.convert(path)));
    }

    /**
     * Returns an expander for DSLs (only if there is a DSL configured for this package).
     */
    private Expander getDSLExpander(final Path path) {
        final Expander expander = new DefaultExpander();
        final List<DSLMappingFile> dsls = getDSLMappingFiles(path);
        for (DSLMappingFile dsl : dsls) {
            expander.addDSLMapping(dsl.getMapping());
        }
        return expander;
    }

    private List<DSLMappingFile> getDSLMappingFiles(final Path path) {
        final List<DSLMappingFile> dsls = new ArrayList<DSLMappingFile>();
        final org.uberfire.backend.vfs.Path vfsPath = Paths.convert(path);
        final org.uberfire.backend.vfs.Path packagePath = projectService.resolvePackage(vfsPath).getPackageMainResourcesPath();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(packagePath);
        final Collection<Path> dslPaths = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                             FILTER_DSLS);
        for (final org.uberfire.java.nio.file.Path dslPath : dslPaths) {
            final String dslDefinition = ioService.readAllString(dslPath);
            final DSLTokenizedMappingFile dslFile = new DSLTokenizedMappingFile();
            try {
                if (dslFile.parseAndLoad(new StringReader(dslDefinition))) {
                    dsls.add(dslFile);
                } else {
                    logger.error("Unable to parse DSL definition: " + dslDefinition);
                }
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            }
        }
        return dsls;
    }
}
