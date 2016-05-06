package mp.gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/** Un mini web browser */
public class Browser extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = createUI();
        Scene scene = new Scene(root, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** Crea lo scene graph della UI e ne ritorna la radice.
     * @return la radice dello scene graph */
    private Parent createUI() {
        WebView wView = new WebView();       // Visualizza pagine web
        WebEngine we = wView.getEngine();    // La sottostante web engine
        TextField url = new TextField();     // Per immettere l'URL delle pagine
        url.setOnAction(e -> we.load(url.getText()));
        we.getLoadWorker().stateProperty().addListener((o,ov,nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) url.setText(we.getLocation());
        });
        WebHistory h = we.getHistory();
        ObservableList<WebHistory.Entry> hL = h.getEntries();
        Image backIcon = new Image(getClass().getResource("left16.png").toString());
        Button back = new Button(null, new ImageView(backIcon));
        back.setOnAction(e -> h.go(-1));
        //h.currentIndexProperty().addListener((o,ov,nv) -> back.setDisable((int)nv == 0));
        back.disableProperty().bind(h.currentIndexProperty().isEqualTo(0));
        Image forthIcon = new Image(getClass().getResource("right16.png").toString());
        Button forth = new Button(null, new ImageView(forthIcon));
        forth.setOnAction(e -> h.go(1));
        forth.disableProperty().bind(Bindings.createBooleanBinding(
                () -> h.getCurrentIndex() >= hL.size()-1,
                h.currentIndexProperty()));
        back.setOnContextMenuRequested(e -> {    // Menu contestuale pagine precedenti
            ContextMenu cm = new ContextMenu();    // Crea un menu contestuale
            int curr = h.getCurrentIndex();
            for (int i = curr-1 ; i >= 0 ; i--) {  // Aggiunge una voce di menu per
                int offset = i - curr;             // ogni pagina precedente
                MenuItem mi = new MenuItem(hL.get(i).getUrl());
                mi.setOnAction(v -> h.go(offset)); // Ad ognuna associa l'azione di
                cm.getItems().add(mi);             // di andare alla pagina
            }
            cm.setAutoHide(true);    // Diventa invisibile non appena perde il focus
            cm.show(back.getScene().getWindow(), e.getScreenX(), e.getScreenY());
        });
        forth.setOnContextMenuRequested(e -> {    // Menu contestuale pagine successive
            ContextMenu cm = new ContextMenu();
            int curr = h.getCurrentIndex();
            for (int i = curr+1 ; i < hL.size() ; i++) {
                int offset = i - curr;
                MenuItem mi = new MenuItem(hL.get(i).getUrl());
                mi.setOnAction(v -> h.go(offset));
                cm.getItems().add(mi);
            }
            cm.setAutoHide(true);
            cm.show(forth.getScene().getWindow(), e.getScreenX(), e.getScreenY());
        });
        HBox hb = new HBox(back, forth, url);    // Per i controlli
        VBox vb = new VBox(hb, wView);
        HBox.setHgrow(url, Priority.ALWAYS);     // Si estende in orizzontale
        VBox.setVgrow(wView, Priority.ALWAYS);   // Si estende in verticale
        return vb;
    }
}
