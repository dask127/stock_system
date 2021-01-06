package Source;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class VistaController implements Initializable {

    @FXML
    private Button btn_addItem;
    @FXML
    private TableView<Item> tbl_stock;
    @FXML
    private TableColumn tbl_stock_id;
    @FXML
    private TableColumn tbl_stock_name;
    @FXML
    private TableColumn tbl_stock_desc;

    private Model model;

    private ObservableList<Item> items;

    private ObservableList<Category> stock;
    @FXML
    private TableColumn tbl_stock_cat;
    @FXML
    private Button btn_searchItem;
    @FXML
    private TextField input_id_search;
    @FXML
    private TableView tbl_stock_quantity;
    @FXML
    private TableColumn tbl_stock_quantity_category;
    @FXML
    private TableColumn tbl_stock_quantity_2;
    @FXML
    private TableColumn tbl_stock_sell_price;
    @FXML
    private TableColumn tbl_stock_buy_price;
    @FXML
    private Button btn_addcategory;
    @FXML
    private Button btn_sell;
    @FXML
    private TableColumn tbl_stock_item_quantity;
    @FXML
    private Button btn_search_names;
    @FXML
    private TextField input_id_search_name;
    @FXML
    private Button btn_reload;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = new Model();

        // tabla de items
        items = FXCollections.observableArrayList();

        this.tbl_stock_name.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.tbl_stock_id.setCellValueFactory(new PropertyValueFactory("id"));
        this.tbl_stock_desc.setCellValueFactory(new PropertyValueFactory("descripcion"));
        this.tbl_stock_cat.setCellValueFactory(new PropertyValueFactory("categoria"));
        this.tbl_stock_sell_price.setCellValueFactory(new PropertyValueFactory("venta"));
        this.tbl_stock_buy_price.setCellValueFactory(new PropertyValueFactory("compra"));
        this.tbl_stock_item_quantity.setCellValueFactory(new PropertyValueFactory("cantidad"));

        ArrayList<Item> modelItems = model.getAllItems();

        if (!modelItems.isEmpty()) {
            for (Item item : modelItems) {
                items.add(item);
            }
            Collections.reverse(items);
        }
        tbl_stock.setItems(items);

        // tabla de stock
        stock = FXCollections.observableArrayList();

        this.tbl_stock_quantity_category.setCellValueFactory(new PropertyValueFactory("categoria"));
        this.tbl_stock_quantity_2.setCellValueFactory(new PropertyValueFactory("stock"));

        ArrayList<Category> allStock = model.getAllOfCategory();

        if (!allStock.isEmpty()) {
            for (Category aux : allStock) {
                stock.add(aux);
            }
        }
        tbl_stock_quantity.setItems(stock);
    }

    public void refreshStockTable() {

        ArrayList<Category> allStock = model.getAllOfCategory();

        stock.clear();

        for (Category aux : allStock) {
            this.stock.add(aux);
        }
        this.tbl_stock_quantity.refresh();

    }

    @FXML
    public void refreshMainTable() {

        ArrayList<Item> allStock = model.getAllItems();

        items.clear();

        for (Item item : allStock) {
            items.add(item);
        }
        Collections.reverse(items);
        this.tbl_stock_quantity.refresh();
    }

    @FXML
    private void openAdder(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addItem.fxml"));

            Parent root = loader.load();
            AddItemController controlador = loader.getController();
            controlador.initAttributes(items);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Item input_item = controlador.getNewItem();

            if (input_item != null) {

                this.items.add(0, input_item);
                model.addItem(input_item, input_item.getCategoria());
                this.tbl_stock.refresh();
                this.refreshStockTable();
            }

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void selectItem(MouseEvent event) {
        // agarro el item al que le hice click
        Item selected = this.tbl_stock.getSelectionModel().getSelectedItem();
        if (selected != null) {

            int index = 0;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detailItem.fxml"));

                Parent root = loader.load();
                DetailItemController controlador = loader.getController();
                controlador.initAttributes(selected, this.model, this.items);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                refreshMainTable();
                refreshStockTable();

            } catch (IOException ex) {
                Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void openSearcher(ActionEvent event) {
        String id = this.input_id_search.getText();
        this.input_id_search.setText("");

        Item item = model.getItemById(id);

        if (item == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El ítem con id: " + "'" + id + "'" + " no existe.");
            alert.showAndWait();

        } else {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detailItem.fxml"));

                Parent root = loader.load();
                DetailItemController controlador = loader.getController();
                controlador.initAttributes(item, model, this.items);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                this.refreshStockTable();
                this.refreshMainTable();

            } catch (IOException ex) {
                Logger.getLogger(VistaController.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    @FXML
    private void openAdderCategory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCategory.fxml"));

            Parent root = loader.load();
            AddCategoryController controlador = loader.getController();
            controlador.initAttributes(stock);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Category nueva = controlador.getNewCategory();

            model.createCategory(nueva);

            this.stock.add(nueva);
            this.tbl_stock_quantity.refresh();

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void selectCategory(MouseEvent event) {
        // agarro la categoria
        Category selected = (Category) this.tbl_stock_quantity.getSelectionModel().getSelectedItem();
        int index = 0;

        if (selected != null) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("detailCategory.fxml"));

                Parent root = loader.load();
                DetailCategoryController controlador = loader.getController();
                controlador.initAttributes(selected, this.model, stock);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();

                refreshStockTable();
                refreshMainTable();

            } catch (IOException ex) {
                Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void openNameSearcher(ActionEvent event) {

        String nombre = this.input_id_search_name.getText();
        this.input_id_search_name.setText("");

        ArrayList<Item> encontrados = model.getItemsByName(nombre);
        if (!encontrados.isEmpty()) {
            items.clear();
            for (Item item : encontrados) {
                items.add(item);
            }
        }
    }
}
