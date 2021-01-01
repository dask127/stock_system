/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

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
    @FXML
    private Label dtl_price_sell_input;
    @FXML
    private Label dtl_price_buy_input;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initAttributes(Item selected, Model model) {

        this.model = model;
        this.item = selected;
        this.dtl_category_input.setText(selected.getCategoria());
        this.dtl_description_input.setText(selected.getDescripcion());
        this.dtl_id_input.setText(selected.getId());
        this.dtl_name_input.setText(selected.getNombre());
        this.dtl_price_sell_input.setText("$" + selected.getVenta());
        this.dtl_price_buy_input.setText("$" + selected.getCompra());
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

    public Item getItem(){
        return this.item;
    }
}
