package spaska.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spaska.clusterers.IClusterer;
import spaska.data.readers.Validator;
import spaska.gui.engines.ClusterEngine;

/**
 * 
 * @author <a href="mailto:vesko.m.georgiev@gmail.com">Vesko Georgiev</a>
 */
public class ClustererTab extends SpaskaTab {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory
            .getLogger(ClustererTab.class);
    private ComboList<IClusterer> clusterCombo;
    private ComboList<Validator> validatorCombo;

    public ClustererTab() {
        validatorCombo = new ComboList<Validator>("Choose Validators",
                getEngine().getValidators());
        clusterCombo = new ComboList<IClusterer>("Choose Clusterers",
                getEngine().getClusterizators(), 1);

        buildGui();
    }

    private void buildGui() {
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(2, 2, 2, 2);

        // 1 row
        c.gridy = 0;
        c.weighty = 0;

        c.gridx = 0;
        c.weightx = 0;
        add(browse, c);

        c.gridwidth = 3;
        c.gridx = 1;
        c.weightx = 1;
        add(textField, c);

        // 2 row
        c.gridy = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 2;
        c.gridx = 0;
        c.weightx = 1;
        add(validatorCombo, c);

        c.gridwidth = 2;
        c.gridx = 2;
        c.weightx = 1;
        add(clusterCombo, c);

        // 3 row
        c.gridy = 2;
        c.weighty = 0;

        c.gridwidth = 1;
        c.anchor = GridBagConstraints.SOUTHEAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 3;
        c.weightx = 1;

        add(run, c);
    }

    @Override
    public String getTitle() {
        return "Clusterer";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Utils.FILE_DATASET)) {
            openFile();
        }
        if (e.getActionCommand().equals(Utils.START)) {
            try {
                getEngine().reset();

                getEngine().setFile(openedFile);
                setEngineArgs(validatorCombo.getParameters());
                setEngineArgs(clusterCombo.getParameters());

                setButtonStop();
                start();
            } catch (Exception ex) {
                showError(ex);
            }
        } else if (e.getActionCommand().equals(Utils.STOP)) {
            setButtonStart();
            stop();
        }
    }

    private <T> void setEngineArgs(
            Map<Class<? extends T>, Map<String, String>> classToParameters)
            throws Exception {
        for (Entry<Class<? extends T>, Map<String, String>> entry : classToParameters
                .entrySet()) {
            Class<? extends T> cls = entry.getKey();
            Map<String, String> params = entry.getValue();

            LOG.info("Set " + cls + " with " + params);
            if (Validator.class.isAssignableFrom(cls)) {
                getEngine().addValidator((Validator) cls.newInstance(), params);
            } else if (IClusterer.class.isAssignableFrom(cls)) {
                getEngine()
                        .setClusterer((IClusterer) cls.newInstance(), params);
            }
        }
    }

    @Override
    protected ClusterEngine getEngine() {
        if (engine == null) {
            engine = new ClusterEngine();
        }
        return (ClusterEngine) engine;
    }

}
