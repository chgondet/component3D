/* Filename: HelloWorldComponent.java
 * Created by Perry Arellano-Jones on 12/11/14.
 * Copyright Inductive Automation 2014
 */
package com.inductiveautomation.ignition.examples.ce.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.inductiveautomation.ignition.client.model.LocaleListener;

import com.inductiveautomation.vision.api.client.components.model.AbstractVisionPanel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static com.inductiveautomation.factorypmi.application.i18n.TranslationUtils.t;

/**
 * This is the actual component for the Ignition SDK's component example module.
 *
 * @author Carl Gould
 */
public class Vision3d extends AbstractVisionPanel implements LocaleListener {

    public static final int ANIMATION_OFF = 0;
    public static final int ANIMATION_RTL = 1;
    public static final int ANIMATION_LTR = 2;
    private String text = "Hello World";
    private Color fillColor = Color.BLACK;
    private Color strokeColor = Color.BLACK;
    private float strokeWidth = 0f;
    private Color hydrogenColor = Color.lightGray;
    private Color hydrogenSpecular = Color.white;
    private Color oxygenColor = Color.red;
    private Color oxygenSpecular = Color.white;

    private int animation = ANIMATION_OFF;
    private int animationRate = 30;
    private Timer _timer;
    private int _position = 0;


    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    final Molecule Mol;
     Scene scene;
    final JFXPanel fxPanel ;


    Logger logger;

    private static final String IT_M_TASK_NAME = "IT-Mation Vision3d";
    private int lastWidth;

    public Vision3d() {
        logger = LogManager.getLogger(this.getClass());
        logger.info(IT_M_TASK_NAME + " - Constructor " );
        logger.info(IT_M_TASK_NAME + " - Start " );

        setOpaque(false);
        setPreferredSize(new Dimension(130, 45));
        setFont(new Font("Dialog", Font.PLAIN, 24));
        fxPanel = new JFXPanel();
        add(fxPanel);
        Mol = new Molecule();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private void initFX(JFXPanel fxPanel) {
        logger.info(IT_M_TASK_NAME + " - initFX " );
        start(fxPanel);
    }

    public void start(JFXPanel jfxPanel/*Stage primaryStage*/) {
        logger.info(IT_M_TASK_NAME + " - start " );
        scene = new Scene(Mol, this.getWidth(), this.getHeight(), true);
        scene.setFill(javafx.scene.paint.Color.AZURE);
        handleKeyboard(scene, Mol.world);
        handleMouse(scene, Mol.world);
        scene.setCamera(Mol.camera);

        jfxPanel.setScene(scene);

    }



    private void handleMouse(Scene scene, final Node root) {

        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    Mol.cameraXform.ry.setAngle(Mol.cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);  //
                    Mol.cameraXform.rx.setAngle(Mol.cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);  // -
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = Mol.camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    Mol.camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    Mol.cameraXform2.t.setX(Mol.cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                    Mol.cameraXform2.t.setY(Mol.cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  // -
                }
            }
        }); // setOnMouseDragged
    } //handleMouse
    private void handleKeyboard(Scene scene, final Node root) {

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Z:
                        Mol.cameraXform2.t.setX(0.0);
                        Mol.cameraXform2.t.setY(0.0);
                        Mol.cameraXform.ry.setAngle(Mol.CAMERA_INITIAL_Y_ANGLE);
                        Mol.cameraXform.rx.setAngle(Mol.CAMERA_INITIAL_X_ANGLE);
                        break;
                    case X:
                        Mol.axisGroup.setVisible(!Mol.axisGroup.isVisible());
                        break;
                    case V:
                        Mol.moleculeGroup.setVisible(!Mol.moleculeGroup.isVisible());
                        break;
                    case A:
                        Mol.cameraXform.ry.setAngle(Mol.cameraXform.ry.getAngle() + 1);
                        break;
                } // switch
            } // handle()
        });  // setOnKeyPressed
    }  //  handleKeyboard()


    private ActionListener animationListener = new ActionListener() {
        int t = 0;

        /** This gets executed when animation is turned on, to move the animationPosition between 0 and 99 */
        public void actionPerformed(ActionEvent e) {
            t = (t + 1) % 100;
            if (animation == ANIMATION_RTL) {
                _position = 100 - t;
            } else {
                _position = t;
            }
            Mol.cameraXform.ry.setAngle(Mol.cameraXform.ry.getAngle()+1);
            Mol.cameraXform.rx.setAngle(Mol.cameraXform.ry.getAngle()+2);
            repaint();
        }
    };
    private Rectangle _area;
    private Stroke _stroke = null;




    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.add(new Vision3d());

        f.setBounds(100, 100, 300, 300);
        f.setVisible(true);
    }

    /**
     * Overriding paintComponent is how you make a component that has custom graphics. All of the graphics code here
     * would be covered in any Java2D book.
     */
    @Override
    protected void paintComponent(Graphics _g) {
        //logger.info(IT_M_TASK_NAME + " - paintComponent " );

       if (_area.width != lastWidth) {
            logger.info(IT_M_TASK_NAME + " - resize " + String.valueOf(lastWidth));
            lastWidth = _area.width;
        }
        Graphics2D g = (Graphics2D) _g;

        // Preserve the original transform to roll back to at the end
        AffineTransform originalTx = g.getTransform();

        // Turn on anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate the inner area, compensating for borders
        _area = SwingUtilities.calculateInnerArea(this, _area);
        // Now translate so that 0,0 is is at the inner origin
        g.translate(_area.x, _area.y);

        // Set the font to our component's font property

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();

        // Calculate the x,y for the String's baseline in order to center it
        int stringWidth = fm.stringWidth(text);
        int stringX = (_area.width - stringWidth) / 2;

        // This is the _easy_ way to draw a string, but that's no fun...
        //		g.drawString(text, stringX, stringY);

        if (_position == 0) {
            // Perfectly centered
            paintTextAt(g, stringX);
        } else {
            // Draw twice to achieve marquee effect
            float dX = _area.width * (_position / 100f);
            paintTextAt(g, stringX + dX);
            paintTextAt(g, stringX - _area.width + dX);
        }

        // Reverse any transforms we made
        g.setTransform(originalTx);
    }

    private void paintTextAt(Graphics2D g, float xPosition) {
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = font.createGlyphVector(frc, getText());

        Rectangle2D bounds = vector.getVisualBounds();

        float yPosition = (float) (_area.getHeight() + bounds.getHeight()) / 2f;

        Shape textShape = vector.getOutline(xPosition, yPosition);

        g.setColor(fillColor);
        g.fill(textShape);

        if (_stroke != null) {
            g.setColor(strokeColor);
            g.setStroke(_stroke);
            g.draw(textShape);
        }
    }

    /*
     * Even though the animation is fun, it is really there to show how to use these startup/shutdown lifecycle methods
     * to ensure that any long-running logic in your component gets properly shut down.
     */

    @Override
    protected void onStartup() {
        // Seems like a no-op, but actually will trigger logic to re-start the timer if necessary
        setAnimation(animation);
    }

    @Override
    protected void onShutdown() {
        if (_timer != null && _timer.isRunning()) {
            _timer.stop();
        }
    }

    /**
     * getText() returns a translated version of the text string. Always use this version when displaying the text,
     * rather than accessing the variable directly.
     */
    public String getText() {
        return t(this, text);   // This function returns a translated version of the text
    }

    public void setText(String text) {
        logger.info(IT_M_TASK_NAME + " - setText " );
        // Firing property changes like this is required for any property that has the BOUND_MASK set on it.
        // (See this component's BeanInfo class)
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, text);

        repaint();
    }

    public int getAnimation() {
        return animation;
    }

    public void setAnimation(int animation) {
        logger.info(IT_M_TASK_NAME + " - setAnimation " );
        this.animation = animation;
        if (animation == ANIMATION_OFF && _timer != null && _timer.isRunning()) {
            _timer.stop();
            _position = 0;
            repaint();
        }
        if (animation != ANIMATION_OFF) {
            if (_timer == null) {
                _timer = new Timer(animationRate, animationListener);
            }
            if (!_timer.isRunning()) {
                _timer.start();
            }
        }
    }

    public Color getHydrogenColor() {
        return hydrogenColor;
    }

    public void setHydrogenColor(Color hydrogenColor) {
        logger.info(IT_M_TASK_NAME + " - setHydrogenColor " );
        this.hydrogenColor = hydrogenColor;
        Mol.setHydrogenColor(hydrogenColor);
        Mol.clear();
        Mol.build();
        start(fxPanel);

    }

    public Color getOxygenColor() {
        return oxygenColor;
    }

    public void setOxygenColor(Color oxygenColor) {
        logger.info(IT_M_TASK_NAME + " - setOxygenColor " );
        this.oxygenColor = oxygenColor;
        Mol.setOxygenColor(oxygenColor);
        Mol.clear();
        Mol.build();
        start(fxPanel);

    }

    public Color getOxygenSpecular() {
        return oxygenSpecular;
    }

    public void setOxygenSpecular(Color oxygenSpecular) {
        this.oxygenSpecular = oxygenSpecular;
        logger.info(IT_M_TASK_NAME + " - setOxygenSpecular " );
        Mol.setOxygenSpecular(oxygenSpecular);
        Mol.clear();
        Mol.build();
        start(fxPanel);
    }

    public Color getHydrogenSpecular() {
        return hydrogenSpecular;
    }

    public void setHydrogenSpecular(Color hydrogenSpecular) {
        this.hydrogenSpecular = hydrogenSpecular;
        logger.info(IT_M_TASK_NAME + " - setHydrogenSpecular " );
        Mol.setHydrogenSpecular(hydrogenSpecular);
        Mol.clear();
        Mol.build();
        start(fxPanel);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
        repaint();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        if (strokeWidth > 0) {
            _stroke = new BasicStroke(strokeWidth);
        } else {
            _stroke = null;
        }
        repaint();
    }

    public int getAnimationRate() {
        return animationRate;
    }

    public void setAnimationRate(int animationRate) {
        this.animationRate = Math.max(10, animationRate);
        if (_timer != null) {
            _timer.setDelay(animationRate);
        }
    }

    /**
     * This function is called whenever the user's locale changes. Add code here to deal with any
     * translations, number formats, or date formats that need to change as a result of the locale
     * changing. Some items may need to be revalidated/repainted to cause the screen to update.
     */
    @Override
    public void localeChanged(Locale locale) {
        logger.info(IT_M_TASK_NAME + " - localeChanged " );
        //We need to fire a change on text in order to trigger html based displays to refresh.
        firePropertyChange("text", null, getText());
        repaint();
    }
}
