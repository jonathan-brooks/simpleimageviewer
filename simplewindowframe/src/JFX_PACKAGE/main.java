/*
Author: Jonathan Brooks
Date Started: 28 February 2018
Last Update:  28 February 2018
Purpose: Bare bones JFX image viewer with minimal frame around image to conserve display space
Only supports PNG and JPG. Simple project to show code, able to add functionaility on request.
*/

// Package
package JFX_PACKAGE;
// Imports
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class main extends Application {
    private static double xOffset = 0;
    private static double yOffset = 0;

    @Override
    public void start(Stage STAGE) throws Exception {
        // Create a Scen
        // Create a Grid Pane with V and H gaps set to 0
        GridPane GRID_PANE = gridPane(0,0);
        // Create a context menu
        ContextMenu CONTEXT_MENU = new ContextMenu();
        // Make the Grid Pane movable while staying undecorated
        GRID_PANE.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    xOffset = event.getX();
                    yOffset = event.getY();
                    CONTEXT_MENU.hide();
                }
            }
        });

        GRID_PANE.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    STAGE.setX(event.getScreenX() - xOffset);
                    STAGE.setY(event.getScreenY() - yOffset);
                }
            }
        });
        // Menu Items to be added to the Context Menu
        MenuItem OPEN_IMAGE = new MenuItem("Open Image");       // Opens a new Image
        MenuItem CLOSE_IMAGE = new MenuItem("Close Image");     // Closes the current image
        MenuItem ABOUT = new MenuItem("About");                 // Displays program information to user
        MenuItem EXIT = new MenuItem("Exit");                   // Exit the program
        // Create Image View
        ImageView IMAGE_VIEW = new ImageView();
        // Locate and load logo image
        Image DEFAULT_IMAGE = new Image(main.class.getResourceAsStream("logo.png"));
        // Set the default image
        IMAGE_VIEW.setImage(DEFAULT_IMAGE);
        // Create Menu Item Event Handler
        EventHandler CONTEXT_MENU_ITEM_EVENT_HANDLER = new EventHandler() {
            @Override
            public void handle(Event event) {
                // Grab selected menu item and pull text to determine next step
                MenuItem MENU_ITEM = (MenuItem) event.getSource();
                String MENU_ITEM_TEXT = MENU_ITEM.getText();
                // Compare selected item to cases and take next step as needed
                switch (MENU_ITEM_TEXT.toLowerCase()) {
                    case "open image":
                        FileChooser FILE_SELECT = fileChooser();
                        FILE_SELECT.setInitialDirectory(new File(System.getProperty("user.home") + "/Pictures"));
                        File FILE = FILE_SELECT.showOpenDialog(null);
                        try {
                            BufferedImage bufferedImage = ImageIO.read(FILE);
                            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                            IMAGE_VIEW.setImage(image);
                            STAGE.setWidth(bufferedImage.getWidth()+2);
                            STAGE.setHeight(bufferedImage.getHeight()+2);
                        } catch (IOException ex) { errorAlertStage("IO File select error"); }
                        break;
                    case "close image":
                        IMAGE_VIEW.setImage(DEFAULT_IMAGE);
                        STAGE.setHeight(102);
                        STAGE.setWidth(102);
                        break;
                    case "about":
                        aboutAlertStage();
                        break;
                    case "exit":
                        Platform.exit();
                        break;
                    default: errorAlertStage("Menu Item not Found: "+MENU_ITEM_TEXT);
                }
            }
        };
        // Add Event Handler to Menu Items
        OPEN_IMAGE.setOnAction(CONTEXT_MENU_ITEM_EVENT_HANDLER);
        CLOSE_IMAGE.setOnAction(CONTEXT_MENU_ITEM_EVENT_HANDLER);
        ABOUT.setOnAction(CONTEXT_MENU_ITEM_EVENT_HANDLER);
        EXIT.setOnAction(CONTEXT_MENU_ITEM_EVENT_HANDLER);
        // Add Menu Items to the Context Menu
        CONTEXT_MENU.getItems().addAll(OPEN_IMAGE, CLOSE_IMAGE, ABOUT, EXIT);
        // Apply context menu handler to show context menu for the Grid Pane
        GRID_PANE.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                CONTEXT_MENU.show(GRID_PANE, event.getScreenX(), event.getScreenY());
            }
        });

        GRID_PANE.setBorder(new Border(new BorderStroke(
                Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        // Add the Image view to the grid pane
        GRID_PANE.add(IMAGE_VIEW, 0, 0);
        // Start with a no title bar stage,
        STAGE.setTitle("Simple Image Frame");
        STAGE.initStyle(StageStyle.UNDECORATED);
        STAGE.setScene(new Scene(GRID_PANE, 102, 102));
        STAGE.show();
    }

    public void errorAlertStage(String ERROR_MESSAGE) {
        Alert ERROR_ALERT = new Alert(Alert.AlertType.INFORMATION);
        ERROR_ALERT.setTitle("An Unexpected Error has Occurred");
        ERROR_ALERT.setHeaderText("");

        GridPane GRID_PANE = gridPane(0,0);

        Label ERROR_LABEL = new Label(ERROR_MESSAGE);
        GRID_PANE.add(ERROR_LABEL,0,0);
        ERROR_ALERT.getDialogPane().setContent(GRID_PANE);
        ERROR_ALERT.show();
    }

    public void aboutAlertStage() {
        // Create Alert, set Alert Title and Header Text
        Alert ABOUT_ALERT = new Alert(Alert.AlertType.INFORMATION);
        ABOUT_ALERT.setTitle("Image Viewer");
        ABOUT_ALERT.setHeaderText("");
        // Create a Grid Pane for layout with 0 for HGap and VGap
        GridPane GRID_PANE = gridPane(0,0);
        // Create Labels to display informational output to user
        Label ABOUT_PROGRAM_NAME = new Label("Image Viewer");
        Label ABOUT_VERSION_INFO = new Label("Version: 1.1");
        Label ABOUT_PURPOSE_STATEMENT = new Label("Basic image viewer without the clutter");
        // Add Labels to the Grid Pane layout
        GRID_PANE.add(ABOUT_PROGRAM_NAME, 0,0);
        GRID_PANE.add(ABOUT_VERSION_INFO, 0, 1);
        GRID_PANE.add(new Label(""),0,2);
        GRID_PANE.add(ABOUT_PURPOSE_STATEMENT,0,3);
        // Set Grid Pane as the Alert Content and show to user
        ABOUT_ALERT.getDialogPane().setContent(GRID_PANE);
        ABOUT_ALERT.show();
    }

    public GridPane gridPane(int hGap, int vGap) {
        GridPane GRID_PANE = new GridPane();
        GRID_PANE.setHgap(hGap);
        GRID_PANE.setVgap(vGap);
        return GRID_PANE;
    }

    public FileChooser fileChooser() {
        FileChooser FILE_SELECT = new FileChooser();
        FileChooser.ExtensionFilter EXT_FILTER_JPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter EXT_FILTER_PNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FILE_SELECT.getExtensionFilters().addAll(EXT_FILTER_PNG, EXT_FILTER_JPG);

        return FILE_SELECT;
    }
}
