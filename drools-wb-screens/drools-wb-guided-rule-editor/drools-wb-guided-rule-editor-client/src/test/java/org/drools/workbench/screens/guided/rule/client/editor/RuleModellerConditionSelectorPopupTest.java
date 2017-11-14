/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collections;

import javax.enterprise.inject.Instance;

import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.gwtmockito.fakes.FakeProvider;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.oracle.DSLConditionSentence;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.MockInstanceImpl;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub(Text.class)
@RunWith(GwtMockitoTestRunner.class)
public class RuleModellerConditionSelectorPopupTest {

    private static final String DSL_SENTENCE_CHOICE_KEY = "dslSentence";
    private static final String DSL_SENTENCE_CHOICE_VALUE = "DSLdslSentence";

    @Mock
    private SyncBeanDef syncBeanDef;

    @Mock
    private SyncBeanManager syncBeanManager;

    @Spy
    @InjectMocks
    private AsyncPackageDataModelOracleFactory asyncPackageDataModelOracleFactory;

    @Mock
    private IncrementalDataModelService incrementalDataModelService;

    private Caller<IncrementalDataModelService> incrementalDataModelServiceCaller;

    private Instance<DynamicValidator> validatorInstance;

    private AsyncPackageDataModelOracle oracle;

    @Mock
    private Path resourcePath;

    @Mock
    private ListBox choices;

    @Mock
    private RuleModel model;

    @Mock
    private RuleModeller ruleModeller;

    private PackageDataModelOracleBaselinePayload dataModelPayload;

    private PackageDataModelOracle dataModel;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> valueCaptor;

    @Mock
    private DSLSentence dslSentence;

    @Before
    public void setUp() throws Exception {
        // Mock The ListBox with loaded choices
        GwtMockito.useProviderForType(ListBox.class, new FakeProvider<ListBox>() {
            @Override
            public ListBox getFake(Class<?> aClass) {
                return choices;
            }
        });

        // Mock partially the AsyncPackageDataModelOracle
        incrementalDataModelServiceCaller = new CallerMock<>(incrementalDataModelService);
        validatorInstance = new MockInstanceImpl<>();
        oracle = spy(new AsyncPackageDataModelOracleImpl(incrementalDataModelServiceCaller, validatorInstance));

        // Mock partially the AsyncPackageDataModelOracleFactory
        doReturn(syncBeanDef).when(syncBeanManager).lookupBean(AsyncPackageDataModelOracle.class);
        doReturn(oracle).when(syncBeanDef).getInstance();
    }

    @Test
    public void testLoadDslConditionsDslEnabledButNotPresent() throws Exception {
        // DSL sentences enabled
        doReturn(true).when(ruleModeller).isDSLEnabled();

        // DSL Conditions not present
        dataModel = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator()).build();
        dataModelPayload = new PackageDataModelOracleBaselinePayload();
        dataModelPayload.setAllPackageElements(dataModel.getAllExtensions());

        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(resourcePath, dataModelPayload);

        new RuleModellerConditionSelectorPopup(model, ruleModeller, 0, oracle);

        verify(choices, atLeastOnce()).addItem(keyCaptor.capture(), valueCaptor.capture());
        Assertions.assertThat(keyCaptor.getAllValues()).doesNotContain(DSL_SENTENCE_CHOICE_KEY);
        Assertions.assertThat(valueCaptor.getAllValues()).doesNotContain(DSL_SENTENCE_CHOICE_VALUE);
    }

    @Test
    public void testLoadDslConditionsDslEnabledAndPresent() throws Exception {
        // DSL sentences enabled
        doReturn(true).when(ruleModeller).isDSLEnabled();

        // DSL Conditions present
        dataModel = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .addExtension(DSLConditionSentence.INSTANCE, Collections.singletonList(dslSentence))
                .build();
        dataModelPayload = new PackageDataModelOracleBaselinePayload();
        dataModelPayload.setAllPackageElements(dataModel.getAllExtensions());

        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(resourcePath, dataModelPayload);

        new RuleModellerConditionSelectorPopup(model, ruleModeller, 0, oracle);

        verify(choices, atLeastOnce()).addItem(keyCaptor.capture(), valueCaptor.capture());
        Assertions.assertThat(keyCaptor.getAllValues()).contains(DSL_SENTENCE_CHOICE_KEY);
        Assertions.assertThat(valueCaptor.getAllValues()).contains(DSL_SENTENCE_CHOICE_VALUE);
    }

    @Test
    public void testLoadDslConditionsPresentButDslDisabled() throws Exception {
        // DSL sentences disabled
        doReturn(false).when(ruleModeller).isDSLEnabled();

        // DSL Conditions present
        dataModel = PackageDataModelOracleBuilder.newPackageOracleBuilder(new RawMVELEvaluator())
                .addExtension(DSLConditionSentence.INSTANCE, Collections.singletonList(dslSentence))
                .build();
        dataModelPayload = new PackageDataModelOracleBaselinePayload();
        dataModelPayload.setAllPackageElements(dataModel.getAllExtensions());

        asyncPackageDataModelOracleFactory.makeAsyncPackageDataModelOracle(resourcePath, dataModelPayload);

        new RuleModellerConditionSelectorPopup(model, ruleModeller, 0, oracle);

        verify(choices, atLeastOnce()).addItem(keyCaptor.capture(), valueCaptor.capture());
        Assertions.assertThat(keyCaptor.getAllValues()).doesNotContain(DSL_SENTENCE_CHOICE_KEY);
        Assertions.assertThat(valueCaptor.getAllValues()).doesNotContain(DSL_SENTENCE_CHOICE_VALUE);
    }
}
