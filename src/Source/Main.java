package Source;
 
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
 
public class Main extends Application {
 
    @Override
    public void start(Stage primaryStage) {

        try {
            //Cargo la vista                      
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("vista.fxml"));
 
            // Cargo la ventana
            Pane ventana = (Pane) loader.load();
 
            // Cargo el scene
            Scene scene = new Scene(ventana);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
 
            // Seteo la scene y la muestro
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         launch(args);
    }
 
}
