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
package org.drools.workbench.screens.guided.dtable.shared;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class XLSConversionResult {

    private String errorMessage;
    private Set<String> infoMessages = new HashSet<>();

    public XLSConversionResult() {
        this.errorMessage = "";
    }

    public XLSConversionResult(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isConverted() {
        return Objects.equals("", errorMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addInfoMessage(final String infoMessage) {
        this.infoMessages.add(infoMessage);
    }

    public Set<String> getInfoMessages() {
        return Collections.unmodifiableSet(infoMessages);
    }
}
