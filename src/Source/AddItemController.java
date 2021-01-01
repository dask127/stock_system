/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class AddItemController implements Initializable {

    @FXML
    private TextField add_name_input;
    @FXML
    private TextArea add_descrip_input;

    @FXML
    private ChoiceBox add_category_input;
    @FXML
    private Button add_btn;

    private Item newItem;

    private Model model;

    private ObservableList<Item> items;
    @FXML
    private TextField add_price_sell_input;
    @FXML
    private TextField add_price_buy_input;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        model = new Model();
        //obtengo las categorias actuales desde el model
        ArrayList categories = model.getCategories();

        //para cada categoria se la agrego al choceBox
        for (Object category : categories) {
            this.add_category_input.getItems().add(category.toString());
        }
    }

    //inicio y traigo los items de la ventana principal
    public void initAttributes(ObservableList<Item> items) {
        this.items = items;
    }

    @FXML
    private void submit(ActionEvent event) {

        String nombre = this.add_name_input.getText();
        String descripcion = this.add_descrip_input.getText();
        int precioVenta = Integer.parseInt(this.add_price_sell_input.getText());
        int precioCompra = Integer.parseInt(this.add_price_buy_input.getText());
        String categoria = (String) this.add_category_input.getValue();

        Item item = new Item(nombre, descripcion, precioVenta, precioCompra);
        item.setId(model.createId());
        item.setCategoria(categoria);

        if (!items.contains(item)) {
            this.newItem = item;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Informacion");
            alert.setContentText("Se ha añadido correctamente");
            alert.showAndWait();

            Stage stage = (Stage) this.add_btn.getScene().getWindow();
            stage.close();
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El item ya existe");
            alert.showAndWait();
        }
    }

    public Item getNewItem() {
        return newItem;
    }

}
