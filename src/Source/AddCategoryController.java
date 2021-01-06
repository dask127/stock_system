/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class AddCategoryController implements Initializable {

    @FXML
    private TextField dtl_category_name;
    @FXML
    private TextField dtl_category_alert;
    @FXML
    private Button dtl_category_btn;

    private Category newCategory;

    private Category modifyCategory;

    private Model model;

    private ObservableList<Category> stock;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Category modifyCategory = null;
    }

    public void initAttributes(ObservableList<Category> stock) {
        this.stock = stock;
    }

    public void setStartCategory(Category categoria, Model model) {
        this.model = model;
        this.modifyCategory = categoria;
        this.dtl_category_name.setText(categoria.getCategoria());
        this.dtl_category_alert.setText("" + categoria.getAlerta());
    }

    @FXML
    private void SaveCategory(ActionEvent event) {

        String nombre = this.dtl_category_name.getText();
        int alert = Integer.parseInt(this.dtl_category_alert.getText());

        // si hay categoria para modificar...
        if (modifyCategory != null) {
            Category categoria = new Category(nombre, this.modifyCategory.getStock(), alert);
            if (categoria.equals(this.modifyCategory)) {

                Alert aux_alert = new Alert(Alert.AlertType.ERROR);
                aux_alert.setHeaderText(null);
                aux_alert.setTitle("Error");
                aux_alert.setContentText("No se ha modificado la categoría");
                aux_alert.showAndWait();
            } else {
                int flag = 1;

                for (int i = 0; i < stock.size(); i++) {
                    Category ff = stock.get(i);

                    // si ya existe una categoria con el mismo nombre
                    if (ff.getCategoria().equals(nombre)) {

                        Alert aux_alert = new Alert(Alert.AlertType.ERROR);
                        aux_alert.setHeaderText(null);
                        aux_alert.setTitle("Error");
                        aux_alert.setContentText("Ya existe una categoría con ese nombre");
                        aux_alert.showAndWait();

                        i = stock.size();
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    model.modifyCategory(modifyCategory.getCategoria(), categoria.getCategoria());
                    this.newCategory = categoria;
                }
            }
            // sino pregunto si hay alguna clase con el mismo nombre
        } else {

            if (stock.size() != 0) {

                for (int i = 0; i < stock.size(); i++) {
                    Category ff = stock.get(i);

                    // si ya existe una categoria con el mismo nombre
                    if (ff.getCategoria().equals(nombre)) {
                        Alert aux_alert = new Alert(Alert.AlertType.ERROR);
                        aux_alert.setHeaderText(null);
                        aux_alert.setTitle("Error");
                        aux_alert.setContentText("Ya existe una categoría con ese nombre");
                        aux_alert.showAndWait();
                    } else {
                        this.newCategory = new Category(nombre, 0, alert);
                    }
                }
            } else this.newCategory = new Category(nombre, 0, alert);
        }

        Stage stage = (Stage) this.dtl_category_btn.getScene().getWindow();
        stage.close();
    }

    public Category getNewCategory() {
        return newCategory;
    }

}
