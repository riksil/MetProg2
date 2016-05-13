package mp.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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

        ProgressIndicator pi = new ProgressIndicator();
        pi.progressProperty().bind(we.getLoadWorker().progressProperty());

        HBox hb = new HBox(back, forth, url, pi, downloadImg(we));    // Per i controlli
        VBox vb = new VBox(hb, wView);
        HBox.setHgrow(url, Priority.ALWAYS);     // Si estende in orizzontale
        VBox.setVgrow(wView, Priority.ALWAYS);   // Si estende in verticale
        return vb;
    }

    /** Ritorna un componente grafico (un Node) che contiene due bottoni per
     * gestire il dowload asincrono e la visualizzazione delle immagini
     * contenute nelle pagine scaricate dalla web engine data.
     * @param we  una web engine
     * @return un componente grafico (un Node) con due bottoni */
    private static Node downloadImg(WebEngine we) {
        Stage win = new Stage();                 // La finestra di primo livello
        FlowPane imgPane = new FlowPane();       // Il contenitore per le immagini
        ScrollPane sp = new ScrollPane(imgPane);
        VBox vb = new VBox();
        ScrollPane sp2 = new ScrollPane(vb);
        SplitPane split = new SplitPane(sp, sp2);
        win.setScene(new Scene(split, 400, 300));
        ExecutorService exec = Executors.newCachedThreadPool(r -> {   // Factory
            Thread t = new Thread(r);     // dei thread usati dall'esecutore per
            t.setDaemon(true);            // far sì che siano daemon thread, cioè
            return t;                     // non bloccano la chiusura del programma
        });
        Button loadB = new Button(null, new ImageView(Browser.class
                .getResource("load16.png").toString()));
        Tooltip ttLoad = new Tooltip("Scarica le immagini della pagina");
        ttLoad.setStyle("-fx-background-color:ivory; -fx-text-fill:black");
        loadB.setTooltip(ttLoad);
        loadB.disableProperty().bind(Bindings.createBooleanBinding(
                () -> we.getDocument() == null, we.documentProperty()));
        loadB.setOnAction(e -> {
            Set<URI> uris = imageURIs(we);
            ImageTask task = new ImageTask(uris,
                    img -> Platform.runLater(() ->                   // Esegue il runnable in modo
                    imgPane.getChildren().add(new ImageView(img)))); // asincrono sul JavaFX App Thread
            ProgressIndicator pi = new ProgressIndicator();
            pi.progressProperty().bind(task.progressProperty());
            Button stopB = new Button("X");
            stopB.setFont(new Font(stopB.getFont().getName(), 8));
            stopB.setOnAction(ev -> task.cancel());
            stopB.disableProperty().bind(task.runningProperty().not());
            HBox hb = new HBox(pi, stopB);
            hb.setAlignment(Pos.BOTTOM_LEFT);
            vb.getChildren().add(new Label(we.getLocation(), hb));
            exec.submit(task);
        });
        Button winB = new Button(null, new ImageView(Browser.class
                .getResource("win16.png").toString()));
        winB.setOnAction(e -> { win.show(); win.toFront(); });
        return new HBox(loadB, winB);
    }

    private static class ImageTask extends Task<Void> {
        /** Crea un task asincrono per scaricare le immagini relative all'insieme
         * di URI dato e per eseguire su ognuna di esse l'azione specificata.
         * @param uu  insieme di URI di immagini
         * @param act  azione eseguita per ogni immagine scaricata */
        ImageTask(Set<URI> uu, Consumer<Image> act) {
            uris = uu;
            action = act;
        }

        @Override
        protected Void call() throws Exception {
            int count = 0;          // Per il conteggio delle immagini scaricate
            for (URI u : uris) {
                Image img = new Image(u.toString());  // Scarica l'immagine
                action.accept(img);                   // Esegue l'azione sull'immagine
                count++;
                updateProgress(count, uris.size());
                if (isCancelled()) break;
            }
            return null;
        }

        private final Set<URI> uris;
        private final Consumer<Image> action;
    }

    /** Ritorna l'insieme degli URI delle immagini contenute nella pagina della
     * web engine data. Eventuali URI malformati sono ignorati. Se il Document
     * ritornato dalla web engine non ha un URI, ritorna un insieme vuoto.
     * @param we  una web engine
     * @return l'insieme degli URI delle immagini della pagina */
    private static Set<URI> imageURIs(WebEngine we) {
        Document doc = we.getDocument();
        Set<URI> uris = new HashSet<>();
        try {
            URI base = new URI(doc.getDocumentURI());
            NodeList lst = doc.getElementsByTagName("img");
            for (int i = 0 ; i < lst.getLength() ; i++) {
                String imgAddr = lst.item(i).getAttributes().getNamedItem("src").getNodeValue();
                try {
                    uris.add(base.resolve(imgAddr));
                } catch (Exception e) {}
            }
        } catch (URISyntaxException e) { e.printStackTrace(); }
        return uris;
    }
}
