/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class DetailCategoryController implements Initializable {

    @FXML
    private Label dtl_category_name;
    @FXML
    private Label dtl_category_stock;
    @FXML
    private Label dtl_category_alert;
    @FXML
    private Label dtl_category_item_1;
    @FXML
    private Label dtl_category_item_2;
    @FXML
    private Label dtl_category_item_3;
    @FXML
    private Label dtl_category_item_4;
    @FXML
    private Button dtl_category_edit_btn;
    @FXML
    private Button dtl_category_delete_btn;

    private Category selectedCategory;

    private Model model;

    private ObservableList <Category> categorias;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initAttributes(Category selected, Model model, ObservableList cats) {
       this.categorias = cats;
        this.selectedCategory = selected;
        this.model = model;

        ArrayList<Label> categoryLabels = new ArrayList<Label>();
        categoryLabels.add(dtl_category_item_1);
        categoryLabels.add(dtl_category_item_2);
        categoryLabels.add(dtl_category_item_3);
        categoryLabels.add(dtl_category_item_4);

        ArrayList<Item> categoryItems = model.getItemsByCategory(selected.getCategoria());

        if (!categoryItems.isEmpty()) {
            int index = 0;
            for (Label label : categoryLabels) {
                if (index < categoryItems.size()) {
                    label.setText(categoryItems.get(index).getNombre());
                    index++;
                }
            }
        }

        this.dtl_category_name.setText(selected.getCategoria());
        this.dtl_category_stock.setText("" + selected.getStock());
        this.dtl_category_alert.setText("" + selected.getAlerta());

    }

    @FXML
    private void editCategory(ActionEvent event) {
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addCategory.fxml"));

            Parent root = loader.load();
            AddCategoryController controlador = loader.getController();
            controlador.initAttributes(this.categorias);
            controlador.setStartCategory(this.selectedCategory, model);

            

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Category modified = controlador.getNewCategory();
            this.initAttributes(modified, model, categorias);


        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteCategory(ActionEvent event) {
        this.model.removeCategory(this.selectedCategory.getCategoria());

        Alert aux_alert = new Alert(Alert.AlertType.CONFIRMATION);
        aux_alert.setHeaderText(null);
        aux_alert.setTitle("Confirmación");
        aux_alert.setContentText("Se ha borrado exitosamente la categoría: " + this.selectedCategory.getCategoria());
        aux_alert.showAndWait();

        Stage stage = (Stage) this.dtl_category_delete_btn.getScene().getWindow();
        stage.close();
    }
}
