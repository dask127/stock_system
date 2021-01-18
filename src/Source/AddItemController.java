/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private Item itemToModify;

    private ObservableList<Item> items;
    @FXML
    private TextField add_price_sell_input;
    @FXML
    private TextField add_price_buy_input;
    @FXML
    private TextField add_quantity_input;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemToModify = null;
        model = new Model();
        // obtengo las categorias actuales desde el model
        ArrayList<Category> categories = model.getAllOfCategory();

        this.add_price_sell_input.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    add_price_sell_input.setText(oldValue);
                }
            }
        });

        this.add_price_buy_input.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    add_price_buy_input.setText(oldValue);
                }
            }
        });

        this.add_quantity_input.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    add_quantity_input.setText(oldValue);
                }
            }
        });

        if (!categories.isEmpty()) {
            // para cada categoria se la agrego al choceBox
            for (Category category : categories) {
                this.add_category_input.getItems().add(category.getCategoria());
            }
        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("No se pueden añadir ítems sin categoría.");
            alert.showAndWait();

            Stage stage = (Stage) this.add_btn.getScene().getWindow();
            stage.close();
        }
    }

    // inicio y traigo los items de la ventana principal
    public void initAttributes(ObservableList<Item> items) {
        this.items = items;
    }

    @FXML
    private void submit(ActionEvent event) {

        String descripcion = null;
        String categoria = null;
        String nombre = null;
        int precioCompra = 0;
        int precioVenta = 0;
        int cantidad = 0;

        try {
            nombre = this.add_name_input.getText();
            descripcion = this.add_descrip_input.getText();
            precioVenta = Integer.parseInt(this.add_price_sell_input.getText());
            precioCompra = Integer.parseInt(this.add_price_buy_input.getText());
            cantidad = Integer.parseInt(this.add_quantity_input.getText());
            categoria = (String) this.add_category_input.getValue();

            if ((nombre == null) || (descripcion == null) || (categoria == null) || (precioCompra == 0)
                    || (precioVenta == 0) || (cantidad == 0)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("Complete todos los campos.");
                alert.showAndWait();

            } else {
                Item item = new Item(nombre, descripcion, precioVenta, precioCompra, cantidad);
                item.setCategoria(categoria);

                if (this.itemToModify == null) {
                    item.setId(model.createId());

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
                } else {
                    item.setId(itemToModify.getId());
                    item.setFechaCompra(itemToModify.getFechaCompra());
                    model.modifyItem(itemToModify, item);
                    this.newItem = item;

                    Stage stage = (Stage) this.add_btn.getScene().getWindow();
                    stage.close();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Complete todos los campos.");
            alert.showAndWait();
        }
    }

    public void setItem(Item item) {
        this.itemToModify = item;

        this.add_btn.setText("Editar");
        this.add_name_input.setText(item.getNombre());
        this.add_descrip_input.setText(item.getDescripcion());
        this.add_price_sell_input.setText("" + item.getVenta());
        this.add_price_buy_input.setText("" + item.getCompra());
        this.add_quantity_input.setText("" + item.getCantidad());
        this.add_category_input.setValue(item.getCategoria());
    }

    public Item getNewItem() {
        return newItem;
    }

}
