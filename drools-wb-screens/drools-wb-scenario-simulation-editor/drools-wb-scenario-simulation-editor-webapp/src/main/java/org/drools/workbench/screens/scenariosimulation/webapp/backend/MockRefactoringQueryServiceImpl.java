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

package org.drools.workbench.screens.scenariosimulation.webapp.backend;

import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Specializes;

import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.uberfire.paging.PageResponse;

@Specializes
public class MockRefactoringQueryServiceImpl extends RefactoringQueryServiceImpl {

    @Override
    public int queryHitCount(final RefactoringPageRequest request) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public PageResponse<RefactoringPageRow> query(final RefactoringPageRequest request) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public List<RefactoringPageRow> query(final String queryName,
                                          final Set<ValueIndexTerm> queryTerms) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public PageResponse<RefactoringPageRow> queryToPageResponse(final QueryOperationRequest request) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }

    @Override
    public List<RefactoringPageRow> queryToList(final QueryOperationRequest request) {
        throw new UnsupportedOperationException("Not available in Submarine");
    }
}
