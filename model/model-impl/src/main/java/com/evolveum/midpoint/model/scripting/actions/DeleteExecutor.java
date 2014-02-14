/*
 * Copyright (c) 2010-2014 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.model.scripting.actions;

import com.evolveum.midpoint.model.scripting.Data;
import com.evolveum.midpoint.model.scripting.ExecutionContext;
import com.evolveum.midpoint.model.scripting.ScriptExecutionException;
import com.evolveum.midpoint.prism.Item;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ActivationStatusType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ActivationType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.FocusType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ShadowType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_2.ActionExpressionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mederly
 */
@Component
public class DeleteExecutor extends BaseActionExecutor {

    private static final Trace LOGGER = TraceManager.getTrace(DeleteExecutor.class);

    private static final String NAME = "delete";

    @Autowired
    private OperationsHelper operationsHelper;

    @PostConstruct
    public void init() {
        rootExpressionEvaluator.registerActionExecutor(NAME, this);
    }

    @Override
    public Data execute(ActionExpressionType expression, Data input, ExecutionContext context, OperationResult result) throws ScriptExecutionException {

        if (input != null) {
            for (Item item : input.getData()) {
                if (item instanceof PrismObject) {
                    PrismObject<? extends ObjectType> prismObject = (PrismObject) item;
                    ObjectType objectType = prismObject.asObjectable();
                    operationsHelper.applyDelta(createDeleteDelta(objectType), context, result);
                    context.println("Deleted " + item.toString());
                } else {
                    String m = "Item is not a PrismObject, 'delete' operation skipped: " + item.toString();
                    LOGGER.warn(m);
                    context.println(m);
                }
            }
        }
        return null;
    }

    private ObjectDelta createDeleteDelta(ObjectType objectType) {
        return ObjectDelta.createDeleteDelta(objectType.getClass(), objectType.getOid(), prismContext);
    }

}
