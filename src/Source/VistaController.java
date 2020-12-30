package Source;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML
    private TableColumn tbl_stock_price;

    private Model model;

    private ObservableList<Item> items;
    @FXML
    private TableColumn tbl_stock_cat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        items = FXCollections.observableArrayList();

        this.tbl_stock_name.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.tbl_stock_id.setCellValueFactory(new PropertyValueFactory("id"));
        this.tbl_stock_desc.setCellValueFactory(new PropertyValueFactory("descripcion"));
        this.tbl_stock_price.setCellValueFactory(new PropertyValueFactory("precio"));
        this.tbl_stock_cat.setCellValueFactory(new PropertyValueFactory("categoria"));

        model = new Model();

        ArrayList<Item> modelItems = model.getAllItems();

        if (!modelItems.isEmpty()) {
            for (Item item : modelItems) {
                items.add(item);
            }
        }

        tbl_stock.setItems(items);

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
                this.items.add(input_item);
                model.addItem(input_item, input_item.getCategoria());
                this.tbl_stock.refresh();
            }

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
