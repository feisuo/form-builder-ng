/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.form.builder.ng.model.client.form.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.ng.model.client.CommonGlobals;
import org.jbpm.form.builder.ng.model.client.FormBuilderException;
import org.jbpm.form.builder.ng.model.client.effect.FBFormEffect;
import org.jbpm.form.builder.ng.model.client.form.FBFormItem;
import org.jbpm.form.builder.ng.model.client.form.HasSourceReference;
import org.jbpm.form.builder.ng.model.client.messages.I18NConstants;
import org.jbpm.form.builder.ng.model.client.resources.FormBuilderResources;
import org.jbpm.form.builder.ng.model.shared.api.FormBuilderDTO;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class CanvasFormItem extends FBFormItem implements HasSourceReference {

    private final I18NConstants i18n = CommonGlobals.getInstance().getI18n();
    
    private final Canvas canvas = Canvas.createIfSupported();
    private final Label notSupported = new Label(i18n.CanvasNotSupported());
    
    private String fallbackUrl = FormBuilderResources.INSTANCE.canvasNotSupported().getSafeUri().asString();
    private String cssClassName;
    private String id;
    private String dataType;
    
    public CanvasFormItem() {
        this(new ArrayList<FBFormEffect>());
    }
    
    public CanvasFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
        if (canvas == null) {
            add(notSupported);
        } else {
            add(canvas);
        }
        setWidth("300px");
        setHeight("200px");
    }

    @Override
    public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cssClassName", this.cssClassName);
        map.put("dataType", this.dataType);
        map.put("height", this.getHeight());
        map.put("width", this.getWidth());
        map.put("fallbackUrl", this.fallbackUrl);
        map.put("id", this.id);
        return map;
    }

    @Override
    public void saveValues(Map<String, Object> asPropertiesMap) {
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.fallbackUrl = extractString(asPropertiesMap.get("fallbackUrl"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.dataType = extractString(asPropertiesMap.get("dataType"));
        populate(this.canvas);
    }

    private void populate(Canvas canvas) {
        if (canvas != null) {
            if (this.cssClassName != null) {
                canvas.setStyleName(this.cssClassName);
            }
            if (this.getHeight() != null) {
                canvas.setHeight(this.getHeight());
            }
            if (this.getWidth() != null) {
                canvas.setWidth(this.getWidth());
            }
        }
    }
    
    @Override
    public FormBuilderDTO getRepresentation() {
        FormBuilderDTO dto = super.getRepresentation();
        dto.setString("cssClassName", cssClassName);
        dto.setString("fallbackUrl", fallbackUrl);
        dto.setString("id", id);
        dto.setString("dataType", dataType);
        return dto;
    }
    
    @Override
    public void populate(FormBuilderDTO dto) throws FormBuilderException {
        if (!dto.getClassName().endsWith("CanvasRepresentation")) {
            throw new FormBuilderException(i18n.RepNotOfType(dto.getClassName(), "CanvasRepresentation"));
        }
        super.populate(dto);
        this.fallbackUrl = dto.getString("fallbackUrl");
        this.cssClassName = dto.getString("cssClassName");
        this.id = dto.getString("id");
        this.dataType = dto.getString("dataType");

        populate(this.canvas);
    }

    @Override
    public FBFormItem cloneItem() {
        CanvasFormItem clone = super.cloneItem(new CanvasFormItem());
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.fallbackUrl = this.fallbackUrl;
        clone.cssClassName = this.cssClassName;
        clone.dataType = this.dataType;
        clone.id = this.id;
        clone.populate(clone.canvas);
        return clone;
    }

    @Override
    public Widget cloneDisplay(Map<String, Object> formData) {
        Canvas cv = Canvas.createIfSupported();
        if (cv == null) {
            return new Label(notSupported.getText());
        }
        populate(cv);
        super.populateActions(cv.getElement());
        return cv;
    }

    @Override
    public void setSourceReference(String sourceReference) {
        this.fallbackUrl = sourceReference;
    }

    @Override
    public String getSourceReference() {
        return this.fallbackUrl;
    }

    @Override
    public List<String> getAllowedTypes() {
        List<String> types = new ArrayList<String>();
        types.add("svg");
        return types;
    }

}
