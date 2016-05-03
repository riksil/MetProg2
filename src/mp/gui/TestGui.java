package mp.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Una classe per fare le prime sperimentazioni con JavaFX */
public class TestGui extends Application {
    public static void main(String[] args) {
        launch(args);    // Lancia l'applicazione, ritorna quando l'applicazione
    }                    // termina. Può essere invocato una sola volta

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root = createShapes();
        //Parent root = createTexts();
        Parent root = createTextChange();    // La radice del grafo di scena
        Scene scene = new Scene(root, 600, 300);
        scene.setFill(Color.AQUA);           // Colore di background della scena
        primaryStage.setScene(scene);        // Imposta la scena della finestra
        primaryStage.show();  // Rende visibile la finestra (o stage) principale
    }

    private static Parent createShapes() {
        Rectangle r = new Rectangle(120, 30, 300, 80);  // Rettangolo in (120, 30)
        Ellipse e = new Ellipse(200, 50, 100, 30);      // Ellisse con centro (200, 50)
        r.setFill(Color.RED);
        e.setFill(Color.BLUE);
        return new Group(r, e);
    }

    private static Parent createTexts() {
        Text t1 = new Text("Hello JavaFx");    // Oggetti che visualizzano testo
        Text t2 = new Text("Ciao");
        VBox vb = new VBox(t1, t2);
        vb.setAlignment(Pos.CENTER);           // Allineamento dei nodi
        vb.setSpacing(30);                     // Spazio tra i nodi
        return vb;
    }

    private static Parent createTextChange() {
        Text txt = new Text("Hello JavaFX");
        StackPane sp = new StackPane(txt);
        sp.setPrefHeight(80);
        Button showHide = new Button("Hide");
        showHide.setOnAction(e -> {       // Invocato quando il bottone showHide è cliccato
            if (txt.getOpacity() > 0) {
                FadeTransition fade = new FadeTransition(Duration.millis(500), txt);
                fade.setToValue(0);
                fade.play();
                //txt.setOpacity(0);
                showHide.setText("Show");
            } else {
                FadeTransition fade = new FadeTransition(Duration.millis(500), txt);
                fade.setToValue(1);
                fade.play();
                //txt.setOpacity(1);
                showHide.setText("Hide");
            }
        });
        Slider size = new Slider(8, 40, txt.getFont().getSize()); // Per impostare la dimensione della fonte
            // Listener invocato quando il valore dello Slider cambia
        size.valueProperty().addListener((o,ov,nv) -> tSize(txt, (double)nv));
        ComboBox<String> cb = new ComboBox<>();           // Per scegliere il nome della fonte
        cb.setPrefWidth(100);
        cb.getItems().addAll(Font.getFontNames());        // I nomi di tutte le fonti disponibili
        cb.setValue(txt.getFont().getName());             // Imposta la font attuale
        cb.setOnAction(e -> tFName(txt, cb.getValue()));  // Invocato quando è scelta una nuova fonte
        ColorPicker cp = new ColorPicker(Color.BLACK);    // Per scegliere il colore del testo
        cp.setOnAction(e -> {                  // Invocato quando è scelto un nuovo colore
            txt.setFill(cp.getValue());        // Imposta il colore di riempimento del testo
            txt.setStroke(cp.getValue());      // Imposta il colore di contorno del testo
        });
        HBox hb = new HBox(showHide, size, cb, cp);    // Layout per i controlli
        TextField tf = new TextField(txt.getText());   // Per cambiare il testo
        tf.setMaxWidth(400);               // Per evitare che si allarghi troppo
            // Invocato quando il contenuto del campo tf cambia
        tf.textProperty().addListener((o,ov,nv) -> txt.setText(nv));
        tf.setOnAction(e -> txt.setText(tf.getText()));   // Invocato quando ENTER è premuto
        hb.setAlignment(Pos.CENTER);
        hb.setSpacing(30);
        VBox vb = new VBox(sp, hb, tf);
        vb.setAlignment(Pos.CENTER);
        vb.setSpacing(30);
        return vb;
    }


    /** Modifica la dimensione della fonte del nodo Text specificato.
     * @param t  un nodo Text
     * @param s  la nuova dimensione della fonte */
    private static void tSize(Text t, double s) {
        t.setFont(new Font(t.getFont().getName(), s));
    }

    /** Modifica il nome completo della fonte del nodo Text specificato.
     * @param t  un nodo Text
     * @param fName  il nuovo nome completo della fonte */
    private static void tFName(Text t, String fName) {
        t.setFont(new Font(fName, t.getFont().getSize()));
    }
}
