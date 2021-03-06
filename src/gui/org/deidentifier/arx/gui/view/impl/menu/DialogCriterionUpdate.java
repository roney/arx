/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2015 Florian Kohlmayer, Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.gui.view.impl.menu;

import java.util.List;

import org.deidentifier.arx.gui.Controller;
import org.deidentifier.arx.gui.model.ModelCriterion;
import org.deidentifier.arx.gui.model.ModelDPresenceCriterion;
import org.deidentifier.arx.gui.model.ModelKAnonymityCriterion;
import org.deidentifier.arx.gui.model.ModelLDiversityCriterion;
import org.deidentifier.arx.gui.model.ModelRiskBasedCriterion;
import org.deidentifier.arx.gui.model.ModelTClosenessCriterion;
import org.deidentifier.arx.gui.resources.Resources;
import org.deidentifier.arx.gui.view.SWTUtil;
import org.deidentifier.arx.gui.view.def.IDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import de.linearbits.swt.table.DynamicTable;
import de.linearbits.swt.table.DynamicTableColumn;

/**
 * A dialog for adding and configuring privacy criteria.
 *
 * @author Fabian Prasser
 */
public class DialogCriterionUpdate extends TitleAreaDialog implements IDialog {

    /** View */
    private Button               ok         = null;

    /** View */
    private Button               cancel     = null;

    /** View */
    private EditorCriterion<?>   editor     = null;

    /** Model */
    private List<ModelCriterion> elements   = null;

    /** Model */
    private ModelCriterion       selection  = null;

    /** Image */
    private Image                symbolL    = null;

    /** Image */
    private Image                symbolT    = null;

    /** Image */
    private Image                symbolK    = null;

    /** Image */
    private Image                symbolD    = null;

    /** Image */
    private Image                symbolR    = null;

    /** Controller */
    private Controller           controller = null;

    /** Root */
    private Composite            root       = null;

    /** Is cancel operation supported */
    private final boolean        edit;

    /**
     * Constructor.
     *
     * @param controller
     * @param parent
     * @param elements
     * @param edit
     */
    public DialogCriterionUpdate(final Controller controller,
                                 final Shell parent,
                                 List<ModelCriterion> elements,
                                 boolean edit) {
        super(parent);
        super.setShellStyle(super.getShellStyle() | SWT.RESIZE | SWT.MAX); 
        this.elements = elements;
        this.controller = controller;
        this.edit = edit;
    }

    /**
     * Constructor.
     *
     * @param controller
     * @param parent
     * @param elements
     * @param cancel
     * @param selection
     */
    public DialogCriterionUpdate(final Controller controller,
                                   final Shell parent,
                                   List<ModelCriterion> elements,
                                   boolean cancel,
                                   ModelCriterion selection) {
        this(controller, parent, elements, cancel);
        this.selection = selection;
    }

    @Override
    public boolean close() {
        if (symbolL != null) symbolL.dispose();
        if (symbolT != null) symbolT.dispose();
        if (symbolK != null) symbolK.dispose();
        if (symbolD != null) symbolD.dispose();
        if (symbolR != null) symbolR.dispose();
        return super.close();
    }

    /**
     * Returns the selected criterion.
     *
     * @return
     */
    public ModelCriterion getCriterion() {
        return this.selection;
    }

    /**
     * Update
     */
    private void update() {
        if (editor != null) {
            editor.dispose();
        }
        
        if (selection != null) {
            if (edit && ok != null) {
                ok.setEnabled(true);
            }
            if (selection instanceof ModelLDiversityCriterion) {
                editor = new EditorCriterionLDiversity(root, (ModelLDiversityCriterion)selection);
            } else if (selection instanceof ModelTClosenessCriterion) {
                editor = new EditorCriterionTCloseness(root, (ModelTClosenessCriterion)selection);
            } else if (selection instanceof ModelKAnonymityCriterion) {
                editor = new EditorCriterionKAnonymity(root, (ModelKAnonymityCriterion)selection);
            } else if (selection instanceof ModelDPresenceCriterion) {
                editor = new EditorCriterionDPresence(root, (ModelDPresenceCriterion)selection);
            } else if (selection instanceof ModelRiskBasedCriterion) {
                editor = new EditorCriterionRiskBased(root, (ModelRiskBasedCriterion)selection);
            }
        } else {
            if (edit && ok != null) {
                ok.setEnabled(false);
            }
        }
        
        root.layout();
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImages(Resources.getIconSet(newShell.getDisplay()));
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {

        parent.setLayoutData(SWTUtil.createFillGridData());

        // Create OK Button
        ok = createButton(parent, Window.OK, Resources.getMessage("ProjectDialog.3"), true); //$NON-NLS-1$
        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setReturnCode(Window.OK);
                close();
            }
        });
        ok.setEnabled(elements.size() != 0);

        if (edit) {
                
            // Create Cancel Button
            cancel = createButton(parent, Window.CANCEL, Resources.getMessage("ProjectDialog.4"), false); //$NON-NLS-1$
            cancel.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent e) {
                    selection = null;
                    setReturnCode(Window.CANCEL);
                    close();
                }
            });
        }
    }
    
    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        if (!edit) {
            setTitle(Resources.getMessage("CriterionSelectionDialog.9")); //$NON-NLS-1$
            setMessage(Resources.getMessage("CriterionSelectionDialog.8"), IMessageProvider.NONE); //$NON-NLS-1$
        } else {
            setTitle(Resources.getMessage("CriterionSelectionDialog.7")); //$NON-NLS-1$
            setMessage(Resources.getMessage("CriterionSelectionDialog.6"), IMessageProvider.NONE); //$NON-NLS-1$
        }
        return contents;
    }
    
    @Override
    protected Control createDialogArea(final Composite parent) {

        parent.setLayout(SWTUtil.createGridLayout(1));

        final DynamicTable table = SWTUtil.createTableDynamic(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        final GridData d = SWTUtil.createFillGridData();
        d.heightHint = 200;
        table.setLayoutData(d);

        DynamicTableColumn column1 = new DynamicTableColumn(table, SWT.NONE);
        column1.setText(Resources.getMessage("DialogCriterionUpdate.0")); //$NON-NLS-1$
        column1.setWidth("10%", "30px"); //$NON-NLS-1$ //$NON-NLS-2$
        DynamicTableColumn column2 = new DynamicTableColumn(table, SWT.NONE);
        column2.setText(Resources.getMessage("CriterionSelectionDialog.2")); //$NON-NLS-1$
        column2.setWidth("45%", "100px"); //$NON-NLS-1$ //$NON-NLS-2$
        DynamicTableColumn column3 = new DynamicTableColumn(table, SWT.NONE);
        column3.setText(Resources.getMessage("CriterionSelectionDialog.3")); //$NON-NLS-1$
        column3.setWidth("45%", "100px"); //$NON-NLS-1$ //$NON-NLS-2$
        
        column1.pack();
        column2.pack();
        column3.pack();


        this.symbolL = controller.getResources().getImage("symbol_l.png"); //$NON-NLS-1$
        this.symbolT = controller.getResources().getImage("symbol_t.png"); //$NON-NLS-1$
        this.symbolK = controller.getResources().getImage("symbol_k.png"); //$NON-NLS-1$
        this.symbolD = controller.getResources().getImage("symbol_d.png"); //$NON-NLS-1$
        this.symbolR = controller.getResources().getImage("symbol_r.png"); //$NON-NLS-1$
        
        for (ModelCriterion c : elements) {

            TableItem item = new TableItem(table, SWT.NONE);
            
            if (c instanceof ModelLDiversityCriterion) {
                item.setText(new String[] { "", c.getLabel(), ((ModelLDiversityCriterion)c).getAttribute() }); //$NON-NLS-1$
                item.setImage(0, symbolL);
            } else if (c instanceof ModelTClosenessCriterion) {
                item.setText(new String[] { "", c.getLabel(), ((ModelTClosenessCriterion)c).getAttribute() }); //$NON-NLS-1$
                item.setImage(0, symbolT);
            } else if (c instanceof ModelKAnonymityCriterion) {
                item.setText(new String[] { "", c.getLabel(), "" }); //$NON-NLS-1$ //$NON-NLS-2$
                item.setImage(0, symbolK);
            } else if (c instanceof ModelDPresenceCriterion) {
                item.setText(new String[] { "", c.getLabel(), "" }); //$NON-NLS-1$ //$NON-NLS-2$
                item.setImage(0, symbolD);
            } else if (c instanceof ModelRiskBasedCriterion) {
                item.setText(new String[] { "", c.getLabel(), "" }); //$NON-NLS-1$ //$NON-NLS-2$
                item.setImage(0, symbolR);
            }
        }

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (table.getSelectionIndex() != -1) {
                    selection = elements.get(table.getSelectionIndex());
                    if (edit) ok.setEnabled(true);
                    update();
                } else {
                    selection = null;
                    if (edit) ok.setEnabled(false);
                    update();
                }
            }
        });

        Group border = new Group(parent, SWT.SHADOW_ETCHED_IN);
        border.setText(Resources.getMessage("DialogCriterionUpdate.15")); //$NON-NLS-1$
        GridData data = SWTUtil.createFillGridData();
        data.grabExcessVerticalSpace = false;
        border.setLayoutData(data);
        border.setLayout(new FillLayout());
        
        this.root = new Composite(border, SWT.NONE);
        this.root.setLayout(SWTUtil.createGridLayout(1));


        if (selection != null) {
            table.setSelection(elements.indexOf(selection));
        } else if (table.getItemCount() != 0){
            table.setSelection(0);
            selection = elements.get(0);
        }
        update();
        
        return parent;
    }

    @Override
    protected ShellListener getShellListener() {
        return new ShellAdapter() {
            @Override
            public void shellClosed(final ShellEvent event) {
                setReturnCode(Window.CANCEL);
            }
        };
    }

    @Override
    protected boolean isResizable() {
        return false;
    }
}
