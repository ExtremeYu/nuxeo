/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *
 */
package org.nuxeo.ecm.automation.core.operations.document;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import java.util.Map;

@Operation(id = ResetSchema.ID, category = Constants.CAT_DOCUMENT, label = "Reset Schema", description = "Reset all values")
public class ResetSchema {

    public static final String ID = "Document.ResetSchema";

    @Context
    protected OperationContext context;

    @Context
    protected CoreSession session;

    @Param(name = "schema", required = false)
    protected String schema;

    @Param(name = "xpath", required = false)
    protected String xpath;

    private void resetSchemaProperties(DocumentModel target) throws OperationException {
        if (xpath != null) {
            target.setPropertyValue(xpath, null);
        } else if (schema != null) {
            for (Map.Entry<String, Object> entry : target.getProperties(schema).entrySet()) {
                target.setProperty(schema, entry.getKey(), null);
            }
        } else {
            throw new OperationException("No schema or xpath was provided");
        }
    }

    @OperationMethod
    public DocumentModel run(DocumentModel target) throws OperationException {
        resetSchemaProperties(target);
        return target;
    }

    @OperationMethod
    public DocumentModelList run(DocumentModelList targets) throws OperationException {
        for (DocumentModel target : targets) {
            resetSchemaProperties(target);
        }
        return targets;
    }

}
