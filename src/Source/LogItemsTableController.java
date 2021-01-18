/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class LogItemsTableController implements Initializable {

    @FXML
    private TableColumn tbl_id;
    @FXML
    private TableColumn tbl_name;
    @FXML
    private TableColumn tbl_sell_date;
    @FXML
    private TableColumn tbl_price;
    @FXML
    private TableColumn tbl_quantity;
    @FXML
    private TableView<Item> tbl;
    @FXML
    private Label label_price;

    private String action;
    private ArrayList<Item> items;
    private LogModel model;
    private ObservableList<Item> dayItems;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initAttributes(String action, ArrayList<Item> items) {

        this.action = action;
        this.items = items;

        // tabla de items
        dayItems = FXCollections.observableArrayList();

        this.tbl_name.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.tbl_id.setCellValueFactory(new PropertyValueFactory("id"));

        if (action.equals("SELL")) {
            this.tbl_price.setCellValueFactory(new PropertyValueFactory("venta"));
        } else {
            this.tbl_price.setText("Precio compra");
            this.tbl_price.setCellValueFactory(new PropertyValueFactory("compra"));
        }


        this.tbl_quantity.setCellValueFactory(new PropertyValueFactory("cantidad"));

        if (action.equals("SELL")) {
            this.tbl_sell_date.setCellValueFactory(new PropertyValueFactory("fechaVenta"));
        } else {
            this.tbl_sell_date.setText("Fecha de compra");
            this.tbl_sell_date.setCellValueFactory(new PropertyValueFactory("fechaCompra"));
        }

        if (!items.isEmpty()) {
            for (Item item : items) {
                dayItems.add(item);
            }
        }

        tbl.setItems(dayItems);

        double total = 0;

        if (action.equals("SELL")) {
            for (Item item : items) {
                total = total + item.getVenta() * item.getCantidad();
            }
        } else {
            for (Item item : items) {
                total = total + item.getCompra() * item.getCantidad();
            }
        }

        label_price.setText("$" + total);
    }

    @FXML
    private void selectItem(MouseEvent event) {
        Item selected = (Item) this.tbl.getSelectionModel().getSelectedItem();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("detailItem.fxml"));

            Parent root = loader.load();
            DetailItemController controlador = loader.getController();
            controlador.initAttributes(selected, null, null);
            controlador.deleteBtns();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
