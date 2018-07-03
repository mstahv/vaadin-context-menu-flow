/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.contextmenu;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.data.binder.HasItemsAndComponents;
import com.vaadin.flow.dom.Element;

/**
 * Server-side component for {@code <vaadin-context-menu>}
 * 
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
@HtmlImport("flow-component-renderer.html")
public class ContextMenu extends GeneratedVaadinContextMenu<ContextMenu>
        implements HasItemsAndComponents<Object> {

    private Component target;

    private Element template;
    private ListBox<Object> listbox;
    private Element container;

    /**
     * Creates an empty context menu.
     */
    public ContextMenu() {
        template = new Element("template");
        getElement().appendChild(template);

        listbox = new ListBox<>();
        container = listbox.getElement();

        getElement().appendVirtualChild(container);

        getElement().getNode()
                .runWhenAttached(ui -> ui.beforeClientResponse(this,
                        context -> attachComponentRenderer()));
    }

    /**
     * Sets the target component for this context menu.
     * <p>
     * The context menu can be opened with a right click or a long touch on the
     * target component.
     * 
     * @param target
     *            the target component for this context menu
     */
    public void setTarget(Component target) {
        this.target = target;
        getElement().getNode().runWhenAttached(
                ui -> ui.beforeClientResponse(this, context -> ui.getPage()
                        .executeJavaScript("$0.listenOn=$1", this, target)));
    }

    /**
     * Gets the target component of this context menu, or {@code null} if it
     * doesn't have a target.
     * 
     * @return the target component of this context menu
     * @see #setTarget(Component)
     */
    public Component getTarget() {
        return target;
    }

    /**
     * Adds the given components into this context menu.
     * <p>
     * The elements in the DOM will not be children of the
     * {@code <vaadin-context-menu>} element, but will be inserted into an
     * overlay that is attached into the {@code <body>}.
     *
     * @param components
     *            the components to add
     */
    @Override
    public void add(Component... components) {
        Objects.requireNonNull(components, "Components to add cannot be null");
        for (Component component : components) {
            Objects.requireNonNull(component,
                    "Component to add cannot be null");
            container.appendChild(component.getElement());
        }
    }

    @Override
    public void remove(Component... components) {
        Objects.requireNonNull(components,
                "Components to remove cannot be null");
        for (Component component : components) {
            Objects.requireNonNull(component,
                    "Component to remove cannot be null");
            if (container.equals(component.getElement().getParent())) {
                container.removeChild(component.getElement());
            } else {
                throw new IllegalArgumentException("The given component ("
                        + component + ") is not a child of this component");
            }
        }
    }

    @Override
    public void removeAll() {
        container.removeAllChildren();
    }

    @Override
    public Stream<Component> getChildren() {
        Builder<Component> childComponents = Stream.builder();
        container.getChildren().forEach(childElement -> ComponentUtil
                .findComponents(childElement, childComponents::add));
        return childComponents.build();
    }

    @Override
    public void setItems(Collection<Object> items) {
        listbox.setItems(items);
    }

    @Override
    public void addComponents(Object afterItem, Component... components) {
        listbox.addComponents(afterItem, components);
    }

    @Override
    public void prependComponents(Object beforeItem, Component... components) {
        listbox.prependComponents(beforeItem, components);
    }

    public void setCloseOnClick(boolean closeOnClick) {
        setCloseOn(closeOnClick ? "click" : "none");
    }

    public boolean isCloseOnClick() {
        return "click".equals(getCloseOnString());
    }

    private void attachComponentRenderer() {
        String appId = UI.getCurrent().getInternals().getAppId();
        int nodeId = container.getNode().getId();
        String renderer = String.format(
                "<flow-component-renderer appid=\"%s\" nodeid=\"%s\"></flow-component-renderer>",
                appId, nodeId);
        template.setProperty("innerHTML", renderer);
    }

}
