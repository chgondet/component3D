
package com.inductiveautomation.ignition.examples.ce.components;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import java.awt.*;

/**
 * Created by Christophe on 20/01/2016.
 */


public class Molecule extends Group {
     static final double CAMERA_INITIAL_DISTANCE = -450;
     static final double CAMERA_INITIAL_X_ANGLE = 70.0;
     static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double HYDROGEN_ANGLE = 104.5;

    private Color hydrogenColor = Color.lightGray;
    private Color hydrogenSpecular = Color.lightGray;
    private Color oxygenColor = Color.lightGray;
    private Color oxygenSpecular = Color.lightGray;

    final Xform world = new Xform();
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();

    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();

    public javafx.scene.paint.Color getfxColor(java.awt.Color awtColor)
    {

        return javafx.scene.paint.Color.rgb(awtColor.getRed(),awtColor.getGreen(),awtColor.getBlue(),(double)(awtColor.getAlpha())/255.0);
    }

    public Molecule() {
        super();
        build();
    };

    public void build() {
        buildCamera();
        buildAxes();
        buildMolecule();
        getChildren().addAll(world);
    }

    public void clear() {
        getChildren().removeAll(world);
        axisGroup.reset();
        moleculeGroup.reset();
        world.reset();
    }

    private void buildCamera() {
        this.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }
    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(javafx.scene.paint.Color.DARKRED);
        redMaterial.setSpecularColor(javafx.scene.paint.Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(javafx.scene.paint.Color.DARKGREEN);
        greenMaterial.setSpecularColor(javafx.scene.paint.Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(javafx.scene.paint.Color.DARKBLUE);
        blueMaterial.setSpecularColor(javafx.scene.paint.Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }
    private void buildMolecule() {

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(getfxColor(oxygenColor));
        redMaterial.setSpecularColor(getfxColor(oxygenSpecular));

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(getfxColor(hydrogenColor));
        whiteMaterial.setSpecularColor(getfxColor(hydrogenSpecular));

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(javafx.scene.paint.Color.DARKGREY);
        greyMaterial.setSpecularColor(javafx.scene.paint.Color.GREY);

        // Molecule Hierarchy
        // [*] moleculeXform
        //     [*] oxygenXform
        //         [*] oxygenSphere
        //     [*] hydrogen1SideXform
        //         [*] hydrogen1Xform
        //             [*] hydrogen1Sphere
        //         [*] bond1Cylinder
        //     [*] hydrogen2SideXform
        //         [*] hydrogen2Xform
        //             [*] hydrogen2Sphere
        //         [*] bond2Cylinder

        Xform moleculeXform = new Xform();
        moleculeXform.debug();
        Xform oxygenXform = new Xform();
        Xform hydrogen1SideXform = new Xform();
        Xform hydrogen1Xform = new Xform();
        Xform hydrogen2SideXform = new Xform();
        Xform hydrogen2Xform = new Xform();

        // Oxygène
        Sphere oxygenSphere = new Sphere(40.0);
        oxygenSphere.setMaterial(redMaterial);
        oxygenXform.getChildren().add(oxygenSphere);

        // Hydrogène 1
        Sphere hydrogen1Sphere = new Sphere(30.0);
        hydrogen1Sphere.setMaterial(whiteMaterial);
        hydrogen1Sphere.setTranslateX(0.0);
        hydrogen1Xform.getChildren().add(hydrogen1Sphere);
        hydrogen1Xform.setTx(100.0);
        hydrogen1SideXform.getChildren().add(hydrogen1Xform);
        Cylinder bond1Cylinder = new Cylinder(5, 100);
        bond1Cylinder.setMaterial(greyMaterial);
        bond1Cylinder.setTranslateX(50.0);
        bond1Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond1Cylinder.setRotate(90.0);
        hydrogen1SideXform.getChildren().add(bond1Cylinder);

        // Hydrogène 2
        Sphere hydrogen2Sphere = new Sphere(30.0);
        hydrogen2Sphere.setMaterial(whiteMaterial);
        hydrogen2Sphere.setTranslateZ(0.0);
        hydrogen2SideXform.getChildren().add(hydrogen2Xform);
        hydrogen2Xform.getChildren().add(hydrogen2Sphere);
        hydrogen2Xform.setTx(100.0);
        Cylinder bond2Cylinder = new Cylinder(5, 100);
        bond2Cylinder.setMaterial(greyMaterial);
        bond2Cylinder.setTranslateX(50.0);
        bond2Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond2Cylinder.setRotate(90.0);
        hydrogen2SideXform.getChildren().add(bond2Cylinder);
        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);

        moleculeXform.getChildren().add(oxygenXform);
        moleculeXform.getChildren().add(hydrogen1SideXform);
        moleculeXform.getChildren().add(hydrogen2SideXform);
        moleculeGroup.getChildren().add(moleculeXform);
        world.getChildren().addAll(moleculeGroup);
    }

    public Color getHydrogenColor() {
        return hydrogenColor;
    }

    public void setHydrogenColor(Color hydrogenColor) {
        this.hydrogenColor = hydrogenColor;
    }

    public Color getHydrogenSpecular() {
        return hydrogenSpecular;
    }

    public void setHydrogenSpecular(Color hydrogenSpecular) {
        this.hydrogenSpecular = hydrogenSpecular;
    }

    public Color getOxygenColor() {
        return oxygenColor;
    }

    public void setOxygenColor(Color oxygenColor) {
        this.oxygenColor = oxygenColor;
    }

    public Color getOxygenSpecular() {
        return oxygenSpecular;
    }

    public void setOxygenSpecular(Color oxygenSpecular) {
        this.oxygenSpecular = oxygenSpecular;
    }
}
