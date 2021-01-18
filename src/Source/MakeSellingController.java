/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Source;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class MakeSellingController implements Initializable {

    @FXML
    private AnchorPane div_button;
    @FXML
    private VBox vbox_layout;

    private ArrayList<Item> carrito;

    private Model model;
    @FXML
    private Button id_btn;
    @FXML
    private TextField id_input;

    private ArrayList<Item> selledItems;
    @FXML
    private Button sell_btn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vbox_layout.setSpacing(20);
        selledItems = new ArrayList<Item>();
    }

    public void intiAttributes(ArrayList<Item> carrito, Model model) {
        this.carrito = carrito;
        this.model = model;

        if (!carrito.isEmpty()) {
            // Boton de eliminar / ID / Nombre / Precio / Cantidad - input
            for (Item item : carrito) {
                addItemToFront(item);
            }

        } else {
            Label label = new Label("No hay ítems en el carro.");
            label.setAlignment(Pos.CENTER);

            this.vbox_layout.getChildren().add(label);
            vbox_layout.setAlignment(Pos.CENTER);
        }
    }

    @FXML
    private void getCoordinates(ActionEvent event) {
        System.out.println(vbox_layout.getChildren());
    }

    @FXML
    private void addById(ActionEvent event) {
        String id = this.id_input.getText();
        this.id_input.setText("");

        Item returned = model.getItemById(id);

        if (returned != null) {
            if (!carrito.contains(returned)) {

                boolean empty = false;

                try {
                  Label label = (Label) vbox_layout.getChildren().get(0);
                  empty = true;
                } catch (Exception e) {
                    empty = false;
                }


                if (empty == true) {
                    vbox_layout.getChildren().clear();
                }

                carrito.add(returned);
                addItemToFront(returned);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("El ítem con id: " + "'" + id + "'" + " ya está en el carro.");
                alert.showAndWait();

            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El ítem con id: " + "'" + id + "'" + " no existe.");
            alert.showAndWait();

        }
    }

    public void addItemToFront(Item ff) {

        Button button1 = new Button("Eliminar");

        Label label_id = new Label(ff.getId());
        label_id.setMinWidth(100);
        label_id.setMaxWidth(100);
        label_id.setAlignment(Pos.CENTER);

        Label label_name = new Label(ff.getNombre());
        label_name.setMinWidth(150);
        label_name.setMaxWidth(150);
        // label_name.setWrapText(true);
        label_name.setAlignment(Pos.CENTER);

        Label label_precio = new Label("$" + ff.getVenta());
        label_precio.setMinWidth(100);
        label_precio.setMaxWidth(100);
        label_precio.setAlignment(Pos.CENTER);

        TextField label_cantidad = new TextField("1");

        label_cantidad.setMinWidth(100);
        label_cantidad.setMaxWidth(100);
        label_cantidad.setAlignment(Pos.CENTER);

        HBox hbox = new HBox(40);

        // asi es el event listener
        button1.setOnAction((ActionEvent event) -> {

            // para agarrarlo, lo tengo que castear, pero asi agarro el padre
            HBox hboxy = (HBox) button1.getParent();
            Label label_id_aux = (Label) hboxy.getChildren().get(1);
            String id = label_id_aux.getText();

            for (int i = 0; i < carrito.size(); i++) {
                Item aa = carrito.get(i);
                if (aa.getId().equals(id)) {
                    carrito.remove(i);
                }
            }

            hboxy.getChildren().clear();

            if (carrito.isEmpty()) {
                Label label = new Label("No hay ítems en el carro.");
                label.setAlignment(Pos.CENTER);

                this.vbox_layout.getChildren().add(label);
                vbox_layout.setAlignment(Pos.CENTER);
            }
        });

        hbox.getChildren().add(button1);
        hbox.getChildren().add(label_id);
        hbox.getChildren().add(label_name);
        hbox.getChildren().add(label_precio);
        hbox.getChildren().add(label_cantidad);

        this.vbox_layout.getChildren().add(hbox);

    }

    @FXML
    private void sellItems(ActionEvent event) {
        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<Integer> cantidades = new ArrayList<Integer>();

        if (!carrito.isEmpty()) {
            for (Node hbox_node : vbox_layout.getChildren()) {

                // nodo seria como el OBJ de JSON, ambiguo pero tenes que saber que tiene
                HBox aux = (HBox) hbox_node;
                Label label_id = (Label) aux.getChildren().get(1);
                String id = label_id.getText();

                ids.add(id);

                TextField quantity_input = (TextField) aux.getChildren().get(4);
                int quantity = Integer.parseInt(quantity_input.getText());

                cantidades.add(quantity);
            }

            // si se pone true, es porque algo no esta bien
            boolean flag = false;

            for (int i = 0; i < ids.size(); i++) {
                Item ff = searchById(ids.get(i));

                int model_quantity = model.getQuantityOfItem(ff);

                int quantity = cantidades.get(i);

                if (model_quantity < quantity) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Error");
                    alert.setContentText("Se supera el stock en: " + ff.getNombre() + ". Insertado: " + quantity + ". Disponible: " + model_quantity + ".");
                    alert.showAndWait();

                    flag = true;
                }
            }

            if (flag != true) {
                for (int j = 0; j < ids.size(); j++) {

                    Item ff = searchById(ids.get(j));
                    int quantity = cantidades.get(j);

                    selledItems.add(this.model.sellItem(ff, quantity));
                }

                ids.clear();
                cantidades.clear();

                carrito.clear();
                vbox_layout.getChildren().clear();

                Stage stage = (Stage) this.sell_btn.getScene().getWindow();
                stage.close();
            }
        }
    }

    public Item searchById(String id) {
        Item ff = null;
        for (Item item : carrito) {
            if (id.equals(item.getId())) {
                ff = item;
                break;
            }
        }
        return ff;
    }

    public ArrayList<Item> getSelledItems() {
        return selledItems;
    }
}
