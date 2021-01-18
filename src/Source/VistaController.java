package Source;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import Source.LogDayItem;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.TrayNotification;
import tray.notification.NotificationType;

/**
 * FXML Controller class
 *
 * @author octav
 */
public class VistaController implements Initializable {

    private Model model;
    private LogModel logModel;
    private ObservableList<Item> items;
    private ObservableList<Category> stock;
    private ArrayList<Item> cart;
    LogModel modelo_log;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // para que no este tan lleno el inicializador, lo divido en funciones
        // de acuerdo a las pestañas
        initStockPage();
        initLogPage();

        // le hago un pasamanos, de las ids ya usadas para que las tenga en cuenta
        ArrayList<String> ids = this.logModel.getItemIdentificators();
        this.model.setLogIdentificators(ids);

        checkStockAlerts();
    }

    // de aca arranca la tabla de stockaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
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
    @FXML
    private Button btn_refresh_stock;

    public void initStockPage() {

        model = new Model();

        cart = new ArrayList<Item>();
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

    @FXML
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

                ArrayList <Item> items = new ArrayList<Item>();
                items.add(input_item);
                logModel.insertOperation("BUY", items);

                this.tbl_stock.refresh();
                this.refreshStockTable();
                refreshLog(this.logModel.getAllItems());
            }

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
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
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("El ítem no se ha encontrado.");
            alert.showAndWait();
        }
    }

    @FXML
    private void getDetailOfItem(ActionEvent event) {
        Item selected = this.tbl_stock.getSelectionModel().getSelectedItem();

        if (selected != null) {

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
    private void DeleteItem(ActionEvent event) {
        Item selected = this.tbl_stock.getSelectionModel().getSelectedItem();

        if (selected != null) {
            this.model.removeItem(selected);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setTitle("Informacion");
            alert.setContentText("Se ha borrado correctamente");
            alert.showAndWait();

            refreshMainTable();
            refreshStockTable();
        }
    }

    @FXML
    private void EditItem(ActionEvent event) {
        Item selected = this.tbl_stock.getSelectionModel().getSelectedItem();

        if (selected != null) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("addItem.fxml"));

                Parent root = loader.load();
                AddItemController controlador = loader.getController();
                controlador.initAttributes(items);
                controlador.setItem(selected);

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
    private void addItemToCart(ActionEvent event) {

        Item selected = (Item) this.tbl_stock.getSelectionModel().getSelectedItem();

        if (selected != null) {
            if (!cart.contains(selected)) {
                cart.add(selected);
            }
        }
    }

    @FXML
    private void seeCart(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("makeSelling.fxml"));

            Parent root = loader.load();
            MakeSellingController controlador = loader.getController();
            controlador.intiAttributes(cart, model);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            refreshMainTable();
            refreshStockTable();

            ArrayList<Item> selled = controlador.getSelledItems();

            if (!selled.isEmpty()) {
                logModel.insertOperation("SELL", selled);

                ArrayList<LogDayItem> day_logs = logModel.getAllItems();
                refreshLog(day_logs);
            }

        } catch (IOException ex) {
            Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // de aca se viene lo lindo, EL HISTORIAAAAAAAAL *Musica dramatica de fondo*
    // ---------------------
    @FXML
    private VBox vbox_main;
    @FXML
    private ScrollPane scroll_log;
    @FXML
    private TextField input_log_id;
    @FXML
    private TextField input_log_name;

    @FXML
    private TextField input_log_date_day;
    @FXML
    private TextField input_log_date_month;
    @FXML
    private TextField input_log_date_year;

    public void initLogPage() {
        vbox_main.setSpacing(50);
        vbox_main.setAlignment(Pos.TOP_RIGHT);
        this.logModel = new LogModel();
        setLogPage();

        this.input_log_date_day.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    input_log_date_day.setText(oldValue);
                }
            }
        });

        this.input_log_date_month.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    input_log_date_month.setText(oldValue);
                }
            }
        });

        this.input_log_date_year.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,9}?")) {
                    input_log_date_year.setText(oldValue);
                }
            }
        });
    }

    public String dateTranslator(String fecha) {
        String[] aux = fecha.split("-");
        String retorno = aux[0];

        switch (aux[1]) {
            case "01":
                retorno += " " + "de enero";
                break;
            case "02":
                retorno += " " + "de febrero";
                break;
            case "03":
                retorno += " " + "de marzo";
                break;
            case "04":
                retorno += " " + "de abril";
                break;
            case "05":
                retorno += " " + "de mayo";
                break;
            case "06":
                retorno += " " + "de junio";
                break;
            case "07":
                retorno += " " + "de julio";
                break;
            case "08":
                retorno += " " + "de agosto";
                break;
            case "09":
                retorno += " " + "de septiembre";
                break;
            case "10":
                retorno += " " + "de octubre";
                break;
            case "11":
                retorno += " " + "de noviembre";
                break;
            case "12":
                retorno += " " + "de diciembre";
                break;
        }

        retorno = retorno + " " + aux[2];

        return retorno;
    }

    public void setLogPage() {
        ArrayList<LogDayItem> day_logs = logModel.getAllItems();
        refreshLog(day_logs);
    }

    public void refreshLog(ArrayList<LogDayItem> day_logs) {

        vbox_main.getChildren().clear();

        for (LogDayItem logDayItem : day_logs) {
            VBox container = new VBox();

            Font font = new Font("Seriff", 30);
            Label date = new Label(dateTranslator(logDayItem.getFecha()));
            date.setFont(font);
            date.setUnderline(true);

            container.getChildren().add(date);

            ArrayList<LogItem> items = logDayItem.getAcciones_del_dia();

            //lo doy vuelta para que se ordene en base a orden de agregacion (eso existe?)
            Collections.reverse(items);

            for (LogItem day_actions : items) {

                // arriba el id, abajo los items y el boton
                VBox day_container = new VBox();

                // Una accion del dia baby!
                Label accion_dia = new Label();
                String text;

                if (day_actions.getAction().equals("SELL")) {
                    text = "[VENTA] Se vendió: ";
                } else {
                    text = "[COMPRA] Se compró: ";
                }

                ArrayList<Item> day_items = day_actions.getItems();

                for (int i = 0; i < day_items.size(); i++) {
                    Item ff = day_items.get(i);

                    if (i + 1 == day_items.size()) {
                        text += ff.getNombre() + " x" + ff.getCantidad() + ".";
                    } else {
                        text += ff.getNombre() + " x" + ff.getCantidad() + ", ";
                    }
                }

                // tiene todos los items vendidos, y la accion
                accion_dia.setText(text);

                // el id y en bold
                Label id = new Label("ID: " + day_actions.getId());
                id.setStyle("-fx-font-weight: bold");

                // primero añado el id
                day_container.getChildren().add(id);

                // aca creo un div horizontal donde primero va
                // la accion e id, y luego el boton
                HBox items_btn = new HBox();
                items_btn.setMaxHeight(100);

                // añado la accion e items
                items_btn.getChildren().add(accion_dia);

                // creo el boton y se lo agrego
                Button info = new Button("Info");
                items_btn.getChildren().add(info);
                info.setAlignment(Pos.TOP_CENTER);

                info.setOnAction((ActionEvent event) -> {
                    ArrayList<Item> actionItems;

                    actionItems = logModel.getActionWithDate(logDayItem.getFecha(), day_actions.getId());
                    String action = day_actions.getAction();

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogItemsTable.fxml"));

                        Parent root = loader.load();
                        LogItemsTableController controlador = loader.getController();
                        controlador.initAttributes(action, actionItems);

                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                        Stage stage = new Stage();

                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setScene(scene);
                        stage.showAndWait();

                    } catch (IOException ex) {
                        Logger.getLogger(VistaController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                });

                items_btn.setMargin(info, new Insets(0, 0, 10, 30));

                // agrego la accion y boton abajo
                day_container.getChildren().add(items_btn);

                // agrego todo lo anterio al container de acciones de ese dia
                container.getChildren().add(day_container);
                container.setMargin(day_container, new Insets(5, 0, 5, 5));
            }

            // agrego ese container de dia a la lista
            vbox_main.getChildren().add(container);
            vbox_main.setMargin(container, new Insets(0, 0, 0, 50));
        }
    }

    @FXML
    private void searchLogById(ActionEvent event) {
        String id_buscado = null;

        if (id_buscado != null) {
            id_buscado = input_log_id.getText();

            ArrayList<LogDayItem> logs = this.logModel.searchByItemId(id_buscado);

            if (!logs.isEmpty()) {
                refreshLog(logs);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("No se encontraron ítems con el id: " + id_buscado + ".");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Complete el campo.");
            alert.showAndWait();
        }
    }

    @FXML
    private void searchLogByDate(ActionEvent event) {

        String day = null;
        String month = null;
        String year = null;

        day = input_log_date_day.getText();
        month = input_log_date_month.getText();
        year = input_log_date_year.getText();

        if ((day != null) && (month != null) && (year != null)) {

            // completo los strings para que me sean compatibles
            if (day.length() == 1) {
                day = "0" + day;
            }
            if (month.length() == 1) {
                month = "0" + month;
            }
            if (year.length() == 2) {
                year = "20" + year;
            }

            String searched_date = day + "-" + month + "-" + year;

            LogDayItem log = this.logModel.searchByDate(searched_date);

            if (log != null) {
                ArrayList<LogDayItem> logs = new ArrayList<LogDayItem>();
                logs.add(log);
                refreshLog(logs);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("No existe historial con esa fecha.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Complete todos los campos.");
            alert.showAndWait();
        }
    }

    @FXML
    private void searchLogByName(ActionEvent event) {
        String searched_name = null;

        searched_name = input_log_name.getText();

        if (searched_name != null) {
            ArrayList<LogDayItem> logs = this.logModel.searchByItemName(searched_name);

            if (!logs.isEmpty()) {
                refreshLog(logs);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("No se encuentran coincidencias con: '" + searched_name + "'.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error");
            alert.setContentText("Complete el campo.");
            alert.showAndWait();
        }
    }

    // misc---------------------------------------------------------------------------
    public void checkStockAlerts() {
        ArrayList<Category> categorias = model.getAllOfCategory();

        int i = 0;
        int base = 3000;
        int items = 0;

        for (Category category : categorias) {
            if (category.getAlerta() > category.getStock()) {
                items++;
            }
        }

        items = items * base;

        for (Category category : categorias) {
            // si el numero de la alerta es mas grande que el stock
            if (category.getAlerta() > category.getStock()) {

                String title = "Alerta de stock";
                String message = "Falta stock en: " + category.getCategoria();

                TrayNotification tray = new TrayNotification();
                tray.setTitle(title);
                tray.setMessage(message);
                tray.setNotificationType(NotificationType.WARNING);
                AnimationType type = AnimationType.POPUP;
                tray.setAnimationType(type);

                if (i != 0) {
                    items = items - base;
                    tray.showAndDismiss(Duration.millis(items));
                } else {
                    tray.showAndDismiss(Duration.millis(items));
                }

                i++;
            }
        }
    }

}
