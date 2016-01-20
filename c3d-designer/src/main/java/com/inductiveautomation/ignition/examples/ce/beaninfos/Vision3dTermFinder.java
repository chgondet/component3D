package com.inductiveautomation.ignition.examples.ce.beaninfos;

import javax.swing.*;
import java.util.List;

import com.inductiveautomation.factorypmi.designer.i18n.search.ComponentPropertyTermFinder;
import com.inductiveautomation.factorypmi.designer.i18n.search.TermDescriptor;
import com.inductiveautomation.ignition.examples.ce.components.Vision3d;

/**
 * Filename: Vision3dTermFinder.java
 * Created on Sep 24, 2015
 * Author: Kathy Applebaum
 * Copyright Inductive Automation 2015
 * Project: ComponentExample
 */
public class Vision3dTermFinder extends ComponentPropertyTermFinder {

    public Vision3dTermFinder() {
    }

    @Override
    public void getTermsFor(JComponent component, List<TermDescriptor> results) {
        super.getTermsFor(component, results);
        if (component instanceof Vision3d) {
            // Replace this with specific terms you want to add. Terms can be fixed, or
            // can come from properties of the component.
            results.add(new TermDescriptor("Hello", "Hello"));
        }
    }
}
