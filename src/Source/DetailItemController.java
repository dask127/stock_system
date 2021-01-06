/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class DetailItemController implements Initializable {

    @FXML
    private Label dtl_name_input;
    @FXML
    private Label dtl_description_input;
    @FXML
    private Label dtl_id_input;
    @FXML
    private Label dtl_category_input;
    @FXML
    private Button dtl_edit_btn;
    @FXML
    private Button dtl_eliminate_btn;

    private Item item;

    private Model model;

    private ObservableList<Item> items;
    @FXML
    private Label dtl_price_sell_input;
    @FXML
    private Label dtl_price_buy_input;
    @FXML
    private Label dtl_quantity_input;
    @FXML
    private Label dtl_buy_date;
    @FXML
    private Label dtl_sell_date;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initAttributes(Item selected, Model model, ObservableList listaItems) {

        this.items = listaItems;
        this.model = model;
        this.item = selected;
        this.dtl_category_input.setText(selected.getCategoria());
        this.dtl_description_input.setText(selected.getDescripcion());
        this.dtl_id_input.setText(selected.getId());
        this.dtl_name_input.setText(selected.getNombre());
        this.dtl_price_sell_input.setText("$" + selected.getVenta());
        this.dtl_price_buy_input.setText("$" + selected.getCompra());
        this.dtl_quantity_input.setText("" + selected.getCantidad());

        String date = dateTranslator(selected.getFechaCompra());
        this.dtl_buy_date.setText(date);

        String text;
        if (selected.getFechaVenta() == null)
            text = "no vendido";
        else
            text = dateTranslator(selected.getFechaVenta());

        this.dtl_sell_date.setText(text);
    }

    @FXML
    private void deleteItem(ActionEvent event) {
        this.model.removeItem(this.item);
        this.item = null;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Informacion");
        alert.setContentText("Se ha borrado correctamente");
        alert.showAndWait();

        Stage stage = (Stage) this.dtl_eliminate_btn.getScene().getWindow();
        stage.close();
    }

    public Item getItem() {
        return this.item;
    }

    public String dateTranslator(String fecha) {
        String[] aux = fecha.split("-");

        String retorno = aux[0];

        // ENE / FEB/ MAR / ABR / MAY / JUN / JUL / AGO / SEP / OCT / NOV / DIC

        switch (aux[1]) {
            case "01":
                retorno += " " + "ENE";
                break;
            case "02":
                retorno += " " + "FEB";
                break;
            case "03":
                retorno += " " + "MAR";
                break;
            case "04":
                retorno += " " + "ABR";
                break;
            case "05":
                retorno += " " + "MAY";
                break;
            case "06":
                retorno += " " + "JUN";
                break;
            case "07":
                retorno += " " + "JUL";
                break;
            case "08":
                retorno += " " + "AGO";
                break;
            case "09":
                retorno += " " + "SEP";
                break;
            case "10":
                retorno += " " + "OCT";
                break;
            case "11":
                retorno += " " + "NOV";
                break;
            case "12":
                retorno += " " + "DIC";
                break;
        }

        retorno = retorno + " " + aux[2];

        return retorno;
    }

    @FXML
    private void editItem(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addItem.fxml"));

            Parent root = loader.load();
            AddItemController controlador = loader.getController();
            controlador.initAttributes(items);
            controlador.setItem(item);

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Item input_item = controlador.getNewItem();
            initAttributes(input_item, model, items);


        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
