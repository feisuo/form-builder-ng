/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.form.builder.services.impl;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jbpm.form.builder.services.api.FormDisplayService;
import org.jbpm.task.Content;
import org.jbpm.task.I18NText;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.utils.ContentMarshallerHelper;


public class FormDisplayServiceImpl implements FormDisplayService { 
    
    @Inject
    private TaskQueryService queryService;
    @Inject
    private TaskContentService contentService;
    
    @Inject
    private TaskInstanceService instanceService;
    
    public String getFormDisplay(long taskId) {
        Task task = queryService.getTaskInstanceById(taskId);
        
        
        Object input = null;
        long contentId = task.getTaskData().getDocumentContentId();
        if (contentId != -1) {
            Content content = contentService.getContentById(contentId);
            input = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        }
        
  
        // check if a template exists
        String name = null;
        List<I18NText> names = task.getNames();
        for (I18NText text: names) {
            if ("en-UK".equals(text.getLanguage())) {
                name = text.getText();
            }
        }
       
        
        InputStream template = getClass().getResourceAsStream("/ftl/DefaultTask.ftl");
        

        // merge template with process variables
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("task", task);
        renderContext.put("content", input);
        if (input instanceof Map) {
            Map<?, ?> map = (Map) input;
            for (Map.Entry<?, ?> entry: map.entrySet()) {
                if (entry.getKey() instanceof String) {
                    renderContext.put((String) entry.getKey(), entry.getValue());
                }
            }
        }
        return render(name, template, renderContext);
    
    
    }
    
    public void completeForm(long id, String userId, Map<String, String> params) {
        instanceService.complete(id, userId, (Map)params);
    }
    
    
    public String render(String name, InputStream src, Map<String, Object> renderContext) {
        String str = null;
        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(name, new InputStreamReader(src), cfg);
            //final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            StringWriter out = new StringWriter();
            temp.process(renderContext, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process form template", e);
        }
        return str;
    }
 
}